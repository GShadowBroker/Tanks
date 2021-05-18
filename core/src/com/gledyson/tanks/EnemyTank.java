package com.gledyson.tanks;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class EnemyTank extends Tank {
    public EnemyTank(float centerX, float centerY, float width, float height, float angle, TextureRegion tankTexture, TextureRegion shotTexture, float shotWidth, float shotHeight, float shotSpeed, float shotRate) {
        super(centerX, centerY, width, height, angle, tankTexture, shotTexture, shotWidth, shotHeight, shotSpeed, shotRate);
    }
}
