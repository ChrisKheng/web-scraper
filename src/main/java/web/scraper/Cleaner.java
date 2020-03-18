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
    private long count;

    public Cleaner(IndexURLTree tree, List<List<Data>> buffers, List<List<Seed>> queues, List<IndexBuilder> builders) {
        this.tree = tree;
        this.buffers = buffers;
        this.queues = queues;
        this.builders = builders;
        this.count = 0;
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
        writeFromTreeToDisk();
        writeFromQueuesToDisk();
        writeSetToDisk();
        writeStatsToDisk();

        System.out.println("Done!!!!!!!!!!!!!!!!!!!!!!!!!");
    }

    public void writeRemainingToTree() {
        for (List<Data> buffer : buffers) {
            for (Data data : buffer) {
                try {
                    if (tree.addURLandContent(data.getNewUrl() , data.getDocument())) {
                        this.count++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void writeFromTreeToDisk() {
        try {
            File file = new File("./res.txt");
            file.createNewFile();

            FileWriter writer = new FileWriter(file);

            // TODO: traverse entire directory tree to write URLs (and HTML?) into file
            // for (String url : tree) {
            //     writer.write(url);
            //     writer.write("\n");
            // }

           writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }

    // Temporary
    private void writeFromQueuesToDisk() {
        try {
            File file = new File("./res2.txt");
            file.createNewFile();
            FileWriter writer = new FileWriter(file);            
            writer.write("------------- Urls which are in the queues ------------\n");

            int count = 0;
            for (List<Seed> queue : queues) {
                for (Seed seed : queue) {
                    writer.write(String.format("Queue %d: %s\n", count, seed.getNewUrl()));                    
                }
                count++;
            }
            writer.close();

       } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeSetToDisk() {
        try {
             // Duplicate checking
            TreeSet<String> set = new TreeSet<>();
            queues.forEach(queue -> 
                queue.forEach(seed -> {
                    set.add(seed.getNewUrl());
                }));

            File file = new File("./res3.txt");
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            writer.write(String.format("Total size: %d\n", set.size()));

            for (String url : set) {
                writer.write(url);
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

            writer.write(".............Stats............\n");
            // TODO: add findURLs() method to calculate no. of URLs in IUT
            writer.write(String.format("%d new urls are found.\n", tree.size()));
            writer.write(String.format("%d urls are in queues.\n", numUrls));
            writer.write(String.format("%d urls and pages were written from buffer to tree\n", count));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
