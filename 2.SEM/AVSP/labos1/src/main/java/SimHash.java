import org.apache.commons.codec.digest.DigestUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.math.BigInteger;
import java.util.Arrays;

public class SimHash {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        int N = Integer.parseInt(reader.readLine());
        String[] simHashes = new String[N];

        for (int i = 0; i < N; i++) {
            simHashes[i] = generateSimHashText(reader.readLine());
        }

        int Q = Integer.parseInt(reader.readLine());
        for (int i = 0; i < Q; i++) {
            String[] queryParts = reader.readLine().split(" ");
            int I = Integer.parseInt(queryParts[0]);
            int K = Integer.parseInt(queryParts[1]);

            int count = 0;
            for (int j = 0; j < N; j++) {
                if (j == I) continue;

                if (hammingDistance(simHashes[I], simHashes[j]) <= K) {
                    count++;
                }
            }
            System.out.println(count);
        }
    }

    private static int hammingDistance(String simHash1, String simHash2) {
        BigInteger hashedValue1 = new BigInteger(simHash1, 16);
        BigInteger hashedValue2 = new BigInteger(simHash2, 16);

        BigInteger hammingDistanceResult = hashedValue1.xor(hashedValue2);

        return hammingDistanceResult.bitCount();
    }

    private static String generateSimHashText(String text) {
        final int hashBits = 128;
        int[] sh = new int[hashBits];

        String[] units = text.split(" ");

        for (String unit : units) {
            byte[] hashBytes = DigestUtils.md5(unit);

            for (int i = 0; i < hashBytes.length * 8; i++) {
                int byteIndex = i / 8;
                int bitPosition = 7 - (i % 8);
                if (isBitSet(hashBytes[byteIndex], bitPosition)) {
                    sh[i] += 1;
                } else {
                    sh[i] -= 1;
                }
            }
        }

        sh = Arrays.stream(sh).map(element -> element >= 0 ? 1 : 0).toArray();

        StringBuilder simHashValue = new StringBuilder();
        for (int i = 0; i < hashBits; i += 4) {
            String binaryGroup = "" + sh[i] + sh[i + 1] + sh[i + 2] + sh[i + 3];

            simHashValue.append(Integer.toString(Integer.parseInt(binaryGroup, 2), 16));
        }

        return simHashValue.toString();
    }

    private static Boolean isBitSet(byte b, int bit) {
        return (b & (1 << bit)) != 0;
    }
}
