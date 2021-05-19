package com.gledyson.tanks;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

public class PlayerTank extends Tank {

    public PlayerTank(float centerX, float centerY, float width, float height, float angle, TextureRegion tankTexture, TextureRegion tankDestroyedTexture, TextureRegion shotTexture, float shotWidth, float shotHeight, float shotSpeed, float shotRate) {
        super(centerX, centerY, width, height, angle, tankTexture, tankDestroyedTexture, shotTexture, shotWidth, shotHeight, shotSpeed, shotRate);
    }

    @Override
    public void fire(Tank tank, Sound shotSound) {
        float xOffset = MathUtils.sinDeg(-tank.getTankAngle()) * (tank.getWidth() / 2);
        float yOffset = MathUtils.cosDeg(-tank.getTankAngle()) * (tank.getHeight() / 2);

        Shot newShot = new Shot(
                tank.getPositionX() + (tank.getWidth() / 2) - xOffset,
                tank.getPositionY() + (tank.getHeight() / 2) - yOffset,
                tank.getShotWidth(), tank.getShotHeight(),
                tank.getShotTexture(),
                tank.getShotSpeed(),
                tank.getShotRate(),
                tank.getTankAngle() + 180
        );

        tank.getShots().add(newShot);

        shotSound.play();

        tank.setElapsedTimeSinceLastShot(0f);
    }
}
