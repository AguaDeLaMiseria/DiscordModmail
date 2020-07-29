package com.aguadelamiseria.modmail;

import java.awt.Color;
import java.time.format.DateTimeFormatter;
import java.util.List;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
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
        List<TextChannel> list = modmail.getMailGuild().getTextChannels();

        if (list.stream().anyMatch(channel -> channel.getTopic() != null && channel.getTopic().equalsIgnoreCase(message.getAuthor().getId()))) {
            targetChannel = modmail.getMailGuild().getTextChannels().stream().filter(channel -> channel.getTopic() != null && channel.getTopic().equals(message.getAuthor().getId())).findFirst().get();
        } else {

            Category category = modmail.getMailGuild().getCategories().stream().filter(categori -> categori.getName().equalsIgnoreCase(this.modmail.getThreadCategory())).findFirst().get();
            targetChannel = category.createTextChannel(message.getAuthor().getName()).setTopic(message.getAuthor().getId()).complete();
            Member member = modmail.getMainGuild().retrieveMember(message.getAuthor()).complete();

            String joinedDate = member.getTimeJoined().format(DateTimeFormatter.RFC_1123_DATE_TIME);
            String registeredDate = message.getAuthor().getTimeCreated().format(DateTimeFormatter.RFC_1123_DATE_TIME);

            EmbedBuilder embedOutside = new EmbedBuilder()
                    .setColor(Color.CYAN)
                    .setAuthor(modmail.getLang().get("modmail_identifier"), modmail.getJda().getSelfUser().getAvatarUrl(), modmail.getJda().getSelfUser().getAvatarUrl())
                    .addField(modmail.getLang().get("new_thread"), modmail.getLang().get("new_message"), false);
            message.getChannel().sendMessage(embedOutside.build()).queue();

            EmbedBuilder embedInfo = new EmbedBuilder()
                    .setColor(Color.GREEN)
                    .setThumbnail(message.getAuthor().getAvatarUrl())
                    .setDescription(modmail.getLang().get("new_thread_by") + " " + event.getAuthor().getAsTag())
                    .addField(modmail.getLang().get("user_details"), "Creado el " + registeredDate + "\n" + modmail.getLang().get("id") + message.getAuthor().getId(), false)
                    .addField(modmail.getLang().get("member_details"), "Se uniel " + joinedDate + "\nApodo: " + member.getEffectiveName(), false);
            targetChannel.sendMessage(embedInfo.build()).queue();
        }
        String rawMessage = message.getContentRaw();
        EmbedBuilder embedIncoming = new EmbedBuilder()
                .setColor(Color.CYAN)
                .setTitle(modmail.getLang().get("topic"))
                .setAuthor(message.getAuthor().getAsTag(), message.getAuthor().getAvatarUrl(), message.getAuthor().getAvatarUrl());

        if (!rawMessage.isEmpty())
            embedIncoming.addField(modmail.getLang().get("small_response"), message.getContentRaw(), false);
        if (!message.getAttachments().isEmpty())
            embedIncoming.setImage(message.getAttachments().stream().findFirst().get().getProxyUrl());

        targetChannel.sendMessage(embedIncoming.build()).queue();
    }
}
