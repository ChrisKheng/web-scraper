package web.scraper;

import java.util.TreeSet;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Cleaner extends Thread {
    private TreeSet<String> tree;

    public Cleaner(TreeSet<String> tree) {
        this.tree = tree;
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

            System.out.println("Done!!!!!!!!!!!!!!!!!!!!!!!!!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}