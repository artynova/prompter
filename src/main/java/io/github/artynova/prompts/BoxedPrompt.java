package io.github.artynova.prompts;

import io.github.artynova.PromptManager;

import java.io.PrintStream;
import java.util.Map;
import java.util.Scanner;

/**
 * <p>
 * A generic {@link Prompt} implementation that defers primitive type prompts to
 * their object counterparts, non-nullable because primitive types cannot handle
 * nulls.
 * </p>
 * <p>
 * Example: a prompt for int that defers to {@link Integer}.
 * </p>
 *
 * @param <V> Primitive class that the prompt is for.
 */
public final class BoxedPrompt<V> extends Prompt<V> {
    /**
     * Map of primitive classes' objects to their wrapper classes' objects.
     */
    private static final Map<Class<?>, Class<?>> PRIMITIVES_TO_WRAPS =
        Map.of(int.class, Integer.class, byte.class, Byte.class, short.class,
            Short.class, long.class, Long.class, float.class, Float.class,
            double.class, Double.class, char.class, Character.class,
            boolean.class, Boolean.class);
    /**
     * The non-primitive prompt to which this object outsources the work.
     */
    private final Prompt<?> nonPrimitivePrompt;

    /**
     * Constructs a new {@link BoxedPrompt}.
     *
     * @param valueClass Class object of a primitive type the prompt is for.
     *                   Needed because {@link V} type parameter does not
     *                   provide enough information due to type erasure.
     */
    public BoxedPrompt(final Class<V> valueClass) {
        super(valueClass);
        nonPrimitivePrompt = PromptManager.getPromptFor(box(valueClass));
    }

    /**
     * Returns a class corresponding to a primitive value type (excluding void),
     * or class itself if no mapping is found.
     *
     * @param classToBox The primitive type's class object.
     * @return The resulting class object.
     */
    public static Class<?> box(final Class<?> classToBox) {
        Class<?> result = PRIMITIVES_TO_WRAPS.get(classToBox);
        return result == null ? classToBox : result;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected V tryGetAnswer(final Scanner scanner, final PrintStream out,
        final String message) {
        return (V) nonPrimitivePrompt.promptDefinite(scanner, out,
            message == null ? getDefaultMessage()
                : message); // a primitive cannot be null
    }
}
