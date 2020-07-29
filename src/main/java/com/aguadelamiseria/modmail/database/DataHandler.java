package com.aguadelamiseria.modmail.database;

import com.aguadelamiseria.modmail.Modmail;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CompletableFuture;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.utils.AttachmentOption;

public abstract class DataHandler {

    private final Modmail modmail;
    protected Connection connection;
    private static final String HISTORY_TABLE = "history_table";

    public DataHandler(Modmail modmail) {
        this.modmail = modmail;
    }

    public void addMailLog(List<Message> messages, String userID, String staffID, Date date, String reason) {
        CompletableFuture.runAsync(() -> {

            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                String stringDate = simpleDateFormat.format(date);
                PreparedStatement statement = getConnection().prepareStatement("INSERT INTO "+HISTORY_TABLE+" (user,staff,date,reason) VALUES (?,?,?,?);");
                statement.setString(1, userID);
                statement.setString(2, staffID);
                statement.setString(3, stringDate);
                statement.setString(4, reason);
                statement.executeUpdate();
                statement = getConnection().prepareStatement("SELECT id FROM "+HISTORY_TABLE+" WHERE date=? LIMIT 1;");
                statement.setString(1, stringDate);

                ResultSet resultSet = statement.executeQuery();
                int index = resultSet.getInt("id");
                File dataFolder = new File(this.modmail.getLogsFolder(), "" + index + ".txt");

                try {
                    dataFolder.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    PrintWriter printWriter = new PrintWriter(dataFolder, "UTF-8");
                    printWriter.println(modmail.getLang().get("archive_by") + modmail.getMailGuild().getName() + ".");
                    ListIterator<Message> messageListIterator = messages.listIterator(messages.size());

                    while (messageListIterator.hasPrevious()) {
                        Message msg = messageListIterator.previous();
                        if (msg.getContentRaw().isEmpty()) {
                            printWriter.println(msg.getAuthor().getAsTag() + " (ID: " + msg.getAuthor().getId() + ")");
                        } else {
                            printWriter.println(msg.getAuthor().getAsTag() + ": " + msg.getContentRaw() + " (ID: " + msg.getAuthor().getId() + ")");
                        }

                        if (!msg.getEmbeds().isEmpty())
                            for (MessageEmbed embed : msg.getEmbeds()) {
                                printWriter.println(" ");
                                if (embed.getAuthor() != null)
                                    printWriter.println(modmail.getLang().get("embed_author") + embed.getAuthor().getName());
                                if (embed.getTitle() != null)
                                    printWriter.println(modmail.getLang().get("embed_title") +embed.getTitle());
                                if (embed.getDescription() != null)
                                    printWriter.println(modmail.getLang().get("embed_description") + embed.getDescription());
                                if (!embed.getFields().isEmpty())
                                    for (MessageEmbed.Field field : embed.getFields())
                                        printWriter.println("[" + field.getName() + "] " + field.getValue());
                                if (embed.getImage() != null)
                                    printWriter.println(modmail.getLang().get("embed_image") + embed.getImage().getUrl());
                                printWriter.println(" ");
                            }
                    }
                    printWriter.close();
                } catch (FileNotFoundException|java.io.UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    public void getMailLog(TextChannel channel, int number) {
        CompletableFuture.runAsync(() -> {

            File file = new File(modmail.getLogsFolder(), number + ".txt");
            if (!file.exists()) {
                channel.sendMessage(this.modmail.getLang().get("thread_not_found")).queue();
                return;
            }
            channel.sendMessage(modmail.getLang().get("thread") + " #" + number + ":").addFile(file, new AttachmentOption[] { AttachmentOption.SPOILER }).queue();
        });
    }

    public void getUserHistory(TextChannel channel, long id) {
        CompletableFuture.runAsync(() -> {

            try {
                PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM "+HISTORY_TABLE+" WHERE user=?;");
                statement.setString(1, String.valueOf(id));
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    do {
                        String conclution = modmail.getLang().get("formatted_conclusion") + modmail.getLang().get("none");
                        if (resultSet.getString("reason") != null)
                            conclution = modmail.getLang().get("formatted_conclusion") + resultSet.getString("reason");
                        channel.sendMessage("`" + modmail.getLang().get("thread") + " #" + resultSet.getInt("id") +
                                "`\n" + modmail.getLang().get("formatted_date") + resultSet.getString("date") +
                                "\n" + modmail.getLang().get("formatted_closed_by") + resultSet.getString("staff") +
                                "\n" + conclution).queue();
                    } while (resultSet.next());
                } else {
                    channel.sendMessage(modmail.getLang().get("history_not_found") + modmail.getLang().get("history_not_found")).queue();
                }
                statement.close();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    protected abstract Connection getConnection();

    protected abstract void closeConnection();

    protected abstract void refreshConnection();
}