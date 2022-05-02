package finn.academic.voicerecorder.util;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import finn.academic.voicerecorder.model.Database;

public class FileHandler {
    public static boolean rename(File from, File to) {
        return from.getParentFile().exists() && from.exists() && from.renameTo(to);
    }

    public static String getDeleteName(File file, String folderName) {
        String name = file.getName();
        return name.substring(0, name.length() - ".mp3".length()) + "_" + folderName + ".mp3";
    }

    public static String getFolderName(File file) {
        String name = file.getName();
        return name.substring(name.lastIndexOf('_') + 1, name.length()).replace(".mp3", "");
    }

    public static String getOldName(File file) {
        String name = file.getName();
        String folderName = name.substring(name.lastIndexOf('_'), name.length());
        return name.substring(0, name.lastIndexOf(folderName)) + ".mp3";
    }

    public static boolean createFolderIfNotExists(String path) {
        File folder = new File(path);
        if (folder.exists())
            return true;
        else
            return folder.mkdirs();
    }

    public static boolean createFolderIfNotExistsInDB(String path, String name, Context context) {
        File folder = new File(path);
        Database database = new Database(context, "folder.sqlite", null, 1);

        if (folder.exists())
            return true;
        else
            if (folder.mkdirs()) {
                database.queryData("INSERT INTO Folder VALUES('" + name + "', 0)");
                return true;
            } else {
                return false;
            }
    }

    public static void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }

    public static void moveFile(File srcFileOrDirectory, File desFileOrDirectory) throws IOException {
        File newFile = new File(desFileOrDirectory, srcFileOrDirectory.getName());
        try (FileChannel outputChannel = new FileOutputStream(newFile).getChannel();
             FileChannel inputChannel = new FileInputStream(srcFileOrDirectory).getChannel()) {
            inputChannel.transferTo(0, inputChannel.size(), outputChannel);
            inputChannel.close();
            deleteRecursive(srcFileOrDirectory);
        }
    }
}
