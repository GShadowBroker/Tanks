package com.gledyson.tanks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public abstract class Tank {
    // Position and size
    private float positionX, positionY, height, width;
    private float shotWidth, shotHeight;
    private float tankAngle;

    // BoundingBox
    private final Rectangle boundingBox;

    // Texture
    private final TextureRegion tankTexture;
    private final TextureRegion shotTexture;

    // Shots
    private Array<Shot> shots;
    private final float shotSpeed;
    private final float shotRate;
    private float elapsedTimeSinceLastShot;

    // Characteristics
    private float speed = 64;
    private float rotationSpeed = 150;
    private float reverseSpeed = this.speed / 2;

    public Tank(
            float centerX, float centerY,
            float width, float height,
            TextureRegion tankTexture,
            TextureRegion shotTexture,
            float shotWidth, float shotHeight,
            float shotSpeed, float shotRate
    ) {
        positionX = centerX - (width / 2.0f);
        positionY = centerY - (height / 2.0f);
        this.width = width;
        this.height = height;
        boundingBox = new Rectangle(positionX, positionY, width, height);
        this.tankTexture = tankTexture;
        this.tankAngle = 0f;

        // shots
        this.shots = new Array<>();
        this.shotTexture = shotTexture;
        this.shotWidth = shotWidth;
        this.shotHeight = shotHeight;
        this.shotSpeed = shotSpeed;
        this.shotRate = shotRate;
    }

    public void update(float deltaTime) {
        elapsedTimeSinceLastShot += deltaTime;
    }

    public void updatePosition(float x, float y) {
        setPositionX(x);
        setPositionY(y);
        boundingBox.setX(x);
        boundingBox.setY(y);
    }

    public boolean canFire() {
        return (elapsedTimeSinceLastShot - shotRate >= 0);
    }

    public Rectangle getBoundingBox() {
        return boundingBox;
    }

    public void draw(SpriteBatch batch) {
        batch.draw(
                tankTexture,
                positionX, positionY,
                width / 2, height / 2,
                width, height,
                1, 1,
                tankAngle
        );
    }

    public void fire(Sound shotSound) {

        Shot newShot = new Shot(
                positionX + (width / 2),
                positionY + (width / 2),
                shotWidth, shotHeight,
                shotTexture,
                shotSpeed,
                shotRate,
                tankAngle
        );

        shots.add(newShot);

        shotSound.play();

        elapsedTimeSinceLastShot = 0f;
    }



    public void die() {

    }

    public void dispose() {

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

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getShotWidth() {
        return shotWidth;
    }

    public void setShotWidth(float shotWidth) {
        this.shotWidth = shotWidth;
    }

    public float getShotHeight() {
        return shotHeight;
    }

    public void setShotHeight(float shotHeight) {
        this.shotHeight = shotHeight;
    }

    public Array<Shot> getShots() {
        return shots;
    }

    public void setShots(Array<Shot> shots) {
        this.shots = shots;
    }

    public float getTankAngle() {
        return tankAngle;
    }

    public void setTankAngle(float tankAngle) {
        this.tankAngle = tankAngle;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getRotationSpeed() {
        return rotationSpeed;
    }

    public void setRotationSpeed(float rotationSpeed) {
        this.rotationSpeed = rotationSpeed;
    }

    public float getReverseSpeed() {
        return reverseSpeed;
    }

    public void setReverseSpeed(float reverseSpeed) {
        this.reverseSpeed = reverseSpeed;
    }
}
