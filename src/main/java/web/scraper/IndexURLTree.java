package web.scraper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class IndexURLTree {

    class CustomLock extends ReentrantReadWriteLock {

        public int count = 0;

        public CustomLock() {
            super();
        }
    }

    // Maybe these 2 should in the constructor instead.
    public String ROOT_DIRECTORY = "data";
    public String HTML_FILENAME = "content.html";
    public String SOURCE_FILENAME = "source.txt";
    public String RESULT_FILENAME;
    public String FILE_SEPARATOR = System.getProperty("file.separator");

    private int URL_LIMIT;
    public ConcurrentHashMap<String, CustomLock> lockMap = new ConcurrentHashMap<>();

    private long size = 0;

    public IndexURLTree() {
        this.RESULT_FILENAME = "res.txt";
        this.URL_LIMIT = 1000;
    }

    public IndexURLTree(String outputFileName, int urlLimit) {
        this.RESULT_FILENAME = outputFileName;
        this.URL_LIMIT = urlLimit;
    }

    // A stub method
    public long size() {
        return size;
    }

    /**
     * This method adds a URL and its HTTP content into the Index URL Tree
     *
     * @param d data
     */
    public boolean addURLandContent(Data d) {
        String url = d.getNewUrl();
        String content = d.getDocument();
        String source = d.getSourceUrl();
        //TODO: Add URL and Content passed to this method to the tree
        String path = getPathFromUrl(url);
        File f = new File(path);
        File srcFile = new File(f.getParent().concat(FILE_SEPARATOR + SOURCE_FILENAME));
        if (srcFile.exists()) {
            // file already exist
            return false;
        }
        String lockKey =  f.getPath();
        CustomLock lock = lockMap.compute(lockKey, (k, v) -> {
            if (v == null) {
                v = new CustomLock();
            }
            v.count += 1;
            return v;
        });

        try {
            f.getParentFile().mkdirs();
//            lock.writeLock().lock();
            if (!srcFile.exists()) {
//            if (f.createNewFile()) {
                // file did not exist, file created

                // 2 Threads writing to same file must be at this point at the same time to pass.
                if(lock.writeLock().tryLock()) {
                    try {
                        // Block yourself if u can get lock
                        synchronized (lock) {
                            // Lock is reentrant lock, therefore wait used here instead.
                            lock.wait();
                            lock.writeLock().unlock();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    // Unblock other thread if you are unable to get lock
                    synchronized (lock) {
                        lock.notifyAll();
                    }
                }



                writeDataToFile(f, d);
//                lock.writeLock().unlock();
                deleteLock(lockKey);
                synchronized (this) {
                    size++;
                }
                return true;
            } else {
                // file did exist, file did not create
//                lock.writeLock().unlock();
                deleteLock(lockKey);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void deleteLock(String lockKey) {
        lockMap.computeIfPresent(lockKey, (k,v)-> {
            v.count -= 1;
            if (v.count <= 0) {
                return null;
            }
            return v;
        });
    }


    /**
     * This method checks if the url is already stored in IUT
     *
     * @param url the url to check duplicates for
     * @return true if url exist, otherwise false
     */
    public boolean isDuplicate(String url) {
        String path = getPathFromUrl(url);

        File f = new File(path);

        boolean exist = f.exists();

        if (exist) {
            System.out.printf("Exist ............\n%s\n", path);
        }

        return exist;
    }

    public void writeResult() {
        try {

            File file = new File("." + FILE_SEPARATOR + RESULT_FILENAME);
            file.createNewFile();
            FileWriter fw = new FileWriter(file);
            BufferedWriter writer = new BufferedWriter(fw);

            Files.walk(Paths.get(this.ROOT_DIRECTORY))
                .filter((f) -> f.endsWith(SOURCE_FILENAME))
                .forEach((path) -> {
                        try {
                            String output = new String(Files.readAllBytes(path));
                            writer.append(output);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                );

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeDataToFile(File f, Data d) throws IOException {
        // Create content.html
        if (size < URL_LIMIT) {
            // Create source.txt
            File sourceF = new File(f.getParentFile().getPath() + FILE_SEPARATOR + SOURCE_FILENAME);
//        sourceF.createNewFile();
            FileWriter fw = new FileWriter(sourceF, true);
            if(d.isNewUrlDead()) {
                fw.write(d.getNewUrl() + " --> " + d.getSourceUrl() + " : " + "dead-url" + "\n");
                fw.close();
            } else {
                fw.write(d.getNewUrl() + " --> " + d.getSourceUrl() + " : " + f.getPath() + "\n");
                fw.close();

                fw = new FileWriter(f, true);
                fw.write(d.getDocument());
                fw.close();
            }
        } else {
            // Create source.txt
            File sourceF = new File(f.getParentFile().getPath() + FILE_SEPARATOR + SOURCE_FILENAME);
            //        sourceF.createNewFile();
            FileWriter fw = new FileWriter(sourceF, true);

            fw.write(d.getNewUrl() + " --> " + d.getSourceUrl() + " : " + "ignored" + "\n");
            fw.close();
        }

    }

    /**
     * This method breaks down a url into the directory path that it should exist in.
     *
     * @param url the url to breakdown into path. Should contain at least protocol and domain
     * @return a String containing the path of the url
     */
    public String getPathFromUrl(String url) {
        ArrayList<String[]> breakdown = breakdownUrl(url);
        String[] protocol = breakdown.get(0);
        String[] domain = breakdown.get(1);
        String[] directory = breakdown.get(2);

        StringBuilder builder = new StringBuilder();
        builder.append(ROOT_DIRECTORY + FILE_SEPARATOR);
        for (int i = 0; i < protocol.length; i++) {
            builder.append("pr-" + protocol[i] + FILE_SEPARATOR);
        }
        for (int i = 0; i < domain.length; i++) {
            builder.append("do-" + domain[i] + FILE_SEPARATOR);
        }
        for (int i = 0; i < directory.length; i++) {
            if (directory[i].length() >= 250) {
                String s = "dr-" + directory[i];
                while (s.length() > 250) {
                    builder.append(s.substring(0, 250) + FILE_SEPARATOR);
                    s = "ex-" + s.substring(250);
                }
                if (s.length() != 0) {
                    builder.append(s + FILE_SEPARATOR);
                }
            } else {
                builder.append("dr-" + directory[i] + FILE_SEPARATOR);
            }
        }
        builder.append(HTML_FILENAME);

        // TODO: possible duplicate URLs similar URLs but with -- and /
        // TODO: i.e. (abc.com/test/abc.html) & (abc.com/test--abc.html)
        return builder.toString();
    }

    /**
     * This method takes in a url and breaks it down into 3 parts protocol, domain, and directory
     * protocol is the http:// domain is abc.com directory is /page1/2/3 This method should be the
     * method that controls the depth of our tree. Breakdown more = more depth breakdown less = less
     * depth
     *
     * @param url the url to breakdown into path. Should contain at least protocol and domain
     * @return an arraylist containing 3 string arrays containing protocol, domain and directory of
     * the url
     */
    private ArrayList<String[]> breakdownUrl(String url) {
        url = url.trim();
        // Split url by the ://
        // So we go from http://abc.com/a/b/c to an array with the following [http, abc.com/a/b/c]
        String[] url_first_split = url.split("://", 2);
        String[] protocol = new String[1];
        protocol[0] = url_first_split[0];
        String[] domain = new String[0];
        String[] directory = new String[0];

        // Split url without protocol by /
        // This separates out the website address, and the directory behind.
        // abc.com/a/b/c --> [abc.com, a/b/c]
        if (url_first_split.length > 1 && url_first_split[1].length() > 0) {
            String[] url_second_split = url_first_split[1].split("/", 2);

            // abc.com --> [abc, com]
            domain = url_second_split[0].split("\\.");

            // a/b/c --> [a, b, c]
            directory = null;
            if (url_second_split.length > 1 && url_second_split[1].length() > 0) {
                directory = url_second_split[1].split("/");
            } else {
                directory = new String[0];
            }
        }

        return new ArrayList<>(Arrays.asList(protocol, domain, directory));
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

}
