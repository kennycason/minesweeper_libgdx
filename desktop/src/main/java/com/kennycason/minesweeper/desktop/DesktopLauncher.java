package com.kennycason.minesweeper.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.kennycason.minesweeper.MineSweeper;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Mine Sweeper";
		config.width = MineSweeper.WIDTH;
		config.height = MineSweeper.HEIGHT;
		new LwjglApplication(new MineSweeper(), config);
	}
}
