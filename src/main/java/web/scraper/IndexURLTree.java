package web.scraper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

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

        // URL Example --> https://bn.wikipedia.org/wiki/%E0%A7%A7%E0%A7%AC%E0%A7%A7%E0%A7%AF
        // http://www-solar.mcs.st-and.ac.uk/~clare/Lockyer/helium.html
        // http://www.academia.edu/download/47998758/adma.20100114820160812-11384-qc0oo4.pdf

        // Shall use string split for now, but can switch to guava splitter if too slow
        ArrayList<String[]> breakdown = breakdownUrl(url);
        String[] header = breakdown.get(0);
        String[] webpage = breakdown.get(1);
        String[] extension = breakdown.get(2);

        // THE FOLLOWING IS JUST PSEUDO CODE!!! NOT WORKING YET.
        String currentIndexFile = ""; // Add in first index file here
        for (int i = 0; i < header.length; i++) {
            currentIndexFile = searchForItem(currentIndexFile, header[i]);
            if (currentIndexFile == null) {
                if (createMissingDirectory) {
                    // Add code to create the directory
                } else {
                    //exit
                }
                // Exit
            }
        }

        for (int i = 0; i < webpage.length; i++) {
            currentIndexFile = searchForItem(currentIndexFile, webpage[i]);
            if (currentIndexFile == null) {
                if (createMissingDirectory) {
                    // Add code to create the directory
                } else {
                    //exit
                }
                // Exit
            }
        }

        for (int i = 0; i < extension.length; i++) {
            currentIndexFile = searchForItem(currentIndexFile, extension[i]);
            if (currentIndexFile == null) {
                if (createMissingDirectory) {
                    // Add code to create the directory
                } else {
                    //exit
                }
                // Exit
            }
        }
        return null;
    }

    // This method takes in a url and breaks it down into 3 parts
    // Header, webpage, and extension
    // Header is the http://
    // webpage is abc.com
    // extension is /page1/2/3
    // This method should be the method that controls the depth of our tree. Breakdown more = more depth
    // breakdown less = less depth
    private ArrayList<String[]> breakdownUrl(String url) {

        // Split url by the ://
        // So we go from http://abc.com/a/b/c to an array with the following [http, abc.com/a/b/c]
        String[] url_first_split = url.split("://", 2);
        String[] header = new String[1];
        header[0] = url_first_split[0];

        // Split url without header by /
        // This separates out the website address, and the extension behind.
        // abc.com/a/b/c --> [abc.com, a/b/c]
        String[] url_second_split = url_first_split[1].split("/", 2);

        // abc.com --> [abc, com]
        String[] webpage = url_second_split[0].split("\\.");

        // a/b/c --> [a, b, c]
        String[] extension = url_second_split[1].split("/");

        return new ArrayList<>(Arrays.asList(header, webpage, extension));
    }

    // File format should be in the form of key and value. Similar to the image they sent us.
    private String searchForItem(String filename, String key) {
        //TODO: This method will search for the key in the filename, and return the next url to go to.
        try {
            File file = new File(filename);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            // TODO: Is looping the best way to do this? How expensive is string comparison
            // Could it be faster to put everything in hashmap and just search it?
            // Or binary search after putting things in a list?
            // Alternate Solution 1: Split lines by ',' and put key-value pair into HashSet and check for key
            // Alternate Solution 2: Add lines into list, sort and do binary search for key
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
        String pair = key + "," + value;
        try {
            //TODO: a way to sort the index file for faster search
            FileWriter fw = new FileWriter(filename, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.append(pair);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        IndexURLTree IUT = new IndexURLTree();
        // URL Example --> https://bn.wikipedia.org/wiki/%E0%A7%A7%E0%A7%AC%E0%A7%A7%E0%A7%AF
        // http://www-solar.mcs.st-and.ac.uk/~clare/Lockyer/helium.html
        // http://www.academia.edu/download/47998758/adma.20100114820160812-11384-qc0oo4.pdf
        IUT.testBreakdownUrl("http://www-solar.mcs.st-and.ac.uk/~clare/Lockyer/helium.html");
        IUT.testBreakdownUrl("https://bn.wikipedia.org/wiki/%E0%A7%A7%E0%A7%AC%E0%A7%A7%E0%A7%AF");
        IUT.testBreakdownUrl("http://www.academia.edu/download/47998758/adma.20100114820160812-11384-qc0oo4.pdf");
    }

    public void testBreakdownUrl(String url) {
        ArrayList<String[]> breakdown = breakdownUrl(url);
        String[] header = breakdown.get(0);
        String[] webpage = breakdown.get(1);
        String[] extension = breakdown.get(2);
        System.out.println(url);
        System.out.println(Arrays.toString(header));
        System.out.println(Arrays.toString(webpage));
        System.out.println(Arrays.toString(extension));
    }

}
