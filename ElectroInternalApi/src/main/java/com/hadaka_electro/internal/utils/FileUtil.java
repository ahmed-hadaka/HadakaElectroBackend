//package com.hadaka_electro.internal.utils;
//
//import org.springframework.util.FileSystemUtils;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.nio.file.StandardCopyOption;
//
//public class FileUtil {
//
//    public static void saveFile(String dirName, String fileName, MultipartFile multipartFile) throws IOException {
//
//        try {
//            Path completePath = Paths.get(dirName.toString());
//
//            if (!Files.exists(completePath))
//                Files.createDirectories(completePath);
//
//            Path filePath = completePath.resolve(fileName);
//            InputStream fileInputStream = multipartFile.getInputStream();
//            Files.copy(fileInputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
//
//        } catch (IOException e) {
//            throw new IOException("Could not save the file " + fileName, e);
//        }
//    }
//
//    public static void cleanDir(String dirName) throws IOException, RuntimeException {
//        Path dirPath = Paths.get(dirName);
//
//        if (!Files.exists(dirPath))
//            return;
//
//        try {
//            Files.list(dirPath).forEach(type -> {
//                if (!Files.isDirectory(type)) {
//                    try {
//                        Files.delete(type);
//                    } catch (IOException e) {
//                        e.getMessage();
//                    }
//                } else {
//                    try {
//                        FileSystemUtils.deleteRecursively(type);
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//            });
//        } catch (IOException e) {
//            throw new IOException("can't list the content of Dir: " + dirName);
//        }
//    }
//
//    // delete directory
//    public static void deletePhotosDir(String dirName, int id) throws Exception {
//        Path dirPath = Paths.get(dirName + id);
//        if (Files.exists(dirPath)) {
//            try {
//                FileSystemUtils.deleteRecursively(dirPath);
//            } catch (IOException e) {
//                throw new Exception("Can not delete Photo directory ");
//            }
//        }
//    }
//
//
//    public static void removeFileIfExists(String filePath) {
//        try {
//            Files.deleteIfExists(Path.of(filePath));
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//}

package com.hadaka_electro.internal.utils;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class FileUtil {

    private static Cloudinary cloudinary;

    private static final List<String> KNOWN_ROOT_FOLDERS = Arrays.asList(
            "user_photos", "category_images", "brand_logos", "product_images", "default_images"
    );

    @Autowired
    public FileUtil(Cloudinary cloudinaryInstance) {
        FileUtil.cloudinary = cloudinaryInstance;
    }

    /**
     * Converts any local path or URL into a clean Cloudinary folder/public_id path.
     * Example: "/mnt/.../HadakaElectroBackEnd/brand_logos/5/" -> "brand_logos/5"
     */
    private static String normalizeCloudinaryPath(String rawPath) {
        if (rawPath == null || rawPath.isBlank()) {
            return "";
        }
        String normalized = rawPath.replace("\\", "/");

        for (String rootFolder : KNOWN_ROOT_FOLDERS) {
            int idx = normalized.indexOf(rootFolder);
            if (idx != -1) {
                normalized = normalized.substring(idx);
                break;
            }
        }

        // Remove leading and trailing slashes
        return normalized.replaceAll("^/+", "").replaceAll("/+$", "");
    }

    private static String stripExtension(String pathOrFileName) {
        int lastSlash = pathOrFileName.lastIndexOf('/');
        int lastDot = pathOrFileName.lastIndexOf('.');
        if (lastDot > lastSlash) {
            return pathOrFileName.substring(0, lastDot);
        }
        return pathOrFileName;
    }

    public static void saveFile(String dirName, String fileName, MultipartFile multipartFile) throws IOException {
        try {
            String folder = normalizeCloudinaryPath(dirName);
            String cleanFileName = stripExtension(fileName);
            String publicId = folder.isEmpty() ? cleanFileName : folder + "/" + cleanFileName;

            cloudinary.uploader().upload(
                    multipartFile.getBytes(),
                    ObjectUtils.asMap(
                            "public_id", publicId,
                            "overwrite", true,
                            "invalidate", true,
                            "resource_type", "image"
                    )
            );
        } catch (IOException e) {
            throw new IOException("Could not save the file " + fileName, e);
        }
    }

    public static void cleanDir(String dirName) throws IOException, RuntimeException {
        String folderPrefix = normalizeCloudinaryPath(dirName);
        if (folderPrefix.isEmpty()) {
            return;
        }

        try {
            // Deletes all images inside this folder prefix (e.g., "user_photos/4/")
            cloudinary.api().deleteResourcesByPrefix(
                    folderPrefix + "/",
                    ObjectUtils.asMap("invalidate", true)
            );
        } catch (Exception e) {
            throw new IOException("can't list or clean the content of Dir: " + dirName, e);
        }
    }

    public static void deletePhotosDir(String dirName, int id) throws Exception {
        String baseFolder = normalizeCloudinaryPath(dirName);
        String targetFolder = baseFolder + "/" + id;

        try {
            // 1. Delete all images under the folder prefix (including subfolders like /extras)
            cloudinary.api().deleteResourcesByPrefix(
                    targetFolder + "/",
                    ObjectUtils.asMap("invalidate", true)
            );
            // 2. Delete the empty folder itself in Cloudinary (ignore if folder didn't exist)
            try {
                cloudinary.api().deleteFolder(targetFolder, ObjectUtils.emptyMap());
            } catch (Exception ignored) {
            }
        } catch (Exception e) {
            throw new Exception("Can not delete Photo directory ", e);
        }
    }

    public static void removeFileIfExists(String filePath) {
        try {
            String normalizedPath = normalizeCloudinaryPath(filePath);
            String publicId = stripExtension(normalizedPath);
            if (!publicId.isEmpty()) {
                cloudinary.uploader().destroy(
                        publicId,
                        ObjectUtils.asMap("invalidate", true, "resource_type", "image")
                );
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
