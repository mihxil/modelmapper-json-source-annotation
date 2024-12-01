/*
 * Copyright (C) 2024 Licensed under the Apache License, Version 2.0
 */
package nl.beeldengeluid.mapping.impl;

import java.lang.reflect.Field;
import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import org.meeuw.mapping.Mapper;
import org.meeuw.mapping.impl.Util;
import org.modelmapper.spi.ValueReader;

@Slf4j
public class JsonValueReader implements ValueReader<Object> {

    private final Class<?> sourceClass;
    private final Class<?> destinationClass;



    public JsonValueReader(Class<?> sourceClass, Class<?> destinationClass) {
        this.sourceClass = sourceClass;
        this.destinationClass = destinationClass;
    }

    @Override
    public Object get(Object source, String memberName) {
        Field destField =  Util.getMappedDestinationProperties(sourceClass, destinationClass).get(memberName);
        return Mapper.sourceGetter(destField, sourceClass).apply(source).orElse(null);


    }


    @Override
    public Member<Object> getMember(Object source, String memberName) {
        if (this.sourceClass.isInstance(source)) {
            if (memberNames(source).contains(memberName)) {
                return new JsonMember(this.sourceClass);
            }
        }
        return null;
    }

    @Override
    public Collection<String> memberNames(Object source) {
        return Util.getMappedDestinationProperties(sourceClass, destinationClass).keySet();
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
