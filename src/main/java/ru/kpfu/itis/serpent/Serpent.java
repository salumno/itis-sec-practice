package ru.kpfu.itis.serpent;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class Serpent {

    private final static byte[][] S_TABLE = new byte[][]{
            { 3, 8, 15, 1, 10, 6, 5, 11, 14, 13, 4, 2, 7, 0, 9, 12 },
            { 15, 12, 2, 7, 9, 0, 5, 10, 1, 11, 14, 8, 6, 13, 3, 4 },
            { 8, 6, 7, 9, 3, 12, 10, 15, 13, 1, 14, 4, 0, 11, 5, 2 },
            { 0, 15, 11, 8, 12, 9, 6, 3, 13, 1, 2, 4, 10, 7, 5, 14 },
            { 1, 15, 8, 3, 12, 0, 11, 6, 2, 5, 4, 10, 9, 14, 7, 13 },
            { 15, 5, 2, 11, 4, 10, 9, 12, 0, 3, 14, 8, 13, 6, 7, 1 },
            { 7, 2, 12, 5, 8, 4, 6, 11, 14, 9, 1, 15, 13, 3, 10, 0 },
            { 1, 13, 15, 0, 14, 8, 2, 11, 7, 4, 12, 10, 9, 3, 5, 6 }
    };

    private final static byte[][] INVERTED_S_TABLE = new byte[][]{
            { 13, 3, 11, 0, 10, 6, 5, 12, 1, 14, 4, 7, 15, 9, 8, 2 },
            { 5, 8, 2, 14, 15, 6, 12, 3, 11, 4, 7, 9, 1, 13, 10, 0 },
            { 12, 9, 15, 4, 11, 14, 1, 2, 0, 3, 6, 13, 5, 8, 10, 7 },
            { 0, 9, 10, 7, 11, 14, 6, 13, 3, 5, 12, 2, 4, 8, 15, 1 },
            { 5, 0, 8, 3, 10, 9, 7, 14, 2, 12, 11, 6, 4, 15, 13, 1 },
            { 8, 15, 2, 9, 4, 1, 13, 14, 11, 6, 5, 3, 7, 12, 10, 0 },
            { 15, 10, 1, 13, 5, 3, 6, 0, 4, 9, 14, 7, 2, 12, 8, 11 },
            { 3, 0, 6, 13, 9, 14, 15, 8, 5, 12, 11, 7, 10, 1, 4, 2 }
    };


    private final static int G = 0x9e3779b9;

    public String encrypt(final String plain, final String key) {
        byte[] plainText = getBytesFromHexString(plain);
        byte[] secret = getBytesFromHexString(key);

        if (plainText != null && secret != null) {
            final List<byte[]> keys = generateKeys(secret);

            byte[] c = ip(plainText);

            for (int i = 0; i < 31; i++) {
                final int power = i % 8;
                final byte[] currentKey = keys.get(i);
                final byte[] xorResult = xor(c, currentKey);
                c = l(
                        s(xorResult, power)
                );
            }

            final byte[] intermediateSResult = s(
                    xor(c, keys.get(31)),
                    7
            );
            c = xor(intermediateSResult, keys.get(32));

            c = fp(c);

            return Hex.encodeHexString(c);
        }

        return null;
    }

    public String decrypt(final String encrypted, final String key) {
        byte[] encryptedText = getBytesFromHexString(encrypted);
        byte[] secret = getBytesFromHexString(key);

        if (secret != null && encryptedText != null) {
            final List<byte[]> keys = generateKeys(secret);

            byte[] b = ip(encryptedText);

            final byte[] intermediateInvertedSResult = s(
                    xor(b, keys.get(32)),
                    7
            );
            b = xor(intermediateInvertedSResult, keys.get(31));

            for (int i = 30; i > -1; i--) {
                final int power = i % 8;
                final byte[] currentKey = keys.get(i);
                final byte[] invertedSResult = invertedS(invertedL(b), power);
                b = xor(invertedSResult, currentKey);
            }

            b = fp(b);

            return  Hex.encodeHexString(b);
        }

        return null;
    }

    private byte[] getBytesFromHexString(final String data) {
        try {
            return Hex.decodeHex(data);
        } catch (final DecoderException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<byte[]> generateKeys(final byte[] secret) {
        final List<byte[]> keys = new ArrayList<>();
        final int countOfKeys = 33;
        final int[] w = new int[12];

        final ByteBuffer byteBuffer = ByteBuffer.wrap(secret);
        for (int i = 0; i < 8; i++) {
            w[i] = byteBuffer.getInt();
        }

        for (int i = 0; i < countOfKeys; i++) {
            for (int j = 0; j < 4; j++) {
                w[j + 8] = rol(
                        w[j] ^ w[j + 3] ^ w[j + 7] ^ G ^ (4 * i + j),
                        11
                );
            }

            byteBuffer.clear();
            for (int j = 8; j < 12; j++) {
                byteBuffer.putInt(w[j]);
            }
            int currentSPower = (11 - i) % 8;
            if (currentSPower < 0) {
                currentSPower += 8;
            }
            final byte[] sResult = s(byteBuffer.array(), currentSPower);
            keys.add(ip(sResult));

            for (int j = 0; j < 8; j++) {
                w[j] = w[j + 4];
            }
        }
        return keys;
    }

    private byte[] ip(final byte[] bytes) {
        final Function<Integer, Integer> destinationIndexComputeFunction = i -> 4 * (i - 32 * (i / 32)) + (i / 32);
        return performPermutation(bytes, destinationIndexComputeFunction);
    }

    private byte[] fp(final byte[] bytes) {
        final Function<Integer, Integer> destinationIndexComputeFunction = i -> i / 4 + 32 * (i % 4);
        return performPermutation(bytes, destinationIndexComputeFunction);
    }

    private byte[] performPermutation(final byte[] data, final Function<Integer, Integer> destinationIndexComputeFunction) {
        final byte[] output = new byte[16];
        int currentByteIndex = -1;
        for (int i = 0; i < 128; i++) {
            if (i % 8 == 0) {
                currentByteIndex += 1;
            }
            final int currentByte = data[currentByteIndex] & 0xFF;
            final int shift = 8 - (i % 8) - 1;
            final int bit = (currentByte >>> shift) & 0x01;

            final int bitDestinationIndex = destinationIndexComputeFunction.apply(i);

            final int destinationByteIndex = bitDestinationIndex / 8;
            final int bitUpdater = 0x80 >>> (bitDestinationIndex % 8);
            if (bit == 1) {
                output[destinationByteIndex] |= bitUpdater;
            } else {
                output[destinationByteIndex] &= bitUpdater;
            }
        }
        return output;
    }

    private byte[] l(final byte[] bytes) {
        final int[] x = get32BitsWordsOn128BitsWord(bytes);

        x[0] = rol(x[0], 13);
        x[2] = rol(x[2], 3);
        x[1] = x[1] ^ x[0] ^ x[2];
        x[3] = x[3] ^ x[2] ^ shl(x[0], 3);
        x[1] = rol(x[1], 1);
        x[3] = rol(x[3], 7);
        x[0] = x[0] ^ x[1] ^ x[3];
        x[2] = x[2] ^ x[3] ^ shl(x[1], 7);
        x[0] = rol(x[0], 5);
        x[2] = rol(x[2], 22);

        return get128BitsWordOn32BitWords(x);
    }

    private byte[] invertedL(final byte[] bytes) {
        final int[] x = get32BitsWordsOn128BitsWord(bytes);

        x[2] = rol(x[2], 10);
        x[0] = rol(x[0], 27);
        x[2] = x[2] ^ x[3] ^ shl(x[1], 7);
        x[0] = x[0] ^ x[1] ^ x[3];
        x[3] = rol(x[3], 25);
        x[1] = rol(x[1], 31);
        x[3] = x[3] ^ x[2] ^ shl(x[0], 3);
        x[1] = x[1] ^ x[0] ^ x[2];
        x[2] = rol(x[2], 29);
        x[0] = rol(x[0], 19);

        return get128BitsWordOn32BitWords(x);
    }

    private int[] get32BitsWordsOn128BitsWord(final byte[] bytes) {
        final ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        final int[] words = new int[4];
        for (int i = 0; i < words.length; i++) {
            words[i] = byteBuffer.getInt();
        }
        return words;
    }

    private byte[] get128BitsWordOn32BitWords(final int[] words) {
        final ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);
        byteBuffer.clear();
        for (int word : words) {
            byteBuffer.putInt(word);
        }
        return byteBuffer.array();
    }

    private byte[] s(final byte[] bytes, final int round) {
        return performSBox(S_TABLE, bytes, round);
    }

    private byte[] invertedS(final byte[] bytes, final int round) {
        return performSBox(INVERTED_S_TABLE, bytes, round);
    }

    private byte[] performSBox(final byte[][] sBox, final byte[] data, final int round) {
        final byte[] currentTableLine = sBox[round];
        final byte[] output = new byte[16];
        for (int i = 0; i < output.length; i++) {
            final int currentByte = data[i] & 0xFF;
            final byte leftHalfByte = (byte) (currentByte >>> 4);
            final byte rightHalfByte = (byte) (currentByte & 0x0F);
            output[i] = (byte) ((currentTableLine[leftHalfByte] << 4) | (currentTableLine[rightHalfByte]));
        }
        return output;
    }

    private byte[] xor(final byte[] word1, final byte[] word2) {
        final ByteBuffer byteBufferWord1 = ByteBuffer.wrap(word1);
        final ByteBuffer byteBufferWord2 = ByteBuffer.wrap(word2);
        final int[] result = new int[4];
        for (int i = 0; i < result.length; i++) {
            result[i] = byteBufferWord1.getInt() ^ byteBufferWord2.getInt();
        }
        return get128BitsWordOn32BitWords(result);
    }

    private int shl(final int wordPart, final int shift) {
        return (wordPart << shift);
    }

    private int rol(final int wordPart, final int shift) {
        return (wordPart << shift) | (wordPart >>> (32 - shift));
    }

    public static void main(String[] args) throws DecoderException {
        String str = "15ab503b124365789af0d84ef1e2c3d2";
        Serpent serpent = new Serpent();
        byte[] result = serpent.s(Hex.decodeHex(str), 3);
        System.out.println(Hex.encodeHex(result));

        final int shlResult = serpent.shl(21, 2);
        System.out.println(shlResult);
        System.out.println(shlResult == 84);

        final int rolResult1 = serpent.rol(21, 2);
        System.out.println(rolResult1);
        System.out.println(rolResult1 == 84);

        final int rolResult2 = serpent.rol(134217728, 5);
        System.out.println(rolResult2);
        System.out.println(rolResult2 == 1);

        final int[] words = serpent.get32BitsWordsOn128BitsWord(Hex.decodeHex(str));
        System.out.println(Arrays.toString(words));
        System.out.println(words[0] == 363548731);

        final byte[] word = serpent.get128BitsWordOn32BitWords(words);
        System.out.println(Arrays.equals(word, Hex.decodeHex(str)));

        final byte[] xor = serpent.xor(word, word);
        System.out.println(Arrays.equals(xor, new byte[16]));
    }
}
