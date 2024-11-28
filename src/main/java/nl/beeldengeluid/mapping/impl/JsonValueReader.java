/*
 * Copyright (C) 2024 Licensed under the Apache License, Version 2.0
 */
package nl.beeldengeluid.mapping.impl;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import static nl.beeldengeluid.mapping.impl.Util.MAPPER;
import org.modelmapper.spi.ValueReader;

@Slf4j
public class JsonValueReader implements ValueReader<Object> {



    private final Class<?> sourceClass;
    private final Class<?> destinationClass;

    private final Map<Class<?>, Map<String, Mapping>> mapping = new HashMap<>();


    public JsonValueReader(Class<?> sourceClass, Class<?> destinationClass) {
        this.sourceClass = sourceClass;
        this.destinationClass = destinationClass;
    }

    @Override
    @SneakyThrows
    public Object get(Object source, String memberName) {
        Mapping m = getMapping(source.getClass()).get(memberName);
        Object json = m.field().get(source);
        if (json == null) {
            return null;
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
        JsonNode jsonNode = node.at(m.source().pointer());

        return Util.unwrapJson(jsonNode);
    }


    @Override
    public Member<Object> getMember(Object source, String memberName) {
        if (this.sourceClass.isInstance(source)) {
            Mapping s = getMapping(source.getClass()).get(memberName);
            if (s != null) {
                return new JsonMember(this.sourceClass);
            }
        }
        return null;
    }

    @Override
    public Collection<String> memberNames(Object source) {
        return getMapping(source.getClass()).keySet();
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
                newMap.putAll(superMapping);
            }
            Arrays.stream(this.destinationClass.getDeclaredFields())
                .map(f ->
                    Util.getEntry(sourceClass, f).orElse(null)
                ).filter(Objects::nonNull)
                .forEach(e ->
                    newMap.put(e.getKey(), e.getValue())
                );
            return Collections.unmodifiableMap(newMap);
        });
    }

     class JsonMember extends  ValueReader.Member<Object>{

        public JsonMember(Class<?> valueType) {
            super(valueType);
        }

        @Override
        public Object get(Object source, String memberName) {
            return JsonValueReader.this.get(source, memberName);
        }
     }

}
