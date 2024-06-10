import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class PCY {
    public static void main(String[] args) throws IOException {
        Map<Integer, Integer> itemCounts = new HashMap<>();
        Map<Integer, Integer> hashBuckets = new HashMap<>();
        Map<String, Integer> pairCounts = new HashMap<>();
        List<List<Integer>> baskets = new ArrayList<>();
        int totalBaskets;
        double s;
        int threshold;
        int numberOfBuckets;

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        totalBaskets = Integer.parseInt(reader.readLine().trim());
        s = Double.parseDouble(reader.readLine().trim());
        threshold = (int) (s * totalBaskets);
        numberOfBuckets = Integer.parseInt(reader.readLine().trim());

        String line;
        while ((line = reader.readLine()) != null) {
            List<Integer> basket = new ArrayList<>();
            for (String itemStr : line.split(" ")) {
                int item = Integer.parseInt(itemStr);
                itemCounts.put(item, itemCounts.getOrDefault(item, 0) + 1);
                basket.add(item);
            }
            baskets.add(basket);
        }

        for (List<Integer> basket : baskets) {
            for (int i = 0; i < basket.size(); i++) {
                for (int j = i + 1; j < basket.size(); j++) {
                    int item1 = basket.get(i);
                    int item2 = basket.get(j);
                    if (itemCounts.get(item1) >= threshold && itemCounts.get(item2) >= threshold) {
                        int hash = (item1 * itemCounts.size() + item2) % numberOfBuckets;
                        hashBuckets.put(hash, hashBuckets.getOrDefault(hash, 0) + 1);
                    }
                }
            }
        }

        for (List<Integer> basket : baskets) {
            for (int i = 0; i < basket.size(); i++) {
                for (int j = i + 1; j < basket.size(); j++) {
                    int item1 = basket.get(i);
                    int item2 = basket.get(j);
                    if (itemCounts.get(item1) >= threshold && itemCounts.get(item2) >= threshold) {
                        int hash = (item1 * itemCounts.size() + item2) % numberOfBuckets;
                        if (hashBuckets.get(hash) >= threshold) {
                            String pairKey = item1 + "," + item2;
                            pairCounts.put(pairKey, pairCounts.getOrDefault(pairKey, 0) + 1);
                        }
                    }
                }
            }
        }

        int frequentItemsCount = (int) itemCounts.values().stream().filter(count -> count >= threshold).count();
        int A = frequentItemsCount * (frequentItemsCount - 1) / 2;

        int P = pairCounts.size();

        System.out.println(A);
        System.out.println(P);
        pairCounts.entrySet().stream()
                .filter(entry -> entry.getValue() >= threshold)
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(entry -> System.out.println(entry.getValue()));


    }
}
