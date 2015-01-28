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

package me.ryandowling.allmightybot.data;

import org.apache.commons.lang3.StringEscapeUtils;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.regex.Pattern;

public class Spam {
    private String search;
    private String response;
    private SpamActionType action;
    private int time = 5;

    private Pattern pattern;

    public String getSearch() {
        return this.search;
    }

    public String getResponse() {
        return this.response;
    }

    public SpamActionType getAction() {
        return this.action;
    }

    public int getTime() {
        return this.time;
    }

    public int getTimeout() {
        if (this.time <= 0) {
            return 5 * 60;
        }

        return this.time * 60;
    }

    public boolean shouldTakeAction(MessageEvent event) {
        String message = StringEscapeUtils.escapeJava(event.getMessage());

        if (this.pattern == null) {
            this.pattern = Pattern.compile(Pattern.quote(this.search), Pattern.CASE_INSENSITIVE);
        }

        return this.pattern.matcher(message).find();
    }

    public void takeAction(MessageEvent event) {
        event.getChannel().send().message(this.response);
        switch (this.action) {
            case BAN:
                event.getChannel().send().message(".ban " + event.getUser().getNick());
                break;
            case TIMEOUT:
                event.getChannel().send().message(".timeout " + event.getUser().getNick() + " " + this.getTimeout());
                break;
        }
    }

    public Spam create(String search, String response, SpamActionType action, int time) {
        this.search = search;
        this.response = response;
        this.action = action;
        this.time = time;

        return this;
    }
}
