package com.aguadelamiseria.modmail.command.commands;

import com.aguadelamiseria.modmail.Modmail;
import com.aguadelamiseria.modmail.command.Command;
import com.aguadelamiseria.modmail.command.CommandResult;
import net.dv8tion.jda.api.entities.Message;

public class ThreadCommand extends Command {
    public ThreadCommand(Modmail modmail) {
        super(modmail, modmail.getLang().get("command_thread"), 1, 1);
        setUsage(modmail.getLang().get("usage_thread"));
    }

    public CommandResult execute(Message message, String[] args) {
        try {
            Integer.parseInt(args[0]);
        } catch (NumberFormatException nfe) {
            return CommandResult.USAGE;
        }
        modmail.getDataHandler().getMailLog(message.getTextChannel(), Integer.parseInt(args[0]));
        return CommandResult.SUCCESS;
    }
}
