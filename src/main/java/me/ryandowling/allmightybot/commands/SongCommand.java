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

import java.io.File;
import java.io.IOException;

public class SongCommand extends BaseCommand {
    public SongCommand(String name, int timeout) {
        super(name, timeout);
    }

    @Override
    public boolean run(AllmightyBot bot, MessageEvent event) {
        if (super.run(bot, event)) {
            String song = "Unknown";
            String artist = "Unknown";
            String website = "Unknown";

            try {
                String playing = FileUtils.readFileToString(Utils.getNowPlayingFile().toFile());
                song = playing.substring(playing.lastIndexOf("-") + 2);
                artist = playing.substring(0, playing.lastIndexOf("-") - 1);

                String songPath = FileUtils.readFileToString(Utils.getNowPlayingFileFile().toFile());
                if (songPath.contains("\\")) {
                    songPath = songPath.substring(0, songPath.lastIndexOf("\\"));
                } else {
                    songPath = songPath.substring(0, songPath.lastIndexOf("/"));
                }
                website = FileUtils.readFileToString(new File(songPath, "website.txt"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            event.getChannel().send().message(Utils.replaceVariablesInString(bot.getLangValue("currentSong"), song,
                    artist, website));
            return true;
        }

        return false;
    }
}
