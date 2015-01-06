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

package me.ryandowling.allmightybot.data.twitch.api;

public class TwitchChannel {
    private String display_name;
    private String game;
    private String logo;
    private boolean mature;
    private String status;
    private boolean partner;
    private String url;
    private String name;
    private int followers;
    private int views;

    public String getDisplay_name() {
        return this.display_name;
    }

    public String getLogo() {
        return this.logo;
    }

    public boolean isMature() {
        return this.mature;
    }

    public String getStatus() {
        return this.status;
    }

    public boolean isPartner() {
        return this.partner;
    }

    public String getURL() {
        return this.url;
    }

    public String getName() {
        return this.name;
    }

    public int getFollowers() {
        return this.followers;
    }

    public int getViews() {
        return this.views;
    }
}
