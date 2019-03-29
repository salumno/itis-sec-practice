package ru.kpfu.itis.serpent.parser.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
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
