package de.joshizockt.discordutils.modules;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;

public interface Module {

    void init(JDABuilder builder);
    void init(DefaultShardManagerBuilder builder);

}
