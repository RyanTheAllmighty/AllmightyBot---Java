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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Utils {
    public static Path getCoreDir() {
        return Paths.get(System.getProperty("user.dir"));
    }

    public static Path getSettingsFile() {
        return getCoreDir().resolve("settings.json");
    }

    public static Path getCommandsFile() {
        return getCoreDir().resolve("commands.json");
    }

    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    public static Path getUserLoginTimeFile(String user) {
        Path file = getCoreDir().resolve("users").resolve(user);
        if (!Files.isDirectory(file)) {
            try {
                Files.createDirectories(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file.resolve("loginTime.json");
    }

    public static Path getUserChatFile(String user) {
        Path file = getCoreDir().resolve("users").resolve(user);
        if (!Files.isDirectory(file)) {
            try {
                Files.createDirectories(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file.resolve("chat.json");
    }

    public static Path getEventsFile() {
        String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        Path file = getCoreDir().resolve("events");

        if (!Files.isDirectory(file)) {
            try {
                Files.createDirectories(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        int i = 0;

        do {
            if (i == 0) {
                file = getCoreDir().resolve("events").resolve(date + ".json");
            } else {
                file = getCoreDir().resolve("events").resolve(date + "-" + i + ".json");
            }
            i++;
        } while (Files.exists(file));

        return file;
    }
}
