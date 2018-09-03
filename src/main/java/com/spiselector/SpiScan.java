package com.spiselector;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({SpiSelectorRegister.class})
public @interface SpiScan {

    String[] value() default {"com"};
}
