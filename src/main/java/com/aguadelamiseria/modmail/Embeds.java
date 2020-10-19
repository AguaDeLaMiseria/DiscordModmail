package com.aguadelamiseria.modmail;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.time.format.DateTimeFormatter;

public class Embeds {

    public static EmbedBuilder getUserInfoEmbed(Modmail modmail, User user){
        Member member = modmail.getMainGuild().retrieveMember(user).complete();

        return new EmbedBuilder()
                .setColor(Color.GREEN)
                .setThumbnail(user.getAvatarUrl())
                .setDescription(modmail.getLang().get("new_thread_by") + " " + user.getAsTag())
                .addField(modmail.getLang().get("user_details"),
                        "Creado el " + user.getTimeCreated().format(DateTimeFormatter.RFC_1123_DATE_TIME) +
                                "\n" + modmail.getLang().get("id") + user.getId(), false)
                .addField(modmail.getLang().get("member_details"),
                        "Se unio el " + member.getTimeJoined().format(DateTimeFormatter.RFC_1123_DATE_TIME) +
                                "\nApodo: " + member.getEffectiveName(), false);
    }

    public static EmbedBuilder getAutoResponseEmbed(Modmail modmail){
        return new EmbedBuilder()
                .setColor(Color.CYAN)
                .setAuthor(modmail.getLang().get("modmail_identifier"),
                        modmail.getJda().getSelfUser().getAvatarUrl(), modmail.getJda().getSelfUser().getAvatarUrl())
                .addField(modmail.getLang().get("new_thread"), modmail.getLang().get("new_message"), false);
    }

    public static EmbedBuilder getIncomingMessageEmbed(Modmail modmail, User author){
        return new EmbedBuilder()
                .setColor(Color.CYAN)
                .setTitle(modmail.getLang().get("topic"))
                .setAuthor(author.getAsTag(), author.getAvatarUrl(), author.getAvatarUrl());
    }

    public static EmbedBuilder anonymousOut(Modmail modmail){
        return new EmbedBuilder()
                .setColor(Color.CYAN)
                .setAuthor(
                        modmail.getLang().get("modmail_identifier"),
                        modmail.getJda().getSelfUser().getAvatarUrl(),
                        modmail.getJda().getSelfUser().getAvatarUrl());
    }
    public static EmbedBuilder anonymousIn(Modmail modmail, Member member){
        return new EmbedBuilder()
                .setColor(Color.RED)
                .setAuthor(
                        member.getEffectiveName() + " " + modmail.getLang().get("id")+member.getId())
                .setTitle(modmail.getLang().get("anonymous_response"));
    }

    public static EmbedBuilder responseOut(Member member) {
        return new EmbedBuilder()
                .setColor(Color.CYAN)
                .setAuthor(
                        member.getEffectiveName() + " (" + member.getRoles().get(0).getName() + ")",
                        member.getUser().getAvatarUrl(),
                        member.getUser().getAvatarUrl());
    }

    public static EmbedBuilder responseIn(Modmail modmail, Member member){
        return new EmbedBuilder()
                .setColor(Color.RED)
                .setAuthor(
                        member.getEffectiveName() + " " + modmail.getLang().get("id")+member.getId())
                .setTitle(modmail.getLang().get("response"));
    }
}
