package com.spiselector;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SpiInterface {

    String[] code() default {"defautSpi"};
}
