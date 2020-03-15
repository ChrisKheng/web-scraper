package web.scraper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Cleaner extends Thread {
    private IndexURLTree tree;
    private List<List<Data>> buffers;
    private List<List<Seed>> queues;

    public Cleaner(IndexURLTree tree, List<List<Data>> buffers, List<List<Seed>> queues) {
        this.tree = tree;
        this.buffers = buffers;
        this.queues = queues;
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
        writeStatsToDisk();

        System.out.println("Done!!!!!!!!!!!!!!!!!!!!!!!!!");
    }

    public void writeRemainingToTree() {
        buffers.forEach(buffer -> {
            buffer.forEach(data -> tree.addURLandContent(data.getSourceUrl() , data.getDocument()));
        });
    }

    public void writeFromTreeToDisk() {
        try {
            File file = new File("./result.txt");
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
            File file = new File("./result2.txt");
            file.createNewFile();
            FileWriter writer = new FileWriter(file);            
            writer.write("------------- Urls which are in the queues ------------\n");

            for (List<Seed> queue : queues) {
                for (Seed seed : queue) {
                    writer.write(String.format("%s\n", seed.getNewUrl()));                    
                }
            }

            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeStatsToDisk() {
        try {
            File file = new File("./statistics.txt");
            file.createNewFile();
            FileWriter writer = new FileWriter(file);

            long numUrls = queues.stream().mapToInt(queue -> queue.size()).sum();

            writer.write(".............Stats............\n");
            // TODO: add findURLs() method to calculate no. of URLs in IUT
            writer.write(String.format("%d new urls are found.\n", tree.size()));
            writer.write(String.format("%d urls are in queues.\n", numUrls));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
