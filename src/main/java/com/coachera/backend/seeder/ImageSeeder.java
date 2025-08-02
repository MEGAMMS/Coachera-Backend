package com.coachera.backend.seeder;

import com.coachera.backend.entity.Image;
import com.coachera.backend.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class ImageSeeder {

    private final ImageRepository imageRepository;
    private final Path uploadPath;
    private List<Resource> sourceImageResources = new ArrayList<>();
    private final Random random = new Random();

    public ImageSeeder(ImageRepository imageRepository, @Value("${app.upload.dir}") String uploadDir) throws IOException {
        this.imageRepository = imageRepository;
        this.uploadPath = Path.of(uploadDir);
        Files.createDirectories(uploadPath);
    }

    @Transactional
    public void seedImages() {
        try {
            // Load images from seeder-images folder using ResourcePatternResolver
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath:seeder-images/*");
            
            sourceImageResources.clear();
            for (Resource resource : resources) {
                if (isImageFile(resource.getFilename())) {
                    sourceImageResources.add(resource);
                }
            }

            if (sourceImageResources.isEmpty()) {
                System.out.println("No image files found in seeder-images folder. Will use placeholder images when needed.");
            } else {
                System.out.println("Found " + sourceImageResources.size() + " images in seeder-images folder");
            }
        } catch (Exception e) {
            System.err.println("Error loading seeder images: " + e.getMessage());
            System.out.println("Will use placeholder images when needed.");
        }
    }

    private boolean isImageFile(String fileName) {
        if (fileName == null) return false;
        String lowerCase = fileName.toLowerCase();
        return lowerCase.endsWith(".jpg") || lowerCase.endsWith(".jpeg") || 
               lowerCase.endsWith(".png") || lowerCase.endsWith(".gif") || 
               lowerCase.endsWith(".bmp") || lowerCase.endsWith(".webp");
    }

    /**
     * Get a random image by copying a source image and creating a new Image entity
     */
    public Image getRandomImage() {
        try {
            if (!sourceImageResources.isEmpty()) {
                // Pick a random source image
                Resource sourceResource = sourceImageResources.get(random.nextInt(sourceImageResources.size()));
                return copyAndStoreImage(sourceResource);
            } else {
                // Create a placeholder image
                return createPlaceholderImage();
            }
        } catch (Exception e) {
            System.err.println("Error creating random image: " + e.getMessage());
            return createPlaceholderImage();
        }
    }

    /**
     * Get multiple random images
     */
    public List<Image> getRandomImages(int count) {
        List<Image> randomImages = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            randomImages.add(getRandomImage());
        }
        return randomImages;
    }

    private Image copyAndStoreImage(Resource sourceResource) throws IOException {
        String originalExtension = getFileExtension(sourceResource.getFilename());
        String uuidName = UUID.randomUUID().toString() + originalExtension;
        Path targetPath = uploadPath.resolve(uuidName);

        // Copy the resource to uploads directory using the correct method
        try (var inputStream = sourceResource.getInputStream()) {
            Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }

        // Create and save Image entity
        Image image = new Image();
        image.setUuidName(uuidName);
        return imageRepository.save(image);
    }

    private Image createPlaceholderImage() {
        // Create a placeholder image with a unique UUID
        Image image = new Image();
        image.setUuidName("placeholder-" + UUID.randomUUID().toString() + ".jpg");
        return imageRepository.save(image);
    }

    private String getFileExtension(String fileName) {
        return fileName != null ? fileName.substring(fileName.lastIndexOf(".")) : "";
    }

    /**
     * Clear all seeded images from database and file system
     */
    @Transactional
    public void clearSeededImages() {
        // Clear all images from the uploads directory that start with "placeholder-"
        try {
            Files.list(uploadPath)
                .filter(path -> path.getFileName().toString().startsWith("placeholder-"))
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        System.err.println("Error deleting placeholder image: " + e.getMessage());
                    }
                });
        } catch (IOException e) {
            System.err.println("Error clearing seeded images: " + e.getMessage());
        }
    }
} 
