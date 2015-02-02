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
import me.ryandowling.allmightybot.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.ArrayList;
import java.util.List;

public class DoucheCommand extends BaseCommand {
    public DoucheCommand(String name, int timeout) {
        super(name, timeout);
    }

    private static final List<String> douches = new ArrayList<>();
    private static final Logger logger = LogManager.getLogger(App.class.getName());

    @Override
    public boolean run(AllmightyBot bot, MessageEvent event) {
        if (super.run(bot, event)) {
            List<String> args = Utils.getCommandsArguments(1, event.getMessage(), true);

            if (args.size() != 1) {
                return false;
            }

            String username = args.get(0);

            if (bot.isModerator(username)) {
                logger.warn("Cannot timeout/ban the user " + username + " as they are a moderator!");
                return false;
            }

            if (douches.contains(username)) {
                douches.remove(username);

                event.getChannel().send().message(Utils.replaceVariablesInString(bot.getLangValue("douche"),
                        username, Utils.timeConversion(43200)));
                event.getChannel().send().message(".timeout " + username + " 43200");
            } else {
                douches.add(username);

                event.getChannel().send().message(Utils.replaceVariablesInString(bot.getLangValue("doucheWarning"),
                        username, Utils.timeConversion(600)));
                event.getChannel().send().message(".timeout " + username + " 600");
            }
            return true;
        }

        return false;
    }
}
