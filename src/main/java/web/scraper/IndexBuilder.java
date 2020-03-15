package web.scraper;

import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.logging.Logger;

public class IndexBuilder extends Thread {
    private IndexURLTree tree;
    private List<Data> buffer;
    private Semaphore crawlerSemaphore;
    private Semaphore builderSempahore;
    private Logger logger = Logger.getLogger("IndexBuilder");
    private String name;

    public IndexBuilder(IndexURLTree tree, List<Data> buffer, Semaphore crawlerSemaphore, Semaphore builderSemaphore) {
        this.tree = tree;
        this.buffer = buffer;
        this.crawlerSemaphore = crawlerSemaphore;
        this.builderSempahore = builderSemaphore;
    }

    @Override
    public void run() {
        // TODO: add some sort of check to prevent busy waiting. Probably semaphore or something.
        // Currently I just make the thread sleep.
        this.name = String.format("Index Builder %d", Thread.currentThread().getId());

        while (true) {
            try {
                builderSempahore.acquire(App.BUFFER_SIZE);
                logger.info(String.format("%s %s", this.name, "Entering critical section............."));
                logger.info(String.format("%s buffer size is %d .........", this.name, buffer.size()));

                while (!buffer.isEmpty()) {
                    Data data = buffer.remove(0);
                    writeIUT(data);        
                }
                
                crawlerSemaphore.release(App.BUFFER_SIZE);
                logger.info(String.format("%s %s", this.name, "Leaving critical section............."));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void writeIUT(Data data) {
        tree.addURLandContent(data.getNewUrl(), data.getDocument());

        logger.info("IndexBuilder write....................");
        logger.info(data.getNewUrl());
    }
}
