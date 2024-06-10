import java.io.*;
import java.util.*;

public class NodeRank {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        Locale.setDefault(Locale.US);

        String[] lineOne = reader.readLine().split(" ");
        int n = Integer.parseInt(lineOne[0]);
        double beta = Double.parseDouble(lineOne[1]);

        List<List<Integer>> graph = createGraph(n, reader);

        int numOfQueries = Integer.parseInt(reader.readLine());
        List<List<Integer>> queries = new ArrayList<>();
        for (int i = 0; i < numOfQueries; i++) {
            String[] query = reader.readLine().split(" ");
            int nodeIndexValue = Integer.parseInt(query[0]);
            int itValue = Integer.parseInt(query[1]);
            queries.add(List.of(nodeIndexValue, itValue));
        }

        int itMaxValue = queries.stream()
                .mapToInt(query -> query.get(1))
                .max()
                .orElse(0);

        reader.close();

        List<double[]> rankIterations = new ArrayList<>();
        double[] startingRanks = new double[n];
        for (int i = 0; i < n; i++) {
            startingRanks[i] = 1.0 / n;
        }
        rankIterations.add(startingRanks);

        for (int t = 1; t <= itMaxValue; t++) {
            double[] newRanks = new double[n];

            for (int i = 0; i < n; i++) {
                newRanks[i] = (1 - beta) / n;
            }

            for (int i = 0; i < n; i++) {
                for (int j : graph.get(i)) {
                    newRanks[j] += beta * rankIterations.get(t - 1)[i] / graph.get(i).size();
                }
            }

            rankIterations.add(newRanks);
        }

        for (List<Integer> query : queries) {
            double numberToPrint = rankIterations.get(query.get(1))[query.get(0)];
            System.out.printf(Locale.US, "%.10f%n", numberToPrint);
        }

    }

    private static List<List<Integer>> createGraph(int n, BufferedReader reader) throws IOException {
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            graph.add(new ArrayList<>());
            String[] line = reader.readLine().split(" ");
            for (String node : line) {
                graph.get(i).add(Integer.parseInt(node));
            }
        }
        return graph;
    }
}
