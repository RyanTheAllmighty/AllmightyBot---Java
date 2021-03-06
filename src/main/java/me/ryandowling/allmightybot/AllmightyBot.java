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
import me.ryandowling.allmightybot.data.QuoteType;
import me.ryandowling.allmightybot.data.SeedType;
import me.ryandowling.allmightybot.data.Settings;
import me.ryandowling.allmightybot.data.Spam;
import me.ryandowling.allmightybot.data.TimedMessage;
import me.ryandowling.allmightybot.data.TwitchChatters;
import me.ryandowling.allmightybot.data.WorldType;
import me.ryandowling.allmightybot.listeners.CommandListener;
import me.ryandowling.allmightybot.listeners.LinkListener;
import me.ryandowling.allmightybot.listeners.SpamListener;
import me.ryandowling.allmightybot.listeners.StartupListener;
import me.ryandowling.allmightybot.listeners.TrollChatListener;
import me.ryandowling.allmightybot.listeners.UserListener;
import me.ryandowling.allmightybot.utils.TwitchAPI;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pircbotx.Channel;
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
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AllmightyBot {
    public final static Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Logger logger = LogManager.getLogger(App.class.getName());
    private static final String DATE = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
    private Settings settings;
    private PircBotX pirc;

    private boolean shutDown = false;

    private boolean isShuttingDown = false;

    private Map<String, Date> userJoined = new ConcurrentHashMap<>();
    private Map<String, Map<String, Integer>> userOnlineTime = new ConcurrentHashMap<>();
    private Map<String, List<ChatLog>> userLogs = new ConcurrentHashMap<>();
    private List<Event> events = new ArrayList<>();
    private Map<String, Integer> streamOnlineTime = new ConcurrentHashMap<>();

    private Map<String, String> lang = new HashMap<>();

    private List<Spam> spams = new ArrayList<>();
    private List<String> allowedLinks = new ArrayList<>();
    private List<TimedMessage> timedMessages = new ArrayList<>();

    private StartupListener startupListener = new StartupListener(this);
    private UserListener userListener = new UserListener(this);
    private CommandListener commandListener = new CommandListener(this);
    private SpamListener spamListener = new SpamListener(this);
    private LinkListener linkListener = new LinkListener(this);
    private TrollChatListener trollChatListener = new TrollChatListener(this);

    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    private Map<String, Integer> topUsers;

    private boolean firstTime = false;

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

            input = JOptionPane.showInputDialog(null, "Please enter the username of the Twitch user " + "to act " +
                    "as the bot", "Twitch Username", JOptionPane.QUESTION_MESSAGE);

            if (input == null) {
                logger.error("Failed to input proper things when setting up! Do it properly next time!");
                System.exit(0);
            }

            settings.setTwitchUsername(input);

            input = JOptionPane.showInputDialog(null, "Please enter the IRC oauth token of the Twitch user " +
                    "acting as the bot", "Twitch Token", JOptionPane.QUESTION_MESSAGE);

            if (input == null) {
                logger.error("Failed to input proper things when setting up! Do it properly next time!");
                System.exit(0);
            }

            settings.setTwitchToken(input);

            input = JOptionPane.showInputDialog(null, "Please enter the API token of the Twitch user " + "acting " +
                    "as the bot", "Twitch API Token", JOptionPane.QUESTION_MESSAGE);

            if (input == null) {
                logger.error("Failed to input proper things when setting up! Do it properly next time!");
                System.exit(0);
            }

            settings.setTwitchApiToken(input);

            input = JOptionPane.showInputDialog(null, "Please enter the API client ID of the application using " +
                    "the Twitch API", "Twitch API Client ID", JOptionPane.QUESTION_MESSAGE);

            if (input == null) {
                logger.error("Failed to input proper things when setting up! Do it properly next time!");
                System.exit(0);
            }

            settings.setTwitchApiClientID(input);

            input = JOptionPane.showInputDialog(null, "Please enter the username of the Twitch user whose " +
                    "channel you wish to join! Must be in all lowercase", "User To Join", JOptionPane.QUESTION_MESSAGE);

            if (input == null) {
                logger.error("Failed to input proper things when setting up! Do it properly next time!");
                System.exit(0);
            }

            settings.setTwitchChannel(input);

            input = JOptionPane.showInputDialog(null, "Please enter the name of the caster to display when " +
                    "referencing them", "Casters Name", JOptionPane.QUESTION_MESSAGE);

            if (input == null) {
                logger.error("Failed to input proper things when setting up! Do it properly next time!");
                System.exit(0);
            }

            settings.setCastersName(input);

            input = JOptionPane.showInputDialog(null, "How often do you want to run the timed messages (in " +
                    "minutes)?", "Timed Messages", JOptionPane.QUESTION_MESSAGE);
            try {
                settings.setTimedMessagesInterval(Integer.parseInt(input) * 60);
            } catch (NumberFormatException e) {
                input = null;
            }

            input = JOptionPane.showInputDialog(null, "How long do you want to time people out for when they post " +
                    "links after 1 warning (in minutes)?", "Spam Timeouts", JOptionPane.QUESTION_MESSAGE);
            try {
                settings.setLinkTimeoutLength1(Integer.parseInt(input) * 60);
            } catch (NumberFormatException e) {
                input = null;
            }

            input = JOptionPane.showInputDialog(null, "How long do you want to time people out for when they post " +
                    "links after 2 and more warnings (in minutes)?", "Spam Timeouts", JOptionPane.QUESTION_MESSAGE);
            try {
                settings.setLinkTimeoutLength2(Integer.parseInt(input) * 60);
            } catch (NumberFormatException e) {
                input = null;
            }

            if (input == null) {
                logger.error("Failed to input proper things when setting up! Do it properly next time!");
                System.exit(0);
            }

            int reply = JOptionPane.showConfirmDialog(null, "Do you want the bot to announce itself on join " +
                            "(message " + "customisable in the lang.json file after startup)?", "Self Announce",
                    JOptionPane.YES_NO_OPTION);
            if (reply != JOptionPane.YES_OPTION) {
                settings.setAnnounceOnJoin(false);
            }

            this.settings.initialSetupComplete();
            this.firstTime = true;
        }
    }

    private void loadBot() {
        Configuration.Builder<PircBotX> config = this.settings.getBuilder();

        String chatServerIP = null;
        try {
            chatServerIP = TwitchAPI.getChatServerIP(this.settings.getTwitchChannel());
            config.setServerHostname(chatServerIP);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        // Register the different listeners
        config.addListener(this.startupListener);
        config.addListener(this.userListener);
        config.addListener(this.commandListener);
        config.addListener(this.spamListener);
        config.addListener(this.linkListener);
        config.addListener(this.trollChatListener);

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

        loadBot();

        if (newStream) {
            logger.info("This is a new stream!");
            this.settings.setStartTime(new Date());
            this.saveSettings();
        } else {
            logger.info("This is continuing an existing stream!");
        }

        if (this.firstTime) {
            logger.info("Creating default files!");
            this.settings.setupExampleStuff();
        }

        // Check the Twitch API token to make sure it's all good
        TwitchAPI.checkToken();

        loadCommands();
        loadUserChatLogs();
        loadUserOnlineTime();
        loadStreamOnlineTime();
        loadLang();
        loadSpamWatchers();
        loadTimedMessages();
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

        System.exit(0);
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
                        } else if (command.isQuoteCommand()) {
                            commandConstructor = commandClass.getConstructor(String.class, QuoteType.class, int.class);
                            commandToAdd = (BaseCommand) commandConstructor.newInstance(command.getName(), command
                                    .getQuoteType(), command.getTimeout());
                        } else if (command.isWorldCommand()) {
                            commandConstructor = commandClass.getConstructor(String.class, WorldType.class, int.class);
                            commandToAdd = (BaseCommand) commandConstructor.newInstance(command.getName(), command
                                    .getWorldType(), command.getTimeout());
                        } else if (command.isAliasCommand()) {
                            commandConstructor = commandClass.getConstructor(String.class, String.class, int.class);
                            commandToAdd = (BaseCommand) commandConstructor.newInstance(command.getName(), command
                                    .getAliasedTo(), command.getTimeout());
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
                    } catch (ClassNotFoundException | InstantiationException | InvocationTargetException |
                            NoSuchMethodException | IllegalAccessException e) {
                        logger.error("Error loading command !" + command.getName() + " with type of " + command
                                .getType());
                        if (this.getSettings().shouldForceCommands()) {
                            logger.error("Force commands setting is on so we must exit since a command failed to " +
                                    "load!");
                            System.exit(1);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadUserChatLogs() {
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Utils.getUsersDir())) {
            for (Path path : directoryStream) {
                if (Files.isDirectory(path)) {
                    String username = path.getFileName().toString();
                    if (Files.exists(Utils.getUserChatFile(username))) {
                        Type type = new TypeToken<List<ChatLog>>() {
                        }.getType();
                        List<ChatLog> chatMessages = GSON.fromJson(FileUtils.readFileToString(Utils.getUserChatFile
                                (username).toFile()), type);
                        this.userLogs.put(username, chatMessages);
                    }
                }
            }
        } catch (IOException ex) {
        }
    }

    private void loadUserOnlineTime() {
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

    private void loadLang() {
        if (!Files.exists(Utils.getLangFile())) {
            logger.error("Error! Cannot find the lang.json file! We have to exit!");
            System.exit(1);
        }

        try {
            Type type = new TypeToken<Map<String, String>>() {
            }.getType();
            this.lang = GSON.fromJson(FileUtils.readFileToString(Utils.getLangFile().toFile()), type);
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

    private void loadTimedMessages() {
        try {
            Type listType = new TypeToken<ArrayList<TimedMessage>>() {
            }.getType();
            this.timedMessages = GSON.fromJson(FileUtils.readFileToString(Utils.getTimedMessagesFile().toFile()),
                    listType);
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
        for (Map.Entry<String, Command> entry : CommandBus.getAll().entrySet()) {
            Command command = entry.getValue();
            command.save();
        }

        CommandBus.removeAll();

        loadCommands();

        this.spams.clear();

        loadSpamWatchers();
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
            saveSettings();
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

        this.pirc.sendIRC().quitServer("Bye");
    }

    private void saveSettings() {
        try {
            FileUtils.write(Utils.getSettingsFile().toFile(), GSON.toJson(this.settings));
            logger.debug("Settings saved!");
        } catch (IOException e) {
            e.printStackTrace();
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

        this.saveOnlineTime(nick, joined);
    }

    private void saveAllOnlineTime() {
        for (Map.Entry<String, Date> entry : this.userJoined.entrySet()) {
            this.saveOnlineTime(entry.getKey(), entry.getValue());
        }
    }

    private void saveOnlineTime(String username, Date joinedTime) {
        Map<String, Integer> timeOnline = this.userOnlineTime.get(username);
        int online = 0;

        if (timeOnline == null) {
            timeOnline = new HashMap<>();
        }

        if (timeOnline.containsKey(DATE)) {
            online = timeOnline.get(DATE);
        }

        online += (int) Utils.getDateDiff(joinedTime, new Date(), TimeUnit.SECONDS);

        timeOnline.put(DATE, online);

        this.userOnlineTime.put(username, timeOnline);

        this.userJoined.remove(username);
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

    public void removeTrollChatListener() {
        if (this.trollChatListener != null) {
            this.pirc.getConfiguration().getListenerManager().removeListener(this.trollChatListener);
            this.trollChatListener = null;
        }
    }

    public void triggerShutdown() {
        // Remove the listeners
        this.removeStartupListener();
        this.removeUserListener();
        this.removeCommandListener();
        this.removeSpamListener();
        this.removeLinkListener();
        this.removeTrollChatListener();

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

    public boolean isModerator(String username) {
        for (String mod : this.settings.getModerators()) {
            if (mod.equalsIgnoreCase(username)) {
                return true;
            }
        }

        return false;
    }

    public void startTimedMessages() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Channel toRun = null;
                for (Channel channel : pirc.getUserChannelDao().getAllChannels()) {
                    if (channel.getName().contains(settings.getTwitchChannel().toLowerCase())) {
                        toRun = channel;
                        break;
                    }
                }

                if (toRun == null) {
                    return;
                }

                // Run a random one of the Times Messages

                Random randomGenerator = new Random();
                int index = randomGenerator.nextInt(timedMessages.size());
                TimedMessage message = timedMessages.get(index);

                toRun.send().message(message.getMessage());
            }
        };

        this.executor.scheduleAtFixedRate(runnable, settings.getTimedMessagesInterval(), settings
                .getTimedMessagesInterval(), TimeUnit.SECONDS);
    }

    public String getLangValue(String key) {
        if (!this.lang.containsKey(key)) {
            logger.error("No lang value set for key " + key);
            return "Oh noes!";
        }

        return this.lang.get(key);
    }

    public Date getLastSpokeTime(String username) {
        username = username.toLowerCase();

        if (!this.userLogs.containsKey(username)) {
            return null;
        }

        return this.userLogs.get(username).get(this.userLogs.get(username).size() - 1).getTime();
    }

    public Date getFirstSpokeTime(String username) {
        username = username.toLowerCase();

        if (!this.userLogs.containsKey(username)) {
            return new Date();
        }

        return this.userLogs.get(username).get(0).getTime();
    }

    public List<TimedMessage> getTimedMessages() {
        return this.timedMessages;
    }

    public List<ChatLog> getUsersChatLog(String username) {
        return this.userLogs.get(username.toLowerCase());
    }
}
