package web.scraper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.TreeSet;

public class Cleaner extends Thread {
    private App app;
    private IndexURLTree tree;
    private List<List<Data>> buffers;
    private List<List<Seed>> queues;
    private List<IndexBuilder> builders;
    private TreeSet<Seed> set;
    private long count;

    public Cleaner(App app) {
        this.app = app;
        this.count = 0;
        // Add custom comparator to the set which compares the new urls of two seeds
        // instead
        this.set = new TreeSet<>((seed1, seed2) -> seed1.getNewUrl().compareTo(seed2.getNewUrl()));
    }

    public void run() {
        System.out.println("\nStart cleaning............................");
        initialise();

        checkIfBufferIsEmpty();
        writeRemainingToTree();
        writeResult();
        writeFromQueueToDisk();
        writeStatsToDisk();

        System.out.println("Done!!!!!!!!!!!!!!!!!!!!!!!!!");
    }

    public void initialise() {
        this.tree = app.getTree();
        this.buffers = app.getBuffers();
        this.queues = app.getQueues();
        this.builders = app.getBuilders();
    }

    public void checkIfBufferIsEmpty() {
        boolean isEmpty = false;
        for (List<Data> buffer : buffers) {
            if (buffer.isEmpty()) {
                isEmpty = true;
            }
        }

        if (isEmpty) {
            System.out.printf("Some buffers are empty!\n", isEmpty);
        }
    }

    public void writeRemainingToTree() {
        for (List<Data> buffer : buffers) {
            for (Data data : buffer) {
                try {
                    if (tree.addURLandContent(data)) {
                        this.count++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void writeResult() {
        try {
            this.tree.writeResult();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeFromQueueToDisk() {
        try {
            // Duplicate checking (for new URL)
            queues.forEach(queue -> queue.forEach(seed -> {
                this.set.add(seed);
            }));

            File file = new File("./res2.txt");
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            writer.write("................. Remaining new URLs in queues that has not been crawled ............\n");

            for (Seed seed : this.set) {
                writer.write(seed.getNewUrl() + " --> " + seed.getSourceUrl() + "\n");
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeStatsToDisk() {
        try {
            File file = new File("./statistics.txt");
            file.createNewFile();
            FileWriter writer = new FileWriter(file);

            long numUrls = queues.stream().mapToInt(queue -> queue.size()).sum();
            this.count += builders.stream().mapToLong(builder -> builder.getCount()).sum();

            writer.write("\nFinal statistics:\n");
            // TODO: add findURLs() method to calculate no. of URLs in IUT
            writer.write(String.format("%d new urls are found.\n", tree.size()));
            writer.write(String.format("%d urls are in queues.\n", numUrls));
            writer.write(String.format("%d non-duplicated urls are in queues.\n", this.set.size()));
            writer.write(String.format("%d urls and pages were written from buffer to tree\n", count));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
