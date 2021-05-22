package com.gledyson.tanks.effects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.gledyson.tanks.objects.BaseEntity;

public class TrackPrint extends BaseEntity {
    private final TextureRegion tracksTexture;

    // position and size
    private static final float width = 37;
    private static final float height = 52;

    // timers
    private float lifetime;
    private final float maxDuration = 4f;

    public TrackPrint(TextureRegion texture, float posX, float posY, float angle) {
        super(
                new Rectangle(posX, posY, width, height),
                angle * MathUtils.degreesToRadians
        );

        this.tracksTexture = texture;
        this.lifetime = 0f;
    }

    public void update(float delta) {
        this.lifetime += delta;
    }

    public void draw(SpriteBatch batch) {
        batch.draw(tracksTexture,
                getPositionX(), getPositionY(),
                getWidth() / 2, getHeight() / 2,
                getWidth(), getHeight(),
                1, 1,
                orientation * MathUtils.radiansToDegrees
        );
    }

    public boolean shouldFade() {
        return lifetime > maxDuration;
    }
}
