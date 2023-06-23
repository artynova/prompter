package io.github.artynova;

import io.github.artynova.exceptions.PromptAnswerException;
import io.github.artynova.exceptions.PromptNotImplementedException;
import io.github.artynova.prompts.ArrayPrompt;
import io.github.artynova.prompts.BoxedPrompt;
import io.github.artynova.prompts.OneLinePrompt;
import io.github.artynova.prompts.Prompt;
import io.github.artynova.prompts.PromptablePrompt;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles mappings between classes and prompts for their instances.
 */
public final class PromptManager {

    /**
     * Basic yes/no prompt for a boolean value.
     */
    public static final Prompt<Boolean> YES_NO_PROMPT =
        new OneLinePrompt<>(Boolean.class, answer -> {
            answer = answer.toLowerCase();
            if (!answer.equals("y") && !answer.equals("n")) {
                throw new PromptAnswerException(
                    "Answer to a y/n prompt is not \"y\" or \"n\"");
            }
            return answer.charAt(0) == 'y';
        });
    /**
     * Map of {@link Class} objects to their registered prompts.
     */
    private static final Map<Class<?>, Prompt<?>> CLASS_PROMPTS =
        new HashMap<>();

    static {
        registerOneLiners();
    }

    private PromptManager() {
    }

    /**
     * Registers a {@link Prompt} for the parameter class.
     * This means that a call to {@link #getBeanPrompt(Class) getBeanPrompt}
     * with {@link }
     *
     * @param valueClass Class object for which to register the prompt.
     * After successful registration, a call to
     * {@link #getPromptFor(Class) getPromptFor} with this class will return
     * the registered prompt object.
     * @param prompt Prompt to register.
     * @param <V> Class that the class object describes.
     */
    public static <V> void registerPrompt(final Class<V> valueClass,
        final Prompt<V> prompt) {
        CLASS_PROMPTS.put(valueClass, prompt);
    }

    /**
     * Acquires a registered {@link Prompt} for the class if available.
     * If the prompt is not available but can be instantiated dynamically,
     * the method instantiates it and registers for future reuse.
     *
     * @param valueClass Class object for which to get prompt.
     * @param <V> The class that the class object describes.
     * @return The {@link Prompt} that allows to query an object from an
     * input stream.
     */
    @SuppressWarnings("unchecked")
    public static <V> Prompt<V> getPromptFor(
        final Class<V> valueClass) {
        Prompt<V> prompt = (Prompt<V>) CLASS_PROMPTS.get(
            valueClass); // access control ensures this has a correct result
        if (prompt == null) {
            prompt = tryGenericPrompts(valueClass);
            if (prompt == null) {
                throw new PromptNotImplementedException(valueClass);
            } else {
                registerPrompt(valueClass,
                    prompt); // lazy-register prompts
            }
        }
        return prompt;
    }

    @SuppressWarnings("unchecked")
    private static <V> Prompt<V> tryGenericPrompts(
        final Class<V> valueClass) {
        if (valueClass.isPrimitive()) {
            return new BoxedPrompt<>(valueClass);
        }
        if (valueClass.isArray()) {
            return (Prompt<V>) getArrayPrompt(
                valueClass.getComponentType());
        }
        if (Promptable.class.isAssignableFrom(valueClass)) {
            return (Prompt<V>) getBeanPrompt(
                (Class<? extends Promptable>) valueClass);
        }
        return null;
    }

    private static <E> Prompt<E[]> getArrayPrompt(
        final Class<E> elemClass) {
        return new ArrayPrompt<>(elemClass);
    }

    private static <P extends Promptable> Prompt<P> getBeanPrompt(
        final Class<P> beanClass) {
        return new PromptablePrompt<>(beanClass);
    }

    private static void registerOneLiners() {
        registerPrompt(String.class, new OneLinePrompt<>(String.class,
            answer -> answer)); // string parses to itself
        registerPrompt(Byte.class,
            new OneLinePrompt<>(Byte.class, Byte::parseByte));
        registerPrompt(Boolean.class,
            new OneLinePrompt<>(Boolean.class, bool -> {
                if (!"false".equalsIgnoreCase(bool)
                    && !"true".equalsIgnoreCase(bool)) {
                    throw new PromptAnswerException("Illegal boolean: " + bool);
                }
                return Boolean.parseBoolean(bool);
            }));
        registerPrompt(Character.class,
            new OneLinePrompt<>(Character.class, string -> {
                if (string.length() > 1) {
                    throw new IllegalArgumentException(
                        "Multiple characters provided where one is expected");
                }
                return string.charAt(
                    0); // char 0 is guaranteed to exist, see OneLinePrompt
            }));
        registerPrompt(Short.class,
            new OneLinePrompt<>(Short.class, Short::parseShort));
        registerPrompt(Integer.class,
            new OneLinePrompt<>(Integer.class, Integer::parseInt));
        registerPrompt(Long.class,
            new OneLinePrompt<>(Long.class, Long::parseLong));
        registerPrompt(Float.class,
            new OneLinePrompt<>(Float.class, Float::parseFloat));
        registerPrompt(Double.class,
            new OneLinePrompt<>(Double.class, Double::parseDouble));
    }
}
