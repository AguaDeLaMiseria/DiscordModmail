package com.aguadelamiseria.modmail;

import com.aguadelamiseria.modmail.command.CommandManager;
import com.aguadelamiseria.modmail.database.DataHandler;
import com.aguadelamiseria.modmail.database.SQLite;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

public class Modmail {

    private final JDA jda;
    private final String prefix;
    private final String threadCategory;
    private final String logsChannel;
    private final Guild mailGuild;
    private final Guild mainGuild;
    private final DataHandler dataHandler;
    private final File logsFolder;
    private Map<String, String> languageMap;

    public Modmail(JDA jda, long mailGuildID, long mainGuildID, String prefix, String threadCategory, String logsChannel) {

        this.jda = jda;
        this.mailGuild = jda.getGuildById(mailGuildID);
        this.mainGuild = jda.getGuildById(mainGuildID);
        this.prefix = prefix;
        this.threadCategory = threadCategory;
        this.logsChannel = logsChannel;

        try {
            this.languageMap = (new Gson()).fromJson(
                    Files.readString(Path.of(System.getProperty("user.dir") + "/lang.json")),
                    new TypeToken<HashMap<String, String>>() {}.getType());
        } catch (IOException e) {
            e.printStackTrace();
        }

        jda.addEventListener(new PrivateMessageListener(this));
        jda.addEventListener(new CommandManager(this));

        this.logsFolder = new File(System.getProperty("user.dir") + File.separator + "logs");

        if (!logsFolder.exists())
            try {
                logsFolder.mkdirs();
            } catch (SecurityException e) {
                e.printStackTrace();
            }

        this.dataHandler = new SQLite(this);
    }

    public Guild getMailGuild() {
        return this.mailGuild;
    }

    public Guild getMainGuild() {
        return this.mainGuild;
    }

    public JDA getJda() {
        return this.jda;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public File getLogsFolder() {
        return this.logsFolder;
    }

    public DataHandler getDataHandler() {
        return this.dataHandler;
    }

    public Map<String, String> getLang() {
        return this.languageMap;
    }

    public String getThreadCategory() {
        return this.threadCategory;
    }

    public String getLogsChannel() {
        return this.logsChannel;
    }
}