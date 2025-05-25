package com.coachera.backend.util;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Component
public class StorageUtil {
    private static final String IMAGE_DIR = "classpath:static/sample-images/*";
    private final List<String> availableImages;
    private final Random random = new Random();

    public StorageUtil() throws IOException {
        Resource[] resources = new PathMatchingResourcePatternResolver()
                .getResources(IMAGE_DIR);
        this.availableImages = Arrays.stream(resources)
                .map(Resource::getFilename)
                .toList();
        
        if (availableImages.isEmpty()) {
            throw new IllegalStateException("No sample images found in " + IMAGE_DIR);
        }
    }

    public String getRandomImageName() {
        return availableImages.get(random.nextInt(availableImages.size()));
    }
}