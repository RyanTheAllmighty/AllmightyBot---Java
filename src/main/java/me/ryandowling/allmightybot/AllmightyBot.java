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
package me.ryandowling.allmightybot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.ryandowling.allmightybot.data.Settings;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;

import javax.swing.JOptionPane;
import java.io.IOException;
import java.nio.file.Files;

public class AllmightyBot {
    private final static Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Logger logger = LogManager.getLogger(App.class.getName());
    private Settings settings;
    private PircBotX pirc;

    public AllmightyBot() {
        if (Files.exists(Utils.getSettingsFile())) {
            try {
                this.settings = GSON.fromJson(FileUtils.readFileToString(Utils.getSettingsFile().toFile()), Settings
                        .class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            this.settings = new Settings();
        }

        if (!this.settings.hasInitialSetupBeenCompleted()) {
            String input = null;

            do {
                input = JOptionPane.showInputDialog(null, "Please enter the username of the Twitch user " + "to act " +
                        "as the bot", "Twitch Username", JOptionPane.QUESTION_MESSAGE);
                settings.setTwitchUsername(input);
            } while (input == null);

            do {
                input = JOptionPane.showInputDialog(null, "Please enter the token of the Twitch user " + "acting as " +
                        "the bot", "Twitch Token", JOptionPane.QUESTION_MESSAGE);
                settings.setTwitchToken(input);
            } while (input == null);

            do {
                input = JOptionPane.showInputDialog(null, "Please enter the username of the Twitch user " + "whose " +
                        "channel you wish to join", "User To Join", JOptionPane.QUESTION_MESSAGE);
                settings.setTwitchChannel(input);
            } while (input == null);

            this.settings.initialSetupComplete();
        }

        this.pirc = new PircBotX(this.settings.getBuilder().addListener(new CommandListener()).buildConfiguration());

        try {
            this.pirc.startBot();
        } catch (IOException | IrcException e) {
            e.printStackTrace();
        }

        // Shut it all down
        this.shutDown();
    }

    /**
     * Starts the bot up
     */

    public void startUp() {
        logger.info("Bot starting up!");
    }

    /**
     * Issues a shutdown command to safely shutdown and save all files needed
     */
    public void shutDown() {
        logger.info("Bot shutting down!");
        try {
            FileUtils.write(Utils.getSettingsFile().toFile(), GSON.toJson(this.settings));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
