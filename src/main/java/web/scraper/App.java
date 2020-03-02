/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package web.scraper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.logging.Logger;

public class App {
    private static Logger logger;
    private static TreeSet<String> tree = new TreeSet<>();

    public App(Logger logger) {
        App.logger = logger;
    }

    public void run() throws Exception {
        logger.info("Starting........ =D");

        List<String> seeds = getURLSeeds();
        List<List<String>> subLists = splitList(seeds, 4);

        // TreeSet and LinkedList is NOT thread safe!!!
        // Visit https://riptutorial.com/java/example/30472/treemap-and-treeset-thread-safety
        // for how to ensure thread safety using TreeSet.
        List<String> buffer1 = new LinkedList<>();
        List<String> buffer2 = new LinkedList<>();

        Crawler crawler1 = new Crawler(subLists.get(0), tree, buffer1);
        Crawler crawler2 = new Crawler(subLists.get(1), tree, buffer1);
        Crawler crawler3 = new Crawler(subLists.get(2), tree, buffer2);
        Crawler crawler4 = new Crawler(subLists.get(3), tree, buffer2);

        IndexBuilder indexBuilder = new IndexBuilder(tree, buffer1);

        crawler1.start();
        crawler2.start();
        crawler3.start();
        crawler4.start();

        Thread ib1 = new Thread(indexBuilder);
        ib1.start();

        try {
            crawler1.join();
            crawler2.join();
            crawler3.join();
            crawler4.join();
        } catch (InterruptedException e) {
            logger.severe(e.getMessage());
        }
    }

    // Read urls from seed file.
    public static List<String> getURLSeeds() {
        InputStreamReader reader = new InputStreamReader(System.in);
        BufferedReader bufReader = new BufferedReader(reader);
        return bufReader.lines().collect(Collectors.toList());
    }

    // Split the given url list into the specified number of sub lists. 
    // Condition: number of urls in list given >= num of sublists 
    public static List<List<String>> splitList(List<String> list, int numSubLists) {
        int portionSize = list.size() / numSubLists;

        List<List<String>> result = new LinkedList<>();
        List<String> temp = new LinkedList<>();
        int count = 0;
        int currNumSubLists = 0;
        
        for (String url : list) {
            temp.add(url);
            count++;

            if (count == portionSize && currNumSubLists < numSubLists - 1) {
                result.add(temp);
                temp = new LinkedList<>();
                currNumSubLists++;
                count = 0;
            }
        }
        result.add(temp);

        return result;
    }

    // Write the urls to the disk.
    public static void writeToDisk(TreeSet<String> tree) {
        try {
            File file = new File("./result.txt");
            file.createNewFile();

            FileWriter writer = new FileWriter(file);
            tree.forEach(url -> {
                try {
                    writer.write(url);
                    writer.write("\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Cleaner(tree));

        try {
            // The following 2 line removes log from the following 2 sources.
            java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
            java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);

            new App(Logger.getLogger("App")).run();
        } catch (Exception e) {
            System.err.print("Error occurs!");
        } finally {
            // writeToDisk(tree);
            // logger.info("Done........ =D");
            // System.out.println(tree.size());
        }
    }
}
