package com.aguadelamiseria.modmail.command.commands;

import com.aguadelamiseria.modmail.Modmail;
import com.aguadelamiseria.modmail.command.Command;
import com.aguadelamiseria.modmail.command.CommandResult;
import net.dv8tion.jda.api.entities.Message;

public class HistoryCommand extends Command {

    public HistoryCommand(Modmail modmail) {
        super(modmail, modmail.getLang().get("command_history"), 0, 1);
        setUsage(modmail.getLang().get("usage_history"));
    }

    public CommandResult execute(Message message, String[] args) {

        long targetID;
        if (args.length == 0) {
            try {
                String topic = message.getTextChannel().getTopic();
                if (topic == null)
                    return CommandResult.USAGE;
                targetID = Long.parseLong(topic);
            } catch (NumberFormatException e) {
                return CommandResult.USAGE;
            }
        } else {
            try {
                targetID = Long.parseLong(args[0]);
            } catch (NumberFormatException exception) {
                return CommandResult.USAGE;
            }
        }

        modmail.getDataHandler().getUserHistory(message.getTextChannel(), targetID);
        return CommandResult.SUCCESS;
    }
}
