package io.github.artynova.prompts;

import io.github.artynova.PromptManager;
import io.github.artynova.Promptable;
import io.github.artynova.annotations.runtime.PromptIgnore;
import io.github.artynova.annotations.runtime.PromptMessage;
import io.github.artynova.exceptions.IllegalPromptableException;
import io.github.artynova.exceptions.PromptAnswerException;
import io.github.artynova.utils.NameUtils;
import org.apache.commons.beanutils.PropertyUtils;

import java.beans.PropertyDescriptor;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * Generic {@link Prompt} implementation for an arbitrary {@link Promptable}.
 * This implementation offers to skip the prompt at the start.
 *
 * @param <P> JavaBean-conforming object that implements
 * {@link Promptable}.
 */
public final class PromptablePrompt<P extends Promptable>
    extends Prompt<P> {
    /**
     * Class object for {@link P}.
     */
    private final Class<P> beanClass;
    /**
     * List of initialized information containers about bean's properties.
     */
    private final List<Property<?>> properties;

    /**
     * Constructs a new {@link PromptablePrompt}.
     *
     * @param beanClass {@link Promptable} bean class that the prompt is for.
     * Needed because {@link P} type parameter does not
     * provide enough information due to type erasure.
     */
    public PromptablePrompt(final Class<P> beanClass) {
        super(beanClass);
        this.beanClass = beanClass;
        this.properties = new LinkedList<>();
        initProperties();
    }

    private void initProperties() {
        PropertyDescriptor[] descriptors =
            PropertyUtils.getPropertyDescriptors(beanClass);
        for (PropertyDescriptor descriptor : descriptors) {
            Method writeMethod = descriptor.getWriteMethod();
            if (writeMethod == null
                || writeMethod.isAnnotationPresent(PromptIgnore.class)) {
                continue;
            }
            properties.add(
                new Property<>(descriptor, getPropertyMessage(writeMethod),
                    descriptor.getPropertyType()));
        }
    }

    private String getPropertyMessage(final Method writeMethod) {
        PromptMessage messageAnnotation =
            writeMethod.getAnnotation(PromptMessage.class);
        return messageAnnotation == null ? null
            : writeMethod.getAnnotation(PromptMessage.class).value();
    }

    @Override
    protected P tryGetAnswer(final Scanner scanner,
        final PrintStream out, final String message) {
        try {
            P bean = beanClass.getConstructor().newInstance();
            out.println(message);
            boolean skip =
                PromptManager.YES_NO_PROMPT.promptDefinite(scanner, out,
                    "Try to skip aggregate? y/n: ");
            if (skip) {
                return null;
            }
            for (Property<?> property : properties) {
                property.promptInto(bean, scanner, out);
            }
            return bean;
        } catch (IllegalAccessException e) {
            throw new IllegalPromptableException(
                "Cannot access default constructor", e);
        } catch (NoSuchMethodException e) {
            throw new IllegalPromptableException(
                "No default constructor provided", e);
        } catch (InvocationTargetException e) {
            throw new IllegalPromptableException(
                "Default constructor threw an exception", e);
        } catch (InstantiationException e) {
            throw new IllegalPromptableException(
                "Abstract class provided as bean", e);
        }
    }

    /**
     * Data aggregate object containing initialized information about
     * fields of a Promptable.
     *
     * @param <V>> field type.
     */
    private final class Property<V> {
        /**
         * Descriptor from BeanUtils.
         */
        private final PropertyDescriptor descriptor;
        /**
         * Prompt message.
         */
        private final String message;
        /**
         * Class object describing {@link V}.
         */
        private final Class<V> valueClass;

        private Property(final PropertyDescriptor descriptor,
            final String message, final Class<V> valueClass) {
            this.descriptor = descriptor;
            this.valueClass = valueClass;
            this.message = message == null ? getDefaultMessage() : message;
        }

        private String getDefaultMessage() {
            return "Input " + NameUtils.humanReadableName(descriptor.getName())
                + ": ";
        }

        private void promptInto(final P bean,
            final Scanner scanner, final PrintStream out) {
            Prompt<V> prompt = PromptManager.getPromptFor(valueClass);
            prompt.prompt(scanner, out, message, answer -> {
                try {
                    descriptor.getWriteMethod().invoke(bean, answer);
                } catch (IllegalAccessException e) {
                    throw new IllegalPromptableException(
                        "Cannot access property setter",
                        e); // should not happen under proper operation
                } catch (InvocationTargetException e) {
                    throw new PromptAnswerException(e.getCause().getMessage(),
                        e.getCause()); // actual exception is in the cause
                }
                return answer;
            });
        }
    }
}
