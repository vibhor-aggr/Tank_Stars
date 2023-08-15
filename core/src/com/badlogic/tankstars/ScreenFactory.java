package com.badlogic.tankstars;

import com.badlogic.gdx.Screen;

public class ScreenFactory {
    TankStars ts=TankStars.getInstance();
    public Screen createScreen(String need){
        if(need.equals("Load")){
            return new LoadingScreen(ts);
        }
        else if(need.equals("MainMenu")){
            return new MainMenuScreen(ts);
        }
        else if(need.equals("TankSelect")){
            return new TankSelectScreen(ts);
        }
        else if(need.equals("Play")){
            return new PlayScreen(ts);
        }
        return null;
    }
}
