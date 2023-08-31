package de.joshizockt.discordutils.config;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Set;

public abstract class FileConfig {

    private File file;

    public FileConfig(File file) {
        this.file = file;
        init(file);
        if(file.exists()) updateData();
    }

    @Deprecated
    abstract void init(File file);

    abstract void writeToFile(PrintWriter writer);
    abstract void updateData();

    /**
     * Sets a Config value with the key and the Object o as value.
     * Use {@link #save()} to Save to file.
     * @param key the Key you want to write the Object to
     * @param o the Object you want to write
     */
    abstract public void set(String key, Object o);

    /**
     * Get an Object with any class from the Config. Returns null if nothing is found.
     * @param key The key with which the object is associated with
     * @return the found Object or null if nothing is found.
     */
    abstract public Object get(String key);

    abstract public HashMap<String, Object> getData();

    /**
     * Retrieves all keys from the Config.
     * This only returns the keys of the first level.
     * @return the first level keys of the Config
     */
    public Set<String> getKeys() {
        return getData().keySet();
    }

    /**
     * Get an Object with any class from the Config. Returns the def variable if nothing is found.
     * @param key The key with which the object is associated with
     * @param def The fallback-Object which is used, when the object is null.
     * @return the found Object or the def-object if nothing is found.
     */
    public Object get(String key, Object def) {
        Object o = get(key);
        if(o == null) return def;
        return o;
    }

    /**
     * Get a String from the Config. Returns the def variable if nothing is found.
     * @param key The key with which the string is associated with
     * @return the found string or the def-string if nothing is found.
     * @throws ClassCastException when the Object of 'key' is no instance of a String.
     */
    public String getString(String key) {
        return (String) get(key, key);
    }


    /**
     * Get a String from the Config. Returns the def variable if nothing is found.
     * @param key The key with which the string is associated with
     * @param def The fallback-string which is used, when the string is null.
     * @return the found string or the def-string if nothing is found.
     * @throws ClassCastException when the Object of 'key' is no instance of a String.
     */
    public String getString(String key, String def) {
        return (String) get(key, def);
    }

    /**
     * Get a boolean from the Config. Returns the false if nothing is found.
     * @param key The key with which the boolean is associated with
     * @return the found boolean or false if nothing is found.
     * @throws ClassCastException when the Object of 'key' is no instance of a boolean.
     */
    public boolean getBoolean(String key) {
        return (boolean) get(key, false);
    }

    /**
     * Get a boolean from the Config. Returns the def variable if nothing is found.
     * @param key The key with which the boolean is associated with
     * @param def The fallback-boolean which is used, when the boolean is not found.
     * @return the found boolean or the def-boolean if nothing is found.
     * @throws ClassCastException when the Object of 'key' is no instance of a boolean.
     */
    public boolean getBoolean(String key, boolean def) {
        return (boolean) get(key, def);
    }

    /**
     * Get an int from the Config. Returns 0 if nothing is found.
     * @param key The key with which the int is associated with
     * @return the found int or 0 if nothing is found.
     * @throws ClassCastException when the Object of 'key' is no instance of an int.
     */
    public int getInt(String key) {
        return (int) get(key, false);
    }

    /**
     * Get an int from the Config. Returns the def variable if nothing is found.
     * @param key The key with which the int is associated with
     * @param def The fallback-int which is used, when the int is null.
     * @return the found int or the def-int if nothing is found.
     * @throws ClassCastException when the Object of 'key' is no instance of an int.
     */
    public int getInt(String key, int def) {
        return (int) get(key, def);
    }

    /**
     * Copies the resource File outside of the working directory.
     * @throws NullPointerException if no File was found in the resources.
     */
    public void copyDefaults() {
        if(file.exists()) return;
        InputStream in = getClass().getClassLoader().getResourceAsStream(file.getPath());
        if(in == null) {
            throw new NullPointerException("There was no resource File found named '" + file.getPath() + "'.");
        }
        if(!file.exists()) {
            try {
                if(file.getParentFile() != null && !file.getParentFile().exists()) file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                try {
                    in.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                throw new RuntimeException(e);
            }
        }
        try {
            Files.copy(in, Paths.get(file.getAbsolutePath()), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            try {
                in.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }
        updateData();
    }

    /**
     * Saves the Configuration into the given File
     * @return true if the File was written, false if not.
     */
    public boolean save() {
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        PrintWriter writer;
        try {
            writer = new PrintWriter(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        writeToFile(writer);
        updateData();
        return true;
    }

    /**
     * Sets the File in which the Config is written.
     * @deprecated NOT RECOMMENDED! CREATE A NEW {@link FileConfig} INSTANCE PER FILE!
     * @param file the New File you want to use
     */
    @Deprecated
    public void setFile(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

}
