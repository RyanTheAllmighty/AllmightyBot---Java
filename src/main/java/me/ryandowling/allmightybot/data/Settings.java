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
}
