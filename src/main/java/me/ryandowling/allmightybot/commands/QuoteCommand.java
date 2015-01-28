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

import com.google.gson.reflect.TypeToken;
import me.ryandowling.allmightybot.AllmightyBot;
import me.ryandowling.allmightybot.Utils;
import me.ryandowling.allmightybot.data.Quote;
import me.ryandowling.allmightybot.data.QuoteType;
import org.apache.commons.io.FileUtils;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

public class QuoteCommand extends BaseCommand {
    private final QuoteType quoteType;
    private static List<Quote> quotes = new ArrayList<>();

    public QuoteCommand(String name, QuoteType quoteType, int timeout) {
        super(name, timeout);

        this.quoteType = quoteType;
    }

    @Override
    public boolean run(AllmightyBot bot, MessageEvent event) {
        if (super.run(bot, event)) {
            switch (this.quoteType) {
                case NEW:
                    List<String> arguments = Utils.getCommandsArguments(1, event.getMessage(), true);

                    if (arguments.size() != 1) {
                        return false;
                    }

                    quotes.add(new Quote(arguments.get(0)));

                    event.getChannel().send().message(bot.getLangValue("newQuoteAdded"));
                    break;
                case SAY:
                    if (quotes.size() == 0) {
                        return false;
                    }

                    Random random = new Random();
                    Quote randomQuote = quotes.get(random.nextInt(quotes.size()));

                    Calendar cal = new GregorianCalendar();
                    cal.setTime(randomQuote.getDate());

                    event.getChannel().send().message(Utils.replaceVariablesInString(bot.getLangValue("sayQuote"),
                            randomQuote.getQuote(), bot.getSettings().getCastersName(), cal.get(Calendar.YEAR) + ""));
                    break;
            }
            return true;
        }

        return false;
    }

    @Override
    public void load() {
        super.load();

        if (Files.exists(Utils.getCommandDataFile(this))) {
            try {
                Type type = new TypeToken<List<Quote>>() {
                }.getType();
                quotes = AllmightyBot.GSON.fromJson(FileUtils.readFileToString(Utils.getCommandDataFile(this).toFile
                        ()), type);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void save() {
        super.save();
        try {
            FileUtils.write(Utils.getCommandDataFile(this).toFile(), AllmightyBot.GSON.toJson(quotes));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
