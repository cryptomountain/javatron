# Javatron

Copyright (C) 2001-2002, Taylor Gautier

Copyright (C) 2010-2015, Joe Culbreth

### ABOUT

JavaTron is a Java application that controls a TurtleBeach AudioTron Network MP3 player.  It provides simple buttons for player controls such as play, pause, stop etc., as well as sophisticated playlist management and display. It also has a simple to use radio.txt editor for adding/manipulating internet radio stations. 

### USAGE

##### Windows
Use the Setup Program to install

##### Linux/Mac/Others
java -jar javatron-2.2.8.jar [audiotron-ip] [username] [password]

### VERSION INFO

This version works with TurtleBeach AudioTron Firmware 3.2.25.

Use JavaTron 2.2.2 or less if your firmware revision is less than 2.5.2. (Earlier versions can be found on sourceforge.)

### NOTES

* Save Settings (IP Address, Username, Password) - You can save the AudioTron IP address, username, and password from the "Prefs" screen which is located by pressing the "Prefs" button from the main screen. The default tab is the "Preferences:General" tab which contains the settings for the IP Address, username and password.  Once they are set, press the save button located in the lower right hand corner of the dialog box.  This will save a file called "javatron.prefs" in the JavaTron current directory.

* There is currently no install/uninstall program for Linux or Mac, so you will have to remove this file yourself if you want to reset to the
defaults or are uninstalling JavaTron.

* General - JavaTron keeps cached information about the songs your AudioTron has indexed.  If you need to clear this state (if you reboot the AudioTron and there are new files in your music share(s)) you will need to restart JavaTron.

* Many of the classes and architecture can be used for Android, if you are working on an Android port, I'll be happy to answer any questions

### Building

Javatron is a Netbeans project. It uses the incredibly simple and robust org.desktop framework for dialog building etc. Because of this you need to use a version of Netbeans 7.12 or less. You may need to set some pathing to get it to build and debug in the Netbeans framework. If anyone feels like moving it to an eclipse project, have at it. 


