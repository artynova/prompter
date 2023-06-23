package io.github.artynova.annotations.runtime;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Only effective on {@link io.github.artynova.Promptable Promptable}-marked
 * objects. Only effective on "setXxx" methods for corresponding properties.
 * If this annotation is present, the setter (and corresponding bean property)
 * will not be prompted by the automatic prompt for the consumer class.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PromptIgnore {
}
