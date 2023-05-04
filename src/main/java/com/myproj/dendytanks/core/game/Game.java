package com.myproj.dendytanks.core.game;

import com.myproj.dendytanks.core.IO.Input;
import com.myproj.dendytanks.core.display.Display;
import com.myproj.dendytanks.core.entities.Enemy;
import com.myproj.dendytanks.core.entities.Missile;
import com.myproj.dendytanks.core.entities.Player;
import com.myproj.dendytanks.core.game.level.Level;
import com.myproj.dendytanks.core.graphics.TextureAtlas;
import com.myproj.dendytanks.core.utils.Time;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class Game implements Runnable{ // Runnable нужен для запуска Game в своем потоке

    // int width, int heigth, String title, int _clearColor,int numBuffers
    public static final int    WIDTH           = 841; //800;
    public static final int    HEIGTH          = 636; //600;
    public static final String TITLE           = "Tanks";
    public static final int    CLEAR_COLOR     = 0xff000000;
    public static final int    NUM_BUFFERS     = 3;
    public static final float  UPDATE_RATE     = 60.0f;  // Частота расчетов
    public static final float  UPDATE_INTERVAL = Time.SECOND/UPDATE_RATE; // Время между каждым UPDATE
    public static final long   IDLE_TIME       = 1; // Временная остановка потока, чтобы освободить немного ресурсов для других процессов
    public static final String ATLAS_FILE_NAME = "texture_atlas.png";
    private boolean            running;
    private Thread             gameThread;
    private Graphics2D         graphics;
    private Input              input;
    private TextureAtlas       atlas;
    private Player player;
   // private Enemy enemy;
    private List<Enemy> enemyList = new ArrayList<>();
    private Missile pMissile; // Снаряд
    private Level lvl;
    private Display display;

    public Game(){
        running = false;

        atlas = new TextureAtlas(ATLAS_FILE_NAME);
    }
    public synchronized void start(int numOfLevel, boolean isRedactable){  // Оператор synchronized нужен для того, чтобы метод нельзя было выполнить одновременно из двух разных потоков
        if(running) return;


        //Создаем дисплей заново
        display = new Display();
        display.create(WIDTH, HEIGTH, TITLE, CLEAR_COLOR, NUM_BUFFERS);
        graphics = display.getGraphics();
        input = new Input();
        display.addInputListener(input);


        lvl = new Level(atlas, numOfLevel, display, input, isRedactable);

        //Обнуляем врагов, создаем заново, то же самое делаем и с игроком
        enemyList.clear();

        Point playersPosition = lvl.getPLayersPosition();
        ArrayList<Point> enemiesPositions = lvl.getEnemiesPositions();


        player = new Player((float)playersPosition.getX(), (float)playersPosition.getY(),0.5f,3, atlas, lvl);

        for (Point p : enemiesPositions) {
            enemyList.add(new Enemy((float)p.getX(),(float)p.getY(),0.5f,3, atlas,lvl));
        }


        //При закрытии окна игра останавливается
        display.getWindow().addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);

                System.out.println("You just closed the window");
                stop();
            }
        });



        
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }
    public synchronized void stop(){

        if(!running){
            return;
        }

        if(display == null){
            return;
        }

        running = false;

        try {
            gameThread.join();

            System.out.println("STOP");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        display.destroy();
        display = null;

    }
    private void update(){ //Вся физика и математические расчеты
        Missile missile;
        Missile eMissile;

        player.update(input);


        //Здесь связываются координаты снаряда с координатами плиток для их разрушения либо остановки снаряда игрока
        if(player.getMissile() != null){
             missile = player.getMissile();

            if( (lvl.isBreakable(missile.getX(),missile.getY()))&&(!lvl.isPenetrable4Missile(missile.getX(),missile.getY())) ){
                lvl.deleteTile(missile.getX(), missile.getY());
                missile.setBroken();
            } else if(!lvl.isPenetrable4Missile(missile.getX(), missile.getY())){
                missile.setBroken();
            }
        }


        //Проходка по списку энеми
        for(int i=0; i<enemyList.size(); i++){ //НАЧАЛО ЦИКЛА

            //Здесь связываются координаты ВРАЖЕСКОГО снаряда с координатами плиток для остановки снаряда
            if(enemyList.get(i).getMissile() != null){
                 eMissile = enemyList.get(i).getMissile();

                if(!lvl.isPenetrable4Missile(eMissile.getX(), eMissile.getY())){
                    eMissile.setBroken();
                }
            }

            if((enemyList.get(i).getMissile() != null)&&(player.getMissile() != null)){ //Столкнулись ли два снаряда
                missile = player.getMissile();
                eMissile = enemyList.get(i).getMissile();

                if(eMissile.getRectangle().contains(missile.getX(), missile.getY()) ){
                    missile.setBroken();
                    eMissile.setBroken();
                }
            }

            if(enemyList.get(i).getMissile() != null){ //Попал ли враг по моему танку
                eMissile = enemyList.get(i).getMissile();

                if(     ( (eMissile.getX() >= player.x) && (eMissile.getX() <= player.x + player.width) )
                        &&
                        ( (eMissile.getY() >= player.y) && (eMissile.getY() <= player.y + player.height) )
                ){
                    System.out.println("I lose");
                    eMissile.setBroken();

                    display.getWindow().setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                    renderYouLost();
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    display.getWindow().setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    display.destroy(); //Уничтожается окно и выполняется метод stop()
                }
            }

            if (player.getMissile() != null){ //Попал ли я по танку врага

                 missile = player.getMissile();

                if(     ( (missile.getX() >= enemyList.get(i).x) && (missile.getX() <= enemyList.get(i).x + enemyList.get(i).width) )
                        &&
                        ( (missile.getY() >= enemyList.get(i).y) && (missile.getY() <= enemyList.get(i).y + enemyList.get(i).height) )
                ){
                    enemyList.remove(enemyList.get(i));
                    missile.setBroken();
                }

            }

            if((!enemyList.isEmpty())&&(enemyList.size() != i)){
                    enemyList.get(i).update(input);
            }

        } //КОНЕЦ ЦИКЛА

        lvl.update();
    }
    private void render(){ // После того, как все вышеупомянутое расчитано, данная функция рисует следующую сцену
        display.clear();
        lvl.render(graphics);
        player.render(graphics);

        for (Enemy enemy : enemyList) {
            if(enemy != null){
                enemy.render(graphics); // Враг
            }
        }

        lvl.renderGrass(graphics);
        display.swapBuffers();
    }

    private void renderYouLost(){ // После того, как все вышеупомянутое расчитано, данная функция рисует следующую сцену

        display.clear();
        graphics.setFont(new Font(Font.MONOSPACED, Font.BOLD,80));
        graphics.drawString("YOU LOST",WIDTH/2 - 25*8, HEIGTH/2);
        display.swapBuffers();
        display.swapBuffers();

    }
    @Override
    public void run() { // Тут находится бесконечный цикл, который работает, пока не остановим игру

        int fps = 0;
        int upd = 0;
        int updl = 0; // Сколько раз пытались догнать апдейт

        long count = 0;

        float delta = 0;
        
        long lastTime = Time.get();

        while(running){
            long now = Time.get();
            long elapsedTime = now - lastTime;
            lastTime = now;

            count += elapsedTime;

            boolean render = false;
            delta += ( elapsedTime / UPDATE_INTERVAL );
            while (delta > 1){
                update();
                upd++;
                delta--;
                if(render){
                    updl++;
                }else{
                    render = true;
                }
            }
            if(render){
                render();
                fps++;
            }         else{
                try {
                    Thread.sleep(IDLE_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if(count >= Time.SECOND)     {
                display.setTitle(TITLE + " || Fps: " + fps + " | upd: " + upd + " | updl " + updl);
                upd = 0;
                fps = 0;
                updl = 0;
                count = 0; 
            }
        }
    }
}
