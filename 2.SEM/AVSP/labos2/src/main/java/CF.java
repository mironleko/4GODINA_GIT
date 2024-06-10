import java.io.*;
import java.math.*;
import java.text.*;
import java.util.*;
import java.util.stream.Collectors;

public class CF {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        String firstLine = reader.readLine();

        String[] itemsUsers = firstLine.split(" ");

        int N = Integer.parseInt(itemsUsers[0]);
        int M = Integer.parseInt(itemsUsers[1]);

        String[][] itemUserMatrix = new String[N][M];

        for (int i = 0; i < N; i++) {
            String[] matrixRow = reader.readLine().split(" ");
            for (int j = 0; j < M; j++) {
                itemUserMatrix[i][j] = matrixRow[j];
            }
        }
        int Q = Integer.parseInt(reader.readLine());

        for (int i = 0; i < Q; i++) {
            String[] array = reader.readLine().split(" ");
            int I = Integer.parseInt(array[0]);
            int J = Integer.parseInt(array[1]);
            int T = Integer.parseInt(array[2]);
            int K = Integer.parseInt(array[3]);

            if (T == 0) {
                predictFunction(N, M, J, I, K, itemUserMatrix);
            } else {
                String[][] userItemMatrixTransposed = transposeMatrix(itemUserMatrix, N, M);
                predictFunction(N, M, I, J, K, userItemMatrixTransposed);
            }
        }


    }

    private static void predictFunction(int n, int m, int i, int j, int k, String[][] userItemMatrixTransposed) {
        double result = predictRating(userItemMatrixTransposed, j - 1, i - 1, k, n, m);
        DecimalFormat df = new DecimalFormat("#.000");
        df.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.UK));

        BigDecimal bd = new BigDecimal(result);
        BigDecimal res = bd.setScale(3, RoundingMode.HALF_UP);
        System.out.println(df.format(res));
    }

    private static double predictRating(String[][] itemUserMatrix, int itemIndex, int userIndex, int K, int N, int M) {

        double[] startRowItemArray = new double[itemUserMatrix[itemIndex].length];
        double m = calculateMeanRating(itemUserMatrix, M, itemIndex);

        for (int j = 0; j < M; j++) {
            if (!itemUserMatrix[itemIndex][j].equals("X")) {
                startRowItemArray[j] = Double.parseDouble(itemUserMatrix[itemIndex][j]) - m;
            } else {
                startRowItemArray[j] = 0.0;
            }
        }

        double[] similarities = new double[N];
        Map<Integer, Double> similaritiesMap = new HashMap<>();
        for (int i = 0; i < N; i++) {
            double[] currentRowItemArray = new double[itemUserMatrix[i].length];

            double m_i = calculateMeanRating(itemUserMatrix, M, i);
            for (int j = 0; j < M; j++) {
                if (!itemUserMatrix[i][j].equals("X")) {
                    currentRowItemArray[j] = Double.parseDouble(itemUserMatrix[i][j]) - m_i;
                } else {
                    currentRowItemArray[j] = 0.0;
                }
            }
            if (i == itemIndex) {
                similarities[i] = Double.MIN_VALUE;
            } else {
                similarities[i] = cosineSimilarity(currentRowItemArray, startRowItemArray);
            }
            similaritiesMap.put(i, similarities[i]);
        }


        Map<Integer, Double> kSimilarities = findTopK(similaritiesMap, K, itemUserMatrix, userIndex);

        double sumNumerator = 0.0;
        double sumDenominator = 0.0;
        for (var entry : kSimilarities.entrySet()) {
            sumNumerator += Double.parseDouble(itemUserMatrix[entry.getKey()][userIndex]) * entry.getValue();
            sumDenominator += entry.getValue();
        }
        return sumNumerator / sumDenominator;
    }

    private static Map<Integer, Double> findTopK(Map<Integer, Double> similaritiesMap, int K, String[][] itemUserMatrix, int userIndex) {
        Map<Integer, Double> filteredMap = similaritiesMap.entrySet().stream()
                .filter(entry -> !itemUserMatrix[entry.getKey()][userIndex].equals("X"))
                .filter(entry -> entry.getValue() > 0)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        int limit = Math.min(K, filteredMap.size());

        return filteredMap.entrySet().stream()
                .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
                .limit(limit)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }


    public static double cosineSimilarity(double[] vectorA, double[] vectorB) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += Math.pow(vectorA[i], 2);
            normB += Math.pow(vectorB[i], 2);
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    public static double calculateMeanRating(String[][] userItemMatrix, int M, int i) {
        double sum = 0.0;
        int sumLength = 0;
        for (int j = 0; j < M; j++) {
            if (!userItemMatrix[i][j].equals("X")) {
                sum += Double.parseDouble(userItemMatrix[i][j]);
                sumLength++;
            }
        }

        return sum / sumLength;
    }

    private static String[][] transposeMatrix(String[][] originalMatrix, int N, int M) {
        String[][] transposedMatrix = new String[M][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                transposedMatrix[j][i] = originalMatrix[i][j];
            }
        }
        return transposedMatrix;
    }
}
