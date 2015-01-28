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
import me.ryandowling.allmightybot.commands.PermitCommand;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.regex.Pattern;

public class LinkListener extends ListenerAdapter {
    private AllmightyBot bot;
    private static final String regex = "[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)";
    private static final Pattern LINK_PATTERN = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

    public LinkListener(AllmightyBot bot) {
        this.bot = bot;
    }

    @Override
    public void onMessage(MessageEvent event) throws Exception {
        super.onMessage(event);

        if (!this.bot.getSettings().shouldTimeoutLinks() || event.getUser() == event.getBot().getUserBot() || bot
                .isModerator(event.getUser().getNick()) || PermitCommand.hasPermit(event.getUser().getNick()) ||
                !LINK_PATTERN.matcher(event.getMessage()).find()) {
            return;
        }

        boolean matched = false;

        for (String regex : this.bot.getAllowedLinks()) {
            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            if (pattern.matcher(event.getMessage()).find()) {
                matched = true;
                break;
            }
        }

        if (!matched) {
            event.getChannel().send().message(Utils.replaceVariablesInString(App.INSTANCE.getLangValue
                    ("linkTimeoutMessage"), Utils.timeConversion(App.INSTANCE.getSettings().getLinkTimeoutLength())));
            event.getChannel().send().message(".timeout " + event.getUser().getNick() + " " + App.INSTANCE
                    .getSettings().getLinkTimeoutLength());
        }
    }
}
