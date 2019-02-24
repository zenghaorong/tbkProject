package com.aebiz.baseframework.view.annotation;

import java.lang.annotation.*;

/**
 * Created by wizzer on 2017/2/13.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SJson {
    String value() default "";

    boolean jsonp() default false;

    String jsonpParam() default "callback";
}
