package com.gledyson.tanks;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gledyson.tanks.screens.MainGameScreen;

public class TanksGame extends Game {
    // Game-wide settings
    public final int WIDTH = 480;
    public final int HEIGHT = 800;

    // Game-wide objects
    public SpriteBatch batch;

    // load font
    public BitmapFont font;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        setScreen(new MainGameScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        font.dispose();
        batch.dispose();
    }
}
