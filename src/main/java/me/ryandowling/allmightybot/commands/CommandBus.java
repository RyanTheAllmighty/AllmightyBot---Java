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

import java.util.HashMap;
import java.util.Map;

public class CommandBus {
    private final static Map<String, Command> commands = new HashMap<>();

    public static Command find(String commandName) {
        return commands.get(commandName);
    }

    public static void add(Command command) {
        commands.put(command.getName(), command);
    }

    public static void removeAll() {
        commands.clear();
    }

    public static Map<String, Command> getAll() {
        return commands;
    }

    public static Command findByClassName(String className) {
        for (Map.Entry<String, Command> entry : commands.entrySet()) {
            Command command = entry.getValue();

            if (command.getClass().getName().equalsIgnoreCase(className)) {
                return command;
            }
        }

        return null;
    }

    public static Command findByCommandName(String commandName) {
        for (Map.Entry<String, Command> entry : commands.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(commandName)) {
                return entry.getValue();
            }
        }

        return null;
    }
}
