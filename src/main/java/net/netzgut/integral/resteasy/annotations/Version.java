package net.netzgut.integral.resteasy.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
@Documented
public @interface Version {

    String value();

    String deprecated() default "";
}
