package com.gledyson.tanks;

import com.badlogic.gdx.Application;
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
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import org.w3c.dom.Text;

public class MainGameScreen implements Screen {
    private final TanksGame game;
    private final Controller controller;

    private final OrthographicCamera camera;
    private final OrthographicCamera hudCamera;
    private final Viewport viewport;

    private final PlayerTank playerTank;
    private final EnemyTank enemyTank;
    private final TestBackground background;
    private final Rectangle collisionBox;
    private final TextureAtlas textureAtlas;
    private final BitmapFont font;

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
        font = game.font;

        // create camera and set viewport
        camera = new OrthographicCamera();
        hudCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        hudCamera.position.set(hudCamera.viewportWidth / 2.0f, hudCamera.viewportHeight / 2.0f, 1.0f);
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

        this.enemyTank = new EnemyTank(
                game.WIDTH / 2f, 3 * game.HEIGHT / 4f,
                38, 46,
                0,
                redTankTexture,
                redTankDestroyedTexture,
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

        if (!enemyTank.isDead() && enemyTank.canFire()) {
            enemyTank.fire(enemyTank, shotSound);
        }

        // Draw shots
        // Remove old shots
        drawAndUpdateShots(playerTank, delta);
        drawAndUpdateShots(enemyTank, delta);

        // Check collisions
        evaluateCollisions();

        // Update and draw explosions
        updateAndDrawExplosions(delta);

        // Draw HUD
        font.draw(game.batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 16, hudCamera.viewportHeight - 16);

        game.batch.end();

        // render controller
        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            controller.draw();
        }
    }

    private void evaluateCollisions() {

        Array.ArrayIterator<Shot> iterator = playerTank.getShots().iterator();
        while (iterator.hasNext()) {
            Shot shot = iterator.next();

            // if shell hits
            if (shot.intersects(enemyTank.getBoundingBox())) {
                // Check if already dead
                if (enemyTank.isDead()) {
                    tankHitSound.play();
                    iterator.remove();
                    continue;
                }

                boolean isDead = enemyTank.takeDamageAndCheckDestroyed(shot.getDamage());
                iterator.remove();

                if (isDead) {
                    tankExplodedSound.play();
                    explosions.add(new Explosion(
                            explosionFrames,
                            EXPLOSION_FRAME_INTERVAL,
                            enemyTank.getPositionX(),
                            enemyTank.getPositionY()
                    ));
                    enemyTank.getDestroyed();
                } else {
                    tankHitSound.play();
                    explosions.add(new Explosion(
                            smokeExplosionFrames,
                            SMOKE_EXPLOSION_FRAME_INTERVAL,
                            enemyTank.getPositionX(),
                            enemyTank.getPositionY()
                    ));
                }
            }
        }

        iterator = enemyTank.getShots().iterator();
        while (iterator.hasNext()) {
            Shot shot = iterator.next();

            // if shell hits
            if (shot.intersects(playerTank.getBoundingBox())) {
                // check if player is dead
                if (playerTank.isDead()) {
                    tankHitSound.play();
                    if (engineSound.isPlaying()) engineSound.stop();
                    iterator.remove();
                    continue;
                }

                boolean isDead = playerTank.takeDamageAndCheckDestroyed(shot.getDamage());
                iterator.remove();

                if (isDead) {
                    tankExplodedSound.play();
                    if (engineSound.isPlaying()) engineSound.stop();
                    explosions.add(new Explosion(
                            explosionFrames,
                            EXPLOSION_FRAME_INTERVAL,
                            playerTank.getPositionX(),
                            playerTank.getPositionY()
                    ));
                    playerTank.getDestroyed();
                } else {
                    tankHitSound.play();
                    explosions.add(new Explosion(
                            smokeExplosionFrames,
                            SMOKE_EXPLOSION_FRAME_INTERVAL,
                            playerTank.getPositionX(),
                            playerTank.getPositionY()
                    ));
                }
            }
        }

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

            if (!collisionBox.overlaps(enemyTank.getBoundingBox())) {
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

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) || Gdx.input.isTouched()) {
            // Create new shots
            if (playerTank.canFire()) {
                playerTank.fire(playerTank, shotSound); // adds a single shot to tank's shots array
            }
        }
    }


    @Override
    public void resize(int width, int height) {
        controller.resize(width, height);
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
        tankExplodedSound.dispose();
        font.dispose();
        playerTank.dispose();
        enemyTank.dispose();
    }
}
