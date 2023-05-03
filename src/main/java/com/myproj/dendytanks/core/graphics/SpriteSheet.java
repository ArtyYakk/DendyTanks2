package com.myproj.dendytanks.core.graphics;

import java.awt.image.BufferedImage;

public class SpriteSheet {
    private BufferedImage   sheet; // Несколько спрайтов в ряд, либо же один спрайт
    private int             spriteCount;
    private int             scale;
    private int             spritesInWidth;

    public SpriteSheet(BufferedImage sheet, int spriteCount, int scale){
        this.sheet = sheet;
        this.spriteCount = spriteCount;
        this.scale = scale;

        spritesInWidth = sheet.getWidth() / scale;
    }

    public BufferedImage getSprite(int index){
        index = index % spriteCount;
        int x = index % spritesInWidth * scale;
        int y = index / spritesInWidth * scale;
        return sheet.getSubimage(x,y, sheet.getWidth()/spriteCount, sheet.getHeight()); // Высота спрайта всегда равна ширине одного спрайта
    }

}
