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
import me.ryandowling.allmightybot.data.WorldDetails;
import me.ryandowling.allmightybot.data.WorldType;
import org.apache.commons.io.FileUtils;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class WorldCommand extends BaseCommand {
    private final WorldType worldType;
    private static WorldDetails worldDetails;
    private static boolean hasBeenSet = false;
    private static boolean hasLoaded = false;
    private static boolean hasSaved = false;

    public WorldCommand(String name, WorldType worldType, int timeout) {
        super(name, timeout);

        this.worldType = worldType;
    }

    @Override
    public boolean run(AllmightyBot bot, MessageEvent event) {
        if (super.run(bot, event)) {
            switch (this.worldType) {
                case NEW:
                    List<String> parts = Utils.getCommandsArguments(2, event.getMessage(), true);

                    if (parts.size() != 2) {
                        return false;
                    }

                    try {
                        worldDetails.setNumber(Integer.parseInt(parts.get(0)));
                        worldDetails.setSeed(parts.get(1));
                        hasBeenSet = true;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                    break;
                case QUERY:
                    if (!hasBeenSet) {
                        return false;
                    }

                    event.getChannel().send().message("This is world number " + worldDetails.getNumber() + " with a " +
                            "seed of '" + worldDetails.getSeed() + "'");
                    break;
            }
            return true;
        }

        return false;
    }

    @Override
    public void load() {
        super.load();

        if (Files.exists(Utils.getCommandDataFile(this))) {
            try {
                worldDetails = AllmightyBot.GSON.fromJson(FileUtils.readFileToString(Utils.getCommandDataFile(this)
                        .toFile()), WorldDetails.class);
                hasBeenSet = worldDetails.isSet();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            worldDetails = new WorldDetails(); // If no loading then set a default
        }
    }

    @Override
    public void save() {
        super.save();
        try {
            FileUtils.write(Utils.getCommandDataFile(this).toFile(), AllmightyBot.GSON.toJson(worldDetails));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void newSeed(String seed) {
        worldDetails.setSeed(seed);
        worldDetails.setNumber(worldDetails.getNumber() + 1);
        this.save();
    }
}
