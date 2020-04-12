package web.scraper;

public class Merger {
    public static void main(String[] args) {
        if (args.length <= 1 || !"-output".equals(args[0])) {
            throw new IllegalArgumentException("Input parameters is incorrect!");
        }
        
        String outputFileName = args[1];
        new IndexURLTree(outputFileName).writeResult();
    }
}