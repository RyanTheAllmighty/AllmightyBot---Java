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
import me.ryandowling.allmightybot.data.TwitchChatters;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class RandomCommand extends BaseCommand {
    public RandomCommand(String name, int timeout) {
        super(name, timeout);
    }

    @Override
    public boolean run(AllmightyBot bot, MessageEvent event) {
        if (super.run(bot, event)) {
            try {
                String json = Utils.readURLToString("http://tmi.twitch.tv/group/user/" + bot.getSettings()
                        .getTwitchChannel() + "/chatters");
                TwitchChatters chatters = AllmightyBot.GSON.fromJson(json, TwitchChatters.class);

                List<String> users = new ArrayList<>();

                for (Map.Entry<String, List<String>> entry : chatters.getChatters().entrySet()) {
                    for (String user : entry.getValue()) {
                        if (user.equalsIgnoreCase(bot.getSettings().getTwitchChannel())) {
                            continue;
                        }

                        users.add(user);
                    }
                }

                Random randomGenerator = new Random();
                int index = randomGenerator.nextInt(users.size());
                String username = users.get(index);

                event.getChannel().send().message(Utils.replaceVariablesInString(bot.getLangValue("randomUserPicked")
                        , username));
            } catch (IOException e) {
                e.printStackTrace();
            }

            return true;
        }

        return false;
    }
}
