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
import me.ryandowling.allmightybot.Utils;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LinkCommand extends BaseCommand {
    public LinkCommand(String name, int timeout) {
        super(name, timeout);
    }

    @Override
    public boolean run(AllmightyBot bot, MessageEvent event) {
        if (super.run(bot, event)) {
            List<String> arguments = Utils.getCommandsArguments(1, event.getMessage(), true);

            if (arguments.size() != 1) {
                return false;
            }

            String links = arguments.get(0);

            if (links.equalsIgnoreCase("true") || links.equalsIgnoreCase("yes") || links.equalsIgnoreCase("on")) {
                bot.getSettings().setTimeoutLinks(true);
                event.getChannel().send().message("Link timeouts have been turned on!");
            } else if (links.equalsIgnoreCase("false") || links.equalsIgnoreCase("no") || links.equalsIgnoreCase
                    ("off")) {
                bot.getSettings().setTimeoutLinks(false);
                event.getChannel().send().message("Link timeouts have been turned off!");
            }

            return true;
        }

        return false;
    }
}
