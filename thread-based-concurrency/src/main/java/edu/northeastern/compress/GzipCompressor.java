package edu.northeastern.compress;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.GZIPOutputStream;

public class GzipCompressor implements Compressor {
    private final File outputDir;

    public GzipCompressor() {
        outputDir = new File("output/processed");   // dedicated writable output directory

        if(!outputDir.exists()) {
            outputDir.mkdirs();
        }
    }

    @Override
    public void compress(File file) {
        if(!file.isFile()) {
            System.out.println("Skipping non-file " + file.getName());
            return;
        }
        Path outputPath = outputDir.toPath().resolve(file.getName() + ".gz");
        try (
                InputStream in = new BufferedInputStream(Files.newInputStream(file.toPath()));
                OutputStream out = new GZIPOutputStream(Files.newOutputStream(outputPath));
        ) {
            byte[] buffer = new byte[16 * 1024];
            int length;
            while ((length = in.read(buffer)) != -1) {
                out.write(buffer, 0, length);
            }
            System.out.println("[" + Thread.currentThread().getName() + "] successfully compressed " + file.getName() + "-> processed/" + outputPath.getFileName());
        } catch (IOException e) {
            System.out.println("Failed to compress file " + file.getName() + " : " + e.getMessage());
        }
    }
}
