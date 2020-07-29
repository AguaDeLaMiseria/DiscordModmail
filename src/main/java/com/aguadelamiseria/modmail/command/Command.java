package com.aguadelamiseria.modmail.command;

import com.aguadelamiseria.modmail.Modmail;
import net.dv8tion.jda.api.entities.Message;

public abstract class Command {

    protected Modmail modmail;
    private final String identifier;
    private String usage;
    private final int maxArguments;
    private final int minArguments;

    public Command(Modmail modmail, String identifier, int minArguments, int maxArguments) {
        this.modmail = modmail;
        this.identifier = identifier;
        this.minArguments = minArguments;
        this.maxArguments = maxArguments;
    }

    public abstract CommandResult execute(Message paramMessage, String[] paramArrayOfString);

    public String getIdentifier() {
        return this.identifier;
    }

    public void setUsage(String usage) {
        this.usage = this.identifier + " " + usage;
    }

    public String getUsage() {
        return this.usage;
    }

    public int getMaxArguments() {
        return this.maxArguments;
    }

    public int getMinArguments() {
        return this.minArguments;
    }
}
