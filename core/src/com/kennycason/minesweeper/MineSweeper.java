package com.kennycason.minesweeper;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Random;

public class MineSweeper extends ApplicationAdapter {
	public static final int WIDTH = 460;
	public static final int HEIGHT = 350;

	private final Random random = new Random();

	private OrthographicCamera camera;
	private SpriteBatch batch;
	private ShapeRenderer shapeRenderer;
	private BitmapFont font;
	private Sound explosion;

	private boolean[][] map;
	private boolean[][] clicked;
	private boolean[][] flagged;
	private int[][] sorroundingMines;
	private int numMines;
	private int width;
	private int height;
	private int tileDim = 10;
	private long mouseLastClicked;
	private boolean gameOver;
	private long gameOverTime;

	@Override
	public void create () {
		width = 40;
		height = 30;
		map = new boolean[width][height];
		clicked = new boolean[width][height];
		flagged = new boolean[width][height];
		sorroundingMines = new int[width][height];
		numMines = 150;

		explosion = Gdx.audio.newSound(Gdx.files.internal("explosion.wav"));
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		font = new BitmapFont();
		font.setColor(Color.WHITE);

		camera = new OrthographicCamera();
		camera.setToOrtho(false, WIDTH, HEIGHT);
		camera.update();

		newMap();

		Gdx.input.setInputProcessor(new InputAdapter() {
			@Override
			public boolean touchUp(int screenX, int screenY, int pointer, int button) {
				if (button == 0) {
					handlePrimaryClick(screenX, HEIGHT - screenY);
				}
				else if (button == 1) {
					handleSecondaryClick(screenX, HEIGHT - screenY);
				}
				return true;
			}
		});
	}

	@Override
	public void render () {
		if (!gameOver) {
			handleKeyboard();
			draw();
			if (isWin() || isLose()) {
				gameOver = true;
				gameOverTime = TimeUtils.millis();
				explosion.play();
			}
		}
		else {
			drawEndScreen();
			if (TimeUtils.timeSinceMillis(gameOverTime) > 2000) {
				create();
			}
		}
	}

	@Override
	public void dispose() {
		explosion.dispose();
		batch.dispose();
		shapeRenderer.dispose();
		font.dispose();
	}

	public void handlePrimaryClick(final int mx, final int my) {
		if (gameOver) { return; }

		if (TimeUtils.timeSinceMillis(mouseLastClicked) < 150) { return; }
		mouseLastClicked = TimeUtils.millis();

		final int x = (int) ((mx - 10) / (double) (tileDim + 1));
		final int y = (int) ((my - 10) / (double) (tileDim + 1));
		if (x >= 0 && x < width && y >= 0 && y < height) {
			click(x, y, 0);
		}
	}

	public void handleSecondaryClick(final int mx, final int my) {
		if (gameOver) { return; }
		
		if (TimeUtils.timeSinceMillis(mouseLastClicked) < 150) { return; }
		mouseLastClicked = TimeUtils.millis();

		final int x = (int) ((mx - 10) / (double) (tileDim + 1));
		final int y = (int) ((my - 10) / (double) (tileDim + 1));
		if (x >= 0 && x < width && y >= 0 && y < height) {
			flagged[x][y] = !flagged[x][y];
		}
	}

	public void handleKeyboard() {
		if(Gdx.input.isKeyPressed(Keys.H)) {
			help();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if(Gdx.input.isKeyPressed(Keys.C)) {
			clickAll();
		}
		if(Gdx.input.isKeyPressed(Keys.R)) {
			newMap();
		}
		if(Gdx.input.isKeyPressed(Keys.ESCAPE)) {
			System.exit(0);
		}
	}

	private void click(int x, int y, int depth) {
		if(x < 0 || x >= width || y < 0 || y >= height) {
			return;
		}
		if(clicked[x][y]) {
			return;
		}
		clicked[x][y] = true;
		if(map[x][y]) {
			return;
		}
		if(sorroundingMines[x][y] > 0) {
			return;
		}

		click(x - 1, y - 1, depth + 1);
		click(x, y - 1, depth + 1);
		click(x + 1, y - 1, depth + 1);
		click(x - 1, y, depth + 1);
		click(x + 1, y, depth + 1);
		click(x - 1, y + 1, depth + 1);
		click(x, y + 1, depth + 1);
		click(x + 1, y + 1, depth + 1);
	}

	private void help() {
		if(!isWin() && !isLose()) {
			for(int y = 0; y < height; y++) {
				for(int x = 0; x < width; x++) {
					if(!map[x][y] && !clicked[x][y]) {
						flagged[x][y] = false;
						click(x, y, 0);
						return;
					}
				}
			}
		}
	}

	private void drawRect(final Color color, final int x, final int y) {
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(color);
		shapeRenderer.rect(x, y, tileDim, tileDim);
		shapeRenderer.end();
	}

	public void draw() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();

		shapeRenderer.setProjectionMatrix(camera.combined);

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (clicked[x][y]) {
					if (map[x][y]) { // if is mine
						drawRect(Color.RED,
								x * (tileDim + 1) + 10,
								y * (tileDim + 1) + 10);
					}
					else { // draw empty square;
						drawRect(Color.WHITE,
								x * (tileDim + 1) + 10,
								y * (tileDim + 1) + 10);

						if (sorroundingMines[x][y] > 0) {
							batch.begin();
							font.getData().setScale(0.8f, 0.8f);
							font.setColor(getMineCountColor(sorroundingMines[x][y]));
							font.draw(batch,
									String.valueOf(sorroundingMines[x][y]),
									x * (tileDim + 1) + 11,
									y * (tileDim + 1) + 19);
							batch.end();
						}
					}
				} else {
					drawRect(Color.GRAY,
							x * (tileDim + 1) + 10,
							y * (tileDim + 1) + 10);

					if (flagged[x][y]) {
						drawRect(Color.GREEN,
								x * (tileDim + 1) + 10,
								y * (tileDim + 1) + 10);
					}
				}

			}
		}
	}

	private void drawEndScreen() {
		draw();
		batch.begin();
		font.setColor(Color.RED);

		if(isWin()) {
			font.getData().setScale(2.0f, 2.0f);
			font.draw(batch, "You Win", 20, 120);
		}
		else if(isLose()) {
			font.getData().setScale(2.0f, 2.0f);
			font.draw(batch, "You Lose", 20, 120);
		}
		batch.end();
	}

	private Color getMineCountColor(int num) {
		switch(num) {
			case 1:
				return Color.BLACK;
			case 2:
				return Color.GRAY;
			case 3:
				return Color.BLUE;
			case 4:
				return Color.RED;
			case 5:
				return Color.MAGENTA;
			case 6:
				return Color.GREEN;
			default:
				return Color.BLACK;
		}
	}


	private void newMap() {
		gameOver = false;

		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				clicked[x][y] = false;
				flagged[x][y] = false;
				map[x][y] = false;
			}
		}

		int minesPlaced = 0;
		while(minesPlaced < numMines) {
			int x = random.nextInt(width);
			int y = random.nextInt(height);
			if(!map[x][y]) {
				map[x][y] = true; // place mine
				minesPlaced++;
			}
		}

		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				sorroundingMines[x][y] = adjacentMines(x, y);
			}
		}
	}

	private int adjacentMines(int x, int y) {
		int tot = 0;
		if(isMine(x - 1, y - 1)) {
			tot++;
		}
		if(isMine(x, y - 1)) {
			tot++;
		}
		if(isMine(x + 1, y - 1)) {
			tot++;
		}
		if(isMine(x - 1, y)) {
			tot++;
		}
		if(isMine(x + 1, y)) {
			tot++;
		}
		if(isMine(x - 1, y + 1)) {
			tot++;
		}
		if(isMine(x, y + 1)) {
			tot++;
		}
		if(isMine(x + 1, y + 1)) {
			tot++;
		}
		return tot;
	}

	private boolean isMine(int x, int y) {
		if(x >= 0 && x < width && y >= 0 && y < height) {
			if(map[x][y]) {
				return true;
			}
		}
		return false;
	}

	private void clickAll() {
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				clicked[x][y] = true;
			}
		}
	}

	private boolean isWin() {
		boolean win = true;
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				win &= ((clicked[x][y] && !map[x][y]) ||
						(!clicked[x][y] && map[x][y]));
			}
		}
		return win;
	}

	private boolean isLose() {
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				if(clicked[x][y] && map[x][y]) {
					return true;
				}
			}
		}
		return false;
	}

	private void debug() {
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				System.out.print(map[x][y] + ",");
			}
			System.out.println();
		}
	}

}
