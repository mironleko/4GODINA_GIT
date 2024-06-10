import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class GNAlgorithm {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(GNAlgorithm.class.getResourceAsStream("./test0_primjer.in"))));

        Map<Integer, List<Integer>> graph = new HashMap<>();
        Map<Integer, List<Integer>> attributes = new HashMap<>();
        Map<Integer, Map<Integer, Integer>> graphWithWeights = new HashMap<>();

        String line;
        boolean readingAttributes = false;

        while ((line = reader.readLine()) != null) {
            if (line.isEmpty()) {
                readingAttributes = true;
                continue;
            }

            String[] parts = line.split(" ");

            if (!readingAttributes) {
                int node1 = Integer.parseInt(parts[0]);
                int node2 = Integer.parseInt(parts[1]);
                graph.computeIfAbsent(node1, k -> new ArrayList<>()).add(node2);
                graph.computeIfAbsent(node2, k -> new ArrayList<>()).add(node1);
            } else {
                int node = Integer.parseInt(parts[0]);
                List<Integer> attributeList = new ArrayList<>();
                for (int i = 1; i < parts.length; i++) {
                    attributeList.add(Integer.parseInt(parts[i]));
                }
                attributes.put(node, attributeList);
                graph.putIfAbsent(node, new ArrayList<>());
            }
        }

        int maxSimilarity = attributes.isEmpty() ? 0 : attributes.values().iterator().next().size();

        for (var entry : graph.entrySet()) {
            int node1 = entry.getKey();

            for (int node2 : entry.getValue()) {
                int similarity = calculateSimilarity(attributes.get(node1), attributes.get(node2));
                int dissimilarity = maxSimilarity - (similarity - 1);

                graphWithWeights.computeIfAbsent(node1, k -> new HashMap<>()).put(node2, dissimilarity);
                graphWithWeights.computeIfAbsent(node2, k -> new HashMap<>()).put(node1, dissimilarity);
            }
            graphWithWeights.computeIfAbsent(node1, k -> new HashMap<>());
        }


        Map<Integer, Map<Integer, Integer>> iterationGraphWithWeights = cloneGraph(graphWithWeights);


        Map<Integer, Integer> nodeCommunityMap = new HashMap<>();
        double totalEdgeWeight = calculateTotalEdgeWeight(graphWithWeights);
        Map<Integer, Integer> nodeWeightedDegrees = calculateWeightedDegrees(graphWithWeights);

//        System.out.println("MAPA TEZINA " + nodeWeightedDegrees);
//        System.out.println("UKUPNA TEZINA " + totalEdgeWeight);
        reinitializeCommunities(iterationGraphWithWeights, nodeCommunityMap);

        double modularity = calculateModularity(graphWithWeights, nodeCommunityMap, nodeWeightedDegrees, totalEdgeWeight);

//        System.out.println("MODULARITY " + modularity);

        double maxModularity = modularity;
        Map<Integer, Integer> bestCommunity = new HashMap<>(nodeCommunityMap);

        while (true) {
            Map<String, Double> edgeBetweenness = calculateBetweenness(iterationGraphWithWeights);
            List<String> edgesToRemove = getEdgesWithHighestBetweenness(edgeBetweenness);

            if (edgesToRemove.isEmpty()) {
                break;
            }
            List<String> formattedEdges = formatAndSortEdges(edgesToRemove);
            formattedEdges.forEach(edge -> {
                String[] parts = edge.split("-");
                System.out.println(parts[0] + " " + parts[1]);
            });

            removeEdges(iterationGraphWithWeights, edgesToRemove);
            reinitializeCommunities(iterationGraphWithWeights, nodeCommunityMap);

            if (calculateTotalEdgeWeight(iterationGraphWithWeights) > 0) {
                modularity = calculateModularity(graphWithWeights, nodeCommunityMap, nodeWeightedDegrees, totalEdgeWeight);
//                    System.out.println("MODULARITY " + modularity);
                if (modularity > maxModularity) {
                    maxModularity = modularity;
                    bestCommunity.clear();
                    bestCommunity.putAll(nodeCommunityMap);
                }
            }

        }

        printCommunities(bestCommunity);

    }

    private static void printCommunities(Map<Integer, Integer> bestCommunity) {
        Map<Integer, List<Integer>> communities = new HashMap<>();
        for (var entry : bestCommunity.entrySet()) {
            communities.computeIfAbsent(entry.getValue(), k -> new ArrayList<>()).add(entry.getKey());
        }

        List<List<Integer>> sortedCommunities = new ArrayList<>(communities.values());
        sortedCommunities.forEach(Collections::sort);
        sortedCommunities.sort((a, b) -> {
            if (Objects.equals(a.size(), b.size())) {
                return a.get(0).compareTo(b.get(0));
            }
            return Integer.compare(a.size(), b.size());
        });

        String result = sortedCommunities.stream()
                .map(community -> community.stream()
                        .map(Object::toString)
                        .collect(Collectors.joining("-")))
                .collect(Collectors.joining(" "));

        System.out.println(result.trim());
    }

    private static List<String> formatAndSortEdges(List<String> edges) {
        return edges.stream()
                .map(edge -> {
                    String[] nodes = edge.split("-");
                    int node1 = Integer.parseInt(nodes[0]);
                    int node2 = Integer.parseInt(nodes[1]);
                    if (node1 > node2) {
                        return node2 + "-" + node1;
                    }
                    return node1 + "-" + node2;
                })
                .sorted((e1, e2) -> {
                    String[] parts1 = e1.split("-");
                    String[] parts2 = e2.split("-");
                    int firstCompare = Integer.compare(Integer.parseInt(parts1[0]), Integer.parseInt(parts2[0]));
                    if (firstCompare != 0) return firstCompare;
                    return Integer.compare(Integer.parseInt(parts1[1]), Integer.parseInt(parts2[1]));
                })
                .collect(Collectors.toList());
    }

    private static double calculateModularity(Map<Integer, Map<Integer, Integer>> graphWithWeights, Map<Integer, Integer> nodeCommunityMap, Map<Integer, Integer> nodeWeightedDegrees, double totalEdgeWeight) {
        double modularity = 0.0;
        for (int u : graphWithWeights.keySet()) {
            for (int v : graphWithWeights.keySet()) {
                int Auv = graphWithWeights.containsKey(u) && graphWithWeights.get(u).containsKey(v) ? graphWithWeights.get(u).get(v) : 0;
                int delta = nodeCommunityMap.get(u).equals(nodeCommunityMap.get(v)) ? 1 : 0;
                double expectedEdge = ((double) nodeWeightedDegrees.get(u) * (double) nodeWeightedDegrees.get(v)) / (2.0 * totalEdgeWeight);
                modularity += (Auv - expectedEdge) * delta;
            }
        }
        return roundValue(modularity / (2.0 * totalEdgeWeight));
    }

    private static double calculateTotalEdgeWeight(Map<Integer, Map<Integer, Integer>> graphWithWeights) {
        int totalWeight = 0;
        for (Map.Entry<Integer, Map<Integer, Integer>> entry : graphWithWeights.entrySet()) {
            for (int weight : entry.getValue().values()) {
                totalWeight += weight;
            }
        }
        return totalWeight / 2.0;
    }

    private static Map<Integer, Integer> calculateWeightedDegrees(Map<Integer, Map<Integer, Integer>> graphWithWeights) {
        Map<Integer, Integer> nodeWeightedDegrees = new HashMap<>();
        for (Map.Entry<Integer, Map<Integer, Integer>> entry : graphWithWeights.entrySet()) {
            int node = entry.getKey();
            int weightedDegree = 0;
            for (int weight : entry.getValue().values()) {
                weightedDegree += weight;
            }
            nodeWeightedDegrees.put(node, weightedDegree);
        }
        return nodeWeightedDegrees;
    }


    private static int calculateSimilarity(List<Integer> attributeList1, List<Integer> attributeList2) {
        int count = 0;
        for (int i = 0; i < attributeList1.size(); i++) {
            if (attributeList1.get(i).equals(attributeList2.get(i))) {
                count++;
            }
        }
        return count;
    }

    private static Map<String, Double> calculateBetweenness(Map<Integer, Map<Integer, Integer>> graphWithWeights) {
        Map<String, Double> edgeBetweenness = new HashMap<>();

        for (Integer source : graphWithWeights.keySet()) {
            Map<Integer, List<List<Integer>>> paths = findShortestPaths(graphWithWeights, source);
            for (Map.Entry<Integer, List<List<Integer>>> entry : paths.entrySet()) {
                List<List<Integer>> allShortestPaths = entry.getValue();
                int numberOfShortestPaths = allShortestPaths.size();

                for (List<Integer> path : allShortestPaths) {
                    for (int i = 0; i < path.size() - 1; i++) {
                        String edge = path.get(i) < path.get(i + 1) ?
                                path.get(i) + "-" + path.get(i + 1) :
                                path.get(i + 1) + "-" + path.get(i);
                        edgeBetweenness.put(edge, edgeBetweenness.getOrDefault(edge, 0.0) + 1.0 / numberOfShortestPaths);
                    }
                }
            }
        }

        edgeBetweenness.forEach((edge, value) -> edgeBetweenness.put(edge, roundValue(value / 2.0)));

        return edgeBetweenness;
    }

    private static Map<Integer, List<List<Integer>>> findShortestPaths(Map<Integer, Map<Integer, Integer>> graphWithWeights, int source) {
        Map<Integer, List<List<Integer>>> allShortestPaths = new HashMap<>();
        Map<Integer, Integer> distances = new HashMap<>();
        Map<Integer, List<Integer>> predecessors = new HashMap<>();
        Queue<Integer> queue = new LinkedList<>();

        for (int node : graphWithWeights.keySet()) {
            distances.put(node, Integer.MAX_VALUE);
            predecessors.put(node, new ArrayList<>());
        }
        distances.put(source, 0);
        queue.add(source);

        while (!queue.isEmpty()) {
            int currentNode = queue.poll();
            int currentDistance = distances.get(currentNode);

            for (var neighborEntry : graphWithWeights.get(currentNode).entrySet()) {
                int neighbor = neighborEntry.getKey();
                int weight = neighborEntry.getValue();
                int newDistance = currentDistance + weight;

                if (newDistance < distances.get(neighbor)) {
                    distances.put(neighbor, newDistance);
                    queue.add(neighbor);
                    predecessors.get(neighbor).clear();
                    predecessors.get(neighbor).add(currentNode);
                } else if (Objects.equals(newDistance, distances.get(neighbor))) {
                    predecessors.get(neighbor).add(currentNode);
                }
            }
        }

        for (int target : graphWithWeights.keySet()) {
            if (distances.get(target) < Integer.MAX_VALUE) {
                List<List<Integer>> paths = new ArrayList<>();
                findAllPaths(predecessors, source, target, new ArrayList<>(), paths);
                allShortestPaths.put(target, paths);
            }
        }

        return allShortestPaths;
    }

    private static void findAllPaths(Map<Integer, List<Integer>> predecessors, int source, int target, List<Integer> path, List<List<Integer>> paths) {
        path.add(target);

        if (Objects.equals(target, source)) {
            List<Integer> completePath = new ArrayList<>(path);
            Collections.reverse(completePath);
            paths.add(completePath);
        } else {
            for (int predecessor : predecessors.get(target)) {
                findAllPaths(predecessors, source, predecessor, path, paths);
            }
        }

        path.remove(path.size() - 1);
    }

    private static List<String> getEdgesWithHighestBetweenness(Map<String, Double> edgeBetweenness) {
        double maxBetweenness = 0;
        List<String> edgesToRemove = new ArrayList<>();

        for (double value : edgeBetweenness.values()) {
            if (value > maxBetweenness) {
                maxBetweenness = value;
            }
        }

        for (var entry : edgeBetweenness.entrySet()) {

            if (Double.compare(entry.getValue(), maxBetweenness) == 0) {
                edgesToRemove.add(entry.getKey());
            }
        }

        return edgesToRemove;
    }

    private static Map<Integer, Map<Integer, Integer>> cloneGraph(Map<Integer, Map<Integer, Integer>> originalGraph) {
        Map<Integer, Map<Integer, Integer>> clonedGraph = new HashMap<>();
        for (var entry : originalGraph.entrySet()) {
            clonedGraph.put(entry.getKey(), new HashMap<>(entry.getValue()));
        }
        return clonedGraph;
    }

    private static void removeEdges(Map<Integer, Map<Integer, Integer>> graphWithWeights, List<String> edgesToRemove) {
        for (String edge : edgesToRemove) {
            String[] nodes = edge.split("-");
            int node1 = Integer.parseInt(nodes[0]);
            int node2 = Integer.parseInt(nodes[1]);

            graphWithWeights.get(node1).remove(node2);
            graphWithWeights.get(node2).remove(node1);

            graphWithWeights.putIfAbsent(node1, new HashMap<>());
            graphWithWeights.putIfAbsent(node2, new HashMap<>());
        }
    }

    private static void reinitializeCommunities(Map<Integer, Map<Integer, Integer>> graphWithWeights, Map<Integer, Integer> nodeCommunityMap) {
        nodeCommunityMap.clear();
        int communityId = 1;
        for (int node : graphWithWeights.keySet()) {
            if (!nodeCommunityMap.containsKey(node)) {
                depthFirstSearch(graphWithWeights, node, communityId, nodeCommunityMap);
                communityId++;
            }
        }
    }

    private static void depthFirstSearch(Map<Integer, Map<Integer, Integer>> graphWithWeights, int startNode, int communityId, Map<Integer, Integer> nodeCommunityMap) {
        Stack<Integer> stack = new Stack<>();
        stack.push(startNode);
        nodeCommunityMap.put(startNode, communityId);

        while (!stack.isEmpty()) {
            int node = stack.pop();
            for (int neighbor : graphWithWeights.get(node).keySet()) {
                if (!nodeCommunityMap.containsKey(neighbor)) {
                    nodeCommunityMap.put(neighbor, communityId);
                    stack.push(neighbor);
                }
            }
        }
    }

    private static double roundValue(double value) {
        if (Math.abs(value) < 0.00001) {
            return 0.0;
        }
        return Math.round(value * 10000.0) / 10000.0;
    }
}
