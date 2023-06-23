package io.github.artynova.prompts;

import io.github.artynova.exceptions.IllegalPromptableException;
import io.github.artynova.exceptions.PromptAnswerException;
import io.github.artynova.exceptions.PromptNotImplementedException;

import java.io.PrintStream;
import java.util.Scanner;

/**
 * Base class for all prompts.
 *
 * @param <V> Class instances of which the prompt knows how to acquire
 * from an input stream.
 */
public abstract class Prompt<V> {
    /**
     * The default message, based on the name of the Value class.
     */
    private final String defaultMessage;

    /**
     * Creates a new prompt for the class.
     *
     * @param valueClass Class object, which is required to read some
     * information unavailable via {@link V} due to type erasure.
     */
    protected Prompt(final Class<V> valueClass) {
        this.defaultMessage = "Input " + valueClass.getSimpleName() + ": ";
    }

    /**
     * @return The default message for the class, that is,
     * "Input <i>className</i>: ".
     */
    public String getDefaultMessage() {
        return defaultMessage;
    }

    /**
     * Safely acquires an instance of {@link V}.
     *
     * @param scanner {@link Scanner} that wraps the input stream.
     * It is advised to reuse one scanner across multiple prompts to
     * avoid extra instantiations.
     * @param out {@link PrintStream} where the method outputs prompts.
     * @param message The prompt string, informing the user of what to input.
     * @param answerProcessor Functional interface instance that can perform
     * extra operations on the acquired value before returning it.
     * It may alter the value, validate it by using exceptions, etc.
     * @return The acquired instance.
     */
    public V prompt(final Scanner scanner, final PrintStream out,
        final String message, final AnswerProcessor<V> answerProcessor) {
        while (true) {
            try {
                V answer = tryGetAnswer(scanner, out,
                    message == null ? defaultMessage : message);
                answer = answerProcessor.processAnswer(answer);
                return answer; // exit point, when answer is valid
            } catch (IllegalPromptableException
                | PromptNotImplementedException e) {
                throw e; // re-throw because non-recoverable from user input
            } catch (Exception e) {
                out.println(
                    "Please try again, answer is invalid: " + e.getMessage());
            }
        }
    }

    /**
     * Safely acquires an instance of {@link V}.
     * Works like {@link #prompt(Scanner, PrintStream, String, AnswerProcessor)}
     * but with a predefined "identity" answer processor (answer -> answer).
     *
     * @param scanner {@link Scanner} that wraps the input stream.
     * It is advised to reuse one scanner across multiple prompts to
     * avoid extra instantiations.
     * @param out {@link PrintStream} where the method outputs prompts.
     * @param message The prompt string, informing the user of what to input.
     * @return The acquired instance.
     */
    public V prompt(final Scanner scanner, final PrintStream out,
        final String message) {
        return prompt(scanner, out, message, answer -> answer);
    }

    /**
     * Safely acquires a non-null instance of {@link V}.
     * Works like {@link #prompt(Scanner, PrintStream, String, AnswerProcessor)}
     * but appends the non-null check before the custom processor
     * is called. This method makes the prompt impossible to skip.
     *
     * @param scanner {@link Scanner} that wraps the input stream.
     * It is advised to reuse one scanner across multiple prompts to
     * avoid extra instantiations.
     * @param out {@link PrintStream} where the method outputs prompts.
     * @param message The prompt string, informing the user of what to input.
     * @param answerProcessor Functional interface instance that can perform
     * extra operations on the acquired value before returning it.
     * It may alter the value, validate it by using exceptions, etc.
     * @return The acquired instance.
     */
    public V promptDefinite(final Scanner scanner, final PrintStream out,
        final String message, final AnswerProcessor<V> answerProcessor) {
        return prompt(scanner, out, message, answer -> {
            if (answer == null) {
                throw new PromptAnswerException("Answer should be definite");
            }
            return answerProcessor.processAnswer(answer);
        });
    }

    /**
     * Safely acquires a non-null instance of {@link V}.
     * Combination of {@link #prompt(Scanner, PrintStream, String)} and
     * {@link #promptDefinite(Scanner, PrintStream, String, AnswerProcessor)}.
     * That is, it makes the prompt impossible to skip and uses an
     * "identity" function as the {@link AnswerProcessor}.
     *
     * @param scanner {@link Scanner} that wraps the input stream.
     * It is advised to reuse one scanner across multiple prompts to
     * avoid extra instantiations.
     * @param out {@link PrintStream} where the method outputs prompts.
     * @param message The prompt string, informing the user of what to input.
     * @return The acquired instance.
     */
    public V promptDefinite(final Scanner scanner, final PrintStream out,
        final String message) {
        return promptDefinite(scanner, out, message, answer -> answer);
    }

    /**
     * Method that tries to acquire an instance of {@link V}.
     * In case the user's input does not produce a valid instance (or null),
     * the method should throw a runtime exception, which will then
     * be intercepted by the caller prompting method.
     *
     * @param scanner {@link Scanner} that wraps the input stream.
     * It is advised to reuse one scanner across multiple prompts to
     * avoid extra instantiations.
     * @param out {@link PrintStream} where the method outputs prompts.
     * @param message The prompt string, informing the user of what to input.
     * @return The acquired instance.
     */
    protected abstract V tryGetAnswer(Scanner scanner, PrintStream out,
        String message);
}
