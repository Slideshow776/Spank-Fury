package no.sandramoen.spankfury.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import no.sandramoen.spankfury.SpankFuryGame;

import java.awt.*;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Spank Fury";
		config.resizable = false;

		// resolution
		Dimension dimension = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		double scale = dimension.getWidth() / dimension.getHeight();
		int LGg8ThinQWidth = 2340;
		int LGg8ThinQHeight = 1080;
		config.width = (int) (LGg8ThinQWidth / scale);
		config.height = (int) (LGg8ThinQHeight / scale);

		new LwjglApplication(new SpankFuryGame(null), config);
	}
}
