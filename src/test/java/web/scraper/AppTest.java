/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package web.scraper;

import org.junit.Test;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

public class AppTest {
    @Test public void testAppHasAGreeting() {
        // App classUnderTest = new App();
        // assertNotNull("app should have a greeting", classUnderTest.getGreeting());
    }

    @Test public void testSplitList() {
        // Modify system.in first
        try {
            File file = new File("./seeds.txt");
            System.setIn(new FileInputStream(file));

            App classUnderTest = new App();

            List<String> urls = classUnderTest.getURLSeeds();
            
            List<List<String>> lists = classUnderTest.splitList(urls, 6);
            int size = lists.stream().mapToInt(list -> list.size()).sum();

            assertEquals(urls.size(), size);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
