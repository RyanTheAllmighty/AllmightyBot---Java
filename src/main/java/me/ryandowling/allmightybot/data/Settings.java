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

import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;

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

    public void setTimeoutLinks(boolean timeoutLinks) {
        this.timeoutLinks = timeoutLinks;
    }

    public List<String> getModerators() {
        return this.moderators;
    }
}
