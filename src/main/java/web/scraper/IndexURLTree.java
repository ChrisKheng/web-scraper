package web.scraper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class IndexURLTree {

    // Maybe these 2 should in the constructor instead.
    public String ROOT_DIRECTORY = "data";
    public String HTML_FILENAME = "content.html";
    public String HTML_EXTENSION = ".html";

    private ConcurrentHashMap<String, ReadWriteLock> lockMap = new ConcurrentHashMap<String, ReadWriteLock>();

    private long size = 0;
    private long counter = 0;

    public IndexURLTree() {

    }

    // A stub method
    public long size() {
        return size;
    }

    /**
     * This method adds a URL and its HTTP content into the Indexed URL Tree
     *
     * @param data    contains current URL, source URL and HTML content
     */
    public boolean addURLandContent(Data data) {
        //TODO: Add URL and Content passed to this method to the tree
        String url = data.getNewUrl();
        String source = data.getSourceUrl();
        String document = data.getDocument();

        String[] result = getPathAndKeyFromUrl(url);
        String path = result[0]; // indexed txt file
        String directory = result[1]; // key of file
        // TODO: handle (directory == "")

        File f = new File(path);
        try {
            f.getParentFile().mkdirs();
            if (!f.exists()) {
                f.createNewFile();
            }

            // Creation of Normal & Shortened URL Folders
            File norm = new File(f.getParent() + "/normal");
            File shorten = new File(f.getParent() + "/shorten");
            norm.mkdir();
            shorten.mkdir();

            ReadWriteLock lock = lockMap.get(f.getPath());
            if(lock == null) {
                lock = new ReentrantReadWriteLock();
                ReadWriteLock existingLock = lockMap.putIfAbsent(f.getPath(), lock);

                if (existingLock != null) {
                    lock = existingLock;
                }
            }
            lock.writeLock().lock();
            //TODO: handle concurrency of reading and writing of index file
            if (searchForItem(f.getPath(), directory) == null) {
                String mod_directory = directory.replace("/","--");
                String value = norm.getPath() + "/" + mod_directory + HTML_EXTENSION;
                File newFile = new File(value);
                if (mod_directory.length() > 200 || newFile.exists()) {
                    value = shorten.getPath() + "/" + Long.toString(counter++) + HTML_EXTENSION;
                    newFile = new File(value);
                }
                addItemToIndex(f.getPath(), directory, value, source);
                lock.writeLock().unlock();
                /** atomic operation should end here */
                writeDataToFile(newFile, document);
                return true;
            } else {
                lock.writeLock().unlock();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * This method checks if the url is already stored in IUT
     *
     * @param url the url to check duplicates for
     * @return true if url exist, otherwise false
     */
    public boolean isDuplicate(String url) {
        //TODO: Check if URL is already stored
        String[] result = getPathAndKeyFromUrl(url);
        String path = result[0];
        String directory = result[1];

        File f = new File(path);
        if (!f.exists()) {
            return false;
        }

        boolean exist = true;
        if (searchForItem(f.getPath(), directory) == null) {
            exist = false;
        }

        if (exist) {
            System.out.printf("Exist ............\n%s\n", path);
        }
        
        return exist;
    }

    private void writeDataToFile(File f, String data) throws IOException {
        FileWriter fw = new FileWriter(f);
        fw.write(data);
        fw.close();
    }

    /**
     * This method takes in a url and breaks it down into 3 parts protocol, domain, and directory
     * protocol is the http://
     * domain is abc.com
     * directory is /page1/2/3
     * This method should be the method that controls the depth of our tree.
     * Breakdown more = more depth breakdown less = less depth
     *
     * @param url the url to breakdown into path. Should contain at least protocol and domain
     * @return an arraylist containing 3 string arrays containing protocol, domain and directory of
     * the url
     */
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

    /**
     * This method breaks down a url into the directory path that it should exist in.
     *
     * @param url the url to breakdown into path. Should contain at least protocol and domain
     * @return a String containing the path of the url
     */
    private String[] getPathAndKeyFromUrl(String url) {
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
        if (directory[0].length() > 0) {
            builder.append(directory[0].charAt(0) + ".txt");
        } else {
            builder.append("source.txt");
        }

        // TODO: possible duplicate URLs similar URLs but with -- and /
        // TODO: i.e. (abc.com/test/abc.html) & (abc.com/test--abc.html)
        return new String[]{builder.toString(), String.join("/", directory)};
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
//            HashMap<String, String> indexMap = new HashMap<>();
            while ((line = br.readLine()) != null) {
//                String[] tokens = line.split(",",2);
                String existingKey = line.substring(line.indexOf(","));
                if (existingKey.contains(key)) {
                    br.close();
                    return line;
                }
//                indexMap.put(tokens[0], tokens[1]);
            }
            br.close();
//            return indexMap.get(key);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null; // key-value pair does not exist
    }

    private void addItemToIndex(String filename, String key, String value, String source) {
        //TODO: This method adds a key and value pair into the file to be used in the future.
        String data = key + "," + value + "," + source;
        try {
            //TODO: a way to sort the index file for faster search
            FileWriter fw = new FileWriter(filename, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.append(data);
            bw.newLine();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
