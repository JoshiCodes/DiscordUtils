package de.joshizockt.discordutils.modules.message.reaction;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public abstract class ReactionMessage {

    private String message;
    private boolean sent = false;


    abstract MessageEmbed getEmbed();

    //         BUTTON LABEL , ROLE
    abstract HashMap<String, String> getButtons();
    abstract Type getType();

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
        MessageCreateAction action = channel.sendMessageEmbeds(getEmbed());
        if(getType() == Type.SINGLE || getType() == Type.MULTIPLE) {
            action.setActionRow(buttons);
        } else if(getType() == Type.SELECT_MULTIPLE || getType() == Type.SELECT_SINGLE) {
            StringSelectMenu.Builder select = StringSelectMenu.create("sr_select");
            select.setMaxValues(getType() == Type.SELECT_MULTIPLE ? getButtons().size() : 1);
            getButtons().forEach(select::addOption);
            action.setActionRow(
                    select.build()
            );
        }
        Message msg = action.complete();
        this.message = msg.getId();
        sent = true;
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
        if(getType() == Type.SINGLE || getType() == Type.MULTIPLE) {
            List<Button> buttons = new ArrayList<>();
            getButtons().forEach((label, role) -> buttons.add(Button.primary("sr_" + role, label)));
            msg.editMessageEmbeds(getEmbed()).setActionRow(buttons).queue();
        } else if(getType() == Type.SELECT_MULTIPLE || getType() == Type.SELECT_SINGLE) {
            StringSelectMenu.Builder select = StringSelectMenu.create("sr_select");
            select.setMaxValues(getType() == Type.SELECT_MULTIPLE ? getButtons().size() : 1);
            getButtons().forEach(select::addOption);
            msg.editMessageEmbeds(getEmbed()).setActionRow(
                    select.build()
            ).queue();
        }
        cacheMessage();
    }

    private void cacheMessage() {
        ReactionMessageModule.cacheMessage(this);
    }

    public static enum Type {

        MULTIPLE, SINGLE,
        SELECT_MULTIPLE, SELECT_SINGLE

    }

    public static class Builder {

        private MessageEmbed embed;

        private HashMap<String, String> buttons;
        private Type type = Type.MULTIPLE;

        public Builder setEmbed(MessageEmbed embed) {
            this.embed = embed;
            return this;
        }

        public Builder setEmbed(EmbedBuilder embed) {
            this.embed = embed.build();
            return this;
        }

        /**
         * Adds a Button to the Message.
         * @deprecated With the Change to different Selection-Types ({@link Type}),
         *             this method is deprecated and will be removed in the next major version.
         *             Use {@link #addOption(String, String)} instead.
         * @param label The label of the Button
         * @param roleId The ID of the Role
         * @return This Builder Instance
         */
        @Deprecated(forRemoval = true, since = "1.1.5d")
        public Builder addButton(String label, String roleId) {
            return addOption(label, roleId);
        }

        /**
         * Adds an Option to the Message.
         * The Options will be displayed as Buttons or in a Selection, based on the selected Type.
         * @param label The label of the Option
         * @param roleId The ID of the Role
         * @return This Builder Instance
         */
        public Builder addOption(String label, String roleId) {
            if(buttons == null) {
                buttons = new HashMap<>();
            }
            buttons.put(label, roleId);
            return this;
        }

        /**
         * Adds a Button to the Message.
         * @deprecated With the Change to different Selection-Types ({@link Type}),
         *             this method is deprecated and will be removed in the next major version.
         *             Use {@link #addOption(String, Role)} instead.
         * @param label The label of the Button
         * @param role The Role
         * @return This Builder Instance
         */
        @Deprecated(forRemoval = true, since = "1.1.5d")
        public Builder addButton(String label, Role role) {
            return addOption(label, role);
        }

        /**
         * Adds an Option to the Message.
         * The Options will be displayed as Buttons or in a Selection, based on the selected Type.
         * @param label The label of the Option
         * @param role The Role
         * @return This Builder Instance
         */
        public Builder addOption(String label, Role role) {
            if(buttons == null) {
                buttons = new HashMap<>();
            }
            buttons.put(label, role.getId());
            return this;
        }

        public Builder setType(Type type) {
            this.type = type;
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
            return detect(channel, true);
        }

        /**
         * Returns a ReactionMessage with the specified Options.
         * If the Message is already sent, it will not be sent again.
         * If the Message is not sent, {@link #build()} will be returned and {@link ReactionMessage#send(TextChannel)} will be called.
         * @param channel The Channel where the Message should be sent.
         * @param sendMessage If the Message is not sent, it will be sent.
         * @return The ReactionMessage
         */
        public ReactionMessage detect(TextChannel channel, boolean sendMessage) {

            for(Message msg : channel.getIterableHistory()) {
                if(msg.getEmbeds().isEmpty()) continue;
                if(msg.getEmbeds().get(0).getTitle() == null) continue;
                if(Objects.equals(msg.getEmbeds().get(0).getTitle(), embed.getTitle())) {
                    ReactionMessage message = new ReactionMessage() {
                        @Override
                        MessageEmbed getEmbed() {
                            return embed;
                        }

                        @Override
                        HashMap<String, String> getButtons() {
                            return buttons;
                        }

                        @Override
                        Type getType() {
                            return type;
                        }
                    };
                    message.sent = true;
                    message.message = msg.getId();
                    message.refresh(channel);
                    return message;
                }
            }
            ReactionMessage msg = build();
            if(sendMessage) msg.send(channel);
            return msg;
        }

        /**
         * Builds a new ReactionMessage
         * @return The ReactionMessage
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

                @Override
                Type getType() {
                    return type;
                }
            };
        }

    }

}
