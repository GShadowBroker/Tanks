package com.gledyson.tanks;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public abstract class Tank {
    // Position and size
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
            float angle,
            TextureRegion tankTexture,
            TextureRegion shotTexture,
            float shotWidth, float shotHeight,
            float shotSpeed, float shotRate
    ) {
        boundingBox = new Rectangle(centerX - (width / 2.0f), centerY - (height / 2.0f), width, height);
        this.tankTexture = tankTexture;
        this.tankAngle = angle;

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
                boundingBox.x, boundingBox.y,
                boundingBox.width / 2, boundingBox.height / 2,
                boundingBox.width, boundingBox.height,
                1, 1,
                tankAngle
        );
    }

    public void fire(Tank tank, Sound shotSound) {

        float xOffset = MathUtils.sinDeg(-tank.getTankAngle()) * (tank.getWidth() / 2);
        float yOffset = MathUtils.cosDeg(-tank.getTankAngle()) * (tank.getHeight() / 2);

        Shot newShot = new Shot(
                boundingBox.x + (boundingBox.width / 2) - xOffset,
                boundingBox.y + (boundingBox.width / 2) + yOffset,
                shotWidth, shotHeight,
                shotTexture,
                shotSpeed,
                shotRate,
                tankAngle + 180
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

    public float getHeight() {
        return boundingBox.height;
    }

    public void setHeight(float height) {
        boundingBox.height = height;
    }

    public float getWidth() {
        return boundingBox.width;
    }

    public void setWidth(float width) {
        boundingBox.width = width;
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

    public TextureRegion getTankTexture() {
        return tankTexture;
    }

    public TextureRegion getShotTexture() {
        return shotTexture;
    }

    public float getShotSpeed() {
        return shotSpeed;
    }

    public float getShotRate() {
        return shotRate;
    }

    public float getElapsedTimeSinceLastShot() {
        return elapsedTimeSinceLastShot;
    }

    public void setElapsedTimeSinceLastShot(float elapsedTimeSinceLastShot) {
        this.elapsedTimeSinceLastShot = elapsedTimeSinceLastShot;
    }
}
