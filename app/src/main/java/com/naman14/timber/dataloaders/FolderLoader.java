package com.naman14.timber.dataloaders;

import android.text.TextUtils;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by nv95 on 10.11.16.
 */

public class FolderLoader {

    private static final String[] SUPPORTED_EXT = new String[] {
            "mp3",
            "mp4",
            "m4a",
            "aac",
            "ogg",
            "wav"
    };

    public static List<File> getMediaFiles(File dir, final boolean acceptDirs) {
        ArrayList<File> list = new ArrayList<>();
        list.add(new File(dir, ".."));
        list.addAll(Arrays.asList(dir.listFiles(new FileFilter() {

            @Override
            public boolean accept(File file) {
                if (file.isFile()) {
                    String name = file.getName();
                    return !".nomedia".equals(name) && checkFileExt(name);
                } else if (file.isDirectory()){
                    return acceptDirs && checkDir(file);
                } else
                    return false;
            }
        })));

        return list;
    }

    public static boolean isMediaFile(File file) {
        return file.exists() && file.canRead() && checkFileExt(file.getName());
    }

    private static boolean checkDir(File dir) {
        return dir.exists() && dir.canRead() && !".".equals(dir.getName());
    }

    private static boolean checkFileExt(String name) {
        if (TextUtils.isEmpty(name)) {
            return false;
        }
        int p = name.lastIndexOf(".") + 1;
        if (p < 1) {
            return false;
        }
        String ext = name.substring(p).toLowerCase();
        for (String o : SUPPORTED_EXT) {
            if (o.equals(ext)) {
                return true;
            }
        }
        return false;
    }
}
