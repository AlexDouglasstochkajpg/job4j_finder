package ru.job4j;

import java.util.HashMap;
import java.util.Map;

public class ArgsName {

    private final Map<String, String> values = new HashMap<>();

    public String get(String key) {
        checkForKey(key);
        return values.get(key);
    }

    private void parse(String[] args) {
        for (String oneParameter : args) {
            String[] oneKeyPlusValue = oneParameter.split("=", 2);
            values.put(oneKeyPlusValue[0].trim().substring(1), oneKeyPlusValue[1].trim());
        }
    }

    public static ArgsName of(String[] args) {
        checkBeforePars(args);
        ArgsName names = new ArgsName();
        names.parse(args);
        return names;
    }

    private void checkForKey(String key) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }
        if (!key.matches("^[a-z]+$")) {
            throw new IllegalArgumentException("The key can only contain small letters");
        }
        if (!values.containsKey(key)) {
            throw new IllegalArgumentException(String.format("This key: '%s' is missing", key));
        }
    }

    private static void checkBeforePars(String[] args) {
        if (args.length < 4) {
            throw new IllegalArgumentException("Incorrect parameters. "
                    + "The program must be launched with 4 parameters,\n"
                    + " for example: -d=c: -n=*.?xt -t=mask -o=log.txt\n"
                    + "Keys\n"
                    + "-d - directory in which to start searching.\n"
                    + "-n - file name, mask, or any regular expression.\n"
                    + "-t - search type: mask -- search by mask, name -- by location of name match, regex -- by regular expression.\n"
                    + "-o - write the result to a file.\n");
        }
        for (String oneParameter : args) {
            if (oneParameter.matches("^-=.+")) {
                throw new IllegalArgumentException(
                        String.format("Error: This argument '%s' does not contain a key", oneParameter));
            }
            if (oneParameter.matches("^-[A-Za-z0-9\\.]+=$")) {
                throw new IllegalArgumentException(
                        String.format("Error: This argument '%s' does not contain a value", oneParameter));
            }
            if (!oneParameter.contains("=")) {
                throw new IllegalArgumentException(
                        String.format("Error: This argument '%s' does not contain an equal sign", oneParameter));
            }
            if (!oneParameter.startsWith("-")) {
                throw new IllegalArgumentException(
                        String.format("Error: This argument '%s' does not start with a '-' character", oneParameter));
            }
        }
    }
}
