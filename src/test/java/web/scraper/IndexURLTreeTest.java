package web.scraper;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class IndexURLTreeTest {

    private static IndexURLTree IUT;

    @Before public void createIUT() {
        IUT = new IndexURLTree();
        IUT.ROOT_DIRECTORY = "test_data";
    }

    @After public void cleanIUTDirectory() {
        try {
            FileUtils.deleteDirectory(new File(IUT.ROOT_DIRECTORY));
        } catch (IOException e) {
            System.out.println("Delete directory failed. Check if path is correct.");
            e.printStackTrace();
        }
    }

    @Test
    public void addURLandContent() {
        try {
            // TEST 1
            String url = "https://www.jetbrains.com/";
            String path = IUT.ROOT_DIRECTORY + "/https/www/jetbrains/com/normal/.html";
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

            Data d = new Data("", url, content);

            IUT.addURLandContent(d);

            File f = getFile(path);
            String fContent = new String(Files.readAllBytes(Paths.get(path)));
            assertEquals(content, fContent);

            // TEST 2
            url = "https://www.jetbrains.com/btest2";
            path = IUT.ROOT_DIRECTORY + "/https/www/jetbrains/com/normal/btest2.html";
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

            d = new Data("", url, content);

            IUT.addURLandContent(d);
            f = getFile(path);
            fContent = new String(Files.readAllBytes(Paths.get(path)));
            assertEquals(content, fContent);

            // TEST 3
            url = "http://www.jetbrains.com.sg/test3/multiple/dir";
            path = IUT.ROOT_DIRECTORY + "/http/www/jetbrains/com/sg/normal/test3--multiple--dir.html";
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

            d = new Data("", url, content);

            IUT.addURLandContent(d);
            f = getFile(path);
            fContent = new String(Files.readAllBytes(Paths.get(path)));
            assertEquals(content, fContent);

            // TEST 4
            url = "http://www.jetbrains.com.sg/test3/multiple/dir/TESTLONGDIRTESTLONGDIRTESTLONGDIRTESTLONGDIRTESTLONGDIRTESTLONGDIRTESTLONGDIRTESTLONGDIRTESTLONGDIRTESTLONGDIRTESTLONGDIRTESTLONGDIRTESTLONGDIRTESTLONGDIRTESTLONGDIRTESTLONGDIRTESTLONGDIRTESTLONGDIRTESTLONGDIRTESTLONGDIRTESTLONGDIRTESTLONGDIRTESTLONGDIRTESTLONGDIRTESTLONGDIRTESTLONGDIRTESTLONGDIRTESTLONGDIRTESTLONGDIRTESTLONGDIRTESTLONGDIRTESTLONGDIRTESTLONGDIR";
            path = IUT.ROOT_DIRECTORY + "/http/www/jetbrains/com/sg/shorten/0.html";
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

            d = new Data("", url, content);

            IUT.addURLandContent(d);
            f = getFile(path);
            fContent = new String(Files.readAllBytes(Paths.get(path)));
            assertEquals(content, fContent);

            // TEST 5
            url = "http://www.jetbrains.com.sg/test3/multiple/dir/LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2";
            path = IUT.ROOT_DIRECTORY + "/http/www/jetbrains/com/sg/shorten/1.html";
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

            d = new Data("", url, content);

            IUT.addURLandContent(d);
            f = getFile(path);
            fContent = new String(Files.readAllBytes(Paths.get(path)));
            assertEquals(content, fContent);

            // TEST 6
            url = "http://www.jetbrains.com.sg/test3/multiple/dir/LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2LONGDIRTEST2";
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

            d = new Data("", url, content);
            boolean result = IUT.addURLandContent(d);
            assertEquals(result, false);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void addUrlAndContentTest2() {
        try {

            String url = "https://www.jetbrains.com/test2/sample";
            String path = IUT.ROOT_DIRECTORY + "/https/www/jetbrains/com/normal/test2--sample.html";
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

            Data d = new Data("", url, content);

            IUT.addURLandContent(d);

            File f = getFile(path);
            String fContent = new String(Files.readAllBytes(Paths.get(path)));
            assertEquals(content, fContent);

            // The default path of this is taken. Therefore, will store as shortened instead.
            url = "https://www.jetbrains.com/test2--sample";
            path = IUT.ROOT_DIRECTORY + "/https/www/jetbrains/com/shorten/0.html";
            content = "blah";

            d = new Data("", url, content);

            boolean t = IUT.addURLandContent(d);
            f = getFile(path);
            fContent = new String(Files.readAllBytes(Paths.get(path)));
            assertEquals(content, fContent);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void addUrlAndContentTimeTest() {
        try {

            String url = "https://www.jetbrains.com/test2/sample";
            String path = IUT.ROOT_DIRECTORY + "/https/www/jetbrains/com/normal/test2--sample.html";
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
                Data d = new Data("", url+i, content);
                IUT.addURLandContent(d);
            }
            final long endTime = System.currentTimeMillis();
            System.out.println("Total execution time: " + ((endTime - startTime)/1000.0) + " seconds" );

//            File f = getFile(path);
//            String fContent = new String(Files.readAllBytes(Paths.get(path)));
//            assertEquals(content, fContent);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void isDuplicate() {
        // TEST 1
        String url = "https://www.jetbrains.com/";
        String path = IUT.ROOT_DIRECTORY + "/https/www/jetbrains/com/normal/.html";
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

        Data d = new Data("", url, content);
        IUT.addURLandContent(d);
        assert IUT.isDuplicate(url);


        // TEST 2
        url = "https://www.jetbrains.com/test2";
        path = IUT.ROOT_DIRECTORY + "/https/www/jetbrains/com/normal/test2.html";
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

        d = new Data("", url, content);
        IUT.addURLandContent(d);
        assert IUT.isDuplicate(url);

        // TEST 3
        url = "http://www.jetbrains.com.sg/test3/multiple/dir";
        path = IUT.ROOT_DIRECTORY + "/http/www/jetbrains/com/sg/normal/test3--multiple--dir.html";
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

        d = new Data("", url, content);
        IUT.addURLandContent(d);
        assert IUT.isDuplicate(url);
    }

    private File getFile(String path) {
        File f = new File(path);
        return f;
    }

    //TODO: Add test to check timing of execution
}