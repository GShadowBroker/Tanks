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
                    getPositionX(), getPositionY(),
                    getWidth() / 2, getHeight() / 2,
                    getWidth(), getHeight(),
                    1, 1,
                    getOrientation() * MathUtils.radiansToDegrees
            );
        } else {
            batch.draw(
                    tankTexture,
                    getPositionX(), getPositionY(),
                    getWidth() / 2, getHeight() / 2,
                    getWidth(), getHeight(),
                    1, 1,
                    getOrientation() * MathUtils.radiansToDegrees
            );
        }
    }

    public void fire(Tank tank, Sound shotSound) {

        float xOffset = MathUtils.sin(-tank.getOrientation() * MathUtils.radiansToDegrees) * (tank.getWidth() / 2);
        float yOffset = MathUtils.cos(-tank.getOrientation() * MathUtils.radiansToDegrees) * (tank.getHeight() / 2);

        Shot newShot = new Shot(
                boundingBox.x + (boundingBox.width / 2) - xOffset,
                boundingBox.y + (boundingBox.width / 2) + yOffset,
                shotWidth, shotHeight,
                shotTexture,
                shotSpeed,
                shotRate,
                getOrientation() * MathUtils.radiansToDegrees + 180
        );

        shots.add(newShot);

        shotSound.play();

        elapsedTimeSinceLastShot = 0f;
    }

    public void leaveTracks(boolean inReverse) {

        for (TrackPrint trackprint : tracks) {
            if (boundingBox.overlaps(trackprint.boundingBox)) {
                return;
            }
        }

        float interval;
        if (inReverse) {
            interval = TRAVEL_LENGTH_CONSTANT / getReverseSpeed();
        } else {
            interval = TRAVEL_LENGTH_CONSTANT / getSpeed();
        }

        if (timeSinceLastTrackAdded < interval) return;
        tracks.add(new TrackPrint(
                tracksTexture,
                boundingBox.x, boundingBox.y,
                getOrientation() * MathUtils.radiansToDegrees
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

    public float getSpeed() {
        return maxLinearSpeed;
    }

    public float getRotationSpeed() {
        return maxAngularSpeed;
    }

    public float getReverseSpeed() {
        return maxLinearSpeed / 2;
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

    protected float getElapsedTimeSinceLastShot() {
        return elapsedTimeSinceLastShot;
    }

    public void setElapsedTimeSinceLastShot(float value) {
        this.elapsedTimeSinceLastShot = value;
    }

    protected float getTimeSinceLastTrackAdded() {
        return timeSinceLastTrackAdded;
    }

    protected void setTimeSinceLastTrackAdded(float timeSinceLastTrackAdded) {
        this.timeSinceLastTrackAdded = timeSinceLastTrackAdded;
    }

    public boolean isDead() {
        return dead;
    }

    public Array<TrackPrint> getTracks() {
        return tracks;
    }

    public TextureRegion getTankTexture() {
        return tankTexture;
    }

    public TextureRegion getTankDestroyedTexture() {
        return tankDestroyedTexture;
    }
}
