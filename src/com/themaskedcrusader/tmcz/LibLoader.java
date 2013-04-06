/**
 *  This helpful piece of code was donated to the Bukkit Community by user
 *  fletch_to_99 (http://forums.bukkit.org/members/fletch_to_99.90637775/).
 *  His tutorial found at the link at the bottom of this comment was
 *  so well written that it needed to be included here. If you need to
 *  include external jars in your plugin, this is the way to go!
 *  This code contains some modifications from the tutorial to move
 *  the lib directory to /bukkit_server_path/plugins/lib
 *
 *   http://forums.bukkit.org/threads/tutorial-use-external-library-s-with-your-plugin.103781/
 */

package com.themaskedcrusader.tmcz;

import org.bukkit.Bukkit;

import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

public class LibLoader {
    private static final String LIB_DIR = "/plugins/lib/";
    private static boolean RUNNING_FROM_JAR = false;

    public static boolean extractFromJar(final String fileName, final String destination) throws IOException {
        if (getRunningJar() == null) {   return false;   }
        final File file = new File(destination + fileName);
        if (file.isDirectory()) {
            file.mkdir();
            return false;
        }
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }

        final JarFile jar = getRunningJar();
        final Enumeration<JarEntry> e = jar.entries();
        while (e.hasMoreElements()) {
            final JarEntry je = e.nextElement();
            if (!je.getName().contains(fileName)) {
                continue;
            }

            final InputStream in = new BufferedInputStream(
                    jar.getInputStream(je));

            final OutputStream out = new BufferedOutputStream(
                    new FileOutputStream(file));

            copyInputStream(in, out);
            jar.close();
            return true;
        }

        jar.close();
        return false;

    }

    private static void copyInputStream(final InputStream in,
                                              final OutputStream out) throws IOException {
        try {
            final byte[] buff = new byte[4096];
            int n;
            while ((n = in.read(buff)) > 0) {
                out.write(buff, 0, n);
            }
        } finally {
            out.flush();
            out.close();
            in.close();
        }
    }

    public static URL getJarUrl(final File file) throws IOException {
        String filePath = file.toURI().toURL().toExternalForm();
        filePath = filePath.substring(0, filePath.length() - file.getName().length()) + LIB_DIR;
        return new URL("jar:" + filePath + file.getName() + "!/");
    }

    public static JarFile getRunningJar() throws IOException {
        if (!RUNNING_FROM_JAR) {
            return null; // null if not running from jar
        }
        String path = new File(LibLoader.class.getProtectionDomain()
                .getCodeSource().getLocation().getPath()).getAbsolutePath();
        path = URLDecoder.decode(path, "UTF-8");
        return new JarFile(path);
    }


    static {
        final URL resource = LibLoader.class.getClassLoader()
                .getResource("plugin.yml");
        if (resource != null) {
            RUNNING_FROM_JAR = true;
        }
    }

    private static boolean libExists(File file, String filePath) {
        File lib = new File(filePath + file.getName());
        return lib.exists();
    }

    private static boolean addClassPath(final URL url) throws IOException {
        final URLClassLoader sysLoader = (URLClassLoader) ClassLoader
                .getSystemClassLoader();
        final Class<URLClassLoader> sysClass = URLClassLoader.class;
        try {
            final Method method = sysClass.getDeclaredMethod("addURL",
                    new Class[]{URL.class});
            method.setAccessible(true);
            method.invoke(sysLoader, new Object[]{url});
            return true;
        } catch (final Throwable t) {
            t.printStackTrace();
            throw new IOException("Error adding " + url + " to system classloader");
        }
    }

    private static boolean libOutOfDateOrDoesntExist(File lib, String libPath, Plugin plugin) {
        if (!libExists(lib, libPath)) {
            return true;
        } else {
            String versionKey = "Manifest-Version";
            try {
                JarFile jar = getRunningJar();
                JarInputStream externalJar = new JarInputStream(new FileInputStream(libPath + lib.getName()));
                JarInputStream internalJar = new JarInputStream(getInternalJarStream(jar, lib.getName()));
                Attributes ea = externalJar.getManifest().getMainAttributes();
                Attributes ia = internalJar.getManifest().getMainAttributes();
                int compare = ea.getValue(versionKey).compareToIgnoreCase(ia.getValue(versionKey));
                jar.close();

                return compare == -1;
            } catch (Exception e) {
                return true; // overwrite whatever exists
            }
        }
    }

    private static InputStream getInternalJarStream(JarFile jar, String fileName) throws Exception {
        final Enumeration<JarEntry> e = jar.entries();
        while (e.hasMoreElements()) {
            final JarEntry je = e.nextElement();
            if (!je.getName().contains(fileName)) {
                continue;
            }
            return new BufferedInputStream(jar.getInputStream(je));
        }
        return null;
    }

    ////// MODIFY THIS IF ADDING TO YOUR PLUGIN... THIS IS WHERE THE PLUGIN LOADS ITS LIBRARIES //////

    public static void loadLibraries(Plugin plugin) {
        String libPath = "";
        try {

            for (final File lib : plugin.libs) {
                libPath = lib.getAbsolutePath().substring(0, lib.getAbsolutePath().length() - lib.getName().length()) + LIB_DIR;
                boolean extractLib = libOutOfDateOrDoesntExist(lib, libPath, plugin);
                if (extractLib) {
                    boolean success = extractFromJar(lib.getName(), libPath);
                    if (success) {
                        plugin.getLogger().info("Included library successfully extracted");
                    }
                }
            }

            for (final File lib : plugin.libs) {
                if (!libExists(lib, libPath)) {
                    plugin.getLogger().warning(
                            "There was a critical error loading My plugin! Could not find lib: "
                                    + lib.getName());
                    Bukkit.getServer().getPluginManager().disablePlugin(plugin);
                    return;
                }
                boolean success = addClassPath(getJarUrl(lib));
                if (success) {
                    plugin.getLogger().info("Dynamically loaded required external library.");
                }
            }

        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}
