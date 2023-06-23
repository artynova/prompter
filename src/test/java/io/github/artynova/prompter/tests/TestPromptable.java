package io.github.artynova.prompter.tests;

import io.github.artynova.Promptable;

import java.util.Objects;

/**
 * A simple {@link Promptable} used in tests.
 */
public final class TestPromptable implements Promptable {
    /**
     * Field for testing.
     */
    private String field;

    /**
     * Creates an empty {@link TestPromptable}.
     */
    public TestPromptable() {
    }

    /**
     * Creates a populated {@link TestPromptable}.
     *
     * @param field The test field.
     */
    public TestPromptable(final String field) {
        this.field = field;
    }

    /**
     * @return The test field.
     */
    public String getField() {
        return field;
    }

    /**
     * @param field The test field.
     */
    public void setField(final String field) {
        this.field = field;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TestPromptable that = (TestPromptable) o;
        return Objects.equals(getField(), that.getField());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getField());
    }
}
