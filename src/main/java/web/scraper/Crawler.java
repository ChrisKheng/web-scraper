package web.scraper;

import java.net.MalformedURLException;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Represents a Crawling Thread (CT) which crawls the internet and scrape urls and their html contents.
 */
public class Crawler extends Thread {
    // Not thread safe so every crawler needs to have its own client.
    private WebClient client;
    private TreeSet tree;
    private List<String> buffer;

    public Crawler(TreeSet tree, List<String> buffer) {
        this.tree = tree;
        this.buffer = buffer;

        // Creates a new web client to visit the internet.
        this.client = new WebClient();
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);
    }

    @Override
    public void run() {
        try {
            // Gets the html page.
            String searchUrl = "https://en.wikipedia.org/wiki/Main_Page";
            HtmlPage page = client.getPage(searchUrl);

            // Extracts urls in all the <a> tags.
            List<Object> anchors = (List<Object>) page.getByXPath("//a");
            List<String> urls = anchors.stream()
                .map(anchor -> {
                    String relativePath = ((HtmlAnchor) anchor).getHrefAttribute();
                    try {
                        return page.getFullyQualifiedUrl(relativePath).toString();
                    } catch (MalformedURLException e) {
                        return "";
                    }})
                .filter(url -> !url.equals(""))
                .collect(Collectors.toList());

            writeToBuffer(urls);
       } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeToBuffer(List<String> urls) {
        urls.forEach(url -> {
            if (tree.contains(url)) {
                return;
            }

            buffer.add(url);
        });
    }
}