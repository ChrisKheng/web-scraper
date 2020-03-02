package web.scraper;

import java.net.MalformedURLException;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Represents a Crawling Thread (CT) which crawls the internet and scrape urls
 * and their html contents.
 */
public class Crawler extends Thread {
    // Not thread safe so every crawler needs to have its own client.
    // seeds is the portion of the original urls assigned to a crawler thread.
    private WebClient client;
    private Logger logger;
    private List<String> queue;
    private TreeSet<String> tree;
    private List<String> buffer;
    private long end;
    private String threadName;

    public Crawler(List<String> seeds, TreeSet<String> tree, List<String> buffer) {
        this.queue = new LinkedList<>(seeds);
        this.tree = tree;
        this.buffer = buffer;
        this.logger = Logger.getLogger(String.format("My thread %d", Thread.currentThread().getId()));

        // Creates a new web client to visit the internet.
        this.client = new WebClient();

        client.getOptions().setTimeout(10000);
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);

        long start = System.currentTimeMillis();
        this.end = start + 30 * 1000; // 30 seconds run time
    }

    @Override
    public void run() {
        this.threadName = String.format("Thread %d", Thread.currentThread().getId());

        logger.info(String.format("%s receive %d initial urls", threadName, queue.size()));
        int counter = 0;

        while (counter < 100) {
            counter++;
            logger.info(String.format("%s curr iteration %d", threadName, counter));

            try {
                // Retrives and removes head of queue
                String searchUrl = queue.remove(0);

                // Gets the html page and the urls in it.
                HtmlPage page = client.getPage(searchUrl);
                List<String> urls = getUrls(page);
                logger.info(String.format("%s found %d urls", threadName, urls.size()));

                processUrls(urls);
            } catch (Exception e) {
                logger.warning(String.format("%s %s", threadName, e.getMessage()));
            }
        }

        logger.info(String.format("%s exiting......", threadName));
    }

    public List<String> getUrls(HtmlPage page) {
        logger.info(String.format("%s extracting urls...", threadName));

        // Extracts urls in all the <a> tags.
        List<Object> anchors = (List<Object>) page.getByXPath("//a");
        List<String> urls = anchors.stream().map(anchor -> {
            String relativePath = ((HtmlAnchor) anchor).getHrefAttribute();
            try {
                // Returns the absolute url of the relative url given.
                return page.getFullyQualifiedUrl(relativePath).toString();
            } catch (MalformedURLException e) {
                return "";
            }
        }).filter(url -> !url.equals("")).collect(Collectors.toList());

        return urls;
    }

    // Write the urls to the buffer if the tree does not already contain the url
    // given.
    public void processUrls(List<String> urls) {
        logger.info(String.format("%s processing urls...", threadName));

        int count = 0;

        for (String url : urls) {
            if (tree.contains(url)) {
                continue;
            }
            buffer.add(url);
            tree.add(url); // have this here for now
            queue.add(url);
            count++;
        }

        logger.info(String.format("%s %d urls are new", threadName, count));
    }
}