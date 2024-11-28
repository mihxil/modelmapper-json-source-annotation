package nl.beeldengeluid.mapping.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)

@Repeatable(Sources.class)

public @interface Source {
    
    String pointer() default "";
    
    String field() default "";

    String defaultValue() default "";

    Class<?> sourceClass() default Object.class;
}
