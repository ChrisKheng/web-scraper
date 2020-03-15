package web.scraper;

import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.net.MalformedURLException;

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
    private static final Pattern rootPattern = Pattern.compile("[a-z]+:\\/\\/(?:\\w+\\.?)*\\/");
    private WebClient client;
    private Logger logger;
    private List<String> queue;
    private IndexURLTree tree;
    private List<Pair<String, String>> buffer;
    private String threadName;

    public Crawler(List<String> seeds, IndexURLTree tree, List<Pair<String, String>> buffer) {
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
        // Must put this here cuz if put in constructor, then currentThread is the
        // thread that initialises the crawler
        // , not the real crawler thread itself.
        this.threadName = String.format("Thread %d", Thread.currentThread().getId());

        logger.info(String.format("%s receive %d initial urls", threadName, queue.size()));
        int counter = 0;

        // The crawler thread will keep running as long as there are still urls for it
        // to crawl.
        // You can change the while loop condition if you want the crawler thread to
        // terminate
        // after a certain number of iterations using the counter variable.
        while (!queue.isEmpty()) {
            counter++;
            logger.info(String.format("%s curr iteration %d", threadName, counter));

            // Get the url, visit and retrieve the html page of the url
            String searchUrl = queue.remove(0);            
            try {
                HtmlPage page = client.getPage(searchUrl);
                Pair<String, String> pair = new Pair<>(searchUrl, page.asXml());

                // Synchronises accesses between the crawler threads which share the same buffer
                synchronized (buffer) {
                    buffer.add(pair);
                }

                List<String> urls = getUrls(page);
                logger.info(String.format("%s found %d urls", threadName, urls.size()));

                processUrls(urls);
            } catch (Exception e) {                
                logger.warning(String.format("%s %s", threadName, e.getMessage()));

                // Extract the root of the url and add it to the queue instead
                Matcher m = rootPattern.matcher(searchUrl);
                if (m.find()) {
                    String rootUrl = m.group(0);

                    if (queue.contains(rootUrl)) {
                        String message = String.format("skipping as queue already has %s", rootUrl);
                        logger.info(String.format("%s %s", threadName, message));
                        continue;
                    }                    

                    queue.add(rootUrl);
                    String message = String.format("extract root url instead %s", rootUrl);
                    logger.info(String.format("%s %s", threadName, message));
                }                
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

    // Write the new urls found to the queue if the tree does not already contain the url given.
    public void processUrls(List<String> urls) {
        logger.info(String.format("%s processing urls...", threadName));

        int count = 0;

        // TODO - Check if the url also exists in all other crawler threads or not before adding to the 
        // queue (here the entire for loop may need to be synchronised, check carefully)
        for (String url : urls) {
            synchronized (Crawler.class) {
                // Add the url if it is a valid url and the tree does not already contain the url, which is indicated
                // by tree.add(url) as false is returned if the tree already has the url.
                // This order of checking may be better as it won't need to touch the tree if the url is not even valid.

                try {
                if (isValidUrl(url) && tree.isDuplicate(url)) {
                    queue.add(url);
                    count++;
                }
                } catch (Exception e) {
                   logger.warning(String.format("%s index out of bound %s", threadName, url));
                   e.printStackTrace();
                }
           }
        }

        logger.info(String.format("%s %d urls are new", threadName, count));
    }
    
    // Checks if the url is a http link
    // Removes other links like javascript and mailto
    public boolean isValidUrl(String url) {
        if (url.substring(0,4).equals("http")) return true;
        else return false;
    }
}
