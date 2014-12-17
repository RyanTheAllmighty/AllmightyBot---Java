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

package me.ryandowling.allmightybot.commands;

import me.ryandowling.allmightybot.data.CommandLevel;
import me.ryandowling.allmightybot.data.SeedType;
import me.ryandowling.allmightybot.data.WorldType;

public class TempCommand {
    private String type;
    private String name;
    private String reply;
    private CommandLevel level;
    private SeedType seedType;
    private WorldType worldType;
    private int timeout = 3;

    public String getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public boolean hasReply() {
        return this.reply != null;
    }

    public String getReply() {
        return this.reply;
    }

    public CommandLevel getLevel() {
        return this.level;
    }

    public SeedType getSeedType() {
        return this.seedType;
    }

    public WorldType getWorldType() {
        return this.worldType;
    }

    public int getTimeout() {
        return this.timeout;
    }

    public boolean isSeedCommand() {
        return this.seedType != null;
    }

    public boolean isWorldCommand() {
        return this.worldType != null;
    }
}
