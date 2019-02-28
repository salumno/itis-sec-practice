package ru.kpfu.itis.task2;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

public class MainC1 {

    public static void main(String[] args) {
        final char[] encryptedData = readEncryptedDataFromFile();
        final Map<String, Integer> letters = getLettersFrequency(encryptedData);
        System.out.println(letters);
        // TODO
    }

    private static Map<String, Integer> getLettersFrequency(final char[] encryptedData) {
        final Map<String, Integer> lettersFrequency = new HashMap<>();
        for (Character currentCharacter : encryptedData) {
            final String currentLetter = currentCharacter.toString();
            if (lettersFrequency.containsKey(currentLetter)) {
                lettersFrequency.put(currentLetter, lettersFrequency.get(currentLetter) + 1);
            } else {
                lettersFrequency.put(currentLetter, 1);
            }
        }
        return lettersFrequency;
    }

    private static char[] readEncryptedDataFromFile() {
        char[] encryptedData = new char[0];
        try {
            final String filePath = MainC1.class.getClassLoader().getResource("task2/c1.txt").getPath();
            final BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(filePath));
            encryptedData = defaultIfNull(bufferedReader.readLine(), "").toCharArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return encryptedData;
    }
}
