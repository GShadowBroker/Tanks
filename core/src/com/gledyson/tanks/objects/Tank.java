package com.gledyson.tanks.objects;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.gledyson.tanks.effects.TrackPrint;

public abstract class Tank extends SteerableObject {
    // Position and size
    private final float shotWidth;
    private final float shotHeight;
    private float orientationDeg;

    // Texture
    private final TextureRegion tankTexture;
    private final TextureRegion tankDestroyedTexture;
    private final TextureRegion shotTexture;
    private final TextureRegion tracksTexture;

    // Tracks
    private final Array<TrackPrint> tracks;
    private float timeSinceLastTrackAdded = 0f;

    // Shots
    private final Array<Shot> shots;
    private final float shotSpeed;
    private final float shotRate;
    private float elapsedTimeSinceLastShot;

    // Characteristics
    private final float speed = 64;
    private final float rotationSpeed = 128;
    private final float reverseSpeed = this.speed / 2;
    private static final float TRAVEL_LENGTH_CONSTANT = 64;

    private final int armor = 500;
    private int health = 800;
    private boolean dead = false;

    public Tank(
            float centerX, float centerY,
            float width, float height,
            float angle,
            TextureRegion tankTexture,
            TextureRegion tankDestroyedTexture,
            TextureRegion shotTexture,
            TextureRegion tracksTexture,
            float shotWidth, float shotHeight,
            float shotSpeed, float shotRate
    ) {
        super(
                new Rectangle(centerX - (width / 2.0f), centerY - (height / 2.0f), width, height),
                angle * MathUtils.degreesToRadians
        );

        this.tankTexture = tankTexture;
        this.tankDestroyedTexture = tankDestroyedTexture;
        this.tracksTexture = tracksTexture;
        this.orientationDeg = angle;

        // tracks
        this.tracks = new Array<>();

        // shots
        this.shots = new Array<>();
        this.shotTexture = shotTexture;
        this.shotWidth = shotWidth;
        this.shotHeight = shotHeight;
        this.shotSpeed = shotSpeed;
        this.shotRate = shotRate;
    }

    public void update(float deltaTime) {
        timeSinceLastTrackAdded += deltaTime;
        elapsedTimeSinceLastShot += deltaTime;
    }

    public void updatePosition(float x, float y) {
        setPositionX(x);
        setPositionY(y);
    }

    public boolean canFire() {
        return (elapsedTimeSinceLastShot - shotRate >= 0);
    }

    public void draw(SpriteBatch batch) {
        if (dead) {
            batch.draw(
                    tankDestroyedTexture,
                    boundingBox.x, boundingBox.y,
                    boundingBox.width / 2, boundingBox.height / 2,
                    boundingBox.width, boundingBox.height,
                    1, 1,
                    orientationDeg
            );
        } else {
            batch.draw(
                    tankTexture,
                    boundingBox.x, boundingBox.y,
                    boundingBox.width / 2, boundingBox.height / 2,
                    boundingBox.width, boundingBox.height,
                    1, 1,
                    orientationDeg
            );
        }
    }

    public void fire(Tank tank, Sound shotSound) {

        float xOffset = MathUtils.sin(-tank.getOrientation()) * (tank.getWidth() / 2);
        float yOffset = MathUtils.cos(-tank.getOrientation()) * (tank.getHeight() / 2);

        Shot newShot = new Shot(
                boundingBox.x + (boundingBox.width / 2) - xOffset,
                boundingBox.y + (boundingBox.width / 2) + yOffset,
                shotWidth, shotHeight,
                shotTexture,
                shotSpeed,
                shotRate,
                orientationDeg + 180
        );

        shots.add(newShot);

        shotSound.play();

        elapsedTimeSinceLastShot = 0f;
    }

    public void leaveTracks(boolean inReverse) {
        float interval;
        if (inReverse) {
            interval = TRAVEL_LENGTH_CONSTANT / reverseSpeed;
        } else {
            interval = TRAVEL_LENGTH_CONSTANT / speed;
        }

        if (timeSinceLastTrackAdded < interval) return;
        tracks.add(new TrackPrint(
                tracksTexture,
                boundingBox.x, boundingBox.y,
                orientationDeg
        ));
        timeSinceLastTrackAdded = 0f;
    }

    public boolean takeDamageAndCheckDestroyed(int damage) {
        int totalDamage = damage - this.armor;

        if (totalDamage < 0) totalDamage = 0;

        health -= totalDamage;

        if (health <= 0) {
            this.dead = true;
            return true;
        }

        return false;
    }

    public void dispose() {

    }

    public float getShotWidth() {
        return shotWidth;
    }

    public float getShotHeight() {
        return shotHeight;
    }

    public Array<Shot> getShots() {
        return shots;
    }

    public float getOrientation() {
        return orientationDeg;
    }

    public void setOrientation(float orientation) {
        this.orientationDeg = orientation;
    }

    public float getSpeed() {
        return speed;
    }

    public float getRotationSpeed() {
        return rotationSpeed;
    }

    public float getReverseSpeed() {
        return reverseSpeed;
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

    public void setElapsedTimeSinceLastShot(float elapsedTimeSinceLastShot) {
        this.elapsedTimeSinceLastShot = elapsedTimeSinceLastShot;
    }

    public boolean isDead() {
        return dead;
    }

    public Array<TrackPrint> getTracks() {
        return tracks;
    }

}
