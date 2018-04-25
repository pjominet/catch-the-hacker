# CATCH THE HACKER

## Turn based Text Adventure

You play as a cybersecurity guy, chasing an evil hacker through a network of different computers while trying to stop
 him stealing crytocoins from unsuspecting users.
 
## Prerequisites

* JAVA 8+
* [Maven 3](http://maven.apache.org/download.cgi)

## Install

`mvn install` to initialize all dependencies

## Build a jar

`mvn clean package` to build an executable jar

## Run the game

There are to options in Main on how to run the game:

* `game.debug()` is the test mode for developing and outputting test values and current game states
* `game.run()` is the default game flow that the game should use when playing (or play-testing) the game

Uncomment/comment accordingly.