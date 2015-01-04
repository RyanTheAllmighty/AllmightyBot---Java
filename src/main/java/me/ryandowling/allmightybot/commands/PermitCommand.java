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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PermitCommand extends BaseCommand {
    public PermitCommand(String name, int timeout) {
        super(name, timeout);
    }

    private static final Map<String, Long> permits = new HashMap<>();

    @Override
    public boolean run(AllmightyBot bot, MessageEvent event) {
        if (super.run(bot, event)) {
            List<String> arguments;
            String username;
            int seconds = 120;

            try {
                arguments = Utils.getCommandsArguments(2, event.getMessage(), true);
                seconds = Integer.parseInt(arguments.get(1));
            } catch (StringIndexOutOfBoundsException e) {
                arguments = Utils.getCommandsArguments(1, event.getMessage(), true);
            }

            username = arguments.get(0);

            long permittedUntil = (System.currentTimeMillis() + (seconds * 1000));

            permits.put(username, permittedUntil);

            event.getChannel().send().message(username + " has been permitted to post links for " + Utils
                    .timeConversion(seconds, true) + "!");
            return true;
        }

        return false;
    }

    public static boolean hasPermit(String username) {
        return permits.containsKey(username) && (permits.get(username) > System.currentTimeMillis());

    }
}
