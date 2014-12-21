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
import me.ryandowling.allmightybot.App;
import me.ryandowling.allmightybot.utils.TwitchAPI;
import org.json.simple.parser.ParseException;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.IOException;

public class TopicCommand extends BaseCommand {
    private String topic;
    private long lastUpdated;

    public TopicCommand(String name, int timeout) {
        super(name, timeout);
        checkChannelTopic();
    }

    @Override
    public boolean run(AllmightyBot bot, MessageEvent event) {
        if (super.run(bot, event)) {
            checkChannelTopic();
            event.getChannel().send().message("The channel's topic is '" + this.topic + "'");
            return true;
        }

        return false;
    }

    public void checkChannelTopic() {
        if(this.lastUpdated > (System.currentTimeMillis() - ((60 * 5) * 1000))) {
            // Only check the API if it's been at least 5 minutes
            System.out.println("Topic is coming from cache!");
            return;
        }

        try {
            System.out.println("Topic is coming from API!");
            this.topic = TwitchAPI.getTopic(App.INSTANCE.getSettings().getTwitchChannel());
            this.lastUpdated = System.currentTimeMillis();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            this.topic = "ERROR!";
        }
    }

    public void setChannelTopic(String topic) {
        this.topic = topic;
    }
}
