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
import java.util.stream.Collectors;
import java.util.logging.Logger;

public class App {

    public void run() {
        Logger logger = Logger.getLogger("App");
    
        List<String> seeds = getURLSeeds();

        // TreeSet and LinkedList is NOT thread safe!!!
        // Visit https://riptutorial.com/java/example/30472/treemap-and-treeset-thread-safety
        // for how to ensure thread safety using TreeSet.
        TreeSet<String> tree = new TreeSet<>();
        List<String> buffer = new LinkedList<>();

        logger.info("Starting........ =D");

        // Spawn and start crawler thread
        // seeds can be split into different portion and give to the individual threads.
        // Crawler crawler = new Crawler(seeds, tree, buffer);
        // crawler.start();
        RecursiveCrawler crawler = new RecursiveCrawler(tree, buffer); 
        for (String seed : seeds) {
            crawler.run(seed);
        }
        try {
            crawler.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        writeToDisk(tree);
        logger.info("Done........ =D");
    }

    // Read urls from seed file.
    public static List<String> getURLSeeds() {
        InputStreamReader reader = new InputStreamReader(System.in);
        BufferedReader bufReader = new BufferedReader(reader);

        return bufReader.lines().collect(Collectors.toList());
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
        new App().run();
    }
}
