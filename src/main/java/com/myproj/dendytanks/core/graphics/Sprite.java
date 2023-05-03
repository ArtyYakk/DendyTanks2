package com.myproj.dendytanks.core.graphics;

import com.myproj.dendytanks.core.utils.Utils;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Sprite {
    private SpriteSheet   sheet;
    private float         scale;
    private int           numOfSprite;
    private int           width;
    private int           height;

    private BufferedImage image;
    public Sprite(SpriteSheet sheet, float scale, int distFromBlack, int indexOfSprite){
        this.scale = scale;
        this.sheet = sheet;
        this.numOfSprite = indexOfSprite;
        image = sheet.getSprite(indexOfSprite); // Индекс спрайта из листа, в котором их несколько
        image = Utils.resize(image, (int)(image.getWidth()*scale), (int)(image.getHeight()*scale));
        Utils.removeBackground(image, distFromBlack);

        width = image.getWidth();
        height = image.getHeight();
    }

    public void render(Graphics2D g, float x, float y){

        g.drawImage(image, Math.round(x), Math.round(y), null);


    }

    public int getWidth(){
        return width;
    }
    public int getHeight(){
        return height;
    }
}
