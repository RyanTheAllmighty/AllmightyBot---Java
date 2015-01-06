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
import me.ryandowling.allmightybot.App;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class TwitchAPIRequest {
    private static final String TWITCH_API_BASE = "https://api.twitch.tv/kraken";
    private final String accessToken;
    private final String clientID;
    private String path = "/";
    private HttpsURLConnection connection;

    public TwitchAPIRequest() {
        this.accessToken = App.INSTANCE.getSettings().getTwitchApiToken();
        this.clientID = App.INSTANCE.getSettings().getTwitchApiClientID();
    }

    public TwitchAPIRequest(String path) {
        this();
        if (path.length() == 0 || !path.substring(0, 1).equals("/")) {
            this.path = "/" + path;
        } else {
            this.path = path;
        }
    }

    public String get() throws IOException {
        this.connect("GET");

        InputStream in = this.connection.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = br.readLine()) != null) {
            response.append(inputLine);
        }

        in.close();

        this.disconnect();

        return response.toString();
    }

    public String put(Object object) throws IOException {
        this.connect("PUT", object);

        InputStream in = this.connection.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = br.readLine()) != null) {
            response.append(inputLine);
        }

        in.close();

        this.disconnect();

        return response.toString();
    }

    private void connect(String requestMethod) throws IOException {
        this.connect(requestMethod, null);
    }

    private void connect(String requestMethod, Object object) throws IOException {
        System.out.println("Connecting to " + TWITCH_API_BASE + this.path);
        URL url = new URL(TWITCH_API_BASE + this.path);
        this.connection = (HttpsURLConnection) url.openConnection();
        this.connection.setRequestMethod(requestMethod);
        this.connection.setUseCaches(false);
        this.connection.setDefaultUseCaches(false);
        this.connection.setRequestProperty("Cache-Control", "no-store,max-age=0,no-cache");
        this.connection.setRequestProperty("Expires", "0");
        this.connection.setRequestProperty("Pragma", "no-cache");
        this.connection.setRequestProperty("Authorization", "OAuth " + this.accessToken);
        this.connection.setRequestProperty("Client-ID", "OAuth " + this.clientID);
        this.connection.setRequestProperty("Accept", "application/vnd.twitchtv.v3+json");
        this.connection.setRequestProperty("Content-Type", "application/json");

        if (object != null) {
            this.connection.setRequestProperty("Content-Length", "" + AllmightyBot.GSON.toJson(object).getBytes()
                    .length);
            connection.setDoOutput(true);
        }

        this.connection.connect();

        if (object != null) {
            DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
            writer.write(AllmightyBot.GSON.toJson(object).getBytes());
            writer.flush();
            writer.close();
        }
    }

    private void disconnect() {
        if (this.connection != null) {
            this.connection.disconnect();
        }
    }
}
