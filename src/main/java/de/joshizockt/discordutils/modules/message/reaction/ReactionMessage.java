package de.joshizockt.discordutils.modules.message.reaction;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.components.Component;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class ReactionMessage {

    private String message;
    private boolean sent = false;

    abstract MessageEmbed getEmbed();

    //         BUTTON LABEL , ROLE
    abstract HashMap<String, String> getButtons();

    public String getMessageId() {
        return message;
    }

    /**
     * Sends the message and creates the buttons for the roles
     * @param channel The channel where the message should be sent
     * @return true if the message was sent successfully, false if the message was already sent
     */
    public boolean send(TextChannel channel) {
        if(sent) return false;
        List<Button> buttons = new ArrayList<>();
        getButtons().forEach((label, role) -> buttons.add(Button.primary(role, label)));
        Message msg = channel.sendMessageEmbeds(getEmbed()).addActionRow(buttons).complete();
        this.message = msg.getId();
        cacheMessage();
        return true;
    }

    /**
     * Updates the message with the embed and the Buttons
     * Replaces the content of the old message and the old buttons
     * @param channel The channel where the message is
     */
    public void refresh(TextChannel channel) {
        Message msg = channel.retrieveMessageById(getMessageId()).complete();
        if(msg == null) return;
        List<Button> buttons = new ArrayList<>();
        getButtons().forEach((label, role) -> buttons.add(Button.primary("sr_" + role, label)));
        msg.editMessageEmbeds(getEmbed()).setActionRow(buttons).queue();
        cacheMessage();
    }

    private void cacheMessage() {
        ReactionMessageModule.cacheMessage(this);
    }

    public static class Builder {

        private MessageEmbed embed;

        private HashMap<String, String> buttons;

        public Builder setEmbed(MessageEmbed embed) {
            this.embed = embed;
            return this;
        }

        public Builder setEmbed(EmbedBuilder embed) {
            this.embed = embed.build();
            return this;
        }

        public Builder addButton(String label, String roleId) {
            if(buttons == null) {
                buttons = new HashMap<>();
            }
            buttons.put(label, roleId);
            return this;
        }

        public Builder addButton(String label, Role role) {
            if(buttons == null) {
                buttons = new HashMap<>();
            }
            buttons.put(label, role.getId());
            return this;
        }

        /**
         * Returns a ReactionMessage with the specified Options.
         * If the Message is already sent, it will not be sent again.
         * If the Message is not sent, {@link #build()} will be returned and {@link ReactionMessage#send(TextChannel)} will be called.
         * @param channel The Channel where the Message should be sent.
         * @return The ReactionMessage
         */
        public ReactionMessage detect(TextChannel channel) {

            for(Message msg : channel.getIterableHistory()) {
                if(msg.getEmbeds().size() < 1) continue;
                if(msg.getEmbeds().get(0).getTitle() == null) continue;
                if(msg.getEmbeds().get(0).getTitle().equals(embed.getTitle())) {
                    ReactionMessage message = new ReactionMessage() {
                        @Override
                        MessageEmbed getEmbed() {
                            return embed;
                        }

                        @Override
                        HashMap<String, String> getButtons() {
                            return buttons;
                        }
                    };
                    message.sent = true;
                    message.message = msg.getId();
                    message.refresh(channel);
                    return message;
                }
            }
            ReactionMessage msg = build();
            msg.send(channel);
            return msg;
        }

        /**
         * Builds a new ReactionMessage
         * @return
         */
        public ReactionMessage build() {
            return new ReactionMessage() {
                @Override
                MessageEmbed getEmbed() {
                    return embed;
                }

                @Override
                HashMap<String, String> getButtons() {
                    return buttons;
                }
            };
        }

    }

}
