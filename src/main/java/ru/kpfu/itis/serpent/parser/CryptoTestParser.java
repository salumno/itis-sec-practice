package ru.kpfu.itis.serpent.parser;

import ru.kpfu.itis.serpent.parser.model.Test;
import ru.kpfu.itis.task2.MainC1;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;

public class CryptoTestParser {
    public List<Test> readTestDataFromFile(final String fileLocation) {
        final List<Test> tests = new ArrayList<>();
        try {
            final String filePath = MainC1.class.getClassLoader().getResource(fileLocation).getPath();
            final BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(filePath));

            while (bufferedReader.readLine() != null) {
                final String info = bufferedReader.readLine();
                final int setNumber = getSetNumber(info);
                final int vectorNumber = getVectorNumber(info);

                String key;
                String plain;
                String cipher;
                String decrypted = "";
                String encrypted = "";
                String hundredTimes = "";
                String thousandTimes = "";

                key = getRawValue(bufferedReader.readLine() + bufferedReader.readLine().trim());

                if (setNumber < 5) {
                    plain = getRawValue(bufferedReader.readLine());
                    cipher = getRawValue(bufferedReader.readLine());
                    decrypted = getRawValue(bufferedReader.readLine());
                    hundredTimes = getRawValue(bufferedReader.readLine());
                    thousandTimes = getRawValue(bufferedReader.readLine());
                } else {
                    cipher = getRawValue(bufferedReader.readLine());
                    plain = getRawValue(bufferedReader.readLine());
                    encrypted = getRawValue(bufferedReader.readLine());
                }
                tests.add(
                        Test.builder()
                                .setNumber(setNumber).vectorNumber(vectorNumber)
                                .key(key).plain(plain).cipher(cipher)
                                .decrypted(decrypted).encrypted(encrypted)
                                .hundredTimes(hundredTimes)
                                .thousandTimes(thousandTimes)
                                .build()
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tests;
    }

    private int getVectorNumber(final String info) {
        final String vectorNumber = info.trim().split("#")[1].trim().split(":")[0];
        return Integer.parseInt(vectorNumber);
    }

    private int getSetNumber(final String info) {
        final String setNumber = info.trim().split(",")[0].split(" ")[1];
        return Integer.parseInt(setNumber);
    }

    private String getRawValue(final String param) {
        return defaultIfEmpty(param, "=").trim().split("=")[1];
    }
}
