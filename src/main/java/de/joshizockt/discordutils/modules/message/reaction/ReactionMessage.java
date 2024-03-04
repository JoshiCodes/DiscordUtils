package de.joshizockt.discordutils.modules.message.reaction;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.internal.utils.tuple.Pair;

import java.util.*;

public abstract class ReactionMessage {

    private String message;
    private boolean sent = false;


    abstract MessageEmbed getEmbed();

    //         BUTTON LABEL , ROLE
    abstract TreeMap<String, Pair<String, ButtonStyle>> getButtons();
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
        getButtons().forEach((label, pair) -> buttons.add(Button.of(pair.getRight(), "sr_" + pair.getLeft(), label)));
        MessageCreateAction action = channel.sendMessageEmbeds(getEmbed());
        if(getType() == Type.SINGLE || getType() == Type.MULTIPLE) {
            List<ActionRow> rows = new ArrayList<>();
            // 5 Buttons per Row
            for(int i = 0; i < buttons.size(); i += 5) {
                List<Button> row = new ArrayList<>();
                for(int j = i; j < i + 5; j++) {
                    if(j >= buttons.size()) break;
                    row.add(buttons.get(j));
                }
                rows.add(ActionRow.of(row));
            }
            action.setComponents(rows);
        } else if(getType() == Type.SELECT_MULTIPLE || getType() == Type.SELECT_SINGLE) {
            StringSelectMenu.Builder select = StringSelectMenu.create("sr_select");
            select.setMaxValues(getType() == Type.SELECT_MULTIPLE ? getButtons().size() : 1);
            getButtons().forEach((label, pair) -> select.addOption(label, pair.getLeft())); // no style needed
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
            getButtons().forEach((label, pair) -> buttons.add(Button.of(pair.getRight(), "sr_" + pair.getLeft(), label)));
            List<ActionRow> rows = new ArrayList<>();
            // 5 Buttons per Row
            for(int i = 0; i < buttons.size(); i += 5) {
                List<Button> row = new ArrayList<>();
                for(int j = i; j < i + 5; j++) {
                    if(j >= buttons.size()) break;
                    row.add(buttons.get(j));
                }
                rows.add(ActionRow.of(row));
            }
            msg.editMessageEmbeds(getEmbed()).setComponents(rows).queue();
        } else if(getType() == Type.SELECT_MULTIPLE || getType() == Type.SELECT_SINGLE) {
            StringSelectMenu.Builder select = StringSelectMenu.create("sr_select");
            select.setMaxValues(getType() == Type.SELECT_MULTIPLE ? getButtons().size() : 1);
            getButtons().forEach((label, pair) -> select.addOption(label, pair.getLeft())); // no style needed
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

        private TreeMap<String, Pair<String, ButtonStyle>> buttons;
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
         * @param style The Style of the Button (only needed for Buttons)
         * @return This Builder Instance
         */
        public Builder addOption(String label, String roleId, ButtonStyle style) {
            if(buttons == null) {
                buttons = new TreeMap<>();
            }
            buttons.put(label, Pair.of(roleId, style));
            return this;
        }

        /**
         * Adds an Option to the Message. Uses the PRIMARY Style for the Button.
         * @param label The label of the Option
         * @param roleId The ID of the Role
         * @return This Builder Instance
         */
        public Builder addOption(String label, String roleId) {
            return addOption(label, roleId, ButtonStyle.PRIMARY);
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
         * The Options will be displayed as Buttons (with the selected style) or in a Selection, based on the selected Type.
         * @param label The label of the Option
         * @param role The Role
         * @param style The Style of the Button (only needed for Buttons)
         * @return This Builder Instance
         */
        public Builder addOption(String label, Role role, ButtonStyle style) {
            return addOption(label, role.getId(), style);
        }

        /**
         * Adds an Option to the Message.
         * The Options will be displayed as Buttons or in a Selection, based on the selected Type.
         * @param label The label of the Option
         * @param role The Role
         * @return This Builder Instance
         */
        public Builder addOption(String label, Role role) {
            return addOption(label, role.getId());
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
                        TreeMap<String, Pair<String, ButtonStyle>> getButtons() {
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
                TreeMap<String, Pair<String, ButtonStyle>> getButtons() {
                    return buttons;
                }

                @Override
                Type getType() {
                    return type;
                }
            };
        }

        public TreeMap<String, Pair<String, ButtonStyle>> getButtons() {
            return buttons;
        }

    }

}
