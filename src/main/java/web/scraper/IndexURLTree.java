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

    private String ROOT_DIRECTORY = "data";
    private String HTML_FILENAME = "content.html";

    public IndexURLTree() {

    }

    // A stub method
    public long size() {
        return 0;
    }

    public void addURLandContent(String url, String content) {
        //TODO: Add URL and Content passed to this method to the tree
        String path = getPathFromUrl(url);

        System.out.println(path);

        File f = new File(path);
        if (f.exists()) {
            // file already exist
            return;
        }

        try {
            f.getParentFile().mkdirs();
            if (f.createNewFile()) {
                // file did not exist, file created
                System.out.println(path);
                writeDataToFile(f, content);
            } else {
                // file did exist, file did not create
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isDuplicate(String url) {
        //TODO: Check if URL is already stored
        String path = getPathFromUrl(url);

        System.out.print(path);

        File f = new File(path);
        return f.exists();
    }

    private void writeDataToFile(File f, String data) throws IOException {
        FileWriter fw = new FileWriter(f);
        fw.write(data);
        fw.close();
    }

    @Deprecated
    // This method is not used currently
    private String navigateDirectory(String url, boolean createMissingDirectory) {
        //TODO: This method will read the url, and return name of the file containing the url's html content
        // If createMissingDirectory is true, it will add the url into all the indexes it passes through,
        // and return a filename that the html content should be saved as.

        // URL Example --> https://bn.wikipedia.org/wiki/%E0%A7%A7%E0%A7%AC%E0%A7%A7%E0%A7%AF
        // http://www-solar.mcs.st-and.ac.uk/~clare/Lockyer/helium.html
        // http://www.academia.edu/download/47998758/adma.20100114820160812-11384-qc0oo4.pdf

        // Shall use string split for now, but can switch to guava splitter if too slow
        // Currently only need path. If need the webpage, extension etc, check the getPathFromURL method
//        String path = ROOT_DIRECTORY + getPathFromUrl(url);
//
//        File f = new File(path);
//        if (f.mkdirs()) {
//            // File don't exist
//            System.out.println("don't exist");
//        } else {
//            // File already exist
//            System.out.println("exist");
//        }

        // THE FOLLOWING IS JUST PSEUDO CODE!!! NOT WORKING YET.
//        String currentIndexFile = ""; // Add in first index file here
//        for (int i = 0; i < header.length; i++) {
//            currentIndexFile = searchForItem(currentIndexFile, header[i]);
//            if (currentIndexFile == null) {
//                if (createMissingDirectory) {
//                    // Add code to create the directory
//                } else {
//                    //exit
//                }
//                // Exit
//            }
//        }
//
//        for (int i = 0; i < webpage.length; i++) {
//            currentIndexFile = searchForItem(currentIndexFile, webpage[i]);
//            if (currentIndexFile == null) {
//                if (createMissingDirectory) {
//                    // Add code to create the directory
//                } else {
//                    //exit
//                }
//                // Exit
//            }
//        }
//
//        for (int i = 0; i < extension.length; i++) {
//            currentIndexFile = searchForItem(currentIndexFile, extension[i]);
//            if (currentIndexFile == null) {
//                if (createMissingDirectory) {
//                    // Add code to create the directory
//                } else {
//                    //exit
//                }
//                // Exit
//            }
//        }
        return null;
    }

    // This method takes in a url and breaks it down into 3 parts
    // protocol, domain, and directory
    // protocol is the http://
    // domain is abc.com
    // directory is /page1/2/3
    // This method should be the method that controls the depth of our tree. Breakdown more = more depth
    // breakdown less = less depth
    private ArrayList<String[]> breakdownUrl(String url) {

        // Split url by the ://
        // So we go from http://abc.com/a/b/c to an array with the following [http, abc.com/a/b/c]
        String[] url_first_split = url.split("://", 2);
        String[] protocol = new String[1];
        protocol[0] = url_first_split[0];

        // Split url without protocol by /
        // This separates out the website address, and the directory behind.
        // abc.com/a/b/c --> [abc.com, a/b/c]
        String[] url_second_split = url_first_split[1].split("/", 2);

        // abc.com --> [abc, com]
        String[] domain = url_second_split[0].split("\\.");

        // a/b/c --> [a, b, c]
        String[] directory = null;
        if (url_second_split.length > 1) {
            directory = url_second_split[1].split("/");
        } else {
            directory = new String[0];
        }

        return new ArrayList<>(Arrays.asList(protocol, domain, directory));
    }

    private String getPathFromUrl(String url) {
        ArrayList<String[]> breakdown = breakdownUrl(url);
        String[] protocol = breakdown.get(0);
        String[] domain = breakdown.get(1);
        String[] directory = breakdown.get(2);

        StringBuilder builder = new StringBuilder();
        builder.append(ROOT_DIRECTORY + "/");
        for (int i = 0; i < protocol.length; i++) {
            builder.append(protocol[i] + "/");
        }
        for (int i = 0; i < domain.length; i++) {
            builder.append(domain[i] + "/");
        }
        for (int i = 0; i < directory.length; i++) {
            builder.append(directory[i] + "/");
        }
        builder.append(HTML_FILENAME);
        return builder.toString();
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

    // ========================== The following code is simply used for testing ===============================================================================
//TODO: Refactor test code into test folder
    public static void main(String[] args) {
        IndexURLTree IUT = new IndexURLTree();
        // URL Example --> https://bn.wikipedia.org/wiki/%E0%A7%A7%E0%A7%AC%E0%A7%A7%E0%A7%AF
        // http://www-solar.mcs.st-and.ac.uk/~clare/Lockyer/helium.html
        // http://www.academia.edu/download/47998758/adma.20100114820160812-11384-qc0oo4.pdf
//        IUT.testBreakdownUrl("http://www-solar.mcs.st-and.ac.uk/~clare/Lockyer/helium.html");
//        IUT.testBreakdownUrl("https://bn.wikipedia.org/wiki/%E0%A7%A7%E0%A7%AC%E0%A7%A7%E0%A7%AF");
//        IUT.testBreakdownUrl("http://www.academia.edu/download/47998758/adma.20100114820160812-11384-qc0oo4.pdf");
        IUT.testAddURLandContent(
            "http://www.academia.edu.au",
            "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \n"
                + "\"http://www.w3.org/TR/html4/loose.dtd\">\n"
                + "<html>\n"
                + "<head>\n"
                + "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n"
                + "<title>$title</title>\n"
                + "</head>\n"
                + "<body>$body\n"
                + "</body>\n"
                + "</html>");
//         IUT.testIsDuplicate("http://www.academia.edu/download/47998758/adma.20100114820160812-11384-qc0oo4.pdf");
    }

    public void testBreakdownUrl(String url) {
        ArrayList<String[]> breakdown = breakdownUrl(url);
        String[] protocol = breakdown.get(0);
        String[] domain = breakdown.get(1);
        String[] directory = breakdown.get(2);
        System.out.println(url);
        System.out.println(Arrays.toString(protocol));
        System.out.println(Arrays.toString(domain));
        System.out.println(Arrays.toString(directory));
    }

    public void testNavigateDirectory(String url) {
        navigateDirectory(url, false);
    }

    public void testAddURLandContent(String url, String html) {
        final long startTime = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            addURLandContent(url + i, html);
        }
        final long endTime = System.currentTimeMillis();
        System.out.println((endTime - startTime) / 1000);
    }

    public void testIsDuplicate(String url) {
        final long startTime = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            isDuplicate(url + "FILLER" + i);
        }
        final long endTime = System.currentTimeMillis();
        System.out.println((endTime - startTime) / 1000);
    }

}
