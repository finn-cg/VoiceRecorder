package finn.academic.voicerecorder.util;

import java.io.File;

public class FileHandler {
    public static boolean rename(File from, File to) {
        return from.getParentFile().exists() && from.exists() && from.renameTo(to);
    }

    public static boolean createFolderIfNotExists(String path) {
        File folder = new File(path);
        if (folder.exists())
            return true;
        else
            return folder.mkdirs();
    }
}
