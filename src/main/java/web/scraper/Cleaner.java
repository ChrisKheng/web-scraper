package web.scraper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.TreeSet;

public class Cleaner extends Thread {
    private IndexURLTree tree;
    private List<List<Data>> buffers;
    private List<List<Seed>> queues;
    private List<IndexBuilder> builders;
    private TreeSet<String> set;
    private long count;

    public Cleaner(IndexURLTree tree, List<List<Data>> buffers, List<List<Seed>> queues, List<IndexBuilder> builders) {
        this.tree = tree;
        this.buffers = buffers;
        this.queues = queues;
        this.builders = builders;
        this.count = 0;
        this.set = new TreeSet<>();
    }

    public void run() {
        System.out.println("\nStart cleaning............................");

        int size = buffers.stream().mapToInt(buffer -> buffer.size()).sum();

        boolean isEmpty = true;
        for (List<Data> buffer: buffers) {
            if (!buffer.isEmpty()) {
                isEmpty = false;
            }
        }

        if (isEmpty) {
            System.out.printf("Buffers are still empty cuz initial seeds hasn't finished yet\n", isEmpty);
        }

        writeRemainingToTree();        
        tree.writeResult();
        writeFromQueueToDisk();
        writeStatsToDisk();

        System.out.println("Done!!!!!!!!!!!!!!!!!!!!!!!!!");
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

    public void writeFromQueueToDisk() {
        try {
             // Duplicate checking
            queues.forEach(queue -> 
                queue.forEach(seed -> {
                    this.set.add(seed.getNewUrl());
                }));

            File file = new File("./res2.txt");
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            writer.write("................. Remaining new URLs in queues that has not been crawled ............\n");
            writer.write(String.format("Total size: %d\n", this.set.size()));

            for (String url : this.set) {
                writer.write(url);
                writer.write('\n');
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
