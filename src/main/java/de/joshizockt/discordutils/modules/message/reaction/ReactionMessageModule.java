package de.joshizockt.discordutils.modules.message.reaction;

import de.joshizockt.discordutils.Validate;
import de.joshizockt.discordutils.modules.Module;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectInteraction;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.List;

public class ReactionMessageModule extends ListenerAdapter implements Module {

    private static final List<ReactionMessage> cache = new ArrayList<>();

    public ReactionMessageModule() {}

    @Override
    public void init(JDABuilder builder) {
        builder.addEventListeners(this);
    }

    @Override
    public void init(DefaultShardManagerBuilder builder) {
        builder.addEventListeners(this);
    }

    public static void cacheMessage(ReactionMessage message) {
        cache.add(message);
    }

    public static List<ReactionMessage> getCache() {
        return cache;
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        StringSelectInteraction interaction = event.getInteraction();
        User u = event.getUser();
        Member m = event.getMember();
        if(m == null) {
            return;
        }
        if(u.isBot()) return;
        String id = interaction.getComponentId();
        if(!id.equals("sr_select")) {
            return;
        }
        List<String> values = new ArrayList<>(interaction.getValues());
        values.removeIf(v -> !Validate.isLong(v));
        if(values.isEmpty()) {
            return;
        }
        InteractionHook hook = event.deferReply(true).complete();
        ReactionMessage message = getMessage(event.getMessageId());
        if(message == null) {
            hook.editOriginal("The message was not found").queue();
            return;
        }
        if(message.getType() != ReactionMessage.Type.SELECT_SINGLE && message.getType() != ReactionMessage.Type.SELECT_MULTIPLE) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        Guild guild = event.getGuild();
        if(guild == null) {
            return;
        }
        for(String value : values) {
            Role role = guild.getRoleById(value);
            if(role == null) {
                continue;
            }
            if(m.getRoles().contains(role)) {
                event.getGuild().removeRoleFromMember(m, role).queue();
                sb.append("**[-]** ").append(role.getName()).append("\n");
            } else {
                m.getRoles().forEach(r -> {
                    if(message.getButtons().containsValue(r.getId())) {
                        event.getGuild().removeRoleFromMember(m, r).queue();
                    }
                });
                event.getGuild().addRoleToMember(m, role).queue();
                sb.append("**[+]** ").append(role.getName()).append("\n");
            }
        }
        hook.editOriginal(sb.toString()).queue();
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        Button b = event.getButton();
        User u = event.getUser();
        Member m = event.getMember();
        if(m == null || b.getId() == null) {
            return;
        }
        String id = b.getId();
        if(id.startsWith("sr_")) {
            String roleId = id.replace("sr_", "");
            if(!Validate.isLong(roleId)) {
                return;
            }
            Role role = event.getGuild().getRoleById(roleId);
            if(role == null) {
                return;
            }
            InteractionHook hook = event.deferReply(true).complete();
            ReactionMessage message = getMessage(event.getMessageId());
            if(message == null) {
                hook.editOriginal("The message was not found").queue();
                return;
            }
            if(m.getRoles().contains(role)) {
                event.getGuild().removeRoleFromMember(m, role).queue();
                hook.editOriginal("**[-]** " + role.getName()).queue();
            } else {
                if(message.getType() == ReactionMessage.Type.SINGLE) {
                    m.getRoles().forEach(r -> {
                        if(message.getButtons().containsValue(r.getId())) {
                            event.getGuild().removeRoleFromMember(m, r).queue();
                        }
                    });
                }
                event.getGuild().addRoleToMember(m, role).queue();
                hook.editOriginal("**[+]** " + role.getName()).queue();
            }
        }
    }

    private ReactionMessage getMessage(String messageId) {
        for(ReactionMessage message : cache) {
            if(message.getMessageId().equals(messageId)) {
                return message;
            }
        }
        return null;
    }

}
