package com.gledyson.tanks;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public class ShakeEffect {
    private static float time = 0f;
    private static float currentTime = 0f;
    private static float power = 0f;
    private static float currentPower = 0f;
    private static final Vector3 position = new Vector3();

    public static void shakeIt(float shakePower, float shakeTime) {
        time = shakeTime;
        power = shakePower;
        currentTime = 0f;
    }

    public static Vector3 updateAndGetPosition(float delta) {
        if (currentTime <= time) {
            currentPower = power * ((time - currentTime) / time);

            position.x = (MathUtils.random(1.0f) - 0.5f) * 2 * currentPower;
            position.y = (MathUtils.random(1.0f) - 0.5f) * 2 * currentPower;

            currentTime += delta;
        } else {
            time = 0f;
        }
        return position;
    }

    public static float getTimeLeft() {
        return time;
    }

    public static Vector3 getPosition() {
        return position;
    }
}
