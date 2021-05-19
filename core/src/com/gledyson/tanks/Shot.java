package com.gledyson.tanks;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Shot {
    private Rectangle boundingBox;
    private TextureRegion shotTexture;

    private float speed;
    private float rate;
    private float angle;
    private final int damage = 1000;

    public Shot(float centerX, float centerY,
                float width, float height,
                TextureRegion shotTexture,
                float speed, float rate, float angle) {
        this.shotTexture = shotTexture;
        boundingBox = new Rectangle(centerX - (width / 2), centerY - (height / 2), width, height);
        this.speed = speed;
        this.rate = rate;
        this.angle = angle;
    }

    public Rectangle getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(Rectangle boundingBox) {
        this.boundingBox = boundingBox;
    }

    public void draw(SpriteBatch batch) {
        batch.draw(
                shotTexture,
                boundingBox.x, boundingBox.y,
                boundingBox.width / 2, boundingBox.height / 2,
                boundingBox.width, boundingBox.height,
                1, 1,
                angle
        );
    }

    public void updatePosition(float x, float y) {
        boundingBox.setX(x);
        boundingBox.setY(y);
    }

    public boolean intersects(Rectangle hitBox) {
        return boundingBox.overlaps(hitBox);
    }

    public float getPositionX() {
        return boundingBox.x;
    }

    public void setPositionX(float positionX) {
        boundingBox.x = positionX;
    }

    public float getPositionY() {
        return boundingBox.y;
    }

    public void setPositionY(float positionY) {
        boundingBox.y = positionY;
    }

    public float getWidth() {
        return boundingBox.width;
    }

    public void setWidth(float width) {
        boundingBox.width = width;
    }

    public float getHeight() {
        return boundingBox.height;
    }

    public void setHeight(float height) {
        boundingBox.height = height;
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

    public TextureRegion getShotTexture() {
        return shotTexture;
    }

    public void setShotTexture(TextureRegion shotTexture) {
        this.shotTexture = shotTexture;
    }

    public int getDamage() {
        return damage;
    }
}
