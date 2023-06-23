package io.github.artynova.exceptions;

/**
 * Exception that is thrown when an invalid promptable object is detected
 * during prompt creation.
 */
public class IllegalPromptableException extends RuntimeException {
    /**
     * Constructs an empty {@link IllegalPromptableException}.
     */
    public IllegalPromptableException() {
    }

    /**
     * Constructs a {@link IllegalPromptableException} with a message.
     *
     * @param message The message.
     */
    public IllegalPromptableException(final String message) {
        super(message);
    }

    /**
     * Constructs a {@link IllegalPromptableException} with a message
     * and a cause.
     *
     * @param message The message.
     * @param cause The cause.
     */
    public IllegalPromptableException(final String message,
        final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a {@link IllegalPromptableException} with a cause.
     *
     * @param cause The cause.
     */
    public IllegalPromptableException(final Throwable cause) {
        super(cause);
    }
}
