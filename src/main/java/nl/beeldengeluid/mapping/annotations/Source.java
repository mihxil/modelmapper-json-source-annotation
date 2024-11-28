/*
 * Copyright (C) 2024 Licensed under the Apache License, Version 2.0
 */
package nl.beeldengeluid.mapping.annotations;

import java.lang.annotation.*;
/**
 * This annotation can be put on a field of some destination object, to indicate
 * from which other object json field it must come.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Repeatable(Sources.class)
public @interface Source {

    /**
     * Json pointer inside the other field
     */
    String pointer() default "";

    /**
     * Name of the other field
     */
    String field() default "";

    /**
     * The source class in with the other field can be found
     */
    Class<?> sourceClass() default Object.class;
}
