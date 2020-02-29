package web.scraper;

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
    private void searchForItem(String filename, String key) {
        //TODO: This method will search for the key in the filename, and return the next url to go to.
    }

    private void addItemToIndex(String filename, String key, String value) {
        //TODO: This method adds a key and value pair into the file to be used in the future.
    }

}
