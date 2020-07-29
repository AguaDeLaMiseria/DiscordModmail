package com.aguadelamiseria.modmail.command.commands;

import com.aguadelamiseria.modmail.Modmail;
import com.aguadelamiseria.modmail.command.Command;
import com.aguadelamiseria.modmail.command.CommandResult;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import net.dv8tion.jda.api.entities.Message;

public class StatusCommand extends Command {

    public StatusCommand(Modmail modmail) {
        super(modmail, modmail.getLang().get("command_status"), 0, 0);
        setUsage(modmail.getLang().get("usage_status"));
    }

    public CommandResult execute(Message message, String[] args) {

        String uptimeString;
        Runtime runtime = Runtime.getRuntime();
        RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();

        long uptime = rb.getUptime();
        long maxMemory = runtime.maxMemory() / 1024 / 1024;
        long allocatedMemory = runtime.totalMemory() / 1024 / 1024;
        long freeMemory = runtime.freeMemory() / 1024 / 1024;

        if (uptime < 3600000) {
            uptimeString = uptime / 60000 + " " + modmail.getLang().get("minutes");
        } else if (uptime < 86400000) {
            uptimeString = uptime / 3600000 + " " + modmail.getLang().get("hours");
        } else {
            uptimeString = uptime / 86400000 + " " + modmail.getLang().get("days");
        }
        message.getChannel().sendMessage(
                modmail.getLang().get("uptime") + uptimeString + "\n" +
                modmail.getLang().get("max_memory")  + maxMemory + " MB\n" +
                modmail.getLang().get("assigned_memory") + allocatedMemory + " MB\n" +
                modmail.getLang().get("free_memory") + freeMemory + " MB").queue();

        long time = System.currentTimeMillis();
        message.getChannel().sendMessage("**Ping: **").queue(msg -> msg.editMessageFormat("**Ping: **%d ms", System.currentTimeMillis() - time).queue());
        return CommandResult.SUCCESS;
    }
}
