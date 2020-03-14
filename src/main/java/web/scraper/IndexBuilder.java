package web.scraper;

import java.util.List;
import java.util.logging.Logger;

public class IndexBuilder extends Thread {

    private IndexURLTree tree;
    private List<Pair<String, String>> buffer;
    private Logger logger = Logger.getLogger("IndexBuilder");

    public IndexBuilder(IndexURLTree tree, List<Pair<String, String>> buffer) {
        this.tree = tree;
        this.buffer = buffer;
    }

    @Override
    public void run() {
        // TODO: add some sort of check to prevent busy waiting. Probably semaphore or something.
        // Currently I just make the thread sleep.
        while (true) {
            // logger.info(Integer.toString(buffer.size()));

            if (buffer.size() > 0) {
                Pair<String, String> data = readBUL();
                writeIUT(data);
            } else {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                }
            }
        }
    }

    public Pair<String,String> readBUL() {
        return buffer.remove(0);
    }

    public void writeIUT(Pair<String, String> data) {
        tree.addURLandContent(data.head(), data.tail());
        logger.info(data.head());
    }
}
