package com.companyz.ems.utils;

/**
 * Utility class for SQL-related operations.
 * <p>
 * Provides helper methods for safely handling SQL strings and patterns.
 * </p>
 */
public class SqlUtils {
    /**
     * Escapes special characters in a LIKE pattern to treat them as literals.
     * <p>
     * Replaces SQL wildcard characters (% and _) with their escaped equivalents
     * so they are treated as literal characters in LIKE queries.
     * </p>
     *
     * @param input the input string to escape
     * @return the escaped string safe for use in LIKE clauses
     */
    public static String escapeLikePattern(String input) {
        return input.replace("%", "\\%").replace("_", "\\_");
    }
}
