package edu.northeastern;

import edu.northeastern.compress.Compressor;
import edu.northeastern.compress.GzipCompressor;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Driver {

    public static void executeCompressor() throws InterruptedException {
        URL resourcesURL = Main.class.getClassLoader().getResource("data");

        if(resourcesURL == null) {
            System.out.println("Resources not found");
            return;
        }

        File resourcesDir = null;
        try {
            resourcesDir = new File(resourcesURL.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        File[] files = resourcesDir.listFiles(); // stores the files in the /resources/data directory
        Compressor compressor = new GzipCompressor();

        if (files != null) {
            int numOfLogicalCPUCoresAvailableToJVM = Runtime.getRuntime().availableProcessors();
            System.out.println("Number of logical CPU cores available to JVM: " + numOfLogicalCPUCoresAvailableToJVM);
            ExecutorService executor = Executors.newFixedThreadPool(numOfLogicalCPUCoresAvailableToJVM);
            for (File file : files) {
                executor.submit(() -> compressor.compress(file));
            }
            executor.shutdown();                                    // Stops accepting new tasks
            executor.awaitTermination(1, TimeUnit.HOURS);   // Wait for all submitted tasks to complete or timeout expires (whichever comes first)
        } else {
            System.out.println("No files found or directory is empty.");
        }
    }
}
