package io.github.artynova.exceptions;

/**
 * Exception that signifies an explicit problem with a prompt answer that is
 * not covered by normal exceptions.
 */
public class PromptAnswerException extends RuntimeException {
    /**
     * Constructs an empty {@link PromptAnswerException}.
     */
    public PromptAnswerException() {
    }

    /**
     * Constructs a {@link PromptAnswerException} with a message.
     *
     * @param message The message.
     */
    public PromptAnswerException(final String message) {
        super(message);
    }

    /**
     * Constructs a {@link PromptAnswerException} with a message
     * and a cause.
     *
     * @param message The message.
     * @param cause The cause.
     */
    public PromptAnswerException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a {@link PromptAnswerException} with a cause.
     *
     * @param cause The cause.
     */
    public PromptAnswerException(final Throwable cause) {
        super(cause);
    }
}
