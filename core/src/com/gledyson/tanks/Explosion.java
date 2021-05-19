package com.gledyson.tanks;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Explosion {
    private final TextureRegion[] frames;
    private final Animation<TextureRegion> animation;

    // Position
    private final float explosionX;
    private final float explosionY;

    // Timing
    private final float intervalTime;
    private float stateTime;

    public Explosion(TextureRegion[] explosionFrames, float frameInterval, float posX, float posY) {
        frames = explosionFrames;
        explosionX = posX;
        explosionY = posY;
        intervalTime = frameInterval;
        stateTime = 0f;

        animation = new Animation<>(intervalTime, frames);
    }

    public void update(float delta) {
        stateTime += delta;
    }

    public void draw(SpriteBatch batch) {
        TextureRegion currentFrame = animation.getKeyFrame(stateTime, false);
        batch.draw(currentFrame, explosionX, explosionY);
    }

    public boolean isFinished() {
        return animation.isAnimationFinished(stateTime);
    }
}
