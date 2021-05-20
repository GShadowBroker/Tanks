package com.gledyson.tanks;

import com.badlogic.gdx.Application;
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
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Iterator;

public class MainGameScreen implements Screen {
    private final TanksGame game;
    private final Controller controller;

    private final OrthographicCamera camera;
    //    private final OrthographicCamera hudCamera;
    private final Viewport viewport;

    private final PlayerTank playerTank;
    private final Array<EnemyTank> enemyTankList;
    private final TestBackground background;
    private final Rectangle collisionBox;
    private final TextureAtlas textureAtlas;

    private final Array<Explosion> explosions;
    private final TextureRegion[] explosionFrames;
    private final TextureRegion[] smokeExplosionFrames;

    private static final float EXPLOSION_FRAME_INTERVAL = 0.125f;
    private static final float SMOKE_EXPLOSION_FRAME_INTERVAL = 0.05f;

    // Sound and music
    private final Music engineSound;
    private final Sound shotSound;
    private final Sound tankHitSound;
    private final Sound tankExplodedSound;

    // Timer to pause
    private float timeSincePaused;

    public MainGameScreen(TanksGame game) {
        this.game = game;

        // create camera and set viewport
        camera = new OrthographicCamera();
//        hudCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//        hudCamera.position.set(hudCamera.viewportWidth / 2.0f, hudCamera.viewportHeight / 2.0f, 1.0f);
        viewport = new FitViewport(game.WIDTH, game.HEIGHT, camera);

        // load textures
        this.textureAtlas = new TextureAtlas(Gdx.files.internal("textures.atlas"));

        TextureRegion blueTankTexture = textureAtlas.findRegion("tank_blue");
        TextureRegion redTankTexture = textureAtlas.findRegion("tank_red");

        TextureRegion blueTankDestroyedTexture = textureAtlas.findRegion("tankBody_blue_outline");
        TextureRegion redTankDestroyedTexture = textureAtlas.findRegion("tankBody_red_outline");

        TextureRegion grassTexture = textureAtlas.findRegion("tileGrass1");
        TextureRegion playerShotTexture = textureAtlas.findRegion("shotThin");
        TextureRegion enemyShotTexture = textureAtlas.findRegion("shotRed");

        // create player and enemy tanks
        this.playerTank = new PlayerTank(
                game.WIDTH / 2f, game.HEIGHT / 4f,
                42, 46,
                180,
                blueTankTexture,
                blueTankDestroyedTexture,
                playerShotTexture,
                8, 26,
                650, 3.5f
        );

        enemyTankList = new Array<>();
        spawnEnemyTank(
                redTankTexture,
                redTankDestroyedTexture,
                enemyShotTexture,
                game.WIDTH / 2f, 3 * game.HEIGHT / 4f,
                23f
        );
        spawnEnemyTank(
                redTankTexture,
                redTankDestroyedTexture,
                enemyShotTexture,
                100, 3 * game.HEIGHT / 4f,
                9f
        );
        spawnEnemyTank(
                redTankTexture,
                redTankDestroyedTexture,
                enemyShotTexture,
                410, 3 * game.HEIGHT / 4f,
                0f
        );

        // create terrain
        this.background = new TestBackground(grassTexture, 64);

        // Init sounds
        engineSound = Gdx.audio.newMusic(Gdx.files.internal("engine.wav"));
        engineSound.setLooping(true);
        shotSound = Gdx.audio.newSound(Gdx.files.internal("shot.wav"));
        tankHitSound = Gdx.audio.newSound(Gdx.files.internal("tank_hit.wav"));
        tankExplodedSound = Gdx.audio.newSound(Gdx.files.internal("tank_exploded.wav"));

        // Init
        collisionBox = new Rectangle();
        controller = new Controller(game, textureAtlas);

        // Explosions
        explosions = new Array<>();

        // texture file names
        String[] explosionTextureNames = {
                "explosion1", "explosion2",
                "explosion3", "explosion4",
                "explosion5"};
        String[] smokeExplosionTextureNames = {"explosionSmoke1", "explosionSmoke2",
                "explosionSmoke3", "explosionSmoke4", "explosionSmoke5"};

        // get textures from atlas and build frames
        explosionFrames = new TextureRegion[explosionTextureNames.length];
        smokeExplosionFrames = new TextureRegion[smokeExplosionTextureNames.length];

        for (int i = 0; i < explosionFrames.length; i++) {
            explosionFrames[i] = textureAtlas.findRegion(explosionTextureNames[i]);
        }
        for (int i = 0; i < explosionFrames.length; i++) {
            smokeExplosionFrames[i] = textureAtlas.findRegion(smokeExplosionTextureNames[i]);
        }
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        //        hudCamera.update();
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        // Shake Effect
        if (ShakeEffect.getTimeLeft() > 0) {
            camera.translate(ShakeEffect.updateAndGetPosition(delta));
        } else {
            // recenter camera
            camera.setToOrtho(false, game.WIDTH, game.HEIGHT);
        }

        // player input
        handlePlayerInput(delta);

        /* START DRAWING */
        game.batch.begin();

        // update timers
        timeSincePaused += delta;
        playerTank.update(delta);

        // Draw background
        background.draw(game);

        // Draw tanks
        playerTank.draw(game.batch);
        updateAndDrawEnemyTanks(delta);

        // Draw shots
        // Remove old shots
        drawAndUpdateShots(playerTank, delta);

        // Draw HUD
//        game.font.draw(game.batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 16, hudCamera.viewportHeight - 16);

        // Check collisions
        evaluateCollisions();

        // Update and draw explosions
        updateAndDrawExplosions(delta);

        /* END DRAWING */
        game.batch.end();

        // render controller (has to be after main batch.end())
        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            controller.draw();
        }
    }

    private void spawnEnemyTank(
            TextureRegion tankTexture,
            TextureRegion destroyedTankTexture,
            TextureRegion shotTexture,
            float centerX, float centerY,
            float angle
    ) {
        enemyTankList.add(new EnemyTank(
                centerX, centerY,
                38, 46,
                angle,
                tankTexture,
                destroyedTankTexture,
                shotTexture,
                21, 38,
                800, 4.0f
        ));
    }

    private void updateAndDrawEnemyTanks(float delta) {
        for (Tank enemyTank : enemyTankList) {
            enemyTank.update(delta);
            enemyTank.draw(game.batch);

//            if (!enemyTank.isDead() && enemyTank.canFire()) {
//                enemyTank.fire(enemyTank, shotSound);
//            }

            drawAndUpdateShots(enemyTank, delta);
        }
    }

    private void evaluateCollisions() {

        // Check every shot of playerTank
        Array.ArrayIterator<Shot> iterator = playerTank.getShots().iterator();
        while (iterator.hasNext()) {
            Shot shot = iterator.next();

            for (Tank enemyTank : enemyTankList) {
                // if shell hits
                if (shot.intersects(enemyTank.getBoundingBox())) {
                    handleHit(shot, enemyTank, iterator);
                }
            }
        }

        // Check every enemy tank's shot
        for (Tank enemyTank : enemyTankList) {
            iterator = enemyTank.getShots().iterator();
            while (iterator.hasNext()) {
                Shot shot = iterator.next();

                // if shell hits
                if (shot.intersects(playerTank.getBoundingBox())) {
                    handleHit(shot, playerTank, iterator);
                }
            }
        }
    }

    public void handleHit(Shot shot, Tank tank, Iterator<Shot> iterator) {
        // Check if already dead
        if (tank.isDead()) {
            tankHitSound.play();
            iterator.remove();
            return;
        }

        // if tank is dead
        if (tank.takeDamageAndCheckDestroyed(shot.getDamage())) {
            tankExplodedSound.play();
            ShakeEffect.shakeIt(4f, .2f);
            explosions.add(new Explosion(
                    explosionFrames,
                    EXPLOSION_FRAME_INTERVAL,
                    tank.getPositionX(),
                    tank.getPositionY()
            ));
        } else {
            tankHitSound.play();
            explosions.add(new Explosion(
                    smokeExplosionFrames,
                    SMOKE_EXPLOSION_FRAME_INTERVAL,
                    shot.getPositionX(),
                    shot.getPositionY()
            ));
        }
        iterator.remove(); // removes the shot after hit
    }

    public void drawAndUpdateShots(Tank tank, float deltaTime) {
        Array.ArrayIterator<Shot> iterator = tank.getShots().iterator();
        while (iterator.hasNext()) {
            Shot shot = iterator.next();
            shot.draw(game.batch);
            shot.updatePosition(
                    shot.getPositionX() + MathUtils.sinDeg(-shot.getAngle()) * shot.getSpeed() * deltaTime,
                    shot.getPositionY() + MathUtils.cosDeg(-shot.getAngle()) * shot.getSpeed() * deltaTime
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

    private void updateAndDrawExplosions(float delta) {
        Array.ArrayIterator<Explosion> explosionsIterator = explosions.iterator();
        while (explosionsIterator.hasNext()) {
            Explosion explosion = explosionsIterator.next();

            explosion.update(delta);

            if (explosion.isFinished()) {
                explosionsIterator.remove();
            } else {
                // if animation is not finished
                explosion.draw(game.batch);
            }
        }
    }

    private void handlePlayerInput(float delta) {
        if (playerTank.isDead()) return;

        float currentAngle = playerTank.getTankAngle();
        float rotationRate = playerTank.getRotationSpeed();

        if (timeSincePaused > 1 && Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            timeSincePaused = 0;
            game.setScreen(new PauseScreen(game, this));
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A) || controller.isLeftPressed()) {

            // clockwise
            float finalAngle = currentAngle + rotationRate * delta;

            if (finalAngle > 360) {
                finalAngle = 0;
            } else if (finalAngle < 0) {
                finalAngle = 360;
            }

            playerTank.setTankAngle(finalAngle);

        } else if (Gdx.input.isKeyPressed(Input.Keys.D) || controller.isRightPressed()) {

            // counter-clockwise
            float finalAngle = currentAngle - rotationRate * delta;

            if (finalAngle > 360) {
                finalAngle = 0;
            } else if (finalAngle < 0) {
                finalAngle = 360;
            }

            playerTank.setTankAngle(finalAngle);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.W) || controller.isUpPressed()) {
            float newPosX = playerTank.getPositionX() - playerTank.getSpeed() * MathUtils.sinDeg(-playerTank.getTankAngle()) * delta;
            float newPosY = playerTank.getPositionY() - playerTank.getSpeed() * MathUtils.cosDeg(-playerTank.getTankAngle()) * delta;

            collisionBox.set(
                    newPosX,
                    newPosY,
                    playerTank.getWidth(),
                    playerTank.getHeight());

            boolean isPathBlocked = false;

            for (Tank enemyTank : enemyTankList) {
                if (collisionBox.overlaps(enemyTank.getBoundingBox())) {
                    isPathBlocked = true;
                }
            }

            if (!isPathBlocked) {
                playerTank.updatePosition(newPosX, newPosY);

                if (!engineSound.isPlaying()) {
                    engineSound.play();
                }
            }

        } else if (Gdx.input.isKeyPressed(Input.Keys.S) || controller.isDownPressed()) {
            float newPosX = playerTank.getPositionX() + playerTank.getReverseSpeed() * MathUtils.sinDeg(-playerTank.getTankAngle()) * delta;
            float newPosY = playerTank.getPositionY() + playerTank.getReverseSpeed() * MathUtils.cosDeg(-playerTank.getTankAngle()) * delta;

            collisionBox.set(
                    newPosX,
                    newPosY,
                    playerTank.getWidth(),
                    playerTank.getHeight());

            boolean isPathBlocked = false;

            for (Tank enemyTank : enemyTankList) {
                if (collisionBox.overlaps(enemyTank.getBoundingBox())) {
                    isPathBlocked = true;
                }
            }

            if (!isPathBlocked) {
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

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) || Gdx.input.isTouched()) {
            // Create new shots
            if (playerTank.canFire()) {
                ShakeEffect.shakeIt(3f, .1f);
                playerTank.fire(playerTank, shotSound); // adds a single shot to tank's shots array
            }
        }
    }


    @Override
    public void resize(int width, int height) {
        controller.resize(width, height);
        viewport.update(width, height, true);
//        game.batch.setProjectionMatrix(hudCamera.combined);
//        game.batch.setProjectionMatrix(camera.combined);
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
        tankExplodedSound.dispose();
        playerTank.dispose();
        for (Tank enemyTank : enemyTankList) {
            enemyTank.dispose();
        }
    }
}
