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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.KickEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PartEvent;

public class UserListener extends ListenerAdapter {
    private static final Logger logger = LogManager.getLogger(App.class.getName());
    private AllmightyBot bot;

    public UserListener(AllmightyBot bot) {
        this.bot = bot;
    }

    @Override
    public void onJoin(JoinEvent event) throws Exception {
        super.onJoin(event);
        if (!event.getUser().getNick().equalsIgnoreCase(bot.getSettings().getTwitchUsername())) {
            this.bot.userJoined(event.getUser().getNick());
        }
    }

    @Override
    public void onMessage(MessageEvent event) throws Exception {
        super.onMessage(event);
        if (!event.getUser().getNick().equalsIgnoreCase(bot.getSettings().getTwitchUsername())) {
            this.bot.userSpoke(event.getUser().getNick(), event.getMessage());
        }
    }

    @Override
    public void onPart(PartEvent event) throws Exception {
        super.onPart(event);
        if (!event.getUser().getNick().equalsIgnoreCase(bot.getSettings().getTwitchUsername())) {
            this.bot.userParted(event.getUser().getNick(), false);
        }
    }

    @Override
    public void onKick(KickEvent event) throws Exception {
        super.onKick(event);
        if (!event.getUser().getNick().equalsIgnoreCase(bot.getSettings().getTwitchUsername())) {
            this.bot.userParted(event.getUser().getNick(), true);
        }
    }
}
