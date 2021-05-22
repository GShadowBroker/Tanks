package com.gledyson.tanks.objects;

import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.behaviors.Arrive;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class EnemyTank extends Tank implements IAutomaton {
    protected final Tank targetTank;
//    protected SteeringBehavior<Vector2>[] behaviorsArray;

    public EnemyTank(float centerX, float centerY, float width, float height, float angle, TextureRegion tankTexture, TextureRegion tankDestroyedTexture, TextureRegion shotTexture, TextureRegion tracksTexture, float shotWidth, float shotHeight, float shotSpeed, float shotRate, Tank targetTank) {
        super(centerX, centerY, width, height, angle, tankTexture, tankDestroyedTexture, shotTexture, tracksTexture, shotWidth, shotHeight, shotSpeed, shotRate);

        this.targetTank = targetTank;

//        Arrive<Vector2> arriveBehavior = new Arrive<>(this, targetTank);
//        Pursue<Vector2> pursueBehavior = new Pursue<>(this, targetTank);
//        Evade<Vector2> evadeBehavior = new Evade<>(this, targetTank);
//        Face<Vector2> faceBehavior = new Face<>(this, targetTank);
//        Wander<Vector2> wanderBehavior = new Wander<>(this);
//        wanderBehavior.setWanderRadius(10f);
//
//        behaviorsArray = new SteeringBehavior[]{
//                new Arrive<>(this, targetTank),
//                new Pursue<>(this, targetTank),
//                new Evade<>(this, targetTank),
//                new Face<>(this, targetTank),
//        };

        steeringBehavior = new Arrive<>(this, targetTank);
    }

    @Override
    public void fire(Tank tank, Sound shotSound) {
        float xOffset = MathUtils.sinDeg(-tank.getOrientation() * MathUtils.radiansToDegrees) * (tank.getWidth() / 2);
        float yOffset = MathUtils.cosDeg(-tank.getOrientation() * MathUtils.radiansToDegrees) * (tank.getHeight() / 2);

        Shot newShot = new Shot(
                tank.getPositionX() + (tank.getWidth() / 2) - xOffset,
                tank.getPositionY() + (tank.getHeight() / 2) - yOffset,
                tank.getShotWidth(), tank.getShotHeight(),
                tank.getShotTexture(),
                tank.getShotSpeed(),
                tank.getShotRate(),
                tank.getOrientation() * MathUtils.radiansToDegrees + 180
        );

        tank.getShots().add(newShot);

        shotSound.play();

        tank.setElapsedTimeSinceLastShot(0f);
    }

    @Override
    public void update(float deltaTime) {
        if (isDead()) return;

        setTimeSinceLastTrackAdded(getTimeSinceLastTrackAdded() + deltaTime);
        setElapsedTimeSinceLastShot(getElapsedTimeSinceLastShot() + deltaTime);

        if (steeringBehavior == null) return;

        steeringBehavior.calculateSteering(steeringOutput);

        /*
         * Here you might want to add a motor control layer filtering steering accelerations.
         *
         * For instance, a car in a driving game has physical constraints on its movement:
         * - it cannot turn while stationary
         * - the faster it moves, the slower it can turn (without going into a skid)
         * - it can brake much more quickly than it can accelerate
         * - it only moves in the direction it is facing (ignoring power slides)
         */

        // Apply steering acceleration to move this agent
        applySteering(steeringOutput, deltaTime);
    }

    @Override
    public void applySteering(SteeringAcceleration<Vector2> steeringOutput, float deltaTime) {
        // Update position and linear velocity. Velocity is trimmed to maximum speed

        // add position + linearVelocity
        this.position.mulAdd(linearVelocity, deltaTime);
        // add linearVelocity + steeringOutput and limit it to max speed
        this.linearVelocity.mulAdd(steeringOutput.linear, deltaTime).limit(this.getMaxLinearSpeed());

        // Update orientation and angular velocity
        if (independentFacing) {
            this.orientation += angularVelocity * deltaTime;
            this.angularVelocity += steeringOutput.angular * deltaTime;

        } else {
            // For non-independent facing we have to align orientation to linear velocity
            float newOrientation = calculateOrientationFromLinearVelocity(this);
            if (newOrientation != orientation) {
                this.angularVelocity = (newOrientation - this.orientation) * deltaTime;
                this.orientation = newOrientation;
            }
        }

        // Tank class:

        // setOrientation => the orientation of the tank object, not base entity
        setOrientation((orientation * MathUtils.radiansToDegrees) + 180f);

        // Moves the collision box along
        boundingBox.x = getPositionX();
        boundingBox.y = getPositionY();
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (isDead()) {
            batch.draw(
                    getTankDestroyedTexture(),
                    position.x, position.y,
                    getWidth() / 2, getHeight() / 2,
                    getWidth(), getHeight(),
                    1, 1,
                    getOrientation()
            );
        } else {
            batch.draw(
                    getTankTexture(),
                    position.x, position.y,
                    getWidth() / 2, getHeight() / 2,
                    getWidth(), getHeight(),
                    1, 1,
                    getOrientation()
            );
        }
    }
}
