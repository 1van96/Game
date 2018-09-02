package com.geekbrains.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.xml.soap.Text;

public class EndScreen implements Screen {
    private SpriteBatch batch;
    private Stage stage;
    private Skin skin;
    private BitmapFont font32;
    private BitmapFont font96;
    private GameScreen gs;
    private static Connection connection;
    private static Statement statement;
    private int score[] = new int[3];

    public static void connect() throws SQLException, ClassNotFoundException{
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:Players.db");
        statement = connection.createStatement();
    }


    public static void disconnect(){
        try{
            statement.close();
            connection.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    private void getData() throws SQLException{
        ResultSet resultSet = statement.executeQuery("SELECT * FROM High_scores");
        int i = 0;
        while(resultSet.next()){
            score[i] = resultSet.getInt(2);
            i++;
        }
    }

    public void getHigh() {
        try{
            connect();
            getData();
        }catch(SQLException e){
            e.printStackTrace();
        }catch(ClassNotFoundException e){

        }finally{
            disconnect();
        }
    }

    public EndScreen(SpriteBatch batch, GameScreen gs){
        this.gs = gs;
        this.batch = batch;
    }
    @Override
    public void show() {
        font32 = Assets.getInstance().getAssetManager().get("gomarice32.ttf", BitmapFont.class);
        font96 = Assets.getInstance().getAssetManager().get("gomarice96.ttf", BitmapFont.class);
        createGUI();
    }


    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0.5f, 0.8f, 0.5f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        font96.draw(batch, "Game Over", 0, 600, 1280, 1, false);
        font32.draw(batch, "Your score: " + gs.getPlayer().getScore(), 0, 500, 1280, 1, false);
        font32.draw(batch, "High scores:",0, 450, 1280, 1,false);
        font32.draw(batch, "1. "+ score[0], 0, 400, 1280, 1, false);
        font32.draw(batch, "2. "+ score[1], 0, 350, 1280, 1, false);
        font32.draw(batch, "3. "+ score[2], 0, 300, 1280, 1, false);
        batch.end();
        stage.draw();
    }

    public void update(float dt) {
        stage.act(dt);
    }

    private void createGUI() {
        stage = new Stage(ScreenManager.getInstance().getViewport(), batch);
        Gdx.input.setInputProcessor(stage);
        skin = new Skin();
        skin.addRegions(Assets.getInstance().getAtlas());
        skin.add("font32", font32);

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.getDrawable("simpleButton");
        textButtonStyle.font = font32;
        skin.add("simpleSkin", textButtonStyle);

        Button btnNewGame = new TextButton("Start New Game", skin, "simpleSkin");
        Button btnExitGame = new TextButton("Exit Game", skin, "simpleSkin");
        btnNewGame.setPosition(640 - 160, 180);
        btnExitGame.setPosition(640 - 160, 80);
        getHigh();
        boolean isHigh = false;
        int i;
        for (i = 0; i < 3; i++){
            if (score[i] < gs.getPlayer().getScore()){
                isHigh = true;
                break;
            }
        }
        if (isHigh) {
            switch (i) {
                case 0:
                    score[2] = score[1];
                    score[1] = score[0];
                    score[0] = gs.getPlayer().getScore();
                    try {
                        connect();
                        statement.executeUpdate("UPDATE High_scores SET Score = " + score[0]
                                + " WHERE id = 1;");
                        statement.executeUpdate("UPDATE High_scores SET Score = " + score[1]
                                + " WHERE id = 2;");
                        statement.executeUpdate("UPDATE High_scores SET Score = " + score[2]
                                + " WHERE id = 3;");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } finally {
                        disconnect();
                    }
                    break;
                case 1:
                    score[2] = score[1];
                    score[1] = gs.getPlayer().getScore();
                    try {
                        connect();
                        statement.executeUpdate("UPDATE High_scores SET Score = " + score[1]
                                + " WHERE id = 2;");
                        statement.executeUpdate("UPDATE High_scores SET Score = " + score[2]
                                + " WHERE id = 3;");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } finally {
                        disconnect();
                    }
                    break;
                case 2:
                    score[2] = gs.getPlayer().getScore();
                    try {
                        connect();
                        statement.executeUpdate("UPDATE High_scores SET Score = " + score[2]
                                + " WHERE id = 3;");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } finally {
                        disconnect();
                    }
                    break;
            }
        }
        stage.addActor(btnNewGame);
        stage.addActor(btnExitGame);
        btnNewGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.GAME);
            }
        });
        btnExitGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
    }

    @Override
    public void resize(int width, int height) {
        ScreenManager.getInstance().resize(width, height);
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
