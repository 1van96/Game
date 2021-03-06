package com.geekbrains.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.geekbrains.game.BomberGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		System.setProperty("user.name", "Public");
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1280;
		config.height = 720;
		new LwjglApplication(new BomberGame(), config);
	}
}
