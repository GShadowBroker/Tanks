package com.gledyson.tanks.objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.gledyson.tanks.TanksGame;

public class TestBackground {
    private TextureRegion backgroundTexture;
    private int size;

    public TestBackground(TextureRegion backgroundTexture, int size) {
        this.backgroundTexture = backgroundTexture;
        this.size = size;
    }

    public void draw(TanksGame game) {
        for (int x = 0; x < game.WIDTH; x += size) {
            for (int y = 0; y < game.HEIGHT; y += size) {
                game.batch.draw(backgroundTexture, x, y);
            }
        }
    }
}
