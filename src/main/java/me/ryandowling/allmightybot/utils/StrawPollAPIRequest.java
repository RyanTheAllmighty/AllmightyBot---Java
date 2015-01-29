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

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class StrawPollAPIRequest {
    private static final String STRAWPOLL_API_BASE = "http://strawpoll.me/api/v2";
    private String path = "/";
    private HttpURLConnection connection;

    public StrawPollAPIRequest(String path) {
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

    public String post(Object object) throws IOException {
        this.connect("POST", object);

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
        System.out.println("Connecting to " + STRAWPOLL_API_BASE + this.path);
        URL url = new URL(STRAWPOLL_API_BASE + this.path);
        this.connection = (HttpURLConnection) url.openConnection();
        this.connection.setRequestMethod(requestMethod);
        this.connection.setRequestProperty("Content-Type", "application/json");
        this.connection.setRequestProperty("charset", "UTF-8");
        this.connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36" +
                " (KHTML, like Gecko) Chrome/40.0.2214.93 Safari/537.36");

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
