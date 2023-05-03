package com.myproj.dendytanks.core.entities;

import com.myproj.dendytanks.core.IO.Input;
import com.myproj.dendytanks.core.game.Entity;
import com.myproj.dendytanks.core.game.EntityType;
import com.myproj.dendytanks.core.game.Game;
import com.myproj.dendytanks.core.game.level.Level;
import com.myproj.dendytanks.core.graphics.Sprite;
import com.myproj.dendytanks.core.graphics.SpriteSheet;
import com.myproj.dendytanks.core.graphics.TextureAtlas;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class Player extends Entity {

    public static final int SPRITE_SCALE = 82;
    public static final int SPRITES_PER_HEADING = 1;
    private enum Heading{
        NORTH(0*SPRITE_SCALE, 0*SPRITE_SCALE, 1*SPRITE_SCALE, 1*SPRITE_SCALE),
        EAST(6*SPRITE_SCALE, 0*SPRITE_SCALE, 1*SPRITE_SCALE, 1*SPRITE_SCALE),
        SOUTH(4*SPRITE_SCALE, 0*SPRITE_SCALE, 1*SPRITE_SCALE, 1*SPRITE_SCALE),
        WEST(2*SPRITE_SCALE, 0*SPRITE_SCALE, 1*SPRITE_SCALE, 1*SPRITE_SCALE);
        private int x, y, h, w;
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
    private Heading                 missileHeading;
    private Missile                 missile = null;
    private Map<Heading, Sprite>    spriteMap;
    private float                   scale;
    private float                   speed;
    private  TextureAtlas           atlas;
    private Level lvl;
    public Player(float x, float y, float scale, float speed, TextureAtlas atlas, Level lvl) {
        super(EntityType.Player, x, y);

        heading = Heading.NORTH;
        spriteMap = new HashMap<Heading, Sprite>();
        this.scale = scale;
        this.speed = speed;
        this.atlas = atlas;
        this.lvl = lvl;

        for(Heading h : Heading.values()){
            SpriteSheet sheet  = new SpriteSheet(h.texture(atlas), SPRITES_PER_HEADING, SPRITE_SCALE);
            Sprite sprite = new Sprite(sheet, scale, 3_000_000, 0);
            spriteMap.put(h, sprite);

            width = sprite.getWidth();
            height = sprite.getHeight();
        }

    }

    @Override
    public void update(Input input) {

        if((missile != null)&&(!missile.exists())){
            missile = null;
        }

        float newX = x;
        float newY = y;
        float missileX = SPRITE_SCALE/8f;

        if(input.getKey(KeyEvent.VK_S)){  //Выстрел в направлении танка
           if(missile == null){
               switch (heading){
                   case NORTH -> missile = new Missile(x + missileX,y,scale,speed*2, atlas, 0, false);
                   case EAST -> missile = new Missile(x + missileX,y,scale,speed*2, atlas, 1,false);
                   case SOUTH -> missile = new Missile(x + missileX,y,scale,speed*2, atlas, 2,false);
                   case WEST -> missile = new Missile(x + missileX,y,scale,speed*2, atlas, 3,false);
               }
           }
        }

        if(input.getKey(KeyEvent.VK_UP)){
            newY -= speed;
            heading = Heading.NORTH;
        } else if(input.getKey(KeyEvent.VK_RIGHT)){
            newX += speed;
            heading = Heading.EAST;
        }else if(input.getKey(KeyEvent.VK_DOWN)){
            newY += speed;
            heading = Heading.SOUTH;
        }else if(input.getKey(KeyEvent.VK_LEFT)){
            newX -= speed;
            heading = Heading.WEST;
        }

        if(newX < 0){
            newX = 0;
        } else if (newX >= Game.WIDTH - SPRITE_SCALE*scale) {
            newX = Game.WIDTH - SPRITE_SCALE*scale;
        }

        if(newY < 0){
            newY = 0;
        } else if (newY >= Game.HEIGTH - SPRITE_SCALE*scale) {
            newY = Game.HEIGTH - SPRITE_SCALE*scale;
        }

        //ПРОВЕРКА НА ЗАСТУП ЗА ТЕКСТУРЫ НЕПРОНИЦАЕМЫХ ОБЪЕКТОВ
        switch (heading){
            case NORTH -> {
                if(!lvl.isPenetrable(newX - 2,newY - 7) //Препятствие сверху
                        ||(!lvl.isPenetrable(newX + getSpriteWidth()/2f - 18/2f,newY - 7)) //Препятствие сверху (середина)
                        ||(!lvl.isPenetrable(newX + getSpriteWidth() - 18,newY - 7)) //Препятствие сверху (правее)
                ){
                    newY += speed;
                }
            }
            case EAST -> {
                if(!lvl.isPenetrable(newX + getSpriteWidth() - 18, newY - 3) //Препятствие справа
                        ||(!lvl.isPenetrable(newX + getSpriteWidth() - 18, newY + getSpriteHeight()/2f - 18/2f)) //Препятствие справа (середина)
                        ||(!lvl.isPenetrable(newX + getSpriteWidth() - 18, newY + getSpriteHeight() - 18)) //Препятствие справа (ниже)
                ){
                    newX -= speed;
                }
            }
            case SOUTH -> {
                if(!lvl.isPenetrable(newX - 2, newY + getSpriteHeight() - 17) //Препятствие снизу
                        ||(!lvl.isPenetrable(newX + getSpriteWidth()/2f - 18/2f,newY + getSpriteHeight() - 17)) //Препятствие снизу (середина)
                        ||(!lvl.isPenetrable(newX + getSpriteWidth() - 18,newY + getSpriteHeight() - 17)) //Препятствие снизу (правее)
                ){
                    newY -= speed;
                }
            }
            case WEST -> {
                if(!lvl.isPenetrable(newX - 7, newY - 3) //Препятствие слева
                        ||(!lvl.isPenetrable(newX - 7, newY + getSpriteHeight()/2f - 18/2f)) //Препятствие слева (середина)
                        ||(!lvl.isPenetrable(newX - 7, newY + getSpriteHeight() - 18)) //Препятствие слева (ниже)
                ){
                    newX += speed;
                }
            }
        }

        x = newX;
        y = newY;


      // System.out.println(x + " " + y);

        if(missile != null){
            missile.update(input);
        }
    }

    @Override
    public void render(Graphics2D g) {

        if(missile != null){
            missile.render(g);
        }

        spriteMap.get(heading).render(g, x, y);


    }

    public Missile getMissile(){
        if ((missile != null)&&(missile.exists())) {
            return missile;
        }
        else{
            return null;
        }
    }

    public int getSpriteWidth(){
        return spriteMap.get(heading).getWidth();
    }

    public int getSpriteHeight(){
        return spriteMap.get(heading).getHeight();
    }
}
