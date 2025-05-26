package com.coachera.backend.util;


import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class StorageUtil {
    private static final String IMAGE_DIR = "classpath:static/sample-images/";
    private final List<String> availableImages;
    private final AtomicInteger currentIndex = new AtomicInteger(0);

    public StorageUtil() throws IOException {
        Resource[] resources = new PathMatchingResourcePatternResolver()
                .getResources(IMAGE_DIR + "*");

        File directory = resources[0].getFile().getParentFile();
        if (!directory.exists()) {
            throw new IllegalStateException("Image directory not found");
        }

        File[] files = directory.listFiles();
        if (files == null || files.length == 0) {
            throw new IllegalStateException("No images in directory");
        }

        this.availableImages = new ArrayList<>();

        for (File file : files) {
            String extension = getExtension(file.getName());
            String uuidName = UUID.randomUUID().toString() + "." + extension;

            File renamedFile = new File(file.getParent(), uuidName);
            if (!file.getName().equals(renamedFile.getName())) {
                Files.move(file.toPath(), renamedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }

            availableImages.add(uuidName);
        }

        Collections.shuffle(availableImages);
    }

    public String getRandomImageName() {
        int index = currentIndex.getAndIncrement() % availableImages.size();
        return availableImages.get(index);
    }

    private String getExtension(String filename) {
        return filename.substring(filename.lastIndexOf('.') + 1);
    }
}
