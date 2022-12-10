package com.naman14.timber.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.stream.Collectors;

public class FileUtils {
    public static String readAllText(File file) throws FileNotFoundException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        return reader.lines().collect(Collectors.joining(System.lineSeparator()));
    }
}
