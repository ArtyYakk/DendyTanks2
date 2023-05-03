package com.myproj.dendytanks.core.utils;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static BufferedImage resize(BufferedImage image, int width, int height){
        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        newImage.getGraphics().drawImage(image, 0, 0,  width, height, null);
        return newImage;
    }

    public static Integer[][] levelParser(String filePath){
        Integer[][] result = null;

        try(BufferedReader reader = new BufferedReader(new FileReader(new File(filePath)))){

            String line = null;
            List<Integer[]> lvLines = new ArrayList<Integer[]>();
            while((line = reader.readLine()) != null){
               lvLines.add(str2int_arrays(line.split(" ")));
            }
            result = new Integer[lvLines.size()][lvLines.get(0).length];
            for(int i=0; i<lvLines.size(); i++){
                result[i]  = lvLines.get(i);
            }
        } catch (IOException e){
            e.printStackTrace();
        }

        return result;
    }

    public static void writeToFile(String filePath, Integer[][] tileMap){
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filePath)))){
           for(int i=0; i< tileMap.length; i++){
               writer.write(String.join(" ", int2string_arrays(tileMap[i])));
               writer.newLine();
           }
        } catch (IOException e){
            e.printStackTrace();
        }


    }

    public static void copyFile(String pathFrom, String pathTo){
        Integer[][] mapFrom = levelParser(pathFrom);
        writeToFile(pathTo, mapFrom);
    }


    public static Integer[] str2int_arrays(String[] sArr){
        Integer[] result = new Integer[sArr.length];

        for(int i=0; i<sArr.length; i++){
            result[i] = Integer.parseInt(sArr[i]);
        }
        return result;
    }

    public static String[] int2string_arrays(Integer[] iArr){
        String[] result = new String[iArr.length];

        for(int i=0; i<iArr.length; i++){
            result[i] = Integer.toString(iArr[i]);
        }
        return result;
    }

    public static void removeBackground(BufferedImage image, int distFromBlack){
        for(int i=0; i<image.getWidth(); i++){
            for (int j=0; j<image.getHeight(); j++){
                if((image.getRGB(i,j) >= -16777216) && (image.getRGB(i,j) <= -16777216 + distFromBlack)){
                    image.setRGB(i,j, 0);
                }
            }
        }
    }
//3_000_000 чаще всего подходит, если не слишком много деталей близкого к черному цвета
}
