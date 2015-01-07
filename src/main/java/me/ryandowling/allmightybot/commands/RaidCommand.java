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
import me.ryandowling.allmightybot.data.twitch.api.StreamResponse;
import me.ryandowling.allmightybot.utils.TwitchAPI;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.IOException;
import java.util.List;

public class RaidCommand extends BaseCommand {
    public RaidCommand(String name, int timeout) {
        super(name, timeout);
    }

    @Override
    public boolean run(AllmightyBot bot, MessageEvent event) {
        if (super.run(bot, event)) {
            List<String> args = Utils.getCommandsArguments(1, event.getMessage(), true);

            if (args.size() != 1) {
                return false;
            }

            try {
                StreamResponse response = TwitchAPI.getStreamDetails(args.get(0));

                if (response != null && response.getStream() != null) {
                    event.getChannel().send().message(Utils.replaceVariablesInString(bot.getLangValue("raid"),
                            response.getStream().getChannel().getURL(), response.getStream().getGame(), bot
                                    .getLangValue("raidMessage")));
                    event.getChannel().send().message(response.getStream().getChannel().getURL() + " - " + bot
                            .getLangValue("raidMessage"));
                    event.getChannel().send().message(response.getStream().getChannel().getURL() + " - " + bot
                            .getLangValue("raidMessage"));
                    event.getChannel().send().message(response.getStream().getChannel().getURL() + " - " + bot
                            .getLangValue("raidMessage"));
                    event.getChannel().send().message(response.getStream().getChannel().getURL() + " - " + bot
                            .getLangValue("raidMessage"));
                    event.getChannel().send().message(".host " + args.get(0));
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return false;
    }
}
