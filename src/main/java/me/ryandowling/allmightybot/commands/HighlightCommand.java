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
import org.apache.commons.io.FileUtils;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class HighlightCommand extends BaseCommand {
    public List<String> timesToHighlight = new ArrayList<>();

    public HighlightCommand(String name, int timeout) {
        super(name, timeout);
    }

    @Override
    public boolean run(AllmightyBot bot, MessageEvent event) {
        if (super.run(bot, event)) {
            int uptime = (int) Utils.getDateDiff(bot.getSettings().getStartTime(), new Date(), TimeUnit.SECONDS);

            timesToHighlight.add(Utils.timeConversionRaw(uptime));

            save();

            event.getChannel().send().message("The time " + Utils.timeConversionRaw(uptime) + " has been saved to be" +
                    " highlighted after the stream!");

            return true;
        }

        return false;
    }

    @Override
    public void load() {
        super.save();

        if (Files.exists(Utils.getCommandDataFile(this))) {
            try {
                FileUtils.forceDelete(Utils.getCommandDataFile(this).toFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void save() {
        super.save();

        try {
            FileUtils.write(Utils.getCommandDataFile(this).toFile(), AllmightyBot.GSON.toJson(this.timesToHighlight));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
