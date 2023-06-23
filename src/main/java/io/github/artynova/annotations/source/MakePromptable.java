package io.github.artynova.annotations.source;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Repeatable package-level annotation that is used to specify promptables with
 * a more concise syntax than fully defining the class.
 * </p>
 * <p>
 * This annotation requires a {@link #name()} for the future
 * {@link io.github.artynova.Promptable Promptable},
 * a list of {@link #properties()}, and, optionally, a {@link #baseClass()}
 * (defaults to {@link Object}.
 * </p>
 * <p>
 * {@link io.github.artynova.Promptable Promptables}, which are essentially
 * JavaBeans with extra restrictions,
 * tend to contain quite a lot of boilerplate, so this annotation is meant to
 * speed up development when no complicated functionality is required
 * from the {@link io.github.artynova.Promptable Promptable}.
 * </p>
 *
 * @see PromptProperty
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.PACKAGE)
@Repeatable(MakePromptables.class)
public @interface MakePromptable {
    /**
     * @return Name of the {@link io.github.artynova.Promptable Promptable}.
     */
    String name();

    /**
     * @return Parent class of the
     * {@link io.github.artynova.Promptable Promptable}.
     */
    Class<?> baseClass() default Object.class;

    /**
     * @return Array of properties of the
     * {@link io.github.artynova.Promptable Promptable}.
     */
    PromptProperty[] properties();
}
