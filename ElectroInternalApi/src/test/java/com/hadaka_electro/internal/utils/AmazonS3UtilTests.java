package com.hadaka_electro.internal.utils;

import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public class AmazonS3UtilTests {

    @Test
    public void testListFolder() {
        List<String> folderContent = AmazonS3Util.listFolder("default_images");
        folderContent.forEach(System.out::println);
    }

    @Test
    public void testUploadFile() throws FileNotFoundException {
        String folderName = "test-upload";
        String fileName = "Fujifilm.png";
        String filePath = "/mnt/DCB67EB1B67E8C30/test-upload/" + fileName;

        InputStream inputStream = new FileInputStream(filePath);

        AmazonS3Util.uploadFile(folderName, fileName, inputStream);
    }

    @Test
    public void testDeleteFile() {
        String fileName = "test-upload/Fujifilm.png";
        AmazonS3Util.deleteFile(fileName);
    }

    @Test
    public void testRemoveFolder() {
        String folderName = "test-upload";
        AmazonS3Util.removeFolder(folderName);
    }
}
