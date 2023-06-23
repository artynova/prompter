package io.github.artynova.prompts;

import io.github.artynova.exceptions.PromptAnswerException;

/**
 * Interface with a method that parses a string into some value.
 *
 * @param <V> The output value.
 * @see OneLinePrompt
 */
@FunctionalInterface
public interface StringAnswerParser<V> {
    /**
     * Parser method.
     *
     * @param answer String answer to be parsed.
     * @return Parsed answer.
     * @throws PromptAnswerException When an answer is not acceptable.
     */
    V parse(String answer) throws PromptAnswerException;
}
