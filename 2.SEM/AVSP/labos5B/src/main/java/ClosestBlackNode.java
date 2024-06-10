import java.io.*;
import java.util.*;

public class ClosestBlackNode {
    public static final int UNREACHABLE = -1;
    public static final int BLACK_NODE = 1;

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        String[] lineOne = reader.readLine().split(" ");
        int n = Integer.parseInt(lineOne[0]);
        int e = Integer.parseInt(lineOne[1]);

        List<Integer> typeOfNodes = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            typeOfNodes.add(Integer.parseInt(reader.readLine().trim()));
        }

        List<List<Integer>> graph = createGraph(n, e, reader);

        List<Integer> distances = new ArrayList<>(Collections.nCopies(n, UNREACHABLE));
        List<Integer> nearestBlackNode = new ArrayList<>(Collections.nCopies(n, UNREACHABLE));

        List<Integer> queue = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (Objects.equals(typeOfNodes.get(i), BLACK_NODE)) {
                queue.add(i);
                distances.set(i, 0);
                nearestBlackNode.set(i, i);
            }
        }

        int index = 0;
        while (index < queue.size()) {
            int current = queue.get(index);
            index++;
            for (int neighbor : graph.get(current)) {
                if (Objects.equals(distances.get(neighbor), UNREACHABLE)) {
                    distances.set(neighbor, distances.get(current) + 1);
                    nearestBlackNode.set(neighbor, nearestBlackNode.get(current));
                    queue.add(neighbor);
                }
            }
        }

            for (int i = 0; i < n; i++) {
                if (Objects.equals(distances.get(i), UNREACHABLE)) {
                    System.out.println(UNREACHABLE + " " + UNREACHABLE);
                } else {
                    System.out.println(nearestBlackNode.get(i) + " " + distances.get(i));
                }
            }

    }

    private static List<List<Integer>> createGraph(int numNodes, int numEdges, BufferedReader reader) throws IOException {
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < numNodes; i++) {
            graph.add(new ArrayList<>());
        }

        for (int i = 0; i < numEdges; i++) {
            String[] edge = reader.readLine().split(" ");
            int nodeA = Integer.parseInt(edge[0]);
            int nodeB = Integer.parseInt(edge[1]);
            graph.get(nodeA).add(nodeB);
            graph.get(nodeB).add(nodeA);
        }
        return graph;
    }
}
