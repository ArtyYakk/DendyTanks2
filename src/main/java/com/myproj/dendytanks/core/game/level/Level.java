package com.myproj.dendytanks.core.game.level;

import com.myproj.dendytanks.core.IO.Input;
import com.myproj.dendytanks.core.display.Display;
import com.myproj.dendytanks.core.entities.Enemy;
import com.myproj.dendytanks.core.entities.Player;
import com.myproj.dendytanks.core.game.Entity;
import com.myproj.dendytanks.core.game.Game;
import com.myproj.dendytanks.core.graphics.TextureAtlas;
import com.myproj.dendytanks.core.utils.Utils;

import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Level {

    public static final int TILE_SCALE = 41; //82; //8;
    public static final float TILE_IN_GAME_SCALE = 0.5f;
    public static final float SCALED_TILE_SIZE = TILE_SCALE * TILE_IN_GAME_SCALE; //20.5
    public static final int TILES_IN_WIDTH = Math.round(Game.WIDTH / SCALED_TILE_SIZE);
    public static final int TILES_IN_HEIGTH = Math.round(Game.HEIGTH / SCALED_TILE_SIZE);
    private Integer[][]          tileMap = null; // КАРТА
    private Map<TileType, Tile>  tiles;
    private ArrayList<Point>     grassCords;
    private String levelFile;
    private Display display;
    private Input input;
    private Point playersPosition;
    private ArrayList<Point> enemiesPositions = new ArrayList<>();
    public Level(TextureAtlas atlas, int numOfLevel, Display display, Input input, boolean isRedactable){

        levelFile = "res/level" + numOfLevel + ".lvl";

        String bufferPath = "res/buffer.lvl";
        Utils.copyFile(levelFile, bufferPath);

        if(!isRedactable){ //Если редактирование разрешено, мы меняем оригинальный файл, в обратном же случае - только буферный
            levelFile = bufferPath;
        }

        System.out.println(isRedactable);

        tileMap = new Integer[TILES_IN_WIDTH][TILES_IN_HEIGTH];
        this.display = display;

        tiles = new HashMap<TileType, Tile>();
        tiles.put(TileType.BRICK, new Tile(atlas.cut(32*TILE_SCALE,0*TILE_SCALE, TILE_SCALE, TILE_SCALE), TILE_IN_GAME_SCALE, TileType.BRICK));
        tiles.put(TileType.METAL, new Tile(atlas.cut(32*TILE_SCALE,2*TILE_SCALE, TILE_SCALE, TILE_SCALE), TILE_IN_GAME_SCALE, TileType.METAL));
        tiles.put(TileType.WATER, new Tile(atlas.cut(32*TILE_SCALE,4*TILE_SCALE, TILE_SCALE, TILE_SCALE), TILE_IN_GAME_SCALE, TileType.WATER));
        tiles.put(TileType.GRASS, new Tile(atlas.cut(34*TILE_SCALE,4*TILE_SCALE, TILE_SCALE, TILE_SCALE), TILE_IN_GAME_SCALE, TileType.GRASS));
        tiles.put(TileType.ICE, new Tile(atlas.cut(36*TILE_SCALE,4*TILE_SCALE, TILE_SCALE, TILE_SCALE), TILE_IN_GAME_SCALE, TileType.ICE));
        tiles.put(TileType.PLAYERS_SPAWN, new Tile(atlas.cut(32*TILE_SCALE,18*TILE_SCALE, TILE_SCALE*2, TILE_SCALE*2), TILE_IN_GAME_SCALE*0.5f, TileType.PLAYERS_SPAWN));
        tiles.put(TileType.ENEMIES_SPAWN, new Tile(atlas.cut(34*TILE_SCALE,18*TILE_SCALE, TILE_SCALE*2, TILE_SCALE*2), TILE_IN_GAME_SCALE*0.5f, TileType.ENEMIES_SPAWN));
        tiles.put(TileType.EMPTY, new Tile(atlas.cut(36*TILE_SCALE,6*TILE_SCALE, TILE_SCALE, TILE_SCALE), TILE_IN_GAME_SCALE, TileType.EMPTY));

        tileMap = Utils.levelParser(levelFile);

        grassCords = new ArrayList<Point>();
        for(int i=0; i< tileMap.length; i++){
             for(int j=0; j<tileMap[i].length; j++){

                 Tile tile = tiles.get(TileType.fromNumeric(tileMap[i][j]));

                 if(tile.type() == TileType.GRASS){
                     grassCords.add(new Point(Math.round(j*SCALED_TILE_SIZE),Math.round(i*SCALED_TILE_SIZE)));
                  } //Координаты травы в отдельном массиве

                 if(tile.type() == TileType.PLAYERS_SPAWN){
                     playersPosition = new Point(Math.round(j*SCALED_TILE_SIZE),Math.round(i*SCALED_TILE_SIZE));
                 } //Игрок

                 if(tile.type() == TileType.ENEMIES_SPAWN){
                     enemiesPositions.add(new Point(Math.round(j*SCALED_TILE_SIZE),Math.round(i*SCALED_TILE_SIZE)));
                 } //Враги

             }
        }


        //Работа с мышью для реактирования карты
        display.getContentArea().addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);

                if(input.getKey(KeyEvent.VK_1)){

                    tileMap[(int)Math.floor((e.getY())/SCALED_TILE_SIZE)][(int)Math.floor((e.getX())/SCALED_TILE_SIZE)] = 0; // 1 EMPTY
                    Utils.writeToFile(levelFile, tileMap);
                }
                if(input.getKey(KeyEvent.VK_2)){

                    tileMap[(int)Math.floor((e.getY())/SCALED_TILE_SIZE)][(int)Math.floor((e.getX())/SCALED_TILE_SIZE)] = 1; // 2 BRICK
                    Utils.writeToFile(levelFile, tileMap);
                }
                if(input.getKey(KeyEvent.VK_3)){

                    tileMap[(int)Math.floor((e.getY())/SCALED_TILE_SIZE)][(int)Math.floor((e.getX())/SCALED_TILE_SIZE)] = 2; // 3 METAL
                    Utils.writeToFile(levelFile, tileMap);
                }
                if(input.getKey(KeyEvent.VK_4)){

                    tileMap[(int)Math.floor((e.getY())/SCALED_TILE_SIZE)][(int)Math.floor((e.getX())/SCALED_TILE_SIZE)] = 3; // 4 WATER
                    Utils.writeToFile(levelFile, tileMap);
                }
                if(input.getKey(KeyEvent.VK_5)){

                    tileMap[(int)Math.floor((e.getY())/SCALED_TILE_SIZE)][(int)Math.floor((e.getX())/SCALED_TILE_SIZE)] = 4; // 5 GRASS
                    Utils.writeToFile(levelFile, tileMap);


                }
                if(input.getKey(KeyEvent.VK_6)){

                    tileMap[(int)Math.floor((e.getY())/SCALED_TILE_SIZE)][(int)Math.floor((e.getX())/SCALED_TILE_SIZE)] = 5; // 6 ICE
                    Utils.writeToFile(levelFile, tileMap);
                }
                if(input.getKey(KeyEvent.VK_7)){

                    tileMap[(int)Math.floor((e.getY())/SCALED_TILE_SIZE)][(int)Math.floor((e.getX())/SCALED_TILE_SIZE)] = 6; // 7 PLAYERS_SPAWN
                    Utils.writeToFile(levelFile, tileMap);
                }
                if(input.getKey(KeyEvent.VK_8)){

                    tileMap[(int)Math.floor((e.getY())/SCALED_TILE_SIZE)][(int)Math.floor((e.getX())/SCALED_TILE_SIZE)] = 7; // 8 ENEMIES_SPAWN
                    Utils.writeToFile(levelFile, tileMap);
                }
            }
        });
    }



    public void update(){

        grassCords.clear();
        for(int i=0; i< tileMap.length; i++){
            for(int j=0; j<tileMap[i].length; j++){

                Tile tile = tiles.get(TileType.fromNumeric(tileMap[i][j]));

                if(tile.type() == TileType.GRASS){
                    grassCords.add(new Point(Math.round(j*SCALED_TILE_SIZE),Math.round(i*SCALED_TILE_SIZE)));
                } //Координаты травы в отдельном массиве

            }
        }

    }
    public void render(Graphics2D g){
        for(int i=0; i< tileMap.length; i++){
            for(int j=0; j<tileMap[i].length; j++){

                Tile tile = tiles.get(TileType.fromNumeric(tileMap[i][j]));
                if(tile.type() != TileType.GRASS){
                    tile.render(g,j*SCALED_TILE_SIZE,i*SCALED_TILE_SIZE);
                }

            }
        }
    }

    public void renderGrass(Graphics2D g){
        for(Point p : grassCords){
            tiles.get(TileType.GRASS).render(g, p.x, p.y);
        }
    }

    public Integer[][] getTileMap(){
        return tileMap;
    }
    public float getScaledTileSize(){ //Метод для получения размеров плитки
        return SCALED_TILE_SIZE;
    }
    public Map<TileType, Tile> getTiles(){
        return tiles;
    }

    public void deleteTile(float x, float y){
        tileMap[(int)Math.floor(y/SCALED_TILE_SIZE)][(int)Math.floor(x/SCALED_TILE_SIZE)] = 0;
        Utils.writeToFile(levelFile, tileMap);
    }

    public boolean isBreakable(float x, float y){
        return TileType.fromNumeric(tileMap[(int)Math.floor(y/SCALED_TILE_SIZE)][(int)Math.floor(x/SCALED_TILE_SIZE)]).isBreakable();
    }
    //Два метода для проверки проницаемости текстур, их отличие в способе округления (один для более точного прилета снаряда (Math.floor()), другой
    // для того, чтобы танки не заходили за текстуры)
    public boolean isPenetrable4Missile(float x, float y){
        return TileType.fromNumeric(tileMap[(int)Math.floor(y/SCALED_TILE_SIZE)][(int)Math.floor(x/SCALED_TILE_SIZE)]).isPenetrable4Missile();
    }

    public boolean isPenetrable(float x, float y){
        return TileType.fromNumeric(tileMap[Math.round(y/SCALED_TILE_SIZE)][Math.round(x/SCALED_TILE_SIZE)]).isPenetrable();
    }

    public Point getPLayersPosition(){
        return playersPosition;
    }

    public ArrayList<Point> getEnemiesPositions(){
        return  enemiesPositions;
    }


}
