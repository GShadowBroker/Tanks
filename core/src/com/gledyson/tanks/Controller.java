package com.gledyson.tanks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Controller {
    private OrthographicCamera camera;
    private Viewport viewport;
    private Stage stage;

    private TextureRegion upBtn, downBtn, rightBtn, leftBtn;

    // Commands
    private boolean upPressed, downPressed, rightPressed, leftPressed, firePressed;

    public Controller(TanksGame game, TextureAtlas atlas) {
        camera = new OrthographicCamera();
        viewport = new FitViewport(game.WIDTH, game.HEIGHT, camera);
        stage = new Stage(viewport, game.batch);
        Gdx.input.setInputProcessor(stage);

        // Load textures
        leftBtn = atlas.findRegion("flatDark23");
        rightBtn = atlas.findRegion("flatDark24");
        upBtn = atlas.findRegion("flatDark25");
        downBtn = atlas.findRegion("flatDark26");

        // Create table layout
        Table table = new Table();
        table.left().bottom();

        Image upBtnImage = new Image(upBtn);
        upBtnImage.setSize(50, 50);
        upBtnImage.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                upPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                upPressed = false;
            }
        });

        Image downBtnImage = new Image(downBtn);
        downBtnImage.setSize(50, 50);
        downBtnImage.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                downPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                downPressed = false;
            }
        });

        Image rightBtnImage = new Image(rightBtn);
        rightBtnImage.setSize(50, 50);
        rightBtnImage.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                rightPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                rightPressed = false;
            }
        });

        Image leftBtnImage = new Image(leftBtn);
        leftBtnImage.setSize(50, 50);
        leftBtnImage.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                leftPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                leftPressed = false;
            }
        });

        // Add actions to our table cells (3x3)
        // 1st row
        table.add();
        table.add(upBtnImage).size(upBtnImage.getWidth(), upBtnImage.getHeight());
        table.add();
        table.row().pad(5, 5, 5, 5);
        // 2nd row
        table.add(leftBtnImage).size(leftBtnImage.getWidth(), leftBtnImage.getHeight());
        table.add();
        table.add(rightBtnImage).size(rightBtnImage.getWidth(), rightBtnImage.getHeight());
        table.row().padBottom(5);
        // 3rd row
        table.add();
        table.add(downBtnImage).size(downBtnImage.getWidth(), downBtnImage.getHeight());
        table.add();

        // Add the table to the stage
        stage.addActor(table);
    }

    public void draw() {
        stage.draw();
    }

    public boolean isUpPressed() {
        return upPressed;
    }

    public boolean isDownPressed() {
        return downPressed;
    }

    public boolean isRightPressed() {
        return rightPressed;
    }

    public boolean isLeftPressed() {
        return leftPressed;
    }

    public boolean isFirePressed() {
        return firePressed;
    }

    public void resize(int width, int height) {
        viewport.update(width, height);
    }
}
