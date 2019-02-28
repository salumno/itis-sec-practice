package ru.kpfu.itis.task1;

import com.google.common.hash.Hashing;
import org.apache.commons.lang3.RandomStringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class MainSecondPrototype {
  public static void main(String[] args) {
    final int length = 50;
    final boolean useLetters = true;
    final boolean useNumbers = true;

    final int countOfBytes = 4;

    boolean isCollision = false;

    final String baseString = RandomStringUtils.random(length, useLetters, useNumbers);
    final byte[] baseHash =
        Arrays.copyOf(
            Hashing.sha256().hashString(baseString, StandardCharsets.UTF_8).asBytes(),
            countOfBytes);

    while (!isCollision) {
      final String currentString = RandomStringUtils.random(length, useLetters, useNumbers);
      if (!currentString.equals(baseString)) {
        final byte[] currentHash =
            Arrays.copyOf(
                Hashing.sha256().hashString(currentString, StandardCharsets.UTF_8).asBytes(),
                countOfBytes);
        isCollision = Arrays.equals(baseHash, currentHash);
        if (isCollision) {
          System.out.println("Collision");
          System.out.println(Arrays.toString(currentHash));
          System.out.println(currentString);
        }
      }
    }
  }
}
