package io.github.artynova.annotations.source;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Container annotation for the repeatable {@link MakePromptable}.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.PACKAGE)
public @interface MakePromptables {
    /**
     * @return List of {@link MakePromptable} annotations.
     */
    MakePromptable[] value();
}
