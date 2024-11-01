package org.springframework.samples.petclinic.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CacheManager {

    private static final String CACHE_DIR_NAME = "cache";
    private static final String ENV_CACHE_ROOT = "CACHE_ROOT";
    private static CacheManager instance;
    private final Path cacheDirectory;

    // Private constructor to prevent instantiation from outside the class
    private CacheManager(String baseDirectory) {
        this.cacheDirectory = Paths.get(baseDirectory, CACHE_DIR_NAME);
    }

    // Thread-safe method to get the singleton instance
    public static synchronized CacheManager getInstance() {
        if (instance == null) {
            String baseDirectory = System.getenv(ENV_CACHE_ROOT);
            if (baseDirectory == null || baseDirectory.isEmpty()) {
                baseDirectory = "/var/www/webapp";
            }
            instance = new CacheManager(baseDirectory);
        }
        return instance;
    }

    public File getFile(String filename) {
        var path = cacheDirectory.resolve(filename);
        System.out.println("Path: " + path);

        return cacheDirectory.resolve(filename).toFile();
    }

    public void saveFile(String filename, byte[] data) throws IOException {
        Path filePath = cacheDirectory.resolve(filename);
        Files.write(filePath, data);
        System.out.println("File saved to cache: " + filePath.toAbsolutePath());
    }

    public byte[] readFile(String filename) throws IOException {
        Path filePath = cacheDirectory.resolve(filename);
        if (Files.exists(filePath)) {
            System.out.println("Reading file from cache: " + filePath.toAbsolutePath());
            return Files.readAllBytes(filePath);
        } else {
            System.err.println("File not found in cache: " + filePath.toAbsolutePath());
            return null;
        }
    }

    public void clearCache() {
        try {
            Files.walk(cacheDirectory)
                .filter(Files::isRegularFile)
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        System.err.println("Failed to delete file: " + path.toAbsolutePath());
                    }
                });
            System.out.println("Cache directory cleared.");
        } catch (IOException e) {
            System.err.println("Error clearing cache directory: " + e.getMessage());
        }
    }
}
