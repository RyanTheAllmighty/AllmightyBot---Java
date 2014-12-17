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

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class EyeTimeCommand extends BaseCommand {
    public EyeTimeCommand(String name, int timeout) {
        super(name, timeout);
    }

    @Override
    public boolean run(AllmightyBot bot, MessageEvent event) {
        if (super.run(bot, event)) {
            List<String> parts = Utils.getCommandsArguments(1, event.getMessage(), true);

            if (parts.size() != 1) {
                return false;
            }

            String nick = parts.get(0).toLowerCase();
            int timeWatched = bot.timeInChannel(nick);

            if (timeWatched == 0) {
                event.getChannel().send().message("The user " + nick + " has not been in the channel before!");
            } else {
                int uptime = (int) Utils.getDateDiff(bot.getSettings().getStartTime(), new Date(), TimeUnit.SECONDS);
                double percent = (timeWatched * 1.0 / bot.getTotalLiveTime(true) * 1.0) * 100.0;
                event.getChannel().send().message("The user " + nick + " has been in the channel for " + Utils
                        .timeConversion(timeWatched) + " while the streamer has been online for " + Utils
                        .timeConversion(uptime) + " which means they've been here for " + String.format("%.2f",
                        percent) + "% of the streams!");
            }
            return true;
        }

        return false;
    }
}
