package web.scraper;

import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.logging.Logger;

public class IndexBuilder extends CustomThread {

    private IndexURLTree tree;
    private List<Data> buffer;
    private Semaphore crawlerSemaphore;
    private Semaphore builderSempahore;
    private Logger logger = Logger.getLogger("IndexBuilder");
    private long count; // For keep tracking the number of urls added to the tree (temporal implementation)

    public IndexBuilder(IndexURLTree tree, List<Data> buffer, Semaphore crawlerSemaphore,
        Semaphore builderSemaphore) {
        this.tree = tree;
        this.buffer = buffer;
        this.crawlerSemaphore = crawlerSemaphore;
        this.builderSempahore = builderSemaphore;
        this.count = 0;
    }

    @Override
    public void run() {
        super.setThreadName(String.format("Builder %d", Thread.currentThread().getId()));

        while (!this.isInterrupted()) {
            try {
                builderSempahore.acquire(App.BUFFER_SIZE);
                logger.info(getFormattedMessage("Entering critical section............."));
                logger.info(getFormattedMessage(
                    String.format("buffer size is %d .........", buffer.size())));

                while (!buffer.isEmpty()) {
                    Data data = buffer.remove(0);
                    writeIUT(data);
                }

                crawlerSemaphore.release(App.BUFFER_SIZE);
                logger.info(getFormattedMessage("Leaving critical section............."));
            } catch (InterruptedException e) {
                logger.info(getFormattedMessage("Interrupted.................."));
                break;
            }
        }
    }

    public void writeIUT(Data data) {
        if (tree.addURLandContent(data)) {
            this.count++;
        }

        logger.info(getFormattedMessage("write...................."));
        logger.info(data.getNewUrl());
    }

    public long getCount() {
        return count;
    }
}
