package ru.kpfu.itis.task1;

import com.google.common.hash.Hashing;
import org.apache.commons.lang3.RandomStringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class MainCollision {
  public static void main(String[] args) {
    final int length = 10;
    final boolean useLetters = true;
    final boolean useNumbers = true;

    final int countOfBytes = 4;

    boolean isCollision = false;

    while (!isCollision) {
      final String firstString = RandomStringUtils.random(length, useLetters, useNumbers);
      final String secondString = RandomStringUtils.random(length, useLetters, useNumbers);
      if (!firstString.equals(secondString)) {

        final byte[] firstHash =
            Arrays.copyOf(
                Hashing.sha256().hashString(firstString, StandardCharsets.UTF_8).asBytes(),
                countOfBytes);
        final byte[] secondHash =
            Arrays.copyOf(
                Hashing.sha256().hashString(secondString, StandardCharsets.UTF_8).asBytes(),
                countOfBytes);

        isCollision = Arrays.equals(firstHash, secondHash);
        if (isCollision) {
          System.out.println(Arrays.toString(firstHash));
          System.out.println(Arrays.toString(secondHash));
          System.out.println(firstString);
          System.out.println(secondString);
        }
      }
    }
  }
}
