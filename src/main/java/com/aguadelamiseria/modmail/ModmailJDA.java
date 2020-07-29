package com.aguadelamiseria.modmail;

import java.io.*;
import javax.security.auth.login.LoginException;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.apache.log4j.BasicConfigurator;

public class ModmailJDA {

    public static void main(String[] args) throws LoginException, InterruptedException {

        BasicConfigurator.configure();

        File tokenFile = new File(System.getProperty("user.dir"), "config.json");
        File langFile = new File(System.getProperty("user.dir"), "lang.json");

        if (!tokenFile.exists()) {
            copyResourceToFile("config.json",tokenFile);
            error("Config file not found, was created but you need to insert the contents");
        }

        if (!langFile.exists()) {
            copyResourceToFile("lang.json",langFile);
        }

        JsonObject jsonObject;

        try {
            jsonObject = JsonParser.parseReader(new FileReader(tokenFile)).getAsJsonObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        String token = jsonObject.get("token").getAsString();
        String mailGuildIDString = jsonObject.get("mailGuildID").getAsString();
        String mainGuildIDString = jsonObject.get("mainGuildID").getAsString();
        String prefix = jsonObject.get("prefix").getAsString();
        String status = jsonObject.get("status").getAsString();
        String threadCategory = jsonObject.get("thread_category").getAsString();
        String logsChannel = jsonObject.get("logs_channel").getAsString();

        if (token == null
                || mailGuildIDString == null
                || mainGuildIDString == null
                || prefix == null
                || status == null
                || threadCategory == null
                || logsChannel == null) {
            error("Some of the default config values are missing");
            return;
        }

        long mailGuildID, mainGuildID;

        try {
            mailGuildID = Long.parseLong(mailGuildIDString);
            mainGuildID = Long.parseLong(mainGuildIDString);
        } catch (NumberFormatException e) {
            error("Your server IDs are invalid");
            return;
        }
        JDA jda = JDABuilder.create(token,
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.DIRECT_MESSAGES,
                GatewayIntent.GUILD_MESSAGES)
                .disableCache(CacheFlag.VOICE_STATE,
                        CacheFlag.EMOTE,
                        CacheFlag.MEMBER_OVERRIDES,
                        CacheFlag.CLIENT_STATUS,
                        CacheFlag.ACTIVITY)
                .setActivity(Activity.playing(status))
                .build().awaitReady();

        new Modmail(jda, mailGuildID, mainGuildID, prefix, threadCategory, logsChannel);
    }

    private static void error(String s) throws InterruptedException {
        System.out.println("Configuration error: " + s);
        Thread.sleep(5000L);
    }

    private static void copyResourceToFile(String resourceName, File to){
        try {
            InputStream in = ModmailJDA.class.getResourceAsStream("/"+resourceName);
            OutputStream out = new FileOutputStream(to);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0)
                out.write(buf, 0, len);
            out.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
