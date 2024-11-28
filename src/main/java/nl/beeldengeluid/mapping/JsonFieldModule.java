package nl.beeldengeluid.mapping;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import nl.beeldengeluid.mapping.annotations.Source;
import nl.beeldengeluid.mapping.annotations.Sources;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.ValueReader;


@Slf4j
public class JsonFieldModule<SOURCE, DESTINATION> implements org.modelmapper.Module {
    
    private final Class<SOURCE> sourceClass;
    private final Class<DESTINATION> destinationClass;

    private final Map<Class<?>, Map<String, Mapping>> mapping = new HashMap<>();

    private JsonFieldModule(Class<SOURCE> sourceClass, Class<DESTINATION> destinationClass) {
        this.sourceClass = sourceClass;
        this.destinationClass = destinationClass;
    }

    public static JsonFieldModule<Object, Object> instance() {
        // For this to work we will need annotation scanning library.
        return new JsonFieldModule<>(Object.class, Object.class);
    }

    public static <DESTINATION> JsonFieldModule<Object, DESTINATION> of(Class<DESTINATION> destinationClass) {
        return new JsonFieldModule<>(Object.class, destinationClass);
    }

    public static <SOURCE, DESTINATION> JsonFieldModule<SOURCE, DESTINATION> of(Class<SOURCE> sourceClass, Class<DESTINATION> destinationClass) {
        return new JsonFieldModule<>(sourceClass, destinationClass);
    }

    @Override
    public void setupModule(ModelMapper modelMapper) {
        modelMapper.getConfiguration().addValueReader(new JsonValueReader());
    }

    public class JsonValueReader implements ValueReader<Object> {


        @Override
        public Object get(Object source, String memberName) {
            if (sourceClass.isInstance(source)) {
                Mapping s = getMapping(source.getClass()).get(memberName);
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
                Mapping s = getMapping(source.getClass()).get(memberName);
                if (s != null) {

                    return new JsonMember(sourceClass);
                }
            }
            return null;
        }

        @Override
        public Collection<String> memberNames(Object source) {
            return getMapping(source.getClass()).keySet();
        }

    }

    Map<String, Mapping> getMapping(Class<?> sourceClass) {
        final Map<String, Mapping> superMapping;
        Class<?> superClass = sourceClass.getSuperclass();
        if (superClass != null && this.sourceClass.isAssignableFrom(superClass)) {
            superMapping = getMapping(superClass);
        } else {
            superMapping = null;
        }
        return mapping.computeIfAbsent(sourceClass, clazz -> {
            Map<String, Mapping> newMap = new HashMap<>();
            if (superMapping != null){
                newMap.putAll(superMapping);;
            }
            Arrays.stream(this.destinationClass.getDeclaredFields())
                .map(f -> {
                    return getEntry(sourceClass, f).orElse(null);
                }).filter(Objects::nonNull)
                .forEach(e -> newMap.put(e.getKey(), e.getValue()));
            return Collections.unmodifiableMap(newMap);
        });
    }

    static Optional<Map.Entry<String, Mapping>> getEntry(Class<?> sourceClass, Field f) {
        Source s = null;
        {

            Sources sources = f.getAnnotation(Sources.class);
            if (sources != null) {
                for (Source proposal : sources.value()) {
                    if (proposal.sourceClass().isAssignableFrom(sourceClass)) {
                        if (s == null) {
                            s = proposal;
                        } else {
                            if (s.sourceClass().isAssignableFrom(proposal.sourceClass())) {
                                // this means proposal is more specific
                                s = proposal;
                            }
                        }
                    }
                }
            } else {
                s = f.getAnnotation(Source.class);
            }
        }
        if (s != null) {
            try {
                Field sourceField = sourceClass.getDeclaredField(s.field());
                sourceField.setAccessible(true);
                return Optional.of(new AbstractMap.SimpleEntry<>(f.getName(), new Mapping(s, sourceField)));
            } catch (NoSuchFieldException e) {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
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
                Mapping m = getMapping(source.getClass()).get(memberName);
                Object json = m.field().get(source);
                if (json == null) {
                    json = m.source().defaultValue();
                    if ("".equals(json)) {
                        json = null;
                    }
                    if (json == null) {
                        return null;
                    }
                }
                JsonNode node;

                if (json instanceof byte[] bytes) {
                    node = MAPPER.readTree(bytes);
                } else if (json instanceof String string){
                    node = MAPPER.readTree(string);
                } else if (json instanceof JsonNode n) {
                    node = n;
                } else {
                    throw new IllegalStateException("%s could not be mapped to json %s -> %s".formatted(memberName , m, json));
                }
                return node.at(m.source().pointer()).asText();
            } catch (IllegalAccessException | IOException e) {
                log.warn(e.getMessage(), e);
            }
            return null;
      }
    }
}
