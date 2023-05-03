package com.myproj.dendytanks.core.game.level;

import com.myproj.dendytanks.core.utils.Utils;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Tile {
    private BufferedImage image;
    private TileType type;

    protected Tile(BufferedImage image, float scale, TileType type){
        this.type = type;
        this.image = Utils.resize(image, Math.round(image.getWidth()*scale), Math.round(image.getHeight()*scale));
        Utils.removeBackground(image, 9_000);
    }
    protected void render(Graphics2D g, float x, float y){
        g.drawImage(image, Math.round(x), Math.round(y), null);

    }
    protected TileType type(){
        return type;
    }

}
