package nl.beeldengeluid.mapping.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Source {
    
    String pointer() default "";
    
    String field() default "";

    String defaultValue() default "";
}
