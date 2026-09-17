package com.hadaka_electro.internal.utils;

import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileUtil {

    public static void saveFile(String dirName, String fileName, MultipartFile multipartFile) throws IOException {

        try {

            Path completePath = Paths.get(dirName.toString());

            if (!Files.exists(completePath))
                Files.createDirectories(completePath);

            Path filePath = completePath.resolve(fileName);
            InputStream fileInputStream = multipartFile.getInputStream();
            Files.copy(fileInputStream, filePath, StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {
            throw new IOException("Could not save the file " + fileName, e);
        }
    }

    public static void cleanDir(String dirName) throws IOException, RuntimeException {
        Path dirPath = Paths.get(dirName);

        if (!Files.exists(dirPath))
            return;

        try {
            Files.list(dirPath).forEach(type -> {
                if (!Files.isDirectory(type)) {
                    try {
                        Files.delete(type);
                    } catch (IOException e) {
                        e.getMessage();
                    }
                } else {
                    try {
                        FileSystemUtils.deleteRecursively(type);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } catch (IOException e) {
            throw new IOException("can't list the content of Dir: " + dirName);
        }
    }


    // delete directory
    public static void deletePhotosDir(String dirName, int id) throws Exception {
        Path dirPath = Paths.get(dirName + id);
        if (Files.exists(dirPath)) {
            try {
                FileSystemUtils.deleteRecursively(dirPath);
            } catch (IOException e) {
                throw new Exception("Can not delete Photo directory ");
            }
        }
    }


    public static void removeFileIfExists(String filePath) {
        try {
            Files.deleteIfExists(Path.of(filePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
