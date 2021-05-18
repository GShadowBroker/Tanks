package com.gledyson.tanks.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.gledyson.tanks.TanksGame;

public class DesktopLauncher {
    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Tanks");
        config.setWindowedMode(480, 800);

        new Lwjgl3Application(new TanksGame(), config);
    }
}
