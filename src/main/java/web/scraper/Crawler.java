package web.scraper;

import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLProtocolException;

import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Represents a Crawling Thread (CT) which crawls the internet and scrape urls
 * and their html contents.
 */
public class Crawler extends CustomThread {
    // Not thread safe so every crawler needs to have its own client.
    // seeds is the portion of the original urls assigned to a crawler thread.
    private static final Object lock1 = new Object();
    private final Pattern rootPattern = Pattern.compile("[a-z]+:\\/\\/(?:\\w+\\.?)*\\/");
    private WebClient client;
    private Logger logger;
    private List<Seed> queue;
    private IndexURLTree tree;
    private List<Data> buffer;
    private Semaphore crawlerSemaphore;
    private Semaphore builderSemaphore;

    public Crawler(List<Seed> seeds, IndexURLTree tree, List<Data> buffer, Semaphore crawlerSemaphore,
            Semaphore builderSemaphore) {
        this.queue = seeds;
        this.tree = tree;
        this.buffer = buffer;
        this.crawlerSemaphore = crawlerSemaphore;
        this.builderSemaphore = builderSemaphore;

        this.logger = Logger.getLogger("Crawler thread");

        // Creates a new web client to visit the internet.
        this.client = new WebClient();
        client.getOptions().setTimeout(10000); // 10s
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);
    }

    @Override
    public void run() {
        super.setThreadName(String.format("Crawler %d", Thread.currentThread().getId()));

        logger.info(getFormattedMessage(String.format("receive %d initial urls", queue.size())));
        int counter = 0;

        // The crawler thread will keep running as long as there are still urls for it
        // to crawl.
        // You can change the while loop condition if you want the crawler thread to
        // terminate
        // after a certain number of iterations using the counter variable.
        while (!queue.isEmpty() && !this.isInterrupted()) {
            counter++;
            logger.info(getFormattedMessage(String.format("curr iteration %d", counter)));

            // Get the url, visit and retrieve the html page of the url
            Seed seed = queue.remove(0);
            String sourceUrl = seed.getSourceUrl();
            String searchUrl = seed.getNewUrl();

            try {
                try {
                    // Attempt to visit the url
                    HtmlPage page = client.getPage(searchUrl);
                    List<String> urls = getUrls(page);
                    // logger.info(getFormattedMessage(String.format("found %d urls", urls.size())));
                    processUrls(searchUrl, urls);

                    Data newData = new Data(sourceUrl, searchUrl, page.asXml());
                    addToBuffer(newData);
                } catch (FailingHttpStatusCodeException e) {
                    logger.warning(getFormattedMessage(e.getMessage()));
                    logger.warning(getFormattedMessage(searchUrl));
                    
                    // Add dead url into buffer
                    Data newData = new Data(sourceUrl, searchUrl);
                    addToBuffer(newData);

                    // logger.info(getFormattedMessage("Going to handle 404........."));
                    handle404Issue(searchUrl);
                }
           } catch (UnknownHostException | ConnectException | SSLHandshakeException | SSLProtocolException
                    | MalformedURLException e) {
                logger.warning(getFormattedMessage(e.getMessage()));
            } catch (InterruptedException e) {
                e.printStackTrace();
                logger.info(getFormattedMessage("Interrrupted......................."));
                break;
            } catch (Exception e) {
                logger.warning(getFormattedMessage(String.format("exception: url %s", searchUrl)));
                logger.warning(getFormattedMessage(String.format("exception: %s", e.getMessage())));
                e.printStackTrace();
            }
        }

        this.client.close();
        logger.info(getFormattedMessage("exiting.............."));
    }

    // Add the data object to the buffer if the sourceUrl is not empty
    public void addToBuffer(Data newData) throws InterruptedException {
        if (newData.getSourceUrl().isEmpty()) {
            return;
        }        

        crawlerSemaphore.acquire();
        // logger.info(getFormattedMessage(String.format("entering critical section.............")));

        buffer.add(newData);

        builderSemaphore.release();
        // logger.info(getFormattedMessage(String.format("Leaving critical section.............")));
    }

    // Extract the root of the url and add it to the queue instead
    private void handle404Issue(String searchUrl) {
        // logger.info(getFormattedMessage("Now in handle404................"));

        synchronized (lock1) {
            Matcher m = rootPattern.matcher(searchUrl);

            // If the url does not match the rootPattern
            if (!m.find()) {
                // logger.info("Doesn't match url pattern");
                return;
            }

            // logger.info(getFormattedMessage("After matching................"));

            String rootUrl = m.group(0);

            // To prevent using an invalid 404 url again
            if (rootUrl.equals(searchUrl)) {
                // logger.info(getFormattedMessage("Skipping as rootUrl is the same as original"));
                return;
            }

            Seed newSeed = new Seed(searchUrl, rootUrl);

            // logger.info("Entering if else statement");
            // Only the new url is compared in the seed (i.e. sourceUrl is not compared)
            if (this.queue.contains(newSeed)) {
                // logger.info(getFormattedMessage(String.format("skipping as queue already has %s", rootUrl)));
            } else {
                this.queue.add(newSeed);
                // logger.info(getFormattedMessage(String.format("extract root url instead %s", rootUrl)));
            }
        }

        // logger.info(getFormattedMessage("Getting out from handle404................"));
    }

    // Returns all the urls in the html page given.
    // The urls are the ones enclosed in <a> tag.
    private List<String> getUrls(HtmlPage page) {
        // logger.info(getFormattedMessage("extracting urls..."));

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

    // Write the new urls found to the queue if the tree does not already contain
    // the url given.
    private void processUrls(String searchUrl, List<String> urls) {
        // logger.info(getFormattedMessage("processing urls..."));

        // int count = 0;

        for (String url : urls) {
            // Add the url if it is a valid url and the tree does not already contain the
            // url, which is indicated
            // by tree.add(url) as false is returned if the tree already has the url.
            // This order of checking may be better as it won't need to touch the tree if
            // the url is not even valid.
            // Remember should be if it is NOT duplicate
            Seed seed = new Seed(searchUrl, url);
            if (isValidUrl(url) && !tree.isDuplicate(url) && !queue.contains(seed)) {
                queue.add(seed);
                // count++;
            }
        }

        // logger.info(getFormattedMessage(String.format("%d urls are new", count)));
    }

    // Checks if the url is a http link
    // Removes other links like javascript and mailto
    private boolean isValidUrl(String url) {
        return url.substring(0, 4).equals("http")
            && !url.contains(" ") && !url.contains("\n") && !url.contains("\r");
    }
}
