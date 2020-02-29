package web.scraper;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Logger;

public class IndexBuilder extends Thread {

    private TreeSet<String> tree;
    private LinkedList<String> buffer;
    private Logger logger = Logger.getLogger("IndexBuilder");

    // I put everything for now, but probably only need buffer and IUT.
    public IndexBuilder(TreeSet<String> tree, List<String> buffer) {
        this.tree = tree;
        this.buffer = (LinkedList<String>) buffer;
    }

    @Override
    public void run() {
        // TODO: add some sort of check to prevent busy waiting. Probably semaphore or something.
        // Currently I just make the thread sleep.
        while (true) {
            logger.info(Integer.toString(buffer.size()));

            if (buffer.size() > 0) {
                String data = readBUL();
                writeIUT(data);
            } else {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                }
            }
        }
    }

    public String readBUL() {
        String data = buffer.removeFirst();
        return data;
    }

    public void writeIUT(String data) {
        //TODO: Write code
        // should be simply calling the write method of IndexURLTree
        logger.info(data);

    }

}