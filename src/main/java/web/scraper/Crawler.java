package web.scraper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
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

    public Crawler() {
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

            // Write all urls
            File file = new File("./result.txt");
            file.createNewFile();

            FileWriter writer = new FileWriter(file);
            urls.forEach(url -> {
                try {
                    writer.write(url);
                    writer.write("\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}