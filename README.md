# DiscordModMail
 Simple Discord ModMail Bot for you server.
 
## Description
DiscordModMail is a discord bot that implements the Java Discord API. It allows users from your server to contact staff privately by sending a direct message to the bot. The bot will create a different channel for each user (threads) in a separate discord server (modmail server), where you can discuss and reply accordingly through the bot. You can close the thread at any time, and it will log the thread contents in a .txt file under the *logs* folder, which you can access later through the thread command.

## Features
* Two-server setup
* Custom bot activity
* Logs all threads
* All messages are configurable
* User thread history
* Images support
* Named and anonymous responses
 
 ## Instalation
 Because of how the bot works, you have to host it remotely (like a VPS or dedicated machine).
 
 Clone the repo 
 ```console
 git clone https://github.com/AguaDeLaMiseria/DiscordModMail
 ``` 
 Build the .jar file
 ```console
 mvn install
 ```
 Start the bot
 ```console
 java -jar bot.jar
 ```

 From there, the bot will stop and create a *config.json* file, where you will need to configure the bot, providing your bot token and server IDs. You will also need a category  in the modmail server named as in the config file, and a channel for logging named as in the config file too.
 You can see default configuration files [**here**](https://github.com/AguaDeLaMiseria/DiscordModMail/tree/master/src/main/resources).

## TODO
* Better embed customization
* Editing and deleting messages
* Permission system
* Ability to block users
* Option to block too new users or recently joined members
