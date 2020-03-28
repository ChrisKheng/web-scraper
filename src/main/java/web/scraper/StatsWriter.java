package web.scraper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class StatsWriter extends Thread {
    
    private IndexURLTree tree;
    private List<List<Seed>> queues;
    private List<List<Data>> buffers;

    public StatsWriter (IndexURLTree tree, List<List<Seed>> queues, List<List<Data>> buffers) {
            this.tree = tree;  
            this.queues = queues;
            this.buffers = buffers;
    }
    
    @Override
    public void run() {
        try {
            File file = new File("./statistics.txt");
            file.createNewFile();

            FileWriter writer = new FileWriter(file);
            writer.write(".............Stats............\n");
            
            while (true && !this.isInterrupted()) {
                try{
                    // sleeps thread for 1 hour (currently 1 second for testing)
                    long pastSize = tree.size();
                    long oldQueueUrls = queues.stream().mapToInt(queue -> queue.size()).sum();
                    long oldBufferSize = buffers.stream().mapToInt(buffer -> buffer.size()).sum();
                    Thread.sleep(1000);//*60*60);
                    long newSize = tree.size();
                    long newQueueUrls = queues.stream().mapToInt(queue -> queue.size()).sum();
                    long newBufferSize = buffers.stream().mapToInt(buffer -> buffer.size()).sum();
                    
                    writer.write(String.format("%d urls have been added to the tree in the past hour\n", newSize - pastSize));
                    writer.write(String.format("%d more urls added to queues than removed in the past hour\n", newQueueUrls - oldQueueUrls));
                    writer.write(String.format("%d more urls added to buffers than removed in the past hour\n", newBufferSize - oldBufferSize));
                } catch(Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
