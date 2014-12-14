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

import me.ryandowling.allmightybot.AllmightyBot;
import me.ryandowling.allmightybot.App;
import me.ryandowling.allmightybot.data.CommandLevel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pircbotx.hooks.events.MessageEvent;

public abstract class BaseCommand implements Command {
    private static final Logger logger = LogManager.getLogger(App.class.getName());
    private String name;
    private String reply;
    private CommandLevel level;
    private int timeout;
    private long lastRun;

    public BaseCommand(String name, int timeout) {
        this.name = name;
        this.timeout = timeout;
    }

    public BaseCommand(String name, String reply, int timeout) {
        this.name = name;
        this.reply = reply;
        this.timeout = timeout;
    }

    public void setLevel(CommandLevel level) {
        this.level = level;
    }

    public boolean canRun() {
        if ((this.lastRun + (this.timeout * 1000)) > System.currentTimeMillis()) {
            logger.error("Cannot run command " + this.name + " as it was run less than " + this.timeout + " seconds " +
                    "ago");
            return false;
        }

        this.lastRun = System.currentTimeMillis();

        return true;
    }

    public boolean canAccess(MessageEvent event) {
        if (this.level == null) {
            return false;
        }

        switch (this.level) {
            case ALL:
                return true;
            case REGULAR:
                return event.getUser().getNick().equals(App.INSTANCE.getSettings().getTwitchChannel()) || event
                        .getChannel().isOp(event.getUser()) || App.INSTANCE.isRegular(event.getUser().getNick());
            case MODERATOR:
                return event.getUser().getNick().equalsIgnoreCase(App.INSTANCE.getSettings().getTwitchChannel()) ||
                        event.getChannel().isOp(event.getUser());
            case CASTER:
                return event.getUser().getNick().equalsIgnoreCase(App.INSTANCE.getSettings().getTwitchChannel());
            default:
                return false;
        }
    }

    public String getName() {
        return this.name;
    }

    public String getReply(MessageEvent event) {
        String reply = this.getReply();

        if (this.reply.contains("$[")) {
            String message = event.getMessage().substring(getName().length() + 2); // Remove '!command '
            String[] parts = message.split(" ");

            for (int i = 1; i <= parts.length; i++) {
                String pattern = "$[" + i + "]";
                if (reply.contains(pattern)) {
                    reply = reply.replace(pattern, parts[i - 1]);
                } else {
                    return null; // Whoops, pattern is wrong, don't send anything
                }
            }

            if (reply.contains("$[")) {
                return null; // We still have unreplaced things so no good
            }
        }

        return reply;
    }

    public String getReply() {
        return this.reply;
    }

    public long getLastRun() {
        return this.lastRun;
    }

    @Override
    public boolean run(AllmightyBot bot, MessageEvent event) {
        return (canRun() && canAccess(event));
    }
}
