package nl.beeldengeluid.mapping.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Field;
import java.util.*;
import nl.beeldengeluid.mapping.annotations.Source;
import nl.beeldengeluid.mapping.annotations.Sources;

class Util {

    private Util() {
        // no instances allowed
    }

    static final ObjectMapper MAPPER = new ObjectMapper();
    static {
        MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        MAPPER.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        MAPPER.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        MAPPER.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);
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

    public static Object unwrapJson(JsonNode jsonNode) {
        if (jsonNode.isNull()) {
            return null;
        }
        if (jsonNode.isLong()) {
            return jsonNode.asLong();
        }
        if (jsonNode.isInt()) {
            return jsonNode.asInt();
        }
        if (jsonNode.isBoolean()) {
            return jsonNode.asBoolean();
        }
        if (jsonNode.isTextual()) {
            return jsonNode.asText();
        }
        if (jsonNode.isDouble()) {
            return jsonNode.asDouble();
        }
        if (jsonNode.isFloat()) {
            return jsonNode.asDouble();
        }
        if (jsonNode.isArray()) {
            List<Object> result = new ArrayList<>();
            jsonNode.forEach(e -> {
                result.add(unwrapJson(e));
            });
            return result;
        }
        // Not complete, I suppose
        return jsonNode.asText();

    }


}
