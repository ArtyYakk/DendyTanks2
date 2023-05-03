package com.myproj.dendytanks.core.entities;

import com.myproj.dendytanks.core.IO.Input ;
import com.myproj.dendytanks.core.game.Entity;
import com.myproj.dendytanks.core.game.EntityType;
import com.myproj.dendytanks.core.game.Game;
import com.myproj.dendytanks.core.game.level.Level;
import com.myproj.dendytanks.core.graphics.Sprite;
import com.myproj.dendytanks.core.graphics.SpriteSheet;
import com.myproj.dendytanks.core.graphics.TextureAtlas;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class Enemy extends Entity {

    public static final int SPRITE_SCALE = 82;
    public static final int SPRITES_PER_HEADING = 1;
    private enum Heading{
        NORTH(8*SPRITE_SCALE, 0*SPRITE_SCALE, 1*SPRITE_SCALE, 1*SPRITE_SCALE - 10),
        EAST(14*SPRITE_SCALE, 0*SPRITE_SCALE, 1*SPRITE_SCALE, 1*SPRITE_SCALE),
        SOUTH(12*SPRITE_SCALE, 0*SPRITE_SCALE, 1*SPRITE_SCALE, 1*SPRITE_SCALE - 10),
        WEST(10*SPRITE_SCALE, 0*SPRITE_SCALE, 1*SPRITE_SCALE, 1*SPRITE_SCALE);
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
    private Map<Heading, Sprite>    spriteMap;
    private float                   scale;
    private float                   speed;
    private  TextureAtlas           atlas; // Атлас приходит в конструктор, далее передается снаряду
    private int                     rand          = 1 + (int)(Math.random()*4);
    private Missile                 missile = null;
    private Level                   lvl; //Данная переменная нужна для получения информации о состоянии карты
    private int turner = 0;


    public Enemy(float x, float y, float scale, float speed, TextureAtlas atlas, Level lvl) {
        super(EntityType.Enemy, x, y);

        spriteMap = new HashMap<Heading, Sprite>();
        this.scale = scale;
        this.speed = speed;
        this.atlas = atlas;
        this.lvl = lvl;

        switch (rand){ //Начальное направление танка
            case 1 -> heading = Heading.NORTH;
            case 2 -> heading = Heading.EAST;
            case 3 -> heading = Heading.SOUTH;
            case 4 -> heading = Heading.WEST;
        }

        for(Heading h : Heading.values()){
            SpriteSheet sheet  = new SpriteSheet(h.texture(atlas), SPRITES_PER_HEADING, SPRITE_SCALE);
            Sprite sprite = new Sprite(sheet, scale,50_000,0);
            spriteMap.put(h, sprite);

            width = sprite.getWidth();
            height = sprite.getHeight();
        }
    }

    @Override
    public void update(Input input) {



        if((missile != null)&&(!missile.exists())){ //Обьект снаряда создан, но снаряд уже разорвался
            missile = null;
        }

        float newX = x; //Промежеточное значение X и Y для проверки на выход за границы или объекты
        float newY = y; // -//-
        float missileX = SPRITE_SCALE/8f; //Для небольшого сдвига по X


        //Выстрел в направлении танка
        if(missile == null){
            switch (heading){
                case NORTH -> missile = new Missile(x + missileX,y,scale,speed*2, atlas, 0, true);
                case EAST -> missile = new Missile(x + missileX,y,scale,speed*2, atlas, 1,true);
                case SOUTH -> missile = new Missile(x + missileX,y,scale,speed*2, atlas, 2,true);
                case WEST -> missile = new Missile(x + missileX,y,scale,speed*2, atlas, 3,true);
            }
        }

        //Поворот при условии, что танк проехал указанное количество пикселей
        turner += speed;
        if(turner >= getSpriteHeight()*8){
            changeDirection();
            turner = 0;
        }


        //Перемещение по координатам с заданной скоростью в соответствии с направлением танка
        switch (heading){
            case NORTH -> newY-=speed;
            case EAST -> newX+=speed;
            case SOUTH -> newY+=speed;
            case WEST -> newX-=speed;
        }

        //Проверка на выход за границы рабочего поля по oX
        if(newX < 0){
            newX = 0;
            changeDirection();
        } else if (newX >= Game.WIDTH - SPRITE_SCALE*scale) {
            newX = Game.WIDTH - SPRITE_SCALE*scale;
            changeDirection();
        }
        //Проверка на выход за границы рабочего поля по oY
        if(newY < 0){
            newY = 0;
            changeDirection();
        } else if (newY >= Game.HEIGTH - SPRITE_SCALE*scale) {
            newY = Game.HEIGTH - SPRITE_SCALE*scale;
            changeDirection();
        }

        //ПРОВЕРКА НА ЗАСТУП ЗА ТЕКСТУРЫ НЕПРОНИЦАЕМЫХ ОБЪЕКТОВ
        switch (heading){
            case NORTH -> {
                if(!lvl.isPenetrable(newX - 2,newY - 7) //Препятствие сверху
                        ||(!lvl.isPenetrable(newX + getSpriteWidth()/2f - 18/2f,newY - 7)) //Препятствие сверху (середина)
                        ||(!lvl.isPenetrable(newX + getSpriteWidth() - 18,newY - 7)) //Препятствие сверху (правее)
                ){
                    newY += speed;
                    changeDirection();
                }
            }
            case EAST -> {
                if(!lvl.isPenetrable(newX + getSpriteWidth() - 18, newY - 3) //Препятствие справа
                        ||(!lvl.isPenetrable(newX + getSpriteWidth() - 18, newY + getSpriteHeight()/2f - 18/2f)) //Препятствие справа (середина)
                        ||(!lvl.isPenetrable(newX + getSpriteWidth() - 18, newY + getSpriteHeight() - 18)) //Препятствие справа (ниже)
                ){
                    newX -= speed;
                    changeDirection();
                }
            }
            case SOUTH -> {
                if(!lvl.isPenetrable(newX - 2, newY + getSpriteHeight() - 17) //Препятствие снизу
                        ||(!lvl.isPenetrable(newX + getSpriteWidth()/2f - 18/2f,newY + getSpriteHeight() - 17)) //Препятствие снизу (середина)
                        ||(!lvl.isPenetrable(newX + getSpriteWidth() - 18,newY + getSpriteHeight() - 17)) //Препятствие снизу (правее)
                ){
                    newY -= speed;
                    changeDirection();
                }
            }
            case WEST -> {
                if(!lvl.isPenetrable(newX - 7, newY - 3) //Препятствие слева
                        ||(!lvl.isPenetrable(newX - 7, newY + getSpriteHeight()/2f - 18/2f)) //Препятствие слева (середина)
                        ||(!lvl.isPenetrable(newX - 7, newY + getSpriteHeight() - 18)) //Препятствие слева (ниже)
                ){
                    newX += speed;
                    changeDirection();
                }
            }
        }

        //Теперь можно приравнивать к постоянным x и y
        x = newX;
        y = newY;

        //Если снаряд существует, то запускаем в нем пересчеты (update)
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

    public void changeDirection(){

        switch (heading){

            case NORTH -> {
                rand = 1 + (int)(Math.random()*3);
                switch (rand) {
                    case 1 -> heading = Heading.EAST;
                    case 2 -> heading = Heading.SOUTH;
                    case 3 -> heading = Heading.WEST;
                }
            }

            case EAST -> {
                rand = 1 + (int)(Math.random()*3);
                switch (rand) {
                    case 1 -> heading = Heading.SOUTH;
                    case 2 -> heading = Heading.WEST;
                    case 3 -> heading = Heading.NORTH;
                }
            }

            case SOUTH -> {
                rand = 1 + (int)(Math.random()*3);
                switch (rand) {
                    case 1 -> heading = Heading.WEST;
                    case 2 -> heading = Heading.NORTH;
                    case 3 -> heading = Heading.EAST;
                }
            }

            case WEST -> {
                rand = 1 + (int)(Math.random()*3);
                switch (rand) {
                    case 1 -> heading = Heading.NORTH;
                    case 2 -> heading = Heading.EAST;
                    case 3 -> heading = Heading.SOUTH;
                }
            }

        }

    }

    public Missile getMissile(){
        if ((missile != null)&&(missile.exists())) {
            return missile;
        }
        else{
            return null;
        }
    }

    //Методы для получения размеров спрайта addplr
    public int getSpriteWidth(){
        return spriteMap.get(heading).getWidth();
    }

    public int getSpriteHeight(){
        return spriteMap.get(heading).getHeight();
    }


}

