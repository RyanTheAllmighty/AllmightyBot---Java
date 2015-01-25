Allmighty Bot
====================================

### What is it?

Allmighty Bot is a bot used by me in my Twitch channel. Could probably also be used as a general IRC bot.

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
start up the bot with 'java -jar AllmightyBot.jar' you should pass in true if this is a new stream or false if it's
not (for instance if the bot got disconnected or your computer crashed etc) so to start a new stream you start the
bot with 'java -jar AllmightyBot.jar true' or to continue the stream use 'java -jar AllmightyBot.jar false'

On first startup you'll be asked to input some information. One of those things is a Twitch API token. To get a
Twitch API token for use in the application you can either visit http://www.ryandowling.me/twitch-api-token-generator
to generate a token using my site or you can follow the directions on Twitch's API docs
(https://github.com/justintv/Twitch-API/blob/master/authentication.md) making sure to grant rights for all scopes when
doing so.

You'll also be asked for Twitch IRC oauth token which you can get from http://twitchapps.com/tmi

While the bot is under development and constantly being updated, the bot may or may not work. As this intended for my
use, there may be times when things are added/edited which break it for you. If you have any issues, please feel free
to open an issue in this repository.

### License

This work is licensed under the GNU General Public License v3.0. To view a copy of this license, visit
http://www.gnu.org/licenses/gpl-3.0.txt.