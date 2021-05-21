package com.gledyson.tanks.effects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TrackPrint {
    private final TextureRegion tracksTexture;

    // position and size
    private final float posX, posY, angle;
    private static final float width = 37;
    private static final float height = 52;

    // timers
    private float lifetime;
    private final float maxDuration = 5f;

    public TrackPrint(TextureRegion texture, float posX, float posY, float angle) {
        this.tracksTexture = texture;
        this.posX = posX;
        this.posY = posY;
        this.angle = angle;
        this.lifetime = 0f;
    }

    public void update(float delta) {
        this.lifetime += delta;
    }

    public void draw(SpriteBatch batch) {
        batch.draw(tracksTexture,
                posX, posY,
                width / 2, height / 2,
                width, height,
                1, 1,
                angle
        );
    }

    public boolean shouldFade() {
        return lifetime > maxDuration;
    }
}
