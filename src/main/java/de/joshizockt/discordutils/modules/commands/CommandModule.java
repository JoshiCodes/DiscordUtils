package de.joshizockt.discordutils.modules.commands;

import de.joshizockt.discordutils.modules.Module;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;

public class CommandModule extends ListenerAdapter implements Module {


    @Override
    public void init(JDABuilder builder) {
        builder.addEventListeners(this);
    }

    @Override
    public void init(DefaultShardManagerBuilder builder) {
        builder.addEventListeners(this);
    }



}
