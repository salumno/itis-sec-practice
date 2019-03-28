package ru.kpfu.itis.serpent.parser.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Test {
    private int setNumber;
    private int vectorNumber;
    private String key;
    private String plain;
    private String decrypted;
    private String encrypted;
    private String cipher;
    private String hundredTimes;
    private String thousandTimes;
}
