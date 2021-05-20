package com.gledyson.tanks;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Explosion {
    private final Animation<TextureRegion> animation;

    // Position
    private final float explosionX;
    private final float explosionY;

    // Timing
    private float stateTime;

    public Explosion(TextureRegion[] explosionFrames, float frameInterval, float posX, float posY) {
        explosionX = posX;
        explosionY = posY;
        stateTime = 0f;

        animation = new Animation<>(frameInterval, explosionFrames);
    }

    public void update(float delta) {
        stateTime += delta;
    }

    public void draw(SpriteBatch batch) {
        batch.draw(animation.getKeyFrame(stateTime, false), explosionX, explosionY);
    }

    public boolean isFinished() {
        return animation.isAnimationFinished(stateTime);
    }
}
