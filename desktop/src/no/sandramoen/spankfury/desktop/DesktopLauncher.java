package no.sandramoen.spankfury.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import no.sandramoen.spankfury.SpankFuryGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Spank Fury";
		float scale = 2.0f;
		config.width = (int) (3120 / scale);
		config.height = (int) (1440 / scale);
		config.resizable = false;
		new LwjglApplication(new SpankFuryGame(), config);
	}
}
