package com.myproj.dendytanks.core.game.level;

public enum TileType {
    EMPTY(0, false,true, true),
    BRICK(1,true,false, false),
    METAL(2,false,false, false),
    WATER(3,false,false, true),
    GRASS(4,false,true,true),
    ICE(5,true,true,true),
    PLAYERS_SPAWN(6,false,true, true),
    ENEMIES_SPAWN(7,false,true, true);
    private int n;
    private boolean isBreakable;
    private boolean isPenetrable;
    private boolean isPenetrable4Missile;
    TileType(int n, boolean isBreakable, boolean isPenetrable, boolean isPenetrable4Missile){
        this.n = n;
        this.isBreakable = isBreakable;
        this.isPenetrable = isPenetrable;
        this.isPenetrable4Missile = isPenetrable4Missile;
    }
    public int numeric(){
        return n;
    }
    public static TileType fromNumeric(int n){
        switch(n){
            case 1:
                return BRICK;
            case 2:
                return METAL;
            case 3:
                return WATER;
            case 4:
                return GRASS;
            case 5:
                return ICE;
            case 6:
                return PLAYERS_SPAWN;
            case 7:
                return ENEMIES_SPAWN;
            default:
                return EMPTY;
        }
    }
    public boolean isBreakable(){
        return isBreakable;
    }
    public boolean isPenetrable(){
        return isPenetrable;
    }
    public boolean isPenetrable4Missile(){
        return isPenetrable4Missile;
    }
}
