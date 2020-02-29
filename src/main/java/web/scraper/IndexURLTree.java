package web.scraper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class IndexURLTree {

    public IndexURLTree() {

    }

    public void addURLandContent(String url, String content) {
        //TODO: Add URL and Content passed to this method to the tree
    }

    public void isDuplicate(String url) {
        //TODO: Check if URL is already stored
    }

    private String navigateDirectory(String url, boolean createMissingDirectory) {
        //TODO: This method will read the url, and return name of the file containing the url's html content
        // If createMissingDirectory is true, it will add the url into all the indexes it passes through,
        // and return a filename that the html content should be saved as.

        return null;
    }

    // File format should be in the form of key and value. Similar to the image they sent us.
    private String searchForItem(String filename, String key) {
        //TODO: This method will search for the key in the filename, and return the next url to go to.
        try {
            if (key.contains(".html")) { // reached the end of the file name
                return "";
            }
            File file = new File(filename);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains(key)) { // key-value pair exists and found
                    String[] tokens = line.split(",");
                    return tokens[1];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null; // key-value pair does not exist
    }

    private void addItemToIndex(String filename, String key, String value) {
        //TODO: This method adds a key and value pair into the file to be used in the future.
    }

}
