package de.joshizockt.discordutils.config;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class TextConfig extends FileConfig {

    private String object;

    public TextConfig(File file) {
        super(file);
    }

    @Override
    void init(File file) {  }

    @Override
    void writeToFile(PrintWriter writer) {
        if(object == null) return;
        writer.print(object);
        writer.flush();
        writer.close();
    }

    @Override
    void updateData() {
        try {
            object = new String(Files.readAllBytes(getFile().toPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public HashMap<String, Object> getData() {
        return null;
    }

    /**
     * TextConfig behaves a bit different from the other Configs.
     * The {@param key} is ignored here and instead only the {@param value} is used.
     * This replaces the whole content of the file.
     * @param key the Key you want to write the Object to
     * @param value the Object you want to write
     */
    @Override
    public void set(String key, Object value) {
        set(value);
    }

    public void set(Object value) {
        object = value.toString();
    }

    /**
     * TextConfig behaves a bit different from the other Configs.
     * The {@param key} is ignored here and instead the complete Config is returned.
     * @param key The key with which the object is associated with
     * @return the complete Config
     */
    @Override
    public Object get(String key) {
        return get();
    }

    public String get() {
        return object;
    }

}
