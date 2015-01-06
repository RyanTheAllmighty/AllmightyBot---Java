/*
 * Allmighty Bot - https://github.com/RyanTheAllmighty/AllmightyBot
 * Copyright (C) 2014 Ryan Dowling
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package me.ryandowling.allmightybot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import me.ryandowling.allmightybot.commands.BaseCommand;
import me.ryandowling.allmightybot.commands.Command;
import me.ryandowling.allmightybot.commands.CommandBus;
import me.ryandowling.allmightybot.commands.TempCommand;
import me.ryandowling.allmightybot.data.ChatLog;
import me.ryandowling.allmightybot.data.Event;
import me.ryandowling.allmightybot.data.EventType;
import me.ryandowling.allmightybot.data.SeedType;
import me.ryandowling.allmightybot.data.Settings;
import me.ryandowling.allmightybot.data.Spam;
import me.ryandowling.allmightybot.data.TwitchChatters;
import me.ryandowling.allmightybot.data.WorldType;
import me.ryandowling.allmightybot.listeners.CommandListener;
import me.ryandowling.allmightybot.listeners.LinkListener;
import me.ryandowling.allmightybot.listeners.SpamListener;
import me.ryandowling.allmightybot.listeners.StartupListener;
import me.ryandowling.allmightybot.listeners.UserListener;
import me.ryandowling.allmightybot.utils.TwitchAPI;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;

import javax.swing.JOptionPane;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class AllmightyBot {
    public final static Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Logger logger = LogManager.getLogger(App.class.getName());
    private static final String DATE = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
    private Settings settings;
    private PircBotX pirc;

    private boolean shutDown = false;

    private boolean isShuttingDown = false;

    private Map<String, Date> userJoined;
    private Map<String, Map<String, Integer>> userOnlineTime;
    private Map<String, List<ChatLog>> userLogs;
    private List<Event> events;
    private Map<String, Integer> streamOnlineTime;

    private List<Spam> spams;

    private List<String> allowedLinks;

    private StartupListener startupListener = new StartupListener(this);
    private UserListener userListener = new UserListener(this);
    private CommandListener commandListener = new CommandListener(this);
    private SpamListener spamListener = new SpamListener(this);
    private LinkListener linkListener = new LinkListener(this);

    private Map<String, Integer> topUsers;

    public AllmightyBot() {
        if (Files.exists(Utils.getSettingsFile())) {
            try {
                this.settings = GSON.fromJson(FileUtils.readFileToString(Utils.getSettingsFile().toFile()), Settings
                        .class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            this.settings = new Settings();
        }

        if (!this.settings.hasInitialSetupBeenCompleted()) {
            String input = null;

            do {
                input = JOptionPane.showInputDialog(null, "Please enter the username of the Twitch user " + "to act " +
                        "as the bot", "Twitch Username", JOptionPane.QUESTION_MESSAGE);
                settings.setTwitchUsername(input);
            } while (input == null);

            do {
                input = JOptionPane.showInputDialog(null, "Please enter the token of the Twitch user " + "acting as " +
                        "the bot", "Twitch Token", JOptionPane.QUESTION_MESSAGE);
                settings.setTwitchToken(input);
            } while (input == null);

            do {
                input = JOptionPane.showInputDialog(null, "Please enter the API token of the Twitch user " + "acting " +
                        "as the bot", "Twitch API Token", JOptionPane.QUESTION_MESSAGE);
                settings.setTwitchApiToken(input);
            } while (input == null);

            do {
                input = JOptionPane.showInputDialog(null, "Please enter the API client ID of the application using "
                        + "the Twitch API", "Twitch API Client ID", JOptionPane.QUESTION_MESSAGE);
                settings.setTwitchApiClientID(input);
            } while (input == null);

            do {
                input = JOptionPane.showInputDialog(null, "Please enter the username of the Twitch user whose " +
                        "channel you wish to join! Must be in all lowercase", "User To Join", JOptionPane
                        .QUESTION_MESSAGE);
                settings.setTwitchChannel(input);
            } while (input == null);

            do {
                input = JOptionPane.showInputDialog(null, "Please enter the name of the caster to display when " +
                        "referencing them", "Casters Name", JOptionPane.QUESTION_MESSAGE);
                settings.setCastersName(input);
            } while (input == null);

            this.settings.initialSetupComplete();
        }

        Configuration.Builder<PircBotX> config = this.settings.getBuilder();

        // Register the different listeners
        config.addListener(this.startupListener);
        config.addListener(this.userListener);
        config.addListener(this.commandListener);
        config.addListener(this.spamListener);
        config.addListener(this.linkListener);

        addShutdownHook();

        this.pirc = new PircBotX(config.buildConfiguration());
    }

    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                shutDown();
            }
        }));
    }

    /**
     * Starts the bot up
     */

    public void startUp(boolean newStream) {
        logger.info("Bot starting up!");

        if (newStream) {
            logger.info("This is a new stream!");
            this.settings.setStartTime(new Date());
        } else {
            logger.info("This is continuing an existing stream!");
        }

        // Check the Twitch API token to make sure it's all good
        TwitchAPI.checkToken();

        this.userJoined = new ConcurrentHashMap<>();
        this.userOnlineTime = new ConcurrentHashMap<>();
        this.userLogs = new ConcurrentHashMap<>();
        this.events = new ArrayList<>();
        this.streamOnlineTime = new ConcurrentHashMap<>();

        this.spams = new ArrayList<>();
        this.allowedLinks = new ArrayList<>();

        loadCommands();
        loadUserOnlineTime();
        loadStreamOnlineTime();
        loadSpamWatchers();
        loadAllowedLinks();
        loadEvents();
        loadInitialUsers();

        Runnable r = new Runnable() {
            public void run() {
                while (true) {
                    if (shutDown) {
                        shutDown();
                        break;
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        new Thread(r).start();

        try {
            this.pirc.startBot();
        } catch (IOException | IrcException e) {
            e.printStackTrace();
        }

        // Shut it all down if we are still connected
        if (this.pirc.isConnected()) {
            this.shutDown();
        }
    }

    private void loadEvents() {
        // Load the events from a previous run for today if they exist
        if (Files.exists(Utils.getEventsFile())) {
            try {
                Type listType = new TypeToken<ArrayList<Event>>() {
                }.getType();
                this.events = GSON.fromJson(FileUtils.readFileToString(Utils.getEventsFile().toFile()), listType);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadInitialUsers() {
        try {
            String json = Utils.readURLToString("http://tmi.twitch.tv/group/user/" + this.settings.getTwitchChannel() +
                    "/chatters");
            TwitchChatters chatters = GSON.fromJson(json, TwitchChatters.class);

            for (Map.Entry<String, List<String>> entry : chatters.getChatters().entrySet()) {
                String key = entry.getKey();
                List<String> value = entry.getValue();

                for (String user : value) {
                    this.userJoined(user);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadCommands() {
        // Add all the commands
        try {
            Type listType = new TypeToken<ArrayList<TempCommand>>() {
            }.getType();
            List<TempCommand> tempCommands = GSON.fromJson(FileUtils.readFileToString(Utils.getCommandsFile().toFile
                    ()), listType);

            if (tempCommands != null) {
                for (TempCommand command : tempCommands) {
                    try {
                        Class<?> commandClass = Class.forName(command.getType());
                        Constructor<?> commandConstructor;
                        Command commandToAdd;

                        if (command.isSeedCommand()) {
                            commandConstructor = commandClass.getConstructor(String.class, SeedType.class, int.class);
                            commandToAdd = (BaseCommand) commandConstructor.newInstance(command.getName(), command
                                    .getSeedType(), command.getTimeout());
                        } else if (command.isWorldCommand()) {
                            commandConstructor = commandClass.getConstructor(String.class, WorldType.class, int.class);
                            commandToAdd = (BaseCommand) commandConstructor.newInstance(command.getName(), command
                                    .getWorldType(), command.getTimeout());
                        } else if (command.hasReply()) {
                            commandConstructor = commandClass.getConstructor(String.class, String.class, int.class);
                            commandToAdd = (BaseCommand) commandConstructor.newInstance(command.getName(), command
                                    .getReply(), command.getTimeout());
                        } else {
                            commandConstructor = commandClass.getConstructor(String.class, int.class);
                            commandToAdd = (BaseCommand) commandConstructor.newInstance(command.getName(), command
                                    .getTimeout());
                        }

                        commandToAdd.setLevel(command.getLevel());

                        commandToAdd.load(); // Load anything the command requires

                        CommandBus.add(commandToAdd);
                        logger.debug("Added command !" + command.getName() + " of type " + command.getType());
                    } catch (ClassNotFoundException e) {
                        logger.error("No class found for !" + command.getName() + " with type of " + command.getType());
                    } catch (InstantiationException | InvocationTargetException | NoSuchMethodException |
                            IllegalAccessException e) {
                        logger.error("Error loading command !" + command.getName() + " with type of " + command
                                .getType());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadUserOnlineTime() {
        List<String> fileNames = new ArrayList<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Utils.getUsersDir())) {
            for (Path path : directoryStream) {
                if (Files.isDirectory(path)) {
                    String username = path.getFileName().toString();
                    if (Files.exists(Utils.getUserLoginTimeFile(username))) {
                        Type type = new TypeToken<Map<String, Integer>>() {
                        }.getType();
                        Map<String, Integer> timeInChannel = GSON.fromJson(FileUtils.readFileToString(Utils
                                .getUserLoginTimeFile(username).toFile()), type);
                        this.userOnlineTime.put(username, timeInChannel);
                    }
                }
            }
        } catch (IOException ex) {
        }
    }

    private void loadStreamOnlineTime() {
        if (!Files.exists(Utils.getStreamOnlineTimeFile())) {
            return;
        }

        try {
            Type type = new TypeToken<Map<String, Integer>>() {
            }.getType();
            this.streamOnlineTime = GSON.fromJson(FileUtils.readFileToString(Utils.getStreamOnlineTimeFile().toFile()
            ), type);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadSpamWatchers() {
        // Add all spam banning/timeout things
        try {
            Type listType = new TypeToken<ArrayList<Spam>>() {
            }.getType();
            this.spams = GSON.fromJson(FileUtils.readFileToString(Utils.getSpamFile().toFile()), listType);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadAllowedLinks() {
        // Add all allowed links
        try {
            Type listType = new TypeToken<ArrayList<String>>() {
            }.getType();
            this.allowedLinks = GSON.fromJson(FileUtils.readFileToString(Utils.getLinksFile().toFile()), listType);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reload() {
        CommandBus.removeAll();

        loadCommands();
    }

    /**
     * Issues a shutdown command to safely shutdown and save all files needed
     */
    public void shutDown() {
        if (isShuttingDown) {
            return;
        }

        this.isShuttingDown = true;

        logger.info("Bot shutting down!");

        for (Map.Entry<String, Command> entry : CommandBus.getAll().entrySet()) {
            Command command = entry.getValue();
            command.save();
        }

        try {
            FileUtils.write(Utils.getSettingsFile().toFile(), GSON.toJson(this.settings));
            logger.debug("Settings saved!");

            saveAllOnlineTime();

            for (Map.Entry<String, Map<String, Integer>> entry : this.userOnlineTime.entrySet()) {
                String key = entry.getKey();
                Map<String, Integer> value = entry.getValue();

                if (key == null || value == null) {
                    continue;
                }

                FileUtils.write(Utils.getUserLoginTimeFile(key).toFile(), GSON.toJson(value));
            }
            logger.debug("User login time saved!");

            for (Map.Entry<String, List<ChatLog>> entry : this.userLogs.entrySet()) {
                String key = entry.getKey();
                List<ChatLog> values = entry.getValue();

                if (key == null || values == null) {
                    continue;
                }

                FileUtils.write(Utils.getUserChatFile(key).toFile(), GSON.toJson(values));
            }
            logger.debug("User logs saved!");

            FileUtils.write(Utils.getEventsFile().toFile(), GSON.toJson(this.events));
            logger.debug("Events saved!");

            saveStreamOnlineTime();

            FileUtils.write(Utils.getStreamOnlineTimeFile().toFile(), GSON.toJson(this.streamOnlineTime));
            logger.debug("Online Time saved!");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (this.pirc.isConnected()) {
            this.pirc.sendIRC().quitServer();
        }
    }

    private void saveStreamOnlineTime() {
        // Calculate and add the time we've been online for today
        int timeOnline = (int) Utils.getDateDiff(this.settings.getStartTime(), new Date(), TimeUnit.SECONDS);
        this.streamOnlineTime.put(DATE, timeOnline);
    }

    public int timeInChannel(String nick) {
        if (this.isShuttingDown) {
            logger.debug("We're shutting down, so no more requests please!");
        }

        if (nick == null) {
            logger.debug("A null user was passed into timeInChannel!");
            return 0;
        }

        if (nick.equalsIgnoreCase(this.getSettings().getTwitchUsername())) {
            return 0; // Bot should not be considered here
        }

        if (!this.userOnlineTime.containsKey(nick)) {
            return 0;
        }

        int timeOnline = 0;

        for (Map.Entry<String, Integer> entry : this.userOnlineTime.get(nick).entrySet()) {
            timeOnline += entry.getValue();
        }

        Date joined = this.userJoined.get(nick);

        if (joined != null) {
            // Add the time they've been in the channel for now
            timeOnline += (int) Utils.getDateDiff(joined, new Date(), TimeUnit.SECONDS);
        }

        return timeOnline;
    }

    public void userJoined(String nick) {
        if (this.isShuttingDown) {
            logger.debug("We're shutting down, so no more requests please!");
        }

        if (nick == null) {
            logger.debug("A null user joined!");
            return;
        }

        nick = nick.toLowerCase();

        if (nick.equalsIgnoreCase(this.getSettings().getTwitchUsername())) {
            return; // Bot should not be considered here
        }

        logger.debug("User " + nick + " joined!");
        this.events.add(new Event(EventType.USERJOIN, nick));

        Date joined = this.userJoined.get(nick);

        if (joined == null) {
            // User has just joined
            joined = new Date();
        } else {
            // User was already joined so add their time
            Map<String, Integer> timeOnline = this.userOnlineTime.get(nick);
            int online = 0;

            if (timeOnline != null && timeOnline.containsKey(DATE)) {
                online = timeOnline.get(DATE);
                online += (int) Utils.getDateDiff(joined, new Date(), TimeUnit.SECONDS);
            } else {
                timeOnline = new HashMap<>();
            }

            timeOnline.put(DATE, online);

            this.userOnlineTime.put(nick, timeOnline);
        }

        this.userJoined.put(nick, joined);
    }

    public void userParted(String nick, boolean kicked) {
        if (this.isShuttingDown) {
            logger.debug("We're shutting down, so no more requests please!");
        }

        if (nick == null) {
            logger.debug("A null user parted!");
            return;
        }

        nick = nick.toLowerCase();

        if (nick.equalsIgnoreCase(this.getSettings().getTwitchUsername())) {
            return; // Bot should not be considered here
        }

        logger.debug("User " + nick + " parted!");
        this.events.add(new Event((kicked ? EventType.USERKICK : EventType.USERPART), nick));

        Date joined = this.userJoined.get(nick);

        if (joined != null && this.userOnlineTime.containsKey(nick)) {
            // User has left so add their time
            Map<String, Integer> timeOnline = this.userOnlineTime.get(nick);
            int online = 0;

            if (timeOnline != null) {
                online = timeOnline.get(DATE);
                online += (int) Utils.getDateDiff(joined, new Date(), TimeUnit.SECONDS);
            } else {
                timeOnline = new HashMap<>();
            }

            timeOnline.put(DATE, online);

            this.userOnlineTime.put(nick, timeOnline);
        }

        // Remove the user from the time joined list since they have left
        this.userJoined.remove(nick);
    }

    private void saveAllOnlineTime() {
        for (Map.Entry<String, Date> entry : this.userJoined.entrySet()) {
            String key = entry.getKey();
            Date value = entry.getValue();

            Map<String, Integer> timeOnline = this.userOnlineTime.get(key);
            int online = 0;

            if (timeOnline != null && timeOnline.containsKey(DATE)) {
                online = timeOnline.get(DATE);
                online += (int) Utils.getDateDiff(value, new Date(), TimeUnit.SECONDS);
            } else {
                timeOnline = new HashMap<>();
            }

            timeOnline.put(DATE, online);

            this.userOnlineTime.put(key, timeOnline);

            this.userJoined.remove(key);
        }
    }

    public void userSpoke(String nick, String message) {
        if (this.isShuttingDown) {
            logger.debug("We're shutting down, so no more requests please!");
        }

        if (nick == null) {
            logger.debug("A null user spoke!");
            return;
        }

        nick = nick.toLowerCase();

        if (nick.equalsIgnoreCase(this.getSettings().getTwitchUsername())) {
            return; // Bot should not be considered here
        }

        logger.debug("User " + nick + " spoke!");
        this.events.add(new Event(EventType.USERMESSAGE, nick));

        if (!this.userOnlineTime.containsKey(nick)) {
            this.userJoined(nick);
        }

        List<ChatLog> logs = this.userLogs.get(nick);

        if (logs == null) {
            logs = new ArrayList<>();
        }

        logs.add(new ChatLog(nick, message));

        this.userLogs.put(nick, logs);
    }

    public Settings getSettings() {
        return this.settings;
    }

    public boolean isRegular(String nick) {
        return false;
    }

    public List<Spam> getSpams() {
        return this.spams;
    }

    public void removeStartupListener() {
        if (this.startupListener != null) {
            this.pirc.getConfiguration().getListenerManager().removeListener(this.startupListener);
            this.startupListener = null;
        }
    }

    public void removeUserListener() {
        if (this.userListener != null) {
            this.pirc.getConfiguration().getListenerManager().removeListener(this.userListener);
            this.userListener = null;
        }
    }

    public void removeCommandListener() {
        if (this.commandListener != null) {
            this.pirc.getConfiguration().getListenerManager().removeListener(this.commandListener);
            this.commandListener = null;
        }
    }

    public void removeSpamListener() {
        if (this.spamListener != null) {
            this.pirc.getConfiguration().getListenerManager().removeListener(this.spamListener);
            this.spamListener = null;
        }
    }

    public void removeLinkListener() {
        if (this.linkListener != null) {
            this.pirc.getConfiguration().getListenerManager().removeListener(this.linkListener);
            this.linkListener = null;
        }
    }

    public void triggerShutdown() {
        // Remove the listeners
        this.removeStartupListener();
        this.removeUserListener();
        this.removeCommandListener();
        this.removeSpamListener();
        this.removeLinkListener();

        this.shutDown = true;
    }

    public int getTotalLiveTime(boolean forceSave) {
        int timeOnline = 0;

        if (forceSave) {
            saveStreamOnlineTime();
        }

        for (Map.Entry<String, Integer> entry : this.streamOnlineTime.entrySet()) {
            timeOnline += entry.getValue();
        }

        return timeOnline;
    }

    public List<String> getAllowedLinks() {
        return this.allowedLinks;
    }
}
