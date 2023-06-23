package io.github.artynova.annotations.source;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <p>
 * Annotation that is used to specify parameters needed to generate a property
 * of a {@link io.github.artynova.Promptable Promptable} (the same as the
 * property of a JavaBean). That is, a private field,
 * a "get<i>PrivateFieldName</i>" method, and a "set<i>PrivateFieldName</i>"
 * method. The setter is annotated with the specified message, which can be
 * "null" to request the default message.
 * </p>
 * <p>
 * Does nothing on its own, used with {@link MakePromptable}.
 * </p>
 */
@Retention(RetentionPolicy.SOURCE)
public @interface PromptProperty {
    /**
     * @return Class of this property.
     */
    Class<?> fieldClass();

    /**
     * @return Whether this property must be non-null.
     */
    boolean required() default false; // required property cannot be null

    /**
     * @return Property name (i.e. the name of the private field).
     */
    String name();

    /**
     * @return Message to use when prompting, or null when a default message
     * should be used instead.
     */
    String message();
}
