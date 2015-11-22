package com.gordath.jglTest.java;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import com.gordath.jglTest.core.JGlTest;

/**
 * The program's entry point!
 */
public class JGlTestDesktop {
	public static void main (String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        /**
         * Set the window height.
         */
		config.width = 1024;

        /**
         * Set the window width.
         */
		config.height = 768;

        /**
         * Let the magic begin!!!
         */
		new LwjglApplication(new JGlTest(), config);
	}
}
