package de.joshizockt.discordutils.modules;

import net.dv8tion.jda.api.JDABuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModuleLoader {

    public static void loadModules(JDABuilder builder, Module module, Module... modules) {
        module.init(builder);
        for(Module m : modules) {
            m.init(builder);
        }
    }

}
