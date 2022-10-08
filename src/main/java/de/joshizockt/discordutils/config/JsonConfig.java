package de.joshizockt.discordutils.config;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class JsonConfig extends FileConfig {

    private JSONObject object;

    public JsonConfig(File file) {
        super(file);
    }

    @Override
    void init(File file) {  }

    @Override
    void writeToFile(PrintWriter writer) {
        if(object == null) return;
        writer.print(object.toString(4));
        writer.flush();
        writer.close();
    }

    @Override
    void updateData() {
        try {
            String content = new String(Files.readAllBytes(getFile().toPath()));
            object = new JSONObject(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public HashMap<String, Object> getData() {
        return null;
    }

    @Override
    public void set(String key, Object o) {
        if(object == null) updateData();
        if(object == null) object = new JSONObject();
        object.put(key, o);
    }

    @Override
    public Object get(String key) {
        String[] args = key.split("\\.");
        if(object != null) {
            JSONObject data = object;
            for(int i = 0; i < (args.length-1); i++) {
                String arg = args[i];
                if(data.keySet().contains(arg)) {
                    Object o = object.get(arg);
                    if(o == null) continue;
                    if(o instanceof JSONObject object) {
                        data = object;
                    }
                }
            }
            String arg = args[args.length-1];
            if(data.keySet().contains(arg)) return data.get(arg);
            else return null;
        }
        return null;
    }

}
