package com.aguadelamiseria.modmail;

import java.util.List;
import java.util.Map;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class PrivateMessageListener extends ListenerAdapter {

    private final Modmail modmail;

    public PrivateMessageListener(Modmail modmail) {
        this.modmail = modmail;
    }

    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {

        if (event.getAuthor().isBot()) return;

        TextChannel targetChannel;
        Message message = event.getMessage();
        User author = message.getAuthor();
        Map<String,String> lang = modmail.getLang();
        List<TextChannel> list = modmail.getMailGuild().getTextChannels();

        if (list.stream().anyMatch(channel -> channel.getTopic() != null
                && channel.getTopic().equalsIgnoreCase(author.getId()))) {

            targetChannel = modmail.getMailGuild().getTextChannels()
                    .stream()
                    .filter(channel -> channel.getTopic() != null && channel.getTopic().equals(author.getId()))
                    .findFirst()
                    .get();
        } else {
            Category category = modmail.getMailGuild().getCategories()
                    .stream()
                    .filter(category1 -> category1.getName().equalsIgnoreCase(this.modmail.getThreadCategory()))
                    .findFirst()
                    .get();

            targetChannel = category.createTextChannel(author.getName()).setTopic(author.getId()).complete();

            message.getChannel().sendMessage(Embeds.getAutoResponseEmbed(modmail).build()).queue();
            targetChannel.sendMessage(Embeds.getUserInfoEmbed(modmail,author).build()).queue();
        }

        String rawMessage = message.getContentRaw();
        EmbedBuilder embedIncoming = Embeds.getIncomingMessageEmbed(modmail,author);

        if (!rawMessage.isEmpty())
            embedIncoming.addField(lang.get("small_response"), message.getContentRaw(), false);
        if (!message.getAttachments().isEmpty())
            embedIncoming.setImage(message.getAttachments().stream().findFirst().get().getProxyUrl());

        targetChannel.sendMessage(embedIncoming.build()).queue();
    }
}
