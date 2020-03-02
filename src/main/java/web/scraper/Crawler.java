package web.scraper;

import java.net.MalformedURLException;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Represents a Crawling Thread (CT) which crawls the internet and scrape urls
 * and their html contents.
 */
public class Crawler implements Runnable {
    // Not thread safe so every crawler needs to have its own client.
    // seeds is the portion of the original urls assigned to a crawler thread.
    private WebClient client;
    private TreeSet<String> tree;
    private List<String> buffer;
    private ConcurrentLinkedQueue<String> queue;
    private long end;

    public Crawler(List<String> seeds, TreeSet<String> tree, List<String> buffer) {
        this.queue = new ConcurrentLinkedQueue(seeds);
        this.tree = tree;
        this.buffer = buffer;

        // Creates a new web client to visit the internet.
        this.client = new WebClient();
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);
        
        long start = System.currentTimeMillis();
        this.end = start + 30*1000; // 30 seconds run time
    }

    @Override
    public void run() {
        while(!queue.isEmpty() && System.currentTimeMillis() < end) {
            try {
                // Retrives and removes head of queue
                String searchUrl = queue.poll();
                
                // Gets the html page.
                HtmlPage page = client.getPage(searchUrl);

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

                processUrls(urls);

                // Write urls from buffer to tree
                // To be removed later as this is supposed to be done by the Index Building Thread.
                //tree.addAll(buffer);
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Write the urls to the buffer if the tree does not already contain the url given.
    public synchronized void processUrls(List<String> urls) {
        urls.forEach(url -> {
            if (tree.contains(url) || !validUrl(url)) {
                // skip the current iteration
                return;
            }
            buffer.add(url);
            tree.add(url); // have this here for now
            queue.add(url);
        });
    }
    
    // Checks if the url is a http link
    // Removes other links like javascript and mailto
    public boolean validUrl(String url) {
        if (url.substring(0,4).equals("http")) return true;
        else return false;
    }
}