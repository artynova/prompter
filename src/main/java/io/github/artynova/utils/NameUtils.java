package io.github.artynova.utils;

/**
 * Contains helper methods for working with program elements' names.
 */
public final class NameUtils {
    private NameUtils() {
    }

    /**
     * @param camelCase Camel-case string.
     * @return Naturally-spaced name.
     */
    public static String humanReadableName(final String camelCase) {

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < camelCase.length(); i++) {
            char c = camelCase.charAt(i);
            if (Character.isUpperCase(c) && i > 0) {
                builder.append(" ");
            }
            builder.append(Character.toLowerCase(c));
        }
        return builder.toString();
    }

    /**
     * @param string String.
     * @return The same string, but with first letter capitalized.
     */
    public static String capitalizeFirstLetter(final String string) {
        if ("".equals(string)) {
            return string;
        }
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }
}
