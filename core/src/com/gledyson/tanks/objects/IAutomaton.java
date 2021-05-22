package com.gledyson.tanks.objects;

import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.math.Vector2;

public interface IAutomaton {
    void update(float delta);

    void applySteering(SteeringAcceleration<Vector2> steeringAcceleration, float deltaTime);
}
