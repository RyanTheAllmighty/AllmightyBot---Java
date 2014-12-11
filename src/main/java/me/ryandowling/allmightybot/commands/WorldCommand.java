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
import me.ryandowling.allmightybot.data.WorldType;
import org.pircbotx.hooks.events.MessageEvent;

public class WorldCommand extends BaseCommand {
    private final WorldType worldType;
    private int worldNumber;
    private String worldSeed;
    private boolean hasBeenSet = false;

    public WorldCommand(String name, WorldType worldType, int timeout) {
        super(name, timeout);

        this.worldType = worldType;
    }

    @Override
    public boolean run(AllmightyBot bot, MessageEvent event) {
        if (super.run(bot, event)) {
            switch (this.worldType) {
                case NEW:
                    String message = event.getMessage().substring(getName().length() + 2); // Remove '!command '
                    String[] parts = message.split(" ");

                    if (parts.length != 2) {
                        return false;
                    }

                    try {
                        this.worldNumber = Integer.parseInt(parts[0]);
                    } catch (NumberFormatException e) {
                        return false;
                    }

                    this.worldSeed = parts[1];
                    this.hasBeenSet = true;
                    break;
                case QUERY:
                    if (!this.hasBeenSet) {
                        return false;
                    }

                    event.getChannel().send().message("This is world number " + this.worldNumber + " with a seed of "
                            + this.worldSeed);
                    break;
            }
            return true;
        }

        return false;
    }
}
