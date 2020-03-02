package web.scraper;

import java.util.List;
import java.util.TreeSet;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.net.MalformedURLException;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Represents a Crawling Thread (CT) which crawls the internet and scrape urls
 * and their html contents.
 */
public class Crawler extends Thread {
    // Not thread safe so every crawler needs to have its own client.
    // seeds is the portion of the original urls assigned to a crawler thread.
    private WebClient client;
    private Logger logger;
    private ConcurrentLinkedQueue<String> queue;
    private TreeSet<String> tree;
    private List<String> buffer;
    private String threadName;

    public Crawler(ConcurrentLinkedQueue<String> seeds, TreeSet<String> tree, List<String> buffer) {
        this.queue = seeds; 
        this.tree = tree;
        this.buffer = buffer;
        this.logger = Logger.getLogger("Crawler thread");

        // Creates a new web client to visit the internet.
        this.client = new WebClient();
        client.getOptions().setTimeout(10000); // 10s
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);
    }

    @Override
    public void run() {
        // Must put this here cuz if put in constructor, then currentThread is the thread that initialises the crawler
        // , not the real crawler thread itself.
        this.threadName = String.format("Thread %d", Thread.currentThread().getId());

        logger.info(String.format("%s receive %d initial urls", threadName, queue.size()));
        int counter = 0;

        // The crawler thread will keep running as long as there are still urls for it to crawl.
        // You can change the while loop condition if you want the crawler thread to terminate
        // after a certain number of iterations using the counter variable.
        while (!queue.isEmpty()) {
            counter++;
            logger.info(String.format("%s curr iteration %d", threadName, counter));

            try {
                // Retrieves and removes head of queue
                String searchUrl = queue.poll();

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

    // Returns all the urls in the html page given.
    // The urls are the ones enclosed in <a> tag.
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
            // tree.add(url) is temporary for now cuz should be IBT that is writing to the tree
            if (tree.add(url)) {
                buffer.add(url);
                queue.add(url);
                count++;
            }
        }

        logger.info(String.format("%s %d urls are new", threadName, count));
    }
}