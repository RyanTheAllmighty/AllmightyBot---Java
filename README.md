AllmightyBot - Java
====================================

### Warning
This application is no longer being updated. For the updated version written in NodeJS see [here](https://github.com/RyanTheAllmighty/AllmightyBot)

### What is it?

AllmightyBot is a bot used by me in my Twitch channel. Could probably also be used as a general IRC bot.

### Coding Standards

+ Please keep all line lengths to 120 characters and use 4 spaces rather than tab characters
+ Please keep all variables at the top of the class
+ Please keep all inner classes at the bottom
+ Please don't use star imports
+ Please use the IntelliJ-Coding-Style.xml for the project (if using IntelliJ) in order to keep all formatting consistent
+ Please don't do large commits. My preference is a single commit for a single fix/addition rather than bundled up commits

### Downloads

If you wish to download the bot without having to build it, you can do so via my Jenkins server at https://build.atlcdn.net/job/Allmighty%20Bot/

### Getting Started

To get a runnable Jar file run 'mvn clean install' in the root of the project.

After you have a runnable Jar file, you'll need to setup a bunch of stuff, mainly the commands, languages, etc. A
bunch of example files are generated after setting up the bot.

When starting the bot, you need to provide a command line argument on if this is a new stream or not. So when you
start up the bot with 'java -jar AllmightyBot.jar' you should pass in the argument 'new' if this is a new stream or
no argument if continuing (for instance if the bot got disconnected or your computer crashed etc) so to start a new
stream you start the bot with 'java -jar AllmightyBot.jar new' or to continue you don't need to specify any extra
arguments.

On first startup you'll be asked to input some information. One of those things is a Twitch API token. To get a
Twitch API token for use in the application you can either visit http://www.ryandowling.me/twitch-api-token-generator
to generate a token using my site or you can follow the directions on Twitch's API docs
(https://github.com/justintv/Twitch-API/blob/master/authentication.md) making sure to grant rights for all scopes when
doing so.

You'll also be asked for Twitch IRC oauth token which you can get from http://twitchapps.com/tmi

In order to get the SongCommand working, you need to have 2 files in the root directory where the bot is. 'nowplaying
.txt' and 'nowplayingfile.txt'. nowplaying.txt contains the artists name and song name seperate by ' - ' so for
instance it should say 'Some Artist - Some Song'. The 'nowplayingfile.txt' file should contain the full path to where
the song is on your computer. In order to have the bot provide the website for the artist, you must add a 'website
.txt' file to each of your folders for each artist. So for instance you have an artist called 'Some Artist' the
folder that their songs are in on your computer should have a 'Some Artist/website.txt' file with nothing but the
link to that artists page. Yes I know this is confusing, but it's how it works with my setup to properly attribute
and recognise all the artists music that I play.

While the bot is under development and constantly being updated, the bot may or may not work. As this intended for my
use, there may be times when things are added/edited which break it for you. If you have any issues, please feel free
to open an issue in this repository.

Before opening an issue if an update breaks things, make sure you try starting the bot fresh with no files so it can
regenerate new files since when certain files update, you won't get the new files which will cause issues, so getting
them from a fresh instance of the bot would be advisable before posting an issue.

### License

This work is licensed under the GNU General Public License v3.0. To view a copy of this license, visit
http://www.gnu.org/licenses/gpl-3.0.txt.
