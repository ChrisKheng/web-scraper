/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package web.scraper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.TreeSet;

/**
 *
 * @author 432br
 */
public class StatsWriter extends Thread {
    
    private IndexURLTree tree;

    public StatsWriter (IndexURLTree tree) {
            this.tree = tree;  
    }
    
    @Override
    public void run() {
        try {
            File file = new File("./statistics.txt");
            file.createNewFile();

            FileWriter writer = new FileWriter(file);
            writer.write(".............Stats............\n");
            
            while (true) {
                try{
                    // sleeps thread for 1 hour (currently 1 second for testing)
                    long pastSize = tree.size();
                    Thread.sleep(1000);//*60*60);
                    long newSize = tree.size();
                    writer.write(String.format("%d new urls found", newSize - pastSize));
                } catch(Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
