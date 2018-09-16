import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class ConsoleHomework {
    static Path path = Paths.get("C:", "ConsoleHomework");

    public static void main(String[] args) throws IOException {
        exit:
        while (true) {
            Scanner sc = new Scanner(System.in);
            String[] parts = sc.nextLine().split(" ");
            String command = parts[0];

            if (!Files.exists(path)) {
                Files.createDirectory(path);
            }
            switch (command) {
                case "cd":
                    changeDirectory(parts);
                    continue;
                case "exit":
                    break exit;
                case "mkdir":
                    createDirectory(parts);
                    break;
                case "mkfile":
                    createFile(parts);
                    break;
                case "copy":
                    copyFile(parts);
                    break;
                case "gc":
                    sortedContent(parts);
                    break;
                case "cf":
                    int countedFiles = countFiles(parts);
                    if (countedFiles != -1) {
                        System.out.println("Directory contains " + countedFiles + " files");
                    }
                    break;
                case "zip":
                    Path zipPath;
                    if (parts[1].charAt(0) == 'C') {
                        zipPath = Paths.get(parts[1]);
                    } else {
                        zipPath = Paths.get(path.toString(), parts[1]);
                    }
                    if (Files.exists(zipPath) && parts.length > 2) {
                        String sourceFile = zipPath.toString();
                        FileOutputStream fos = new FileOutputStream(path.toString() + "\\" + parts[2] + ".zip");
                        ZipOutputStream zipOut = new ZipOutputStream(fos);
                        File fileToZip = new File(sourceFile);
                        zipFile(fileToZip, fileToZip.getName(), zipOut);
                        zipOut.close();
                        fos.close();
                    } else {
                        System.out.println("Invalid source or target path!");
                    }
                case "unzip":
                    unzipFile(parts);
                case "neverUseIt":
                    System.out.println("U got me");// Для того, чтобы не прыгать на дефолт
                    break;
                default:
                    System.out.println("Unknown command");
            }
        }
    }


    private static void changeDirectory(String[] parts) {
        Path changedPath = Paths.get(path.toString(), parts[1]);
        if (Files.exists(changedPath) && Files.isDirectory(changedPath)) {
            path = changedPath;
            System.out.println("Success");
        } else {
            System.out.println("Wrong directory path!");
        }
    }


    private static void createDirectory(String[] parts) throws IOException {
        Path createPath = Paths.get(path.toString(), parts[1]);
        if (!Files.exists(createPath) && !Files.isDirectory(createPath)) {
            path = createPath;
            Files.createDirectory(path);
            System.out.println("Success");
        } else {
            System.out.println("Directory already exists");
        }
    }


    private static void createFile(String[] parts) throws IOException {
        Path createPath = Paths.get(path.toString(), parts[1]);
        if (!Files.exists(createPath) && !Files.isRegularFile(createPath)) {
            path = createPath;
            Files.createFile(path);
            System.out.println("Success");
        } else {
            System.out.println("File already exists");
        }
    }


    private static void copyFile(String[] parts) throws IOException {
        if (parts[1] == null || parts[2] == null) {
            System.out.println("Missing source file or target directory!");
            return;
        }
        Path copySource = Paths.get(path.toString(), parts[1]);
        Path copyTarget = Paths.get(parts[2]);
        if (Files.exists(copySource) && Files.isRegularFile(copySource) && Files.exists(copyTarget) && Files.isDirectory(copyTarget)) {
            Files.copy(copySource, Paths.get(copyTarget.toString(), parts[1]), REPLACE_EXISTING);
            System.out.println("Success");
        } else {
            System.out.println("Invalid target directory or file name");
        }
    }


    private static void sortedContent(String[] parts) throws IOException {
        Path directorySearch;
        List<String> sortedPaths = new ArrayList<>();
        if (parts.length != 1) {
            directorySearch = Paths.get(parts[1]);
        } else {
            directorySearch = Paths.get(path.toString());
        }
        if (!Files.isDirectory(directorySearch)) {
            System.out.println("Invalid path");
            return;
        } else {
            DirectoryStream<Path> directoryContent = Files.newDirectoryStream(directorySearch);
            Iterator<Path> pathIterator = directoryContent.iterator();
            while (pathIterator.hasNext()) {
                sortedPaths.add(pathIterator.next().toString());
            }
            Collections.sort(sortedPaths);
            for (String string : sortedPaths) {
                System.out.println(string);
            }
        }
    }


    private static int countFiles(String[] parts) throws IOException {
        Path directorySearch;
        List<String> onlyFiles = new ArrayList<>();
        if (parts.length != 1) {
            directorySearch = Paths.get(parts[1]);
        } else {
            directorySearch = Paths.get(path.toString());
        }
        if (!Files.isDirectory(directorySearch)) {
            System.out.println("Invalid path");
            return -1;
        } else {
            DirectoryStream<Path> directoryContent = Files.newDirectoryStream(directorySearch);
            Iterator<Path> pathIterator = directoryContent.iterator();
            while (pathIterator.hasNext()) {
                Path file = pathIterator.next();
                if (Files.isRegularFile(file)) {
                    onlyFiles.add(file.toString());
                }

            }
            return onlyFiles.size();
        }
    }


    private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {

        if (fileToZip.isDirectory()) {
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            return;
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }

    private static void unzipFile(String parts[]) throws IOException {

        String fileZip = path.toString() + "\\" + parts[1] + ".zip";
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip));
        ZipEntry zipEntry = zis.getNextEntry();
        while (zipEntry != null) {
            String fileName = zipEntry.getName();
            File newFile = new File(path.toString() + "\\" + fileName);
            FileOutputStream fos = new FileOutputStream(newFile);
            int len;
            while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
            fos.close();
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();
    }
}