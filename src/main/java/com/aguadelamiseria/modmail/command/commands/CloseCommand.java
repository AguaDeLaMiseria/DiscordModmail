package com.aguadelamiseria.modmail.command.commands;

import com.aguadelamiseria.modmail.Modmail;
import com.aguadelamiseria.modmail.command.Command;
import com.aguadelamiseria.modmail.command.CommandResult;
import java.awt.Color;
import java.sql.Date;
import java.util.List;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.TextChannel;

public class CloseCommand extends Command {

    public CloseCommand(Modmail modmail) {
        super(modmail, modmail.getLang().get("command_close"), 0, -1);
        setUsage(modmail.getLang().get("usage_close"));
    }

    public CommandResult execute(Message message, String[] args) {

        String topic = message.getTextChannel().getTopic();
        if (topic == null)
            return CommandResult.NOT_A_THREAD;

        long targetID;

        try {
            targetID = Long.parseLong(topic);
        } catch (NumberFormatException exception) {
            return CommandResult.NOT_A_THREAD;
        }

        String tag = modmail.getLang().get("not_found");
        Member member = null;
        try {
            member = this.modmail.getMainGuild().retrieveMemberById(targetID).complete();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        if (member != null) tag = member.getUser().getAsTag();
        String conclusion = null;

        if (args.length > 0)
            if (String.join(" ", args).length() <= 120) {
                conclusion = String.join(" ", args);
            } else {
                return CommandResult.BIG_CONCLUSION;
            }

        boolean completed = false;
        MessageHistory messageHistory = message.getChannel().getHistory();

        do {
            List<Message> history = messageHistory.retrievePast(20).complete();
            if (!history.isEmpty())
                continue;
            completed = true;
        } while (!completed);

        TextChannel logChannel = message.getGuild().getTextChannels().stream().filter(channel -> channel.getName().equalsIgnoreCase(this.modmail.getLogsChannel())).findFirst().get();
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(Color.RED)
                .addField(modmail.getLang().get("user"), "**Tag:** " + tag + "\n" + modmail.getLang().get("formatted_id") + topic, true)
                .addField(modmail.getLang().get("staff"), "**Tag:** " + message.getAuthor().getAsTag() + "\n" + modmail.getLang().get("formatted_id") + message.getAuthor().getId(), true)
                .addField(modmail.getLang().get("conclusion"), (conclusion == null) ? modmail.getLang().get("none") : conclusion, false);

        modmail.getDataHandler().addMailLog(messageHistory.getRetrievedHistory(), topic, message.getMember().getId(), new Date(System.currentTimeMillis()), conclusion);
        logChannel.sendMessage(embedBuilder.build()).complete();
        message.getTextChannel().delete().queue();
        return CommandResult.SUCCESS;
    }
}