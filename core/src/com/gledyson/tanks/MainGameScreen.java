package com.gledyson.tanks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MainGameScreen implements Screen {
    private TanksGame game;

    private OrthographicCamera camera;
    private Viewport viewport;

    private PlayerTank playerTank;
    private EnemyTank enemyTank;
    private TestBackground background;

    private Rectangle collisionBox;

    private TextureAtlas textureAtlas;

    // Sound and music
    private Music engineSound;
    private Sound shotSound;
    private Sound tankHitSound;

    public MainGameScreen(TanksGame game) {
        this.game = game;

        // create camera and set viewport
        camera = new OrthographicCamera();
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
                game.WIDTH / 2, game.HEIGHT / 4,
                42, 46,
                blueTankTexture,
                playerShotTexture,
                8, 26,
                650, 2.0f
        );

        this.enemyTank = new EnemyTank(
                game.WIDTH / 2, 3 * game.HEIGHT / 4,
                38, 46,
                redTankTexture,
                enemyShotTexture,
                21, 38,
                800, 2.2f
        );

        // create terrain
        this.background = new TestBackground(grassTexture, 64);

        // Init sounds
        engineSound = Gdx.audio.newMusic(Gdx.files.internal("engine.wav"));
        engineSound.setLooping(true);
        shotSound = Gdx.audio.newSound(Gdx.files.internal("shot.wav"));
        tankHitSound = Gdx.audio.newSound(Gdx.files.internal("tank_hit.wav"));

        // Init rectangle
        collisionBox = new Rectangle();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        camera.update();

        playerTank.update(delta);
        enemyTank.update(delta);

        game.batch.begin();

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

        game.batch.end();

        // player input
        handlePlayerInput(delta);
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

            float newPosX = posX + speed * MathUtils.sinDeg(-angle) * delta;
            float newPosY = posY + speed * MathUtils.cosDeg(-angle) * delta;
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

            float newPosX = posX - reverseSpeed * MathUtils.sinDeg(-angle) * delta;
            float newPosY = posY - reverseSpeed * MathUtils.cosDeg(-angle) * delta;
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
    }
}
