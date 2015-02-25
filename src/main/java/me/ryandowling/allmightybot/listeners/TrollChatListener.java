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
import me.ryandowling.allmightybot.Utils;
import me.ryandowling.allmightybot.commands.TrollChatCommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TrollChatListener extends ListenerAdapter {
    private static final Logger logger = LogManager.getLogger(App.class.getName());
    private AllmightyBot bot;
    private Date lastMessage;

    public TrollChatListener(AllmightyBot bot) {
        this.bot = bot;
    }

    @Override
    public void onMessage(MessageEvent event) throws Exception {
        super.onMessage(event);

        if (TrollChatCommand.activated) {
            // Check if the user has spoken 24 hours before this or not
            if (Utils.getDateDiff(this.bot.getFirstSpokeTime(event.getUser().getNick()), new Date(), TimeUnit
                    .SECONDS) <= 86400) {
                if (this.lastMessage == null || Utils.getDateDiff(this.lastMessage, new Date(), TimeUnit.SECONDS) >=
                        10) {
                    this.lastMessage = new Date();

                    event.getChannel().send().message(this.bot.getLangValue("trollChatTimeoutMessage"));
                }

                event.getChannel().send().message(".timeout " + event.getUser().getNick() + " 14400");
            }
        }
    }
}
