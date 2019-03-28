package ru.kpfu.itis.serpent;

import ru.kpfu.itis.serpent.parser.CryptoTestParser;
import ru.kpfu.itis.serpent.parser.model.Test;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        final CryptoTestParser cryptoTestParser = new CryptoTestParser();
        final List<Test> tests = cryptoTestParser.readTestDataFromFile("tests/test-vectors-256.txt");
        final Serpent serpent = new Serpent();

        int success = 0;
        int failure = 0;

        for (Test test: tests) {
            //todo
        }

        System.out.println("Success: " + success);
        System.out.println("Failure: " + failure);
    }
}
