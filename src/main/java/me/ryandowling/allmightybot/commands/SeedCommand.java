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
import me.ryandowling.allmightybot.data.SeedType;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SeedCommand extends BaseCommand {
    private final SeedType seedType;
    private static final Map<String, String> seeds = new HashMap<>();
    private static long lastSeedMessage;
    private static boolean collectingSeeds = false;

    public SeedCommand(String name, SeedType seedType, int timeout) {
        super(name, timeout);

        this.seedType = seedType;
    }

    @Override
    public boolean run(AllmightyBot bot, MessageEvent event) {
        if (super.run(bot, event)) {
            switch (this.seedType) {
                case NEW:
                    if (!collectingSeeds) {
                        collectingSeeds = true;
                        event.getChannel().send().message(bot.getLangValue("newSeed"));
                    }
                    break;
                case SUGGESTION:
                    if (!collectingSeeds) {
                        if ((lastSeedMessage + 10000) < System.currentTimeMillis()) {
                            // Seeds aren't being asked for so run the world command instead
                            CommandBus.findByClassName(WorldCommand.class.getName()).run(bot, event);
                        }
                    } else {
                        String seedName = event.getMessage().substring(getName().length() + 2); // Remove '!command '

                        seeds.put(event.getUser().getNick(), seedName);

                        if ((lastSeedMessage + 10000) < System.currentTimeMillis()) {
                            event.getChannel().send().message(bot.getLangValue("seedAdded"));
                            lastSeedMessage = System.currentTimeMillis();
                        }
                    }
                    break;
                case RANDOM:
                    if (collectingSeeds && seeds.size() == 0) {
                        collectingSeeds = false;
                        event.getChannel().send().message(bot.getLangValue("seedPickNone"));
                    } else if (collectingSeeds) {
                        collectingSeeds = false;
                        Random random = new Random();
                        List<String> keys = new ArrayList<String>(seeds.keySet());
                        String winner = keys.get(random.nextInt(keys.size()));
                        String seed = seeds.get(winner);

                        event.getChannel().send().message(Utils.replaceVariablesInString(bot.getLangValue("seedPick")
                                , winner, seed));

                        WorldCommand command = (WorldCommand) CommandBus.findByClassName(WorldCommand.class.getName());
                        if (command != null) {
                            command.newSeed(seed);
                        }

                        seeds.clear();
                    }
                    break;
            }
            return true;
        }

        return false;
    }
}
