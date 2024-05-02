package com.mygdx.quest;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main(String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();

		config.setTitle("Angler's Quest");
		config.setWindowedMode(1280, 720);
		// config.setResizable(false);
		config.setForegroundFPS(60);
		config.setWindowIcon(FileType.Internal, "assets/anglers-quest-icon.png");
		// config.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
		
		new Lwjgl3Application(new AnglersQuest(), config);
	}
}
