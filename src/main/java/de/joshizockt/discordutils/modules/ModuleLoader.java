package de.joshizockt.discordutils.modules;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;

public class ModuleLoader {

    public static void loadModules(JDABuilder builder, Module module, Module... modules) {
        module.init(builder);
        if(modules != null && modules.length >= 1) {
            for(Module m : modules) {
                m.init(builder);
            }
        }
    }

    public static void loadModules(DefaultShardManagerBuilder builder, Module module, Module... modules) {
        module.init(builder);
        if(modules != null && modules.length >= 1) {
            for(Module m : modules) {
                m.init(builder);
            }
        }
    }

}
