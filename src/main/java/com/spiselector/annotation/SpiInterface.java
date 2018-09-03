package com.spiselector.annotation;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SpiInterface {

    String[] code() default {"spiNull"};
}
