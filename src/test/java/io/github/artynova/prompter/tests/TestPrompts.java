package io.github.artynova.prompter.tests;

import io.github.artynova.PromptManager;
import io.github.artynova.prompts.Prompt;
import org.junit.jupiter.api.Test;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

final class TestPrompts {
    /**
     * Test a prompt for a given class using a string as overall input.
     *
     * @param valueClass Class that the required prompt prompts.
     * @param input String that contains the mocked user input.
     * @param expected The expected result of the prompt with given input.
     * @param <V> Type of expected result.
     */
    private static <V> void testPrompt(final Class<V> valueClass,
        final String input, final V expected) {
        assertEquals(expected, getPromptResult(valueClass, input));
    }

    private static <V> V getPromptResult(final Class<V> valueClass,
        final String input) {
        Scanner scanner = new Scanner(input);
        return assertDoesNotThrow(() -> {
            try (OutputStream silentOutput = OutputStream.nullOutputStream();
                 PrintStream silentPrintOutput = new PrintStream(
                     silentOutput)) {
                Prompt<V> prompt = PromptManager.getPromptFor(valueClass);
                return prompt.prompt(scanner, silentPrintOutput,
                    "This text is not important: ");
            }
        }); // if it does throw an uncaught exception, something is wrong
    }

    @Test
    void testByte() {
        testPrompt(Byte.class, """
            a
            banana
            \r
            """, null);
        testPrompt(Byte.class, """
            a
            banana
            42
            """, (byte) 42);
    }

    @Test
    void testBoolean() {
        System.out.println("a" +
        "b"); // this is a blatant checkstyle violation to showcase quality control; oh, and this comment is a violation too
        testPrompt(Boolean.class, """
            a
            banana
            \r
            """, null);
        testPrompt(Boolean.class, """
            a
            banana
            true
            """, true);
        testPrompt(Boolean.class, """
            a
            banana
            false
            """, false);
        testPrompt(Boolean.class, """
            a
            banana
            1
            """, true);
        testPrompt(Boolean.class, """
            a
            banana
            0
            """, false);
    }

    @Test
    void testCharacter() {
        testPrompt(Character.class, """
            aa
            banana
            \r
            """, null);
        testPrompt(Character.class, """
            aa
            banana
            F
            """, 'F');
    }

    @Test
    void testShort() {
        testPrompt(Short.class, """
            a
            banana
            \r
            """, null);
        testPrompt(Short.class, """
            a
            banana
            42
            """, (short) 42);
    }

    @Test
    void testInteger() {
        testPrompt(Integer.class, """
            a
            banana
            \r
            """, null);
        testPrompt(Integer.class, """
            a
            banana
            42
            """, 42);
    }

    @Test
    void testLong() {
        testPrompt(Long.class, """
            a
            banana
            \r
            """, null);
        testPrompt(Long.class, """
            a
            banana
            42
            """, 42L);
    }

    @Test
    void testFloat() {
        testPrompt(Float.class, """
            a
            banana
            \r
            """, null);
        testPrompt(Float.class, """
            a
            banana
            42.42f
            """, 42.42f);
    }

    @Test
    void testDouble() {
        testPrompt(Double.class, """
            a
            banana
            \r
            """, null);
        testPrompt(Double.class, """
            a
            banana
            42.42
            """, 42.42);
    }

    // Since basic prompt behaviour is tested in other tests,
    // this one only tests aggregate input in arrays on example of Integers.
    @Test
    void testArray() {
        testPrompt(Integer[].class, """
            y
            """, null);
        assertArrayEquals(new Integer[0],
            getPromptResult(Integer[].class, """
                n
                \r
                """));
        assertArrayEquals(new Integer[] {1, 15, -5},
            getPromptResult(Integer[].class, """
                n
                1
                15
                -5
                \r
                """));
    }

    @Test
    void testPromptable() {
        testPrompt(TestPromptable.class, """
            y
            """, null); // skip aggregate
        testPrompt(TestPromptable.class, """
            n
            forty two
            """, new TestPromptable("forty two"));
    }
}
