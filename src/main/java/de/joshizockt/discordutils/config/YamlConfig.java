package de.joshizockt.discordutils.config;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class YamlConfig extends FileConfig  {

    private Yaml yaml;
    private Map<String, Object> data;

    public YamlConfig(File file) {
        super(file);
    }

    @Override
    void init(File file) {
        yaml = new Yaml();
    }

    @Override
    void writeToFile(PrintWriter writer) {
        if(data == null) return;
        yaml.dump(data, writer);
    }

    @Override
    void updateData() {
        try {
            data = yaml.load(new FileInputStream(getFile()));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public HashMap<String, Object> getData() {
        return new HashMap<>(data);
    }

    @Override
    public void set(String key, Object o) {
        if(data == null) data = new HashMap<>();
        data.put(key, o);
    }

    @Override
    public Object get(String key) {
        String[] args = key.split("\\.");
        Map<String, Object> map = data;
        if(map != null) {
            for(int i = 0; i < (args.length-1); i++) {
                String arg = args[i];
                if(map.containsKey(arg)) {
                    Object o = map.get(arg);
                    if(o == null) continue;
                    if(o instanceof LinkedHashMap linkedHashMap) {
                        map = linkedHashMap;
                    }
                }
            }
            String arg = args[args.length-1];
            if(map.containsKey(arg)) return map.get(arg);
        }
        return null;
    }

}
