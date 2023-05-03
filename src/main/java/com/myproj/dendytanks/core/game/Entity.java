package com.myproj.dendytanks.core.game;

import com.myproj.dendytanks.core.IO.Input;

import java.awt.*;

public abstract class Entity {
    public final EntityType type;
    protected float x;
    protected float y;
    protected int width;
    protected int height;
    protected Entity(EntityType type, float x, float y){
        this.type = type;
        this.x = x;
        this.y = y;
    }
    public abstract void update(Input input);
    public abstract void render(Graphics2D g);
    public abstract int getSpriteWidth();
    public abstract int getSpriteHeight();
    public  float getX(){
        return y;
    }
    public float getY(){
        return x;
    }



}
