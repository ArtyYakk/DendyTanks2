package com.myproj.dendytanks.core.entities;

import com.myproj.dendytanks.core.IO.Input;
import com.myproj.dendytanks.core.game.Entity;
import com.myproj.dendytanks.core.game.EntityType;
import com.myproj.dendytanks.core.game.Game;
import com.myproj.dendytanks.core.graphics.Sprite;
import com.myproj.dendytanks.core.graphics.SpriteSheet;
import com.myproj.dendytanks.core.graphics.TextureAtlas;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class Missile extends Entity {

    public static final int SPRITE_SCALE = 41;
    public static final int SPRITES_PER_HEADING = 1;
    private enum Heading{
        NORTH(40*SPRITE_SCALE, 12*SPRITE_SCALE + 20, 1*SPRITE_SCALE, 1*SPRITE_SCALE),
        EAST(43*SPRITE_SCALE, 12*SPRITE_SCALE + 20, 1*SPRITE_SCALE, 1*SPRITE_SCALE),
        SOUTH(42*SPRITE_SCALE, 12*SPRITE_SCALE + 20, 1*SPRITE_SCALE, 1*SPRITE_SCALE),
        WEST(41*SPRITE_SCALE, 12*SPRITE_SCALE + 20, 1*SPRITE_SCALE, 1*SPRITE_SCALE);
        private int x, y, w, h;
        Heading(int x, int y, int w, int h){
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }
        protected BufferedImage texture(TextureAtlas atlas){
            return atlas.cut(x,y,w,h);
        }
    }
    private Heading                 heading;
    private Map<Heading, Sprite>    spriteMap;
    private float                   scale;
    private float                   speed;
    private int                     direction = -1;
    private boolean                 missileExists = false;
    private boolean                 isEnemy;

    public Missile(float x, float y, float scale, float speed, TextureAtlas atlas, int direction,  boolean isEnemy) {
        super(EntityType.Missile, x, y);

        spriteMap = new HashMap<Heading, Sprite>();
        this.scale = scale;
        this.speed = speed;
        missileExists = true;
        this.isEnemy = isEnemy;

        this.direction = direction;
        switch (direction){
            case 0 -> heading = Heading.NORTH;
            case 1 -> heading = Heading.EAST;
            case 2 -> heading = Heading.SOUTH;
            case 3 -> heading = Heading.WEST;
        }

        for(Heading h : Heading.values()){
            SpriteSheet sheet  = new SpriteSheet(h.texture(atlas), SPRITES_PER_HEADING, SPRITE_SCALE);
            Sprite sprite = new Sprite(sheet, scale, 50_000,0); //!!
            spriteMap.put(h, sprite);

            width = sprite.getWidth();
            height = sprite.getHeight();
        }


    }

    @Override
    public void update(Input input) {

        switch (heading){
            case NORTH -> y-=speed;
            case EAST -> x+=speed;
            case SOUTH -> y+=speed;
            case WEST -> x-=speed;
        }

        if(x < 0){
            missileExists = false;
        } else if (x >= Game.WIDTH - SPRITE_SCALE*scale) {
            missileExists = false;
        }

        if(y < 0){
            missileExists = false;
        } else if (y >= Game.HEIGTH - SPRITE_SCALE*scale) {
            missileExists = false;
        }

    }

    @Override
    public void render(Graphics2D g) {
        spriteMap.get(heading).render(g, x, y+10);
    }
    public boolean exists(){
        return missileExists;
    }
    public void setBroken(){
        missileExists = false;
    }
    public boolean getEnemyStatus(){
        return isEnemy;
    }
    public int getSpriteWidth(){
        return spriteMap.get(heading).getWidth();
    }

    public int getSpriteHeight(){
        return spriteMap.get(heading).getHeight();
    }

    //Методы для получения реальных координат снаряда, а не изображения
    @Override
    public float getX(){
        return x + getSpriteWidth()/2f;
    }
    @Override
    public float getY(){
        return y + getSpriteHeight() - 2;
    }
    public Rectangle2D.Float getRectangle(){
        return new Rectangle2D.Float(x+3,y+12,getSpriteWidth()*0.6f,getSpriteHeight()*0.7f); //20 на 20 спрайт
    }
}

