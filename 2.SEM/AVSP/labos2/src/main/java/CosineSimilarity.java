public class CosineSimilarity {
    public static void main(String[] args) {
        double[] vectorA = new double[]{-2.6, 0, -0.6, 0, 0, 1.4, 0, 0, 1.4,0,0.4,0};
        double[] vectorB = new double[]{-1.0,1.0,0,-2.0,-1.0,0,0.0,0,1.0,0.0,2.0,0};
        System.out.println(cosineSimilarity(vectorA,vectorB));
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
}
