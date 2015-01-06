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
import org.pircbotx.hooks.events.MessageEvent;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class UptimeCommand extends BaseCommand {
    public UptimeCommand(String name, int timeout) {
        super(name, timeout);
    }

    @Override
    public boolean run(AllmightyBot bot, MessageEvent event) {
        if (super.run(bot, event)) {
            List<String> arguments;
            String period = "";

            try {
                arguments = Utils.getCommandsArguments(1, event.getMessage(), true);
                period = arguments.get(0);
            } catch (StringIndexOutOfBoundsException ignored) {
            }

            period = period.toLowerCase();

            int uptime;
            String timePeriodString;

            switch (period) {
                case "all":
                    uptime = bot.getTotalLiveTime(true);
                    timePeriodString = "this year";
                    break;
                default:
                    uptime = (int) Utils.getDateDiff(bot.getSettings().getStartTime(), new Date(), TimeUnit.SECONDS);
                    timePeriodString = "today";
                    break;
            }


            event.getChannel().send().message(App.INSTANCE.getSettings().getCastersName() + " has been live for " +
                    Utils.timeConversion(uptime) + " " + timePeriodString + "!");
            return true;
        }

        return false;
    }
}
