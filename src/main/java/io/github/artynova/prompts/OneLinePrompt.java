package io.github.artynova.prompts;

import java.io.PrintStream;
import java.util.Scanner;

/**
 * Generic {@link Prompt} implementation that accepts one line of input on the
 * same line as the prompt, and transforms the acquired string into
 * an instance of {@link V}.
 * It also automatically parses empty lines as null.
 *
 * @param <V> Class of objects acquired through this prompt.
 */
public final class OneLinePrompt<V> extends Prompt<V> {
    /**
     * Functional for parsing the string answer.
     */
    private final StringAnswerParser<V> parser;

    /**
     * Constructs a new {@link OneLinePrompt}.
     *
     * @param valueClass Class that the prompt is for.
     * Needed because {@link V} type parameter does not provide
     * enough information due to type erasure.
     * @param parser Function interface implementation that performs the
     * parsing from string to {@link V}.
     */
    public OneLinePrompt(final Class<V> valueClass,
        final StringAnswerParser<V> parser) {
        super(valueClass);
        this.parser = parser;
    }

    @Override
    protected V tryGetAnswer(final Scanner scanner, final PrintStream out,
        final String message) {
        out.print(message);
        String answer = scanner.nextLine();
        if (answer.length() == 0) {
            return null;
        }
        return parser.parse(answer);
    }
}
