package com.gledyson.tanks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MainGameScreen implements Screen {
    private final TanksGame game;

    private final OrthographicCamera camera;
    private final OrthographicCamera hudCamera;
    private final Viewport viewport;

    private final PlayerTank playerTank;
    private final EnemyTank enemyTank;
    private final TestBackground background;
    private final Rectangle collisionBox;
    private final TextureAtlas textureAtlas;
    private final BitmapFont font;

    // Sound and music
    private final Music engineSound;
    private final Sound shotSound;
    private final Sound tankHitSound;

    // Timer to pause
    private float timeSincePaused;

    public MainGameScreen(TanksGame game) {
        this.game = game;
        font = game.font;

        // create camera and set viewport
        camera = new OrthographicCamera();
        camera.setToOrtho(true, game.WIDTH, game.HEIGHT);
        hudCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        hudCamera.position.set(hudCamera.viewportWidth / 2.0f, hudCamera.viewportHeight / 2.0f, 1.0f);
        viewport = new StretchViewport(game.WIDTH, game.HEIGHT, camera);

        // load textures
        this.textureAtlas = new TextureAtlas(Gdx.files.internal("textures.atlas"));
        TextureRegion blueTankTexture = textureAtlas.findRegion("tank_blue");
        TextureRegion redTankTexture = textureAtlas.findRegion("tank_red");
        TextureRegion grassTexture = textureAtlas.findRegion("tileGrass1");
        TextureRegion playerShotTexture = textureAtlas.findRegion("shotThin");
        TextureRegion enemyShotTexture = textureAtlas.findRegion("shotRed");

        // create player and enemy tanks
        this.playerTank = new PlayerTank(
                game.WIDTH / 2f, game.HEIGHT / 4f,
                42, 46,
                180,
                blueTankTexture,
                playerShotTexture,
                8, 26,
                650, 3.5f
        );

        this.enemyTank = new EnemyTank(
                game.WIDTH / 2f, 3 * game.HEIGHT / 4f,
                38, 46,
                0,
                redTankTexture,
                enemyShotTexture,
                21, 38,
                800, 4.0f
        );

        // create terrain
        this.background = new TestBackground(grassTexture, 64);

        // Init sounds
        engineSound = Gdx.audio.newMusic(Gdx.files.internal("engine.wav"));
        engineSound.setLooping(true);
        shotSound = Gdx.audio.newSound(Gdx.files.internal("shot.wav"));
        tankHitSound = Gdx.audio.newSound(Gdx.files.internal("tank_hit.wav"));

        // Init
        collisionBox = new Rectangle();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        camera.update();
        hudCamera.update();

        playerTank.update(delta);
        enemyTank.update(delta);

        game.batch.begin();
        // Timers
        timeSincePaused += delta;

        // player input
        handlePlayerInput(delta);

        // Draw background
        background.draw(game);

        // Draw tanks
        playerTank.draw(game.batch);
        enemyTank.draw(game.batch);

        if (enemyTank.canFire()) {
            enemyTank.fire(shotSound);
        }

        // Draw shots
        // Remove old shots
        drawAndUpdateShots(playerTank, delta);
        drawAndUpdateShots(enemyTank, delta);

        // Check collisions
        evaluateCollisions();

        // Explosions

        // Draw HUD
        font.draw(game.batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 16, hudCamera.viewportHeight - 16);

        game.batch.end();


    }

    private void evaluateCollisions() {

        Array.ArrayIterator<Shot> iterator = playerTank.getShots().iterator();
        while (iterator.hasNext()) {
            Shot shot = iterator.next();

            // if shell hits
            if (shot.intersects(enemyTank.getBoundingBox())) {
                tankHitSound.play();
                iterator.remove();
            }
        }

        iterator = enemyTank.getShots().iterator();
        while (iterator.hasNext()) {
            Shot shot = iterator.next();

            // if shell hits
            if (shot.intersects(playerTank.getBoundingBox())) {
                tankHitSound.play();
                iterator.remove();
            }
        }

    }

    public void drawAndUpdateShots(Tank tank, float deltaTime) {
        Array.ArrayIterator<Shot> iterator = tank.getShots().iterator();
        while (iterator.hasNext()) {
            Shot shot = iterator.next();
            shot.draw(game.batch);

            // move based on angle
            float shotX = shot.getPositionX();
            float shotY = shot.getPositionY();

            float angle = shot.getAngle();
            float speed = shot.getSpeed();

            shot.updatePosition(
                    shotX + MathUtils.sinDeg(-angle) * speed * deltaTime,
                    shotY + MathUtils.cosDeg(-angle) * speed * deltaTime
            );

            int threshold = 64;
            if (shot.getPositionY() > game.HEIGHT + threshold ||
                    shot.getPositionY() < -threshold ||
                    shot.getPositionX() > game.WIDTH + threshold ||
                    shot.getPositionX() < -threshold
            ) {
                iterator.remove();
            }
        }
    }


    private void handlePlayerInput(float delta) {
        float currentAngle = playerTank.getTankAngle();
        float rotationRate = playerTank.getRotationSpeed();

        if (timeSincePaused > 1 && Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            timeSincePaused = 0;
            game.setScreen(new PauseScreen(game, this));
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {

            // clockwise
            float finalAngle = currentAngle + rotationRate * delta;

            if (finalAngle > 360) {
                finalAngle = 0;
            } else if (finalAngle < 0) {
                finalAngle = 360;
            }

            playerTank.setTankAngle(finalAngle);

        } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {

            // counter-clockwise
            float finalAngle = currentAngle - rotationRate * delta;

            if (finalAngle > 360) {
                finalAngle = 0;
            } else if (finalAngle < 0) {
                finalAngle = 360;
            }

            playerTank.setTankAngle(finalAngle);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            float posY = playerTank.getPositionY();
            float posX = playerTank.getPositionX();
            float speed = playerTank.getSpeed();
            float angle = playerTank.getTankAngle();

            float newPosX = posX - speed * MathUtils.sinDeg(-angle) * delta;
            float newPosY = posY - speed * MathUtils.cosDeg(-angle) * delta;
            collisionBox.set(newPosX, newPosY, playerTank.getWidth(), playerTank.getHeight());

            if (!collisionBox.overlaps(enemyTank.getBoundingBox())) {
                playerTank.updatePosition(newPosX, newPosY);

                if (!engineSound.isPlaying()) {
                    engineSound.play();
                }
            }

        } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {

            float posY = playerTank.getPositionY();
            float posX = playerTank.getPositionX();
            float reverseSpeed = playerTank.getReverseSpeed();
            float angle = playerTank.getTankAngle();

            float newPosX = posX + reverseSpeed * MathUtils.sinDeg(-angle) * delta;
            float newPosY = posY + reverseSpeed * MathUtils.cosDeg(-angle) * delta;
            collisionBox.set(newPosX, newPosY, playerTank.getWidth(), playerTank.getHeight());

            if (!collisionBox.overlaps(enemyTank.getBoundingBox())) {
                playerTank.updatePosition(newPosX, newPosY);

                if (!engineSound.isPlaying()) {
                    engineSound.play();
                }
            }
        } else {
            engineSound.stop();
        }

        if (playerTank.getPositionX() < 0) {
            playerTank.setPositionX(0);
        } else if (playerTank.getPositionX() > game.WIDTH - playerTank.getWidth()) {
            playerTank.setPositionX(game.WIDTH - playerTank.getWidth());
        }
        if (playerTank.getPositionY() < 0) {
            playerTank.setPositionY(0);
        } else if (playerTank.getPositionY() > game.HEIGHT - playerTank.getWidth()) {
            playerTank.setPositionY(game.HEIGHT - playerTank.getWidth());
        }


        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            // Create new shots
            if (playerTank.canFire()) {
                playerTank.fire(shotSound); // adds a single shot to tank's shots array
            }
        }
    }


    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.setProjectionMatrix(hudCamera.combined);
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
        textureAtlas.dispose();
        shotSound.dispose();
        engineSound.dispose();
        tankHitSound.dispose();
        font.dispose();
    }
}
