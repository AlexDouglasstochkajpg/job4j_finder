package ru.job4j;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class FilesFinder {

    public static List<Path> search(Path root, Predicate<Path> condition) throws IOException {
        SearchFiles searcher = new SearchFiles(condition);
        Files.walkFileTree(root, searcher);
        return searcher.getPaths();
    }

    public static String convertMaskToRegex(String string) {
        string = string.replace(".", "\\.")
                .replace('?', '.')
                .replace("*", ".*");
        return string;
    }

    public static void validationParameters(ArgsName argsForProgram) {
        File file = new File(argsForProgram.get("d"));
        String needFindFile = argsForProgram.get("n");
        String howToSearch = argsForProgram.get("t");
        String fileToWrite = argsForProgram.get("o");
        if (!file.exists()) {
            throw new IllegalArgumentException(String.format("Not exist %s", file.getAbsoluteFile()));
        }
        if (!file.isDirectory()) {
            throw new IllegalArgumentException(String.format("Not directory %s", file.getAbsoluteFile()));
        }
        if (!"regex".equals(howToSearch) && !"mask".equals(howToSearch) && !needFindFile.matches("[A-Za-z0-9]*\\.[a-z0-9]{2,}$")) {
            throw new IllegalArgumentException(String.format("Incorrect search file format: %s", needFindFile));
        }
        if ("mask".equals(howToSearch) && !needFindFile.matches("[A-Za-z0-9?*]*\\.[a-z0-9?*]{2,}$")) {
            throw new IllegalArgumentException(String.format("Incorrect search file format: %s", needFindFile));
        }
        if (!"mask".equals(howToSearch) && !"name".equals(howToSearch) && !"regex".equals(howToSearch)) {
            throw new IllegalArgumentException(String.format("Incorrect third parameter: %s. "
                    + "This must be: \"mask\", \"name\" or \"regex\".", howToSearch));
        }
        if (!fileToWrite.matches("^.*\\.[a-z0-9]{2,5}$")) {
            throw new IllegalArgumentException(String.format("Incorrect file format: %s", fileToWrite));
        }
    }

    public static void writeResultsToFile(String path, List<Path> pathList) throws IOException {
        String newPath = "data\\" + path;
        try (PrintWriter writer = new PrintWriter(new FileWriter(newPath, false))) {
            for (Path onePath : pathList) {
                writer.println(onePath.toString());
                System.out.println(onePath);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        ArgsName argsForProgram = ArgsName.of(args);
        validationParameters(argsForProgram);
        List<Path> results;
        switch (argsForProgram.get("t")) {
            case "regex":
                results = search(Paths.get(argsForProgram.get("d")), path -> path.toFile()
                        .getName()
                        .matches(argsForProgram.get("n")));
                break;
            case "mask":
                String regex = convertMaskToRegex(argsForProgram.get("n"));
                results = search(Paths.get(argsForProgram.get("d")), path -> path.toFile().
                        getName()
                        .matches(regex));
                break;
            case "name":
                results = search(Paths.get(argsForProgram.get("d")), path -> path.toFile()
                        .getName()
                        .endsWith(argsForProgram.get("n")));
                break;
            default:
                results = new ArrayList<>();
        }
        if (results.size() > 0) {
            writeResultsToFile(argsForProgram.get("o"), results);
        }
        System.out.println(results.size());
    }
}
