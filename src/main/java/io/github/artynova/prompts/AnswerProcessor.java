package io.github.artynova.prompts;

import io.github.artynova.exceptions.PromptAnswerException;

@FunctionalInterface()
public interface AnswerProcessor<V> {
    /**
     * Method that performs some transformations on the answer,
     * and may have side effects.
     *
     * @param answer Input answer.
     * @return Transformed answer.
     * @throws PromptAnswerException If answer does not pass additional checks.
     */
    V processAnswer(V answer) throws PromptAnswerException;
}
