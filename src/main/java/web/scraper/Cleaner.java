package web.scraper;

import java.util.TreeSet;
import java.util.logging.Logger;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Cleaner extends Thread {
    Logger logger;
    TreeSet<String> tree;

    public Cleaner(TreeSet<String> tree) {
        this.tree = tree;
        this.logger = Logger.getLogger("Cleaner");
    }

    public void run() {
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

            logger.info("Done........ =D");
            System.out.println("Done!!!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}