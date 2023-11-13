import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FileWatcher {
    private final Path dirPath;
    private final Map<String, Long> fileSizes;

    public FileWatcher(String dirPath) {
        this.dirPath = Paths.get(dirPath);
        this.fileSizes = new HashMap<>();
    }

    public Map<String, String> getNewLogs() {
        Map<String, String> newLogs = new HashMap<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath)) {
            for (Path path : stream) {
                String filename = path.getFileName().toString();
                long currentSize = Files.size(path);
                long lastSize = fileSizes.getOrDefault(filename, 0L);
                if (currentSize > lastSize) {
                    newLogs.put(filename, readNewContent(path, lastSize));
                    fileSizes.put(filename, currentSize);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newLogs;
    }

    private String readNewContent(Path file, long lastSize) {
        StringBuilder newContent = new StringBuilder();
        try (RandomAccessFile raf = new RandomAccessFile(file.toFile(), "r")) {
            raf.seek(lastSize);
            String line;
            while ((line = raf.readLine()) != null) {
                newContent.append(line).append(System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newContent.toString();
    }
}
