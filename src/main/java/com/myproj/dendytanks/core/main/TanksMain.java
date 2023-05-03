package com.myproj.dendytanks.core.main;


import com.myproj.dendytanks.core.game.Game;



public class TanksMain{
    Game tanks;

    public TanksMain(){
        tanks = new Game();
    }

    public void start(int numOfLevel, boolean isRedactable) {
        tanks.start(numOfLevel, isRedactable);
    }

    public void stop() {
       tanks.stop();
    }
}
