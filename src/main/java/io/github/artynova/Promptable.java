package io.github.artynova;

/**
 * <p>
 * Marker for classes that can be prompted, to allow
 * {@link PromptManager PromptManager} to use the generic object prompt.
 * </p>
 * <p>
 * A class marked with this interface is expected to structurally adhere to the
 * JavaBean convention
 * (serialization not required).
 * </p>
 * <ul>
 *     <li>
 *         Since an object's fields are prompted recursively, it can contain
 *         any of the types supported by the library
 *         (including other promptables).
 *     </li>
 *     <li>
 *         The default prompt message for each field makes a best-effort guess
 *         based on the field's name.
 *         To specify a custom message, use the
 *         {@link io.github.artynova.annotations.runtime.PromptMessage
 *         PromptMessage} annotation.
 *     </li>
 *     <li>
 *         If some fields of a {@link Promptable} should not be prompted from
 *         the user, use the
 *         {@link io.github.artynova.annotations.runtime.PromptIgnore
 *         PromptIgnore} annotation.
 *     </li>
 *     <li>
 *         {@link Promptable Promptables} inherit openly settable fields and
 *         meta-information about those fields' messages and ignore markers.
 *         To customize this behaviour, override inherited setter methods:
 *         when building a prompt, only the most specific setter method in the
 *         hierarchy is considered for the purposes of prompt configurations.
 *     </li>
 * </ul>
 *
 * @see io.github.artynova.annotations.source.MakePromptable MakePromptable
 */
public interface Promptable {
}
