package nl.beeldengeluid.mapping;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import nl.beeldengeluid.mapping.annotations.Source;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.ValueReader;


@Slf4j
public class JsonFieldModule<SOURCE, DESTINATION> implements org.modelmapper.Module {
    
    private final Class<SOURCE> sourceClass;
    private final Class<DESTINATION> destinationClass;

    private final Map<String, Mapping> mapping;

    

    public JsonFieldModule(Class<SOURCE> sourceClass, Class<DESTINATION> destinationClass) {
        this.sourceClass = sourceClass;
        this.destinationClass = destinationClass;
        this.mapping = Arrays.stream(destinationClass.getDeclaredFields())
                .map(f -> {

                    Source s = f.getAnnotation(Source.class);
                    if (s == null) {
                        return null;
                    }
                    try {
                        Field sourceField = sourceClass.getDeclaredField(s.field());
                        sourceField.setAccessible(true);
                        return s == null ? null :
                            new AbstractMap.SimpleEntry<>(f.getName(), new Mapping(s, sourceField));
                    } catch (NoSuchFieldException e) {
                        log.warn(e.getMessage(), e);
                        return null;
                    }
                }).filter(Objects::nonNull)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public void setupModule(ModelMapper modelMapper) {
        modelMapper.getConfiguration().addValueReader(new JsonValueReader());
    }

    public class JsonValueReader implements ValueReader<Object> {


        @Override
        public Object get(Object source, String memberName) {
            if (sourceClass.isInstance(source)) {
                Mapping s = mapping.get(memberName);
                if (s != null) {
                    try {
                        return s.field().get(source);
                    } catch (IllegalAccessException e) {
                        log.warn(e.getMessage(), e);
                    }
                }
            }
            return null;
        }

        @Override
        public Member<Object> getMember(Object source, String memberName) {
            if (sourceClass.isInstance(source)) {
                Mapping s = mapping.get(memberName);
                if (s != null) {

                    return new JsonMember(sourceClass);
                }
            }
            return null;
        }

        @Override
        public Collection<String> memberNames(Object source) {
            return mapping.keySet();
        }

    }
    
    @SneakyThrows
    private void set(DESTINATION dest, Field field, Object value) {
        try {
            field.set(dest, value);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        
    }

    record Mapping(Source source, Field field) {

    }

    private static final com.fasterxml.jackson.databind.ObjectMapper MAPPER = new ObjectMapper();
    static {
        MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        MAPPER.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        MAPPER.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        MAPPER.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);
    }

    class JsonMember extends   ValueReader.Member<Object>{

        public JsonMember(Class<?> valueType) {
            super(valueType);
        }

        @Override
        public Object get(Object source, String memberName) {
            try {
                Mapping m = mapping.get(memberName);
                Object json = m.field().get(source);
                JsonNode node;
                if (json instanceof byte[] bytes){
                    node = MAPPER.readTree(bytes);
                } else if (json instanceof JsonNode n) {
                    node = n;
                } else {
                    throw new IllegalStateException();
                }
                return node.at(m.source().pointer()).asText();
            } catch (IllegalAccessException | IOException e) {
                log.warn(e.getMessage(), e);
            }
            return null;
      }
    }
}
