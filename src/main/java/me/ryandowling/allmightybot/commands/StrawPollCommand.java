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
import me.ryandowling.allmightybot.data.strawpoll.StrawPollCreateRequest;
import me.ryandowling.allmightybot.data.strawpoll.StrawPollCreateResponse;
import me.ryandowling.allmightybot.utils.StrawPollAPIRequest;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.IOException;
import java.util.List;

public class StrawPollCommand extends BaseCommand {
    public StrawPollCommand(String name, int timeout) {
        super(name, timeout);
    }

    @Override
    public boolean run(AllmightyBot bot, MessageEvent event) {
        if (super.run(bot, event)) {
            List<String> arguments = Utils.getCommandsArguments(event.getMessage(), true);

            if (arguments.size() < 2) {
                return false;
            }

            StrawPollCreateRequest poll = new StrawPollCreateRequest(arguments.subList(1, arguments.size()).toArray
                    (new String[arguments.size() - 1]), arguments.get(0));

            StrawPollAPIRequest request = new StrawPollAPIRequest("polls");

            StrawPollCreateResponse response;

            try {
                response = AllmightyBot.GSON.fromJson(request.post(poll), StrawPollCreateResponse.class);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

            if (response == null || response.getID() == 0) {
                return false;
            }

            event.getChannel().send().message(Utils.replaceVariablesInString(bot.getLangValue("strawpollResponse"),
                    "http://strawpoll.me/" + response.getID()));
            return true;
        }

        return false;
    }
}
