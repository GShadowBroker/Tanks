package com.gledyson.tanks.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class Shot extends BaseEntity {
    private final TextureRegion shotTexture;

    private final float speed;
    private final float rate;
    private final float angle;
    private final int damage = 1000;

    public Shot(float centerX, float centerY,
                float width, float height,
                TextureRegion shotTexture,
                float speed, float rate, float angle) {

        super(
                new Rectangle(centerX - (width / 2), centerY - (height / 2), width, height),
                angle * MathUtils.degreesToRadians
        );

        this.shotTexture = shotTexture;
        this.speed = speed;
        this.rate = rate;
        this.angle = angle;
    }

    public Rectangle getBoundingBox() {
        return boundingBox;
    }

    public void draw(SpriteBatch batch) {
        batch.draw(
                shotTexture,
                getPositionX(), getPositionY(),
                getWidth() / 2, getHeight() / 2,
                getWidth(), getHeight(),
                1, 1,
                angle
        );
    }

    public void updatePosition(float x, float y) {
        setPositionX(x);
        setPositionY(y);
    }

    public boolean intersects(Rectangle hitBox) {
        return boundingBox.overlaps(hitBox);
    }

    public float getSpeed() {
        return speed;
    }

    public float getRate() {
        return rate;
    }

    public float getAngle() {
        return angle;
    }

    public TextureRegion getShotTexture() {
        return shotTexture;
    }

    public int getDamage() {
        return damage;
    }
}
