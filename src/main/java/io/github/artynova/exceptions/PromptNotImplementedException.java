package io.github.artynova.exceptions;

/**
 * Exception that is thrown when a requested prompt for a class does not exist
 * and cannot be created.
 */
public class PromptNotImplementedException extends RuntimeException {
    /**
     * Constructs an empty {@link PromptNotImplementedException}.
     */
    public PromptNotImplementedException() {
    }

    /**
     * Constructs a {@link PromptNotImplementedException} to notify that
     * a prompt for a given class does not exist.
     *
     * @param valueClass Class for which the prompt was not found.
     */
    public PromptNotImplementedException(final Class<?> valueClass) {
        super("Cannot find prompt for " + valueClass);
    }
}
