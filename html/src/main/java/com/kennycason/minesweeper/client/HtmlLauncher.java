package com.kennycason.minesweeper.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.kennycason.minesweeper.MineSweeper;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                return new GwtApplicationConfiguration(MineSweeper.WIDTH, MineSweeper.HEIGHT);
        }

        @Override
        public ApplicationListener createApplicationListener() {
                return new MineSweeper();
        }

}