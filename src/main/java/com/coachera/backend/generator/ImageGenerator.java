package com.coachera.backend.generator;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.instancio.Instancio;
import org.instancio.Select;

import com.coachera.backend.entity.Image;
import com.coachera.backend.util.StorageUtil;

public class ImageGenerator {
    public static Image createImage() {
        return Instancio.of(Image.class)
                .ignore(Select.field(Image::getId))
                .supply(Select.field(Image::getUuidName), () -> generateImageUuid())
                .create();
    }

    public static List<Image> generate(int count) {
        return Instancio.ofList(Image.class).size(count)
                .ignore(Select.field(Image::getId))
                .supply(Select.field(Image::getUuidName), () -> {
                    try {
                        return getRandomImage();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    return generateImageUuid();
                })
                .create();
    }

    private static String generateImageUuid() {
        // Format: "img_" + UUID + "_" + timestamp
        return String.format("img_%s_%d",
                UUID.randomUUID().toString().substring(0, 8),
                ThreadLocalRandom.current().nextInt(1000, 9999));
    }

    public static String getRandomImage() throws IOException {
        StorageUtil storageUtil = new StorageUtil();
        String randomFilename = storageUtil.getRandomImageName();
        return randomFilename;
    }
}
