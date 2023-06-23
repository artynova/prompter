package io.github.artynova.annotations.runtime;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Only effective on {@link io.github.artynova.Promptable Promptable}-marked
 * objects.
 * Only effective on "setXxx" methods for corresponding properties.
 * This annotation is used to specify a custom message for prompting the
 * property.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PromptMessage {
    /**
     * @return The message.
     */
    String value();
}
