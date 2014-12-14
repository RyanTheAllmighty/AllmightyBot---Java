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
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.regex.Pattern;

public class SpamListener extends ListenerAdapter {
    private AllmightyBot bot;

    public SpamListener(AllmightyBot bot) {
        this.bot = bot;
    }

    private static Pattern doritosPattern = Pattern.compile(Pattern.quote("dropped my bag of Dorito"), Pattern
            .CASE_INSENSITIVE);

    @Override
    public void onMessage(MessageEvent event) throws Exception {
        super.onMessage(event);

        if (doritosPattern.matcher(event.getMessage()).find() && !event.getChannel().isOp(event.getUser())) {
            event.getChannel().send().message("I'm sorry you've dropped them! I shall nom nom nom on them while you " +
                    "get REKT! [Timed out]" + " [30 minutes]");
            event.getChannel().send().message(".timeout " + event.getUser().getNick() + " 1800");
        }
    }
}
