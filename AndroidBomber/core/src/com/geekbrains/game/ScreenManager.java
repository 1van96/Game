package com.geekbrains.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.*;

public class ScreenManager {
    public enum ScreenType {
        MENU, GAME, END
    }

    private static ScreenManager ourInstance = new ScreenManager();

    public static ScreenManager getInstance() {
        return ourInstance;
    }

    private BomberGame game;
    private Screen targetScreen;
    private LoadingScreen loadingScreen;
    private GameScreen gameScreen;
    private MenuScreen menuScreen;
    private EndScreen endScreen;

    private SpriteBatch batch;
    private Viewport viewport;
    private Camera camera;

    public Viewport getViewport() {
        return viewport;
    }

    private ScreenManager() {
    }

    public void init(BomberGame game, SpriteBatch batch) {
        this.game = game;
        this.batch = batch;
        this.camera = new OrthographicCamera(1280, 720);
        this.viewport = new FitViewport(1280, 720, camera);
        this.gameScreen = new GameScreen(batch, camera);
        this.menuScreen = new MenuScreen(batch);
        this.loadingScreen = new LoadingScreen(batch);
        this.endScreen = new EndScreen(batch, gameScreen);
    }

    public void resize(int width, int height) {
        viewport.update(width, height);
        viewport.apply();
    }

    public void changeScreen(ScreenType type) {
        Screen screen = game.getScreen();
        Gdx.input.setInputProcessor(null); // ?
        Assets.getInstance().clear();
        if (screen != null) {
            //screen.dispose();
        }
        resetCamera();
        game.setScreen(loadingScreen);
        switch (type) {
            case MENU:
                targetScreen = menuScreen;
                Assets.getInstance().loadAssets(ScreenType.MENU);
                break;
            case GAME:
                targetScreen = gameScreen;
                Assets.getInstance().loadAssets(ScreenType.GAME);
                break;
            case END:
                targetScreen = endScreen;
                Assets.getInstance().loadAssets(ScreenType.END);
                break;
        }
    }

    public void resetCamera() {
        camera.position.set(640, 360, 0);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
    }

    public void goToTarget() {
        game.setScreen(targetScreen);
    }
}
