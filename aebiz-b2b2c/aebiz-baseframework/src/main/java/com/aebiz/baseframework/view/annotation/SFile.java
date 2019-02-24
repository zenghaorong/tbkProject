package com.aebiz.baseframework.view.annotation;

import java.lang.annotation.*;

/**
 * Created by wizzer on 2017/3/31.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SFile {
    String value() default "";

}
