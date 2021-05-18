package com.gledyson.tanks;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Shot {
    private float positionX, positionY;
    private float width, height;
    private TextureRegion shotTexture;
    private Rectangle boundingBox;

    private float speed;
    private float rate;
    private float angle;

    public Shot(float centerX, float centerY,
                float width, float height,
                TextureRegion shotTexture,
                float speed, float rate, float angle) {
        this.positionX = centerX - (width / 2);
        this.positionY = centerY - (height / 2);
        this.width = width;
        this.height = height;
        this.shotTexture = shotTexture;
        boundingBox = new Rectangle(positionX, positionY, width, height);
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
                positionX, positionY,
                width / 2, height / 2,
                width, height,
                1, 1,
                angle
        );
    }

    public void updatePosition(float x, float y) {
        setPositionX(x);
        setPositionY(y);
        boundingBox.setX(x);
        boundingBox.setY(y);
    }

    public boolean intersects(Rectangle hitBox) {
        return boundingBox.overlaps(hitBox);
    }

    public float getPositionX() {
        return positionX;
    }

    public void setPositionX(float positionX) {
        this.positionX = positionX;
    }

    public float getPositionY() {
        return positionY;
    }

    public void setPositionY(float positionY) {
        this.positionY = positionY;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }
}
