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

import java.util.List;

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
            int uptime = bot.getTotalLiveTime(true);

            if (timeWatched == 0) {
                event.getChannel().send().message(Utils.replaceVariablesInString(bot.getLangValue("eyetimeNone"),
                        nick));
            } else {
                double percent = (timeWatched * 1.0 / bot.getTotalLiveTime(true) * 1.0) * 100.0;
                event.getChannel().send().message(Utils.replaceVariablesInString(bot.getLangValue("eyetime"), nick,
                        Utils.timeConversion(timeWatched), Utils.timeConversion(uptime), String.format("%.2f",
                                percent)));
            }
            return true;
        }

        return false;
    }
}
