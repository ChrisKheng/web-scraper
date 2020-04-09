package web.scraper;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class IndexURLTreeTest {

    private IndexURLTree IUT;

    @Before
    public void createIUT() {
        IUT = new IndexURLTree();
        IUT.ROOT_DIRECTORY = "test_data";
    }

    @After
    public void cleanIUTDirectory() {
        try {
            Path path = Paths.get(IUT.ROOT_DIRECTORY);
            if (Files.exists(path)) {
                Files.walk(path)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
            }
        } catch (IOException e) {
            System.out.println("Delete directory failed. Check if path is correct.");
            e.printStackTrace();
        }
    }

    @Test
    public void getPathFromUrl() {

        String url;
        String path;

        // ########## The following 3 cases shouldn't be possible. But included just in case. ######
        url = "http";
        path = IUT.getPathFromUrl(url);
        assertEquals(path, IUT.ROOT_DIRECTORY + "/pr-http/" + IUT.HTML_FILENAME);

        url = "https";
        path = IUT.getPathFromUrl(url);
        assertEquals(path, IUT.ROOT_DIRECTORY + "/pr-https/" + IUT.HTML_FILENAME);

        url = "https:// ";
        path = IUT.getPathFromUrl(url);
        assertEquals(path, IUT.ROOT_DIRECTORY + "/pr-https/" + IUT.HTML_FILENAME);

        // #################### urls with protocol and domain ######################################

        url = "http://abc";
        path = IUT.getPathFromUrl(url);
        assertEquals(path, IUT.ROOT_DIRECTORY + "/pr-http/do-abc/" + IUT.HTML_FILENAME);

        // url with slash but no directory
        url = "http://abc/";
        path = IUT.getPathFromUrl(url);
        assertEquals(path, IUT.ROOT_DIRECTORY + "/pr-http/do-abc/" + IUT.HTML_FILENAME);

        // url with extra space at the end
        url = "http://abc/ ";
        path = IUT.getPathFromUrl(url);
        assertEquals(path, IUT.ROOT_DIRECTORY + "/pr-http/do-abc/" + IUT.HTML_FILENAME);

        url = "http://abc.com.sg";
        path = IUT.getPathFromUrl(url);
        assertEquals(path,
            IUT.ROOT_DIRECTORY + "/pr-http/do-abc/do-com/do-sg/" + IUT.HTML_FILENAME);

        // #################### urls with protocol, domain and directory ##########################
        url = "http://abc.com/test-directory";
        path = IUT.getPathFromUrl(url);
        assertEquals(path,
            IUT.ROOT_DIRECTORY + "/pr-http/do-abc/do-com/dr-test-directory/" + IUT.HTML_FILENAME);

        url = "http://abc.com/test-directory/";
        path = IUT.getPathFromUrl(url);
        assertEquals(path,
            IUT.ROOT_DIRECTORY + "/pr-http/do-abc/do-com/dr-test-directory/" + IUT.HTML_FILENAME);

        url = "http://abc.com/test-directory/ ";
        path = IUT.getPathFromUrl(url);
        assertEquals(path,
            IUT.ROOT_DIRECTORY + "/pr-http/do-abc/do-com/dr-test-directory/" + IUT.HTML_FILENAME);

        url = "http://abc.com/test-directory/deep/deeper";
        path = IUT.getPathFromUrl(url);
        assertEquals(path,
            IUT.ROOT_DIRECTORY + "/pr-http/do-abc/do-com/dr-test-directory/dr-deep/dr-deeper/"
                + IUT.HTML_FILENAME);
    }

    @Test
    public void addURLandContent() {
        try {
            // url with protocol and domain
            String url = "https://www.jetbrains.com";
            String path = IUT.getPathFromUrl(url);
            String content = "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \n"
                + "\"http://www.w3.org/TR/html4/loose.dtd\">\n"
                + "<html>\n"
                + "<head>\n"
                + "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n"
                + "<title>$title</title>\n"
                + "</head>\n"
                + "<body>$body\n"
                + "</body>\n"
                + "</html>";
            Data d = new Data("http://www.source2.com", url, content);
            IUT.addURLandContent(d);
            File f = getFile(path);
            String fContent = new String(Files.readAllBytes(Paths.get(path)));
            assertEquals(content, fContent);

            // url with protocol, domain and directory with more than 250 words
            url = "https://www.jetbrains.com/test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2";
            path = IUT.getPathFromUrl(url);
            content = "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \n"
                + "\"http://www.w3.org/TR/html4/loose.dtd\">\n"
                + "<html>\n"
                + "<head>\n"
                + "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n"
                + "<title>$title</title>\n"
                + "</head>\n"
                + "<body>$body\n"
                + "</body>\n"
                + "</html>";
            d = new Data("http://test.com", url, content);
            IUT.addURLandContent(d);
            f = getFile(path);
            fContent = new String(Files.readAllBytes(Paths.get(path)));
            assertEquals(content, fContent);

            // url with protocol, domain and directory
            url = "http://www.jetbrains.com.sg/test3";
            path = IUT.getPathFromUrl(url);
            content = "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \n"
                + "\"http://www.w3.org/TR/html4/loose.dtd\">\n"
                + "<html>\n"
                + "<head>\n"
                + "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n"
                + "<title>$title</title>\n"
                + "</head>\n"
                + "<body>$body\n"
                + "</body>\n"
                + "</html>";
            d = new Data("http://test.commm", url, content);
            IUT.addURLandContent(d);
            f = getFile(path);
            fContent = new String(Files.readAllBytes(Paths.get(path)));
            assertEquals(content, fContent);

            // url with protocol, domain and directory
            url = "http://www.jetbrains.com.sg/test4/multiple/dir";
            path = IUT.getPathFromUrl(url);
            content = "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \n"
                + "\"http://www.w3.org/TR/html4/loose.dtd\">\n"
                + "<html>\n"
                + "<head>\n"
                + "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n"
                + "<title>$title</title>\n"
                + "</head>\n"
                + "<body>$body\n"
                + "</body>\n"
                + "</html>";
            d = new Data("http://test.commm", url, content);
            IUT.addURLandContent(d);
            f = getFile(path);
            fContent = new String(Files.readAllBytes(Paths.get(path)));
            assertEquals(content, fContent);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void writeResult() {
        try {
            // TEST 1
            String url = "https://www.jetbrains.com";
            String path = IUT.getPathFromUrl(url);
            String content = "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \n"
                + "\"http://www.w3.org/TR/html4/loose.dtd\">\n"
                + "<html>\n"
                + "<head>\n"
                + "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n"
                + "<title>$title</title>\n"
                + "</head>\n"
                + "<body>$body\n"
                + "</body>\n"
                + "</html>";
            Data d = new Data("http://www.source2.com", url, content);
            IUT.addURLandContent(d);

            // TEST 2
            url = "https://www.jetbrains.com/test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2";
            path = IUT.getPathFromUrl(url);
            content = "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \n"
                + "\"http://www.w3.org/TR/html4/loose.dtd\">\n"
                + "<html>\n"
                + "<head>\n"
                + "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n"
                + "<title>$title</title>\n"
                + "</head>\n"
                + "<body>$body\n"
                + "</body>\n"
                + "</html>";
            d = new Data("http://test.com", url, content);
            IUT.addURLandContent(d);

            // TEST 3
            url = "http://www.jetbrains.com.sg/test3/multiple/dir";
            path = IUT.getPathFromUrl(url);
            content = "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \n"
                + "\"http://www.w3.org/TR/html4/loose.dtd\">\n"
                + "<html>\n"
                + "<head>\n"
                + "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n"
                + "<title>$title</title>\n"
                + "</head>\n"
                + "<body>$body\n"
                + "</body>\n"
                + "</html>";
            d = new Data("http://test.commm", url, content);
            IUT.addURLandContent(d);

            IUT.writeResult();

            path = "./" + IUT.RESULT_FILENAME;
            String fContent = new String(Files.readAllBytes(Paths.get(path)));
            String expectedContent =
                "http://www.jetbrains.com.sg/test3/multiple/dir --> http://test.commm\n"
                    + "https://www.jetbrains.com/test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2 --> http://test.com\n"
                    + "https://www.jetbrains.com --> http://www.source2.com\n";
            assertEquals(expectedContent, fContent);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void isDuplicate() {
        String url = "https://www.jetbrains.com";
        String content = "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \n"
            + "\"http://www.w3.org/TR/html4/loose.dtd\">\n"
            + "<html>\n"
            + "<head>\n"
            + "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n"
            + "<title>$title</title>\n"
            + "</head>\n"
            + "<body>$body\n"
            + "</body>\n"
            + "</html>";
        Data d = new Data("http://test.commm/test123", url, content);
        IUT.addURLandContent(d);
        assert IUT.isDuplicate(url);

        url = "https://www.jetbrains.com/test1";
        content = "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \n"
            + "\"http://www.w3.org/TR/html4/loose.dtd\">\n"
            + "<html>\n"
            + "<head>\n"
            + "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n"
            + "<title>$title</title>\n"
            + "</head>\n"
            + "<body>$body\n"
            + "</body>\n"
            + "</html>";
        d = new Data("www.reddit.com", url, content);
        IUT.addURLandContent(d);
        assert IUT.isDuplicate(url);

        url = "https://www.jetbrains.com/test2/multiple/directory";
        content = "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \n"
            + "\"http://www.w3.org/TR/html4/loose.dtd\">\n"
            + "<html>\n"
            + "<head>\n"
            + "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n"
            + "<title>$title</title>\n"
            + "</head>\n"
            + "<body>$body\n"
            + "</body>\n"
            + "</html>";
        d = new Data("www.reddit.com", url, content);
        IUT.addURLandContent(d);
        assert IUT.isDuplicate(url);
    }

    @Test
    public void addUrlAndContentDifferentDirectoryTimeTest() {
        try {

            String url = "https://www.jetbrains.com/test2/sample";
            String path = IUT.getPathFromUrl(url);
            String content = "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \n"
                + "\"http://www.w3.org/TR/html4/loose.dtd\">\n"
                + "<html>\n"
                + "<head>\n"
                + "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n"
                + "<title>$title</title>\n"
                + "</head>\n"
                + "<body>$body\n"
                + "</body>\n"
                + "</html>";
            final long startTime = System.currentTimeMillis();
            System.out.println("Test started");
            for (int i = 0; i < 1000; i++) {
                Data d = new Data("www.reddit.com", url + i, content);
                IUT.addURLandContent(d);
            }
            final long endTime = System.currentTimeMillis();
            System.out
                .println("Total execution time: " + ((endTime - startTime) / 1000.0) + " seconds");


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void addUrlAndContentDifferentDomainTimeTest() {
        try {

            String url = "https://www.jetbrains%d.com/test2/sample";
            String path = IUT.getPathFromUrl(url);
            String content = "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \n"
                + "\"http://www.w3.org/TR/html4/loose.dtd\">\n"
                + "<html>\n"
                + "<head>\n"
                + "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n"
                + "<title>$title</title>\n"
                + "</head>\n"
                + "<body>$body\n"
                + "</body>\n"
                + "</html>";
            final long startTime = System.currentTimeMillis();
            System.out.println("Test started");
            for (int i = 0; i < 1000; i++) {
                Data d = new Data("www.reddit.com", String.format(url, i), content);
                IUT.addURLandContent(d);
            }
            final long endTime = System.currentTimeMillis();
            System.out
                .println("Total execution time: " + ((endTime - startTime) / 1000.0) + " seconds");


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    /**
     * Test if correctness is maintained when multiple IndexBuilder with identical buffers
     * execute at the same time.
     */
    public void testConcurrency() {
        int numBuffers = 50;
        List<List<Data>> buffers = new LinkedList<>();
        List<IndexBuilderStub> indexBuilders = new LinkedList<>();
        for (int i = 0; i < numBuffers; i++) {
            LinkedList<Data> buffer = new LinkedList();
            buffers.add(buffer);
            indexBuilders.add(new IndexBuilderStub(IUT, buffer));
        }

        String url = "https://www.jetbrains.com/test2/sample";
        String path = IUT.getPathFromUrl(url);
        String content = "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \n"
            + "\"http://www.w3.org/TR/html4/loose.dtd\">\n"
            + "<html>\n"
            + "<head>\n"
            + "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n"
            + "<title>$title</title>\n"
            + "</head>\n"
            + "<body>$body\n"
            + "</body>\n"
            + "</html>";

        int SIZE = 1000;
        for (int i = 0; i < SIZE; i++) {
            Data d = new Data("http://www.source.com", url + i, content);
            for (int j = 0; j < numBuffers; j++) {
                buffers.get(j).add(d);
            }
        }

        for (int i = 0; i < numBuffers; i++) {
            indexBuilders.get(i).start();
        }
        try {
            for (int i = 0; i < numBuffers; i++) {
                indexBuilders.get(i).join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            System.out.println(IUT.size());
            // Ensure size of IUT is correct
            assertEquals(IUT.size(), SIZE);

            // Ensure all written contents are correct.
            String srcPath;
            for (int i = 0; i < SIZE; i++) {
                path = IUT.getPathFromUrl(url + i);
                String fContent = new String(Files.readAllBytes(Paths.get(path)));

                srcPath = Paths.get(path).getParent().toString() + "/" + IUT.SOURCE_FILENAME;
                String srcContent = new String(Files.readAllBytes(Paths.get(srcPath)));

                assertEquals(srcContent, url + i + " --> http://www.source.com\n");
                assertEquals(fContent, content);
            }
            System.out.println(IUT.lockMap.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    /**
     * Test if correctness is maintained when multiple IndexBuilder with different buffers
     * execute at the same time.
     */
    public void testConcurrency2() {
        int numBuffers = 30;
        List<List<Data>> buffers = new LinkedList<>();
        List<IndexBuilderStub> indexBuilders = new LinkedList<>();
        for (int i = 0; i < numBuffers; i++) {
            LinkedList<Data> buffer = new LinkedList();
            buffers.add(buffer);
            indexBuilders.add(new IndexBuilderStub(IUT, buffer));
        }

        String url = "https://www.jetbrains.com/test2/sample";
        String path = IUT.getPathFromUrl(url);
        String content = "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \n"
            + "\"http://www.w3.org/TR/html4/loose.dtd\">\n"
            + "<html>\n"
            + "<head>\n"
            + "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n"
            + "<title>$title</title>\n"
            + "</head>\n"
            + "<body>$body\n"
            + "</body>\n"
            + "</html>";
        int SIZE = 1000;
        for (int i = 0; i < SIZE; i++) {
            Data d = new Data("http://www.source1.com", url + i, content);
            buffers.get(i % numBuffers).add(d);
        }

        for (int i = 0; i < numBuffers; i++) {
            indexBuilders.get(i).start();

        }
        try {
            for (int i = 0; i < numBuffers; i++) {
                indexBuilders.get(i).join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            String srcPath;
            for (int i = 0; i < SIZE; i++) {
                path = IUT.getPathFromUrl(url + i);
                String fContent = new String(Files.readAllBytes(Paths.get(path)));

                srcPath = Paths.get(path).getParent().toString() + "/" + IUT.SOURCE_FILENAME;
                String srcContent = new String(Files.readAllBytes(Paths.get(srcPath)));

                assertEquals(srcContent, url + i + " --> http://www.source1.com\n");
                assertEquals(fContent, content);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class IndexBuilderStub extends CustomThread {

        private IndexURLTree tree;
        private List<Data> buffer;
        private Logger logger = Logger.getLogger("IndexBuilder");
        private long count; // For keep tracking the number of urls added to the tree (temporal implementation)

        public IndexBuilderStub(IndexURLTree tree, List<Data> buffer) {
            this.tree = tree;
            this.buffer = buffer;
            this.count = 0;
        }

        @Override
        public void run() {
            super.setThreadName(String.format("Builder %d", Thread.currentThread().getId()));
            while (!buffer.isEmpty()) {
                Data data = buffer.remove(0);
                writeIUT(data);
                try {
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public void writeIUT(Data data) {
            if (tree.addURLandContent(data)) {
                this.count++;
            }

//            logger.info(getFormattedMessage("write...................."));
//            logger.info(data.getNewUrl());
        }

        public long getCount() {
            return count;
        }
    }

    private File getFile(String path) {
        File f = new File(path);
        return f;
    }

    //TODO: Add test to check timing of execution
}