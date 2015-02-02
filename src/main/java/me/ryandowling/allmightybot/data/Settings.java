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

package me.ryandowling.allmightybot.data;

import me.ryandowling.allmightybot.AllmightyBot;
import me.ryandowling.allmightybot.App;
import me.ryandowling.allmightybot.Utils;
import org.apache.commons.io.FileUtils;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Settings {
    /**
     * If the initial setup has been completed
     */
    private boolean initialSetupComplete;

    /**
     * The bot's username
     */
    private String twitchUsername;

    /**
     * The bot's OAuth token
     */
    private String twitchToken;

    /**
     * The channel we are joining
     */
    private String twitchChannel;

    /**
     * The name of the caster
     */
    private String castersName;

    /**
     * If the bot should announce itself when it joins
     */
    private boolean announceOnJoin;

    /**
     * The time we last started the bot, used to continue online times when the bot fails
     */
    private Date startTime;

    /**
     * The token for the Twitch API
     */
    private String twitchApiToken;

    /**
     * The client ID of the app for the Twitch API
     */
    private String twitchApiClientID;

    /**
     * If users should be timed out for posting links not in the whitelist or have no permit
     */
    private boolean timeoutLinks;

    /**
     * How long in seconds users should be timed out for on second offense
     */
    private int linkTimeoutLength1;

    /**
     * How long in seconds users should be timed out for on third and subsequent offenses
     */
    private int linkTimeoutLength2;

    /**
     * If the bot shouldn't continue loading if one or more commands cannot be loaded
     */
    private boolean forceCommands;

    /**
     * The time in seconds between the Timed Message runs
     */
    private int timedMessagesInterval;

    /**
     * The format of the date for the time command
     */
    private String timeCommandFormat;

    /**
     * The moderators of the channel
     */
    private List<String> moderators;

    /**
     * Sets up some defaults where there is no settings file already there
     */
    public Settings() {
        this.initialSetupComplete = false;
        this.announceOnJoin = true;
    }

    public boolean hasInitialSetupBeenCompleted() {
        return this.initialSetupComplete;
    }

    public void initialSetupComplete() {
        this.initialSetupComplete = true;
        this.startTime = new Date();
        this.timeoutLinks = true;
        this.linkTimeoutLength1 = 10;
        this.linkTimeoutLength2 = 60;
        this.forceCommands = true;
        this.timeCommandFormat = "d/M/Y HH:mm:ss z";

        List<String> modList = new ArrayList<>();
        modList.add(getTwitchUsername().toLowerCase());
        modList.add(getTwitchChannel().toLowerCase());
        this.moderators = modList;
    }

    public void setupExampleStuff() {
        // Timed messages
        App.INSTANCE.getTimedMessages().add(new TimedMessage().create("This is a test message which the bot will run " +
                "" + " every now and then"));

        try {
            FileUtils.writeStringToFile(Utils.getTimedMessagesFile().toFile(), AllmightyBot.GSON.toJson(App.INSTANCE
                    .getTimedMessages()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Spam timeouts
        App.INSTANCE.getSpams().add(new Spam().create("Go fuck yourself", "This is what the bot will say back! Make "
                + "sure to be witty! [Timed out] [30 minutes]", SpamActionType.TIMEOUT, 30));
        App.INSTANCE.getSpams().add(new Spam().create("Knob gobbler", "The bot says ban! [Ban]", SpamActionType.BAN,
                30));

        try {
            FileUtils.writeStringToFile(Utils.getSpamFile().toFile(), AllmightyBot.GSON.toJson(App.INSTANCE.getSpams
                    ()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Link whitelist
        App.INSTANCE.getAllowedLinks().add(".*?\\.*twitter.com.*?");

        try {
            FileUtils.writeStringToFile(Utils.getLinksFile().toFile(), AllmightyBot.GSON.toJson(App.INSTANCE
                    .getAllowedLinks()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Commands file
        try {
            URL inputUrl = System.class.getResource("/json/commands.json");
            FileUtils.copyURLToFile(inputUrl, Utils.getCommandsFile().toFile());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to copy commands.json to disk! Exiting!");
            System.exit(1);
        }

        // Lang file
        try {
            URL inputUrl = System.class.getResource("/json/lang.json");
            FileUtils.copyURLToFile(inputUrl, Utils.getLangFile().toFile());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to copy lang.json to disk! Exiting!");
            System.exit(1);
        }
    }

    public Configuration.Builder<PircBotX> getBuilder() {
        Configuration.Builder<PircBotX> builder = new Configuration.Builder<>();

        builder.setName(this.twitchUsername);
        builder.setLogin(this.twitchUsername);
        builder.setServerPassword(this.twitchToken);
        builder.setAutoNickChange(true);
        builder.setServerHostname("irc.twitch.tv");
        builder.setServerPort(6667);
        builder.addAutoJoinChannel("#" + this.twitchChannel);

        return builder;
    }

    public void setTwitchUsername(String twitchUsername) {
        this.twitchUsername = twitchUsername;
    }

    public void setTwitchToken(String twitchToken) {
        this.twitchToken = twitchToken;
    }

    public void setTwitchChannel(String twitchChannel) {
        this.twitchChannel = twitchChannel;
    }

    public String getTwitchUsername() {
        return this.twitchUsername;
    }

    public String getTwitchChannel() {
        return this.twitchChannel;
    }

    public boolean shouldAnnounceOnJoin() {
        return this.announceOnJoin;
    }

    public String getCastersName() {
        return this.castersName;
    }

    public void setCastersName(String castersName) {
        this.castersName = castersName;
    }

    public Date getStartTime() {
        return this.startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public String getTwitchApiToken() {
        return this.twitchApiToken;
    }

    public void setTwitchApiToken(String twitchApiToken) {
        this.twitchApiToken = twitchApiToken;
    }

    public String getTwitchApiClientID() {
        return this.twitchApiClientID;
    }

    public void setTwitchApiClientID(String twitchApiClientID) {
        this.twitchApiClientID = twitchApiClientID;
    }

    public boolean shouldTimeoutLinks() {
        return this.timeoutLinks;
    }

    public boolean shouldForceCommands() {
        return this.forceCommands;
    }

    public void setTimeoutLinks(boolean timeoutLinks) {
        this.timeoutLinks = timeoutLinks;
    }

    public List<String> getModerators() {
        return this.moderators;
    }

    public int getTimedMessagesInterval() {
        return this.timedMessagesInterval;
    }

    public String getTimeCommandFormat() {
        return this.timeCommandFormat;
    }

    public void setTimedMessagesInterval(int timedMessagesInterval) {
        this.timedMessagesInterval = timedMessagesInterval;
    }

    public void setAnnounceOnJoin(boolean announceOnJoin) {
        this.announceOnJoin = announceOnJoin;
    }

    public int getLinkTimeoutLength1() {
        return this.linkTimeoutLength1;
    }

    public void setLinkTimeoutLength1(int linkTimeoutLength1) {
        this.linkTimeoutLength1 = linkTimeoutLength1;
    }

    public int getLinkTimeoutLength2() {
        return this.linkTimeoutLength2;
    }

    public void setLinkTimeoutLength2(int linkTimeoutLength2) {
        this.linkTimeoutLength2 = linkTimeoutLength2;
    }
}
