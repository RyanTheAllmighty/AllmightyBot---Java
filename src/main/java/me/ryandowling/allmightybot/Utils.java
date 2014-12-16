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

import me.ryandowling.allmightybot.commands.Command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static Path getSpamFile() {
        return getCoreDir().resolve("spam.json");
    }

    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    public static Path getUserLoginTimeFile(String user) {
        Path file = getUsersDir().resolve(user);
        if (!Files.isDirectory(file)) {
            try {
                Files.createDirectories(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file.resolve("loginTime.json");
    }

    public static Path getUsersDir() {
        return getCoreDir().resolve("users");
    }

    public static Path getUserChatFile(String user) {
        Path file = getUsersDir().resolve(user);
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

        return file.resolve(date + ".json");
    }

    public static String timeConversion(int totalSeconds) {
        final int MINUTES_IN_AN_HOUR = 60;
        final int SECONDS_IN_A_MINUTE = 60;

        int seconds = totalSeconds % SECONDS_IN_A_MINUTE;
        int totalMinutes = totalSeconds / SECONDS_IN_A_MINUTE;
        int minutes = totalMinutes % MINUTES_IN_AN_HOUR;
        int hours = totalMinutes / MINUTES_IN_AN_HOUR;

        return hours + " hours, " + minutes + " minutes and " + seconds + " seconds";
    }

    public static String timeConversionRaw(int totalSeconds) {
        final int MINUTES_IN_AN_HOUR = 60;
        final int SECONDS_IN_A_MINUTE = 60;

        int seconds = totalSeconds % SECONDS_IN_A_MINUTE;
        int totalMinutes = totalSeconds / SECONDS_IN_A_MINUTE;
        int minutes = totalMinutes % MINUTES_IN_AN_HOUR;
        int hours = totalMinutes / MINUTES_IN_AN_HOUR;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static List<String> getCommandsArguments(String message, boolean removeFirst) {
        List<String> list = new ArrayList<String>();

        // Found thanks to http://stackoverflow.com/a/7804472
        Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(message);
        while (m.find()) {
            list.add(m.group(1).replace("\"", ""));
        }
        if (removeFirst) {
            list.remove(0);
        }
        return list;
    }

    public static List<String> getCommandsArguments(int count, String message, boolean removeFirst) {
        List<String> list = new ArrayList<String>();

        int begin = 0;

        // Found thanks to http://stackoverflow.com/a/7804472
        Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(message);
        while (m.find()) {
            begin += m.group(1).length() + 1; // Count the space and match
            list.add(m.group(1).replace("\"", ""));
            if (list.size() == count) {
                break;
            }
        }

        list.add(message.substring(begin));

        if (removeFirst) {
            list.remove(0);
        }
        return list;
    }

    public static Path getCommandDataFile(Command command) {
        String name = command.getClass().getName();
        return getCoreDir().resolve("data").resolve(name.substring(name.lastIndexOf(".") + 1) + ".json");
    }

    public static Path getStreamOnlineTimeFile() {
        return getCoreDir().resolve("onlineTime.json");
    }

    public static String readURLToString(String url) throws IOException {
        StringBuilder response = null;
        URL urll = new URL(url);
        URLConnection connection = urll.openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.2; WOW64)");
        connection.setConnectTimeout(5000);
        BufferedReader in;
        in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }

        in.close();

        return response.toString();
    }
}
