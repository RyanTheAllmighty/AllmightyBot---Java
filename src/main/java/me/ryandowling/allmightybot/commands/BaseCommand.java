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
import org.pircbotx.hooks.events.MessageEvent;

public abstract class BaseCommand implements Command {
    private String name;
    private String description;
    private String reply;
    private CommandLevel level;

    public BaseCommand(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public BaseCommand(String name, String description, String reply) {
        this.name = name;
        this.description = description;
        this.reply = reply;
    }

    public void setLevel(CommandLevel level) {
        this.level = level;
    }

    public boolean canAccess(MessageEvent event) {
        if (this.level == null) {
            return false;
        }

        switch (this.level) {
            case ALL:
                return true;
            case MODERATOR:
                return event.getChannel().isOp(event.getUser());
            case CASTER:
                return event.getUser().getNick().equals("ryantheallmighty");
            default:
                return false;
        }
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public String getReply() {
        return this.reply;
    }
}
