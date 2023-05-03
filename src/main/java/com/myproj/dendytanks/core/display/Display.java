package com.myproj.dendytanks.core.display;

import com.myproj.dendytanks.Application;
import com.myproj.dendytanks.MenuController;
import com.myproj.dendytanks.core.IO.Input;
import javafx.fxml.FXMLLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;

public  class Display {
        private static boolean       created = false;
        private static JFrame        window;
        private static Canvas        content;
        private static BufferedImage buffer;   //Изображение
        private static int[]         bufferData;   //Информация об изображении
        private static Graphics      bufferGraphics;
        private static int           clearColor; //Для очистки Изображения

    private static BufferStrategy bufferStrategy; //Дополнительные буферы закулисами

    public Display(){

    }


        public void create(int width, int heigth, String title, int _clearColor,int numBuffers){

            if(created){
                return;
            }

            window = new JFrame("title");

            window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            content = new Canvas();



            Dimension size  = new Dimension(width,heigth);
            content.setPreferredSize(size);

            window.setResizable(false);
            window.getContentPane().add(content);
            window.pack();
            window.setLocationRelativeTo(null);
            window.setVisible(true);

            buffer = new BufferedImage(width, heigth, BufferedImage.TYPE_INT_ARGB);
            bufferData = ((DataBufferInt)buffer.getRaster().getDataBuffer()).getData();
            bufferGraphics = buffer.getGraphics();
            ((Graphics2D)bufferGraphics).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            clearColor = _clearColor;

            content.createBufferStrategy(numBuffers) ;
            bufferStrategy = content.getBufferStrategy();

            created = true;

        }

        public  void clear(){
            Arrays.fill(bufferData, clearColor);
        }


        public  void swapBuffers(){
                Graphics g = bufferStrategy.getDrawGraphics();
                g.drawImage(buffer,0,0, null);
                bufferStrategy.show();
        }

        public Graphics2D getGraphics(){
            return (Graphics2D) bufferGraphics;
        }

        public  void destroy(){
            if(!created) return;

            created = false;
            window.dispose();
        }



        public  void setTitle(String title){
            window.setTitle(title);
        }
        public  void addInputListener(Input inputListener){
            window.add(inputListener);
        }

        public JFrame getWindow(){
            return  window;
        }
        public Canvas getContentArea(){
        return  content;
    }




}