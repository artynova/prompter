package io.github.artynova.prompts;

import io.github.artynova.PromptManager;

import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Generic {@link Prompt} implementation for an arbitrary array.
 *
 * @param <E> Class of array's elements.
 * This implementation offers to skip the prompt at the start.
 */
public final class ArrayPrompt<E> extends Prompt<E[]> {
    /**
     * Class object of the class of array's elements.
     */
    private final Class<E> elemClass;

    /**
     * Constructs a new {@link ArrayPrompt}.
     *
     * @param elemClass Class object of {@link E}.
     * Needed because {@link E} type parameter does not provide
     * enough information due to type erasure.
     */

    @SuppressWarnings("unchecked")
    public ArrayPrompt(final Class<E> elemClass) {
        super((Class<E[]>) elemClass.arrayType());
        this.elemClass = elemClass;
    }

    @SuppressWarnings("unchecked") // array is ensured to have element type Elem
    @Override
    protected E[] tryGetAnswer(final Scanner scanner, final PrintStream out,
        final String message) {
        Prompt<E> elemPrompt = PromptManager.getPromptFor(
            elemClass); // may throw PromptNotImplementedException for Elem
        out.println(message);
        if (Boolean.TRUE.equals(
            PromptManager.YES_NO_PROMPT.promptDefinite(scanner, out,
                "Try to skip aggregate? y/n: "))) {
            return null;
        }
        out.println("To stop input, press Enter with an empty element field.");
        List<E> list = tryGetList(elemPrompt, scanner, out);
        // required to avoid problems with type mismatches
        E[] arr = (E[]) Array.newInstance(elemClass, list.size());
        for (int i = 0; i < list.size(); i++) {
            arr[i] = list.get(i);
        }
        return arr;
    }

    private List<E> tryGetList(final Prompt<E> elemPrompt,
        final Scanner scanner, final PrintStream out) {
        List<E> list = new ArrayList<>();
        E lastElem = elemPrompt.prompt(scanner, out, "Element 1: ");
        int index = 1;
        while (lastElem != null) {
            list.add(lastElem);
            lastElem =
                elemPrompt.prompt(scanner, out, "Element " + (++index) + ": ");
        }
        return list;
    }
}
