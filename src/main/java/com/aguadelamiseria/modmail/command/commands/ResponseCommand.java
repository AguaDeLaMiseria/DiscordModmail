package com.aguadelamiseria.modmail.command.commands;

import com.aguadelamiseria.modmail.Modmail;
import com.aguadelamiseria.modmail.command.Command;
import com.aguadelamiseria.modmail.command.CommandResult;
import java.awt.Color;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.PrivateChannel;

public class ResponseCommand extends Command {

    public ResponseCommand(Modmail modmail) {
        super(modmail, modmail.getLang().get("command_response"), 0, -1);
        setUsage(modmail.getLang().get("usage_response"));
    }

    public CommandResult execute(Message message, String[] args) {

        String topic = message.getTextChannel().getTopic();
        if (topic == null) return CommandResult.NOT_A_THREAD;

        long targetID;

        try {
            targetID = Long.parseLong(topic);
        } catch (NumberFormatException exception) {
            return CommandResult.NOT_A_THREAD;
        }

        Member member;

        try {
            member = this.modmail.getMainGuild().retrieveMemberById(targetID).complete();
        } catch (Exception exception) {
            return CommandResult.PLAYER_NOT_FOUND;
        }

        if (member == null) return CommandResult.NOT_A_THREAD;

        PrivateChannel privateChannel = member.getUser().openPrivateChannel().complete();

        EmbedBuilder embedOutside = new EmbedBuilder()
                .setColor(Color.CYAN)
                .setAuthor(message.getMember().getEffectiveName() + " (" + message.getMember().getRoles().get(0).getName() + ")", message.getAuthor().getAvatarUrl(), message.getAuthor().getAvatarUrl());

        EmbedBuilder embedInside = new EmbedBuilder()
                .setColor(Color.RED)
                .setAuthor(message.getMember().getEffectiveName() + " " + modmail.getLang().get("id")+message.getMember().getId())
                .setTitle(modmail.getLang().get("response"));

        if (args.length != 0) {
            embedOutside.addField(modmail.getLang().get("small_response"), String.join(" ", args), false);
            embedInside.addField(modmail.getLang().get("small_response"), String.join(" ", args), false);
        }
        if (!message.getAttachments().isEmpty()) {
            String url = (message.getAttachments().get(0)).getProxyUrl();
            embedOutside.setImage(url);
            embedInside.setImage(url);
        } else if (args.length == 0) {
            return CommandResult.USAGE;
        }

        MessageEmbed messageEmbed = embedOutside.build();
        privateChannel.sendMessage(messageEmbed).queue();
        message.getChannel().sendMessage(embedInside.build()).complete();
        message.delete().complete();

        return CommandResult.SUCCESS;
    }
}