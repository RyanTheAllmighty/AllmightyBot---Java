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

package me.ryandowling.allmightybot.utils;

import me.ryandowling.allmightybot.AllmightyBot;
import me.ryandowling.allmightybot.data.twitch.api.ChannelPutRequest;
import me.ryandowling.allmightybot.data.twitch.api.StreamResponse;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class TwitchAPI {
    public static void checkToken() {
        System.out.println("Checking Twitch API token");

        TwitchAPIRequest request = new TwitchAPIRequest("/");

        try {
            String response = request.get();
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(response);

            JSONObject token = (JSONObject) jsonObject.get("token");
            boolean valid = (boolean) token.get("valid");
            if (!valid) {
                System.err.println("API token not valid, exiting!");
                System.exit(1);
            }
            System.out.println("Token is valid, API connection successful!");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error connecting to Twitch API, exiting!");
            System.exit(1);
        } catch (ParseException e) {
            e.printStackTrace();
            System.err.println("Error occured parsing JSON from Twitch API, exiting!");
            System.exit(1);
        }

        System.out.println("Finished checking Twitch API token");
    }

    public static String getTopic(String username) throws IOException, ParseException {
        TwitchAPIRequest request = new TwitchAPIRequest("/streams/" + username);

        StreamResponse response = AllmightyBot.GSON.fromJson(request.get(), StreamResponse.class);

        return response.getStream().getChannel().getStatus();
    }

    public static String setTopic(String username, String topic) throws IOException, ParseException {
        TwitchAPIRequest request = new TwitchAPIRequest("/channels/" + username);

        String response = request.put(new ChannelPutRequest(topic));

        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(response);

        return (String) jsonObject.get("status");
    }

    public static StreamResponse getStreamDetails(String username) throws IOException {
        TwitchAPIRequest request = new TwitchAPIRequest("/streams/" + username);

        String response = request.get();

        return AllmightyBot.GSON.fromJson(response, StreamResponse.class);
    }

    public static int getViewerCount(String username) throws IOException {
        TwitchAPIRequest request = new TwitchAPIRequest("/streams/" + username);

        StreamResponse response = AllmightyBot.GSON.fromJson(request.get(), StreamResponse.class);

        if (response == null || response.getStream() == null) {
            return 0;
        }

        return response.getStream().getViewers();
    }
}
