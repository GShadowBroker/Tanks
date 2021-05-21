package com.gledyson.tanks.objects;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

public class EnemyTank extends Tank {

    public EnemyTank(float centerX, float centerY, float width, float height, float angle, TextureRegion tankTexture, TextureRegion tankDestroyedTexture, TextureRegion shotTexture, TextureRegion tracksTexture, float shotWidth, float shotHeight, float shotSpeed, float shotRate) {
        super(centerX, centerY, width, height, angle, tankTexture, tankDestroyedTexture, shotTexture, tracksTexture, shotWidth, shotHeight, shotSpeed, shotRate);
    }

    @Override
    public void fire(Tank tank, Sound shotSound) {
        float xOffset = MathUtils.sinDeg(-tank.getOrientation()) * (tank.getWidth() / 2);
        float yOffset = MathUtils.cosDeg(-tank.getOrientation()) * (tank.getHeight() / 2);

        Shot newShot = new Shot(
                tank.getPositionX() + (tank.getWidth() / 2) - xOffset,
                tank.getPositionY() + (tank.getHeight() / 2) - yOffset,
                tank.getShotWidth(), tank.getShotHeight(),
                tank.getShotTexture(),
                tank.getShotSpeed(),
                tank.getShotRate(),
                tank.getOrientation() + 180
        );

        tank.getShots().add(newShot);

        shotSound.play();

        tank.setElapsedTimeSinceLastShot(0f);
    }
}
