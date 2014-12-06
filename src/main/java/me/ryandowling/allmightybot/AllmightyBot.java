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

import java.io.IOException;
import java.nio.file.Files;

public class AllmightyBot {
    private final static Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Logger logger = LogManager.getLogger(App.class.getName());
    private Settings settings;

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
