package nl.beeldengeluid.mapping.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Field;
import java.util.*;
import nl.beeldengeluid.mapping.annotations.Source;
import nl.beeldengeluid.mapping.annotations.Sources;

class Util {

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


}
