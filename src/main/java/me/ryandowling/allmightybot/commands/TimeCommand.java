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

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TimeCommand extends BaseCommand {
    public TimeCommand(String name, int timeout) {
        super(name, timeout);
    }

    @Override
    public boolean run(AllmightyBot bot, MessageEvent event) {
        if (super.run(bot, event)) {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat dateFormater = new SimpleDateFormat(bot.getSettings().getTimeCommandFormat());
            event.getChannel().send().message(Utils.replaceVariablesInString(bot.getLangValue("currentTime"), App
                    .INSTANCE.getSettings().getCastersName(), dateFormater.format(cal.getTime())));
            return true;
        }

        return false;
    }
}
