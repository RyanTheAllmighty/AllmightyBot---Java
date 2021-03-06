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

package me.ryandowling.allmightybot.listeners;

import me.ryandowling.allmightybot.AllmightyBot;
import me.ryandowling.allmightybot.App;
import me.ryandowling.allmightybot.commands.Command;
import me.ryandowling.allmightybot.commands.CommandBus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

public class CommandListener extends ListenerAdapter {
    private static final Logger logger = LogManager.getLogger(App.class.getName());
    private AllmightyBot bot;

    public CommandListener(AllmightyBot bot) {
        this.bot = bot;
    }

    @Override
    public void onMessage(MessageEvent event) throws Exception {
        super.onMessage(event);
        logger.debug("[" + event.getChannel().getName() + "] [" + event.getUser().getNick() + "] " + event.getMessage
                ());

        if (event.getMessage().startsWith("!")) {
            String commandString = event.getMessage().substring(1);
            if (commandString.contains(" ")) {
                commandString = commandString.substring(0, commandString.indexOf(" "));
            }
            Command command = CommandBus.find(commandString);
            if (command != null) {
                if (!command.run(this.bot, event)) {
                    logger.error(event.getUser().getNick() + " tried to run command " + event.getMessage() + " with " +
                            "no luck!");
                }
            }
        }
    }
}
