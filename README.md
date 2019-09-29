# CheckersRFIB

Checkers (draughts) demo for RFIBricks.

<!-- ## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system. -->

## Prerequisites

### Processing

Download the libraries from [Processing](https://github.com/processing/processing/) and then put them under libs/. The required libraries are shown in compile.sh and run.sh. 

### Octane SDK

For simulation this is not needed, but to utilize RFID system download [Octane SDK](https://support.impinj.com/hc/en-us/articles/202755268-Octane-SDK) here. Similarily, put them under libs/. Note that this repo does not contain library for real RFID demo. 

## Running the program

When using IDE be sure to import libraries in libs/ before compling and running src/RFIB_DEMO.java. Alternatively, specify the classpaths in the shell scripts and then run as follows:

```
sh compile.sh; sh run.sh
```

## How to play

### Operations simulating moving the pieces

* left click: take the piece(s) off the board 
* right click: put the piece(s) on to the board 
 
### Usage

* Before the game, be sure to check the rules of checkers.
* Put the bricks on the board. Note that the valid positions are marked by white spots at the centre. Then press "r" to start the game.
	* Alternatively, press "b" to pre-stack the bricks.
	* Or, left click and right click to set the board. Change the colour of pieces by pressing "t".
* Flashing hints of colour blue/green indicate the movable pieces or the valid moves. 
* Flashing hint of yellow indicates that one take off that piece, or promote that piece. The promoted one would be marked with a crown.
* Follow the hints, or the game will halt. To restart the game, reset the board to a valid state and then press "r".
* Press "t" if you need to manually change the turn.
