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
     * Sets up some defaults where there is no settings file already there
     */
    public Settings() {
        this.initialSetupComplete = false;
    }

    public boolean hasInitialSetupBeenCompleted() {
        return this.initialSetupComplete;
    }

    public void initialSetupComplete() {
        this.initialSetupComplete = true;
    }
}
