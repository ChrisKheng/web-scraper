package web.scraper;

import java.net.MalformedURLException;
import java.util.List;
import java.util.TreeSet;
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
    private TreeSet<String> tree;
    private List<String> buffer;
    private List<String> seeds;

    public Crawler(List<String> seeds, TreeSet<String> tree, List<String> buffer) {
        this.seeds = seeds;
        this.tree = tree;
        this.buffer = buffer;

        // Creates a new web client to visit the internet.
        this.client = new WebClient();
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);
    }

    @Override
    public void run() {
        for (String searchUrl : seeds) {
            try {
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

                writeToBuffer(urls);

                // Write urls from buffer to tree
                // To be removed later as this is supposed to be done by the Index Building Thread.
                tree.addAll(buffer);
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Write the urls to the buffer if the tree does not already contain the url given.
    public void writeToBuffer(List<String> urls) {
        urls.forEach(url -> {
            if (tree.contains(url) || buffer.contains(url)) {
                // skip the current iteration
                return;
            }
            buffer.add(url);
        });
    }
}