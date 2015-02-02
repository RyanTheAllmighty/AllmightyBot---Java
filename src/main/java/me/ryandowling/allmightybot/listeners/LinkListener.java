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

import com.google.gson.reflect.TypeToken;
import me.ryandowling.allmightybot.AllmightyBot;
import me.ryandowling.allmightybot.App;
import me.ryandowling.allmightybot.Utils;
import me.ryandowling.allmightybot.commands.PermitCommand;
import org.apache.commons.io.IOUtils;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class LinkListener extends ListenerAdapter {
    private AllmightyBot bot;
    private static Map<String, Integer> warnings = new HashMap<>();
    private static List<String> domains = new ArrayList<>();
    private static final String regex = "[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)";
    private static final Pattern LINK_PATTERN = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

    public LinkListener(AllmightyBot bot) {
        this.bot = bot;

        try {
            Type type = new TypeToken<List<String>>() {
            }.getType();

            domains = AllmightyBot.GSON.fromJson(IOUtils.toString(System.class.getResource("/json/domains.json")),
                    type);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to read domains.json from jar! Exiting!");
            System.exit(1);
        }
    }

    @Override
    public void onMessage(MessageEvent event) throws Exception {
        super.onMessage(event);

        if (!this.bot.getSettings().shouldTimeoutLinks() || event.getUser() == event.getBot().getUserBot()  || PermitCommand.hasPermit(event.getUser().getNick()) ||
                !LINK_PATTERN.matcher(event.getMessage()).find()) {
            return;
        }

        boolean containsDomain = false;

        // Loop through the list of domains in the domains list (read from the jar) to see if it contains it
        Pattern domainPattern;
        for (String domain : domains) {
            domainPattern = Pattern.compile(".*\\." + domain + " ?\\b", Pattern.CASE_INSENSITIVE);
            if (domainPattern.matcher(event.getMessage()).find()) {
                containsDomain = true;
                break;
            }
        }

        if (!containsDomain) {
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
            if (!warnings.containsKey(event.getUser().getNick())) {
                warnings.put(event.getUser().getNick(), 1);

                event.getChannel().send().message(App.INSTANCE.getLangValue("linkTimeoutMessageWarning1"));
                event.getChannel().send().message(".timeout " + event.getUser().getNick() + " " + 1);
            } else {
                int timeoutLength;

                if (warnings.get(event.getUser().getNick()) == 1) {
                    warnings.put(event.getUser().getNick(), 2);
                    timeoutLength = App.INSTANCE.getSettings().getLinkTimeoutLength1();
                } else {
                    timeoutLength = App.INSTANCE.getSettings().getLinkTimeoutLength2();
                }

                event.getChannel().send().message(Utils.replaceVariablesInString(App.INSTANCE.getLangValue
                        ("linkTimeoutMessageWarning2"), Utils.timeConversion(timeoutLength)));
                event.getChannel().send().message(".timeout " + event.getUser().getNick() + " " + timeoutLength);
            }
        }
    }
}
