package com.gledyson.tanks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ScreenUtils;

public class PauseScreen implements Screen {
    private final OrthographicCamera camera;
    private final TanksGame game;
    private final MainGameScreen gameScreen;

    // Timer to go back
    private float elapsedTime;

    public PauseScreen(TanksGame game, MainGameScreen gameScreen) {
        this.game = game;
        this.gameScreen = gameScreen;

        // Setup camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, game.WIDTH, game.HEIGHT);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        elapsedTime += delta;
        game.font.draw(game.batch, "PAUSED", (game.WIDTH / 2f) - 16, game.HEIGHT / 2f);
        game.batch.end();

        if (elapsedTime > 1 && Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            game.setScreen(gameScreen);
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
