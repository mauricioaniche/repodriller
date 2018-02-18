package org.repodriller.util;

public class PathUtils {

    public static String fullPath(String path) {
        if(path.startsWith("~"))
            path = path.replaceFirst("^~",System.getProperty("user.home"));

        return path;
    }
}
