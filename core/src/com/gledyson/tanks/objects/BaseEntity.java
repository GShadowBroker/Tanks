package com.gledyson.tanks.objects;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class BaseEntity {
    protected final Rectangle boundingBox;
    protected final Vector2 position;
    protected float orientation; // radians

    public BaseEntity(Rectangle boundingBox, float orientation) {
        this.boundingBox = boundingBox;
        this.orientation = orientation;
        this.position = new Vector2(boundingBox.x, boundingBox.y);
    }

    public Rectangle getBoundingBox() {
        return boundingBox;
    }

    public float getOrientation() {
        return orientation;
    }

    public void setOrientation(float orientation) {
        this.orientation = orientation;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPositionX(float posX) {
        boundingBox.x = posX;
        position.x = posX;
    }

    public void setPositionY(float posY) {
        boundingBox.y = posY;
        position.y = posY;
    }

    public float getPositionX() {
        return position.x;
    }

    public float getPositionY() {
        return position.y;
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
}
