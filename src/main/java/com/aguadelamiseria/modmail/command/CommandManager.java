package com.aguadelamiseria.modmail.command;

import com.aguadelamiseria.modmail.Modmail;
import com.aguadelamiseria.modmail.command.commands.AnonymousResponseCommand;
import com.aguadelamiseria.modmail.command.commands.CloseCommand;
import com.aguadelamiseria.modmail.command.commands.HistoryCommand;
import com.aguadelamiseria.modmail.command.commands.ResponseCommand;
import com.aguadelamiseria.modmail.command.commands.StatusCommand;
import com.aguadelamiseria.modmail.command.commands.ThreadCommand;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandManager extends ListenerAdapter {

    private final Modmail modmail;
    private final List<Command> commandList = new ArrayList<>();

    public CommandManager(Modmail modmail) {
        this.modmail = modmail;

        this.commandList.add(new ResponseCommand(modmail));
        this.commandList.add(new AnonymousResponseCommand(modmail));
        this.commandList.add(new CloseCommand(modmail));
        this.commandList.add(new ThreadCommand(modmail));
        this.commandList.add(new HistoryCommand(modmail));
        this.commandList.add(new StatusCommand(modmail));
    }

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        Message message = event.getMessage();
        if (message.getMember() == null) return;
        String rawMessage = message.getContentRaw();
        if (rawMessage.startsWith(this.modmail.getPrefix())) {
            String[] preArgs = rawMessage.split("\\s+");
            String[] args = Arrays.copyOfRange(preArgs, 1, preArgs.length);
            String commandName = preArgs[0].substring(this.modmail.getPrefix().length());
            tryCommand(message, commandName, args);
        }
    }

    public void tryCommand(Message message, String commandName, String[] args) {

        Command command = null;
        for (Command commandFromList : this.commandList) {
            if (commandFromList.getIdentifier().equalsIgnoreCase(commandName)) {
                command = commandFromList;
                break;
            }
        }
        if (command == null)
            return;

        if (command.getMinArguments() > args.length) {
            sendErrorEmbed(command, message.getChannel(), modmail.getLang().get("too_few_args"));
            return;
        }
        if (command.getMaxArguments() < args.length && command.getMaxArguments() != -1) {
            sendErrorEmbed(command, message.getChannel(), modmail.getLang().get("too_many_args"));
            return;
        }

        CommandResult commandResult = command.execute(message, args);

        String error;

        switch (commandResult) {
            case SUCCESS:
                return;
            case PLAYER_NOT_FOUND:
                error = modmail.getLang().get("player_not_found");
                break;
            case USAGE:
                error = modmail.getLang().get("usage");
                break;
            case NOT_A_THREAD:
                error = modmail.getLang().get("not_a_thread");
                break;
            case BIG_CONCLUSION:
                error = modmail.getLang().get("big_conclusion");
                break;
            default:
                error = modmail.getLang().get("unknown");
                break;
        }
        sendErrorEmbed(command, message.getChannel(), error);
    }

    private void sendErrorEmbed(Command command, MessageChannel channel, String error) {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle(modmail.getLang().get("wrong_usage_of") + this.modmail.getPrefix()+command.getIdentifier())
                .setColor(Color.RED)
                .addField(modmail.getLang().get("error"), error, false)
                .addField(modmail.getLang().get("use"), this.modmail.getPrefix() + command.getUsage(), false);

        channel.sendMessage(embedBuilder.build()).queue();
    }
}
