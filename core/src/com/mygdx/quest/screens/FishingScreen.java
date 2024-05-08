package com.mygdx.quest.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.mygdx.quest.AnglersQuest;
import com.mygdx.quest.entities.Player;
import com.mygdx.quest.utils.Fish;
import com.mygdx.quest.utils.Fish.Rarity;

public class FishingScreen extends ScreenAdapter{
    private OrthographicCamera camera;
	private ShapeRenderer shapeRenderer;
	private Rectangle bar;
	private Rectangle fish;
	private boolean isPressed;
	private float timer = 0; // Timer to track the duration of each movement phase
	private boolean movingUp = true; // Flag to track the current movement direction
	private float nextInterval = MathUtils.random(2f, 5f); // Random interval for the next movement phase
	private float stopPositionUp; // Random position to stop moving up
	private float stopPositionDown; // Random position to stop moving down
	private boolean stopMovingUp; // Flag to indicate if the bar should stop moving up
	private boolean stopMovingDown; // Flag to indicate if the bar should stop moving down
	private BitmapFont font;
	private SpriteBatch batch;
	private float fishingTimer;

	private BitmapFont descFont;

	private Image background;
	private Stage stage;

	private Sound reelingSFX;

	private float elapsedTime;
	private static float timeLimit;
	private Fish fishItem;

	final AnglersQuest game;
	float barHeight;
	float speed;
	float fishSpeed;

	// Upgrades
	Player player;
	private boolean hasBait;
	private boolean hasHook;
	private boolean hasTackleBox;
	private boolean hasBobber;
	private boolean hasFishingLine;

	public interface FishingCallback {
		void onFishingCompleted(boolean isFishCaught, Fish fish);
	}

	private FishingCallback fishingCallback;

	public FishingScreen(final AnglersQuest game, Fish fishItem, FishingCallback fishingCallback, Player player) {
		this.game = game;
		this.fishingCallback = fishingCallback;
		this.fishItem = fishItem;
		this.player = player;

		// Check if the player has the upgrades
		hasBait = player.getUpgrades().contains("Bait");
		hasHook = player.getUpgrades().contains("Hook");
		hasTackleBox = player.getUpgrades().contains("Tackle-Box");
		hasBobber = player.getUpgrades().contains("Bobber");
		hasFishingLine = player.getUpgrades().contains("Fishing-Line");
		
		if (fishItem.getRarity() == Rarity.COMMON) {
			timeLimit = 15f;
		} else if (fishItem.getRarity() == Rarity.RARE) {
			timeLimit = 20f;
		} else if (fishItem.getRarity() == Rarity.LEGENDARY) {
			timeLimit = 25f;
		}

		// Apply upgrades
		if (hasTackleBox) {
			timeLimit += 5f;
		}

		// Fail check
		elapsedTime = 0f;

		reelingSFX = Gdx.audio.newSound(Gdx.files.internal("sounds\\reelingSFX.mp3"));

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);

		this.stage = new Stage(new ExtendViewport(AnglersQuest.V_WIDTH, AnglersQuest.V_HEIGHT, game.camera));

		shapeRenderer = new ShapeRenderer();

		// moving/main bar
		float barWidth = 23;
		// float barHeight = 90;
		if (fishItem.getRarity() == Rarity.COMMON) {
			barHeight = 90;
		} else if (fishItem.getRarity() == Rarity.RARE) {
			barHeight = 45;
		} else if (fishItem.getRarity() == Rarity.LEGENDARY) {
			barHeight = 30;
		}

		// Apply upgrades
		if (hasBait) {
			barHeight += 10;
		}
		if (hasBobber) {
			barHeight += 20;
		}

		float barX = 400 - barWidth / 2;
		float barY = 40;
		bar = new Rectangle(barX, barY, barWidth, barHeight);

		// moving fish Icon
		float fishX = 393;
		float fishY = 40;
		float fishWidth = 20;
		float fishHeight = 20;
		fish = new Rectangle(fishX, fishY, fishWidth, fishHeight);

		isPressed = false;

		stopPositionUp = MathUtils.random(40, 212);
		stopPositionDown = MathUtils.random(40, 212);

		// Initialize stop flags
		stopMovingUp = false;
		stopMovingDown = false;

		font = new BitmapFont();
		descFont = new BitmapFont();
		batch = new SpriteBatch();
	}

	@Override
	public void show() {
		System.out.println("FISHING SCREEN");

		Texture bgTexture = game.assets.get("images\\underwater.png");

        background = new Image(bgTexture);
        background.setFillParent(true);

		stage.clear();
		stage.addActor(background);
	}

	@Override
	public void render(float delta) {
		basicHandleInput();
		basicUpdateBarMovement();
		basicPlayMovement();
		elapsedTime += delta;
		basicCheckFishBarContact();

		// Fail check
		// System.out.println(elapsedTime);

		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();


		stage.act();
		stage.draw();
		
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

		float backgroundX = 390;
		float backgroundY = 40;
		float backgroundWidth = 25;
		float backgroundHeight = 220;

		// static bg bar
		shapeRenderer.setColor(0.451f, 0.576f, 0.702f, 1);
		shapeRenderer.rect(backgroundX, backgroundY, backgroundWidth, backgroundHeight);

		// shadow
		shapeRenderer.setColor(Color.DARK_GRAY);
		shapeRenderer.rect(bar.x + 2, bar.y - 2, bar.width, bar.height);

		// main bar
		shapeRenderer.setColor(0.651f, 0.839f, 0.035f, 1);
		shapeRenderer.rect(bar.x, bar.y, bar.width, bar.height);

		// fish
		shapeRenderer.setColor(Color.RED);
		shapeRenderer.rect(fish.x, fish.y, fish.width, fish.height);

		shapeRenderer.end();

		basicRenderRemainingTime();
		basicRenderTimeLimit();
		renderInstructions();
	}

	private void basicHandleInput() {
		if (Gdx.input.isButtonPressed(com.badlogic.gdx.Input.Buttons.LEFT)) {
			isPressed = true;
		} else {
			isPressed = false;
		}
	}

	private void basicUpdateBarMovement() {
		// Define the speed for movement
		if (fishItem.getRarity() == Rarity.COMMON) {
			speed = 90; // Adjust this value as needed for the desired speed
		} else if (fishItem.getRarity() == Rarity.RARE) {
			speed = 120; // Adjust this value as needed for the desired speed
		} else if (fishItem.getRarity() == Rarity.LEGENDARY) {
			speed = 150; // Adjust this value as needed for the desired speed
		}
		
		// Apply upgrades
		if (hasHook) {
			speed -= 10;
		}
		if (hasFishingLine) {
			speed -= 20;
		}

		// Update the timer
		timer += Gdx.graphics.getDeltaTime();

		// Check if it's time to change the movement direction or stop moving
		if (timer >= nextInterval) {
			if (movingUp && !stopMovingUp && bar.y >= stopPositionUp) {
				stopMovingUp = true; // Stop moving up when reaching the stop position
			} else if (!movingUp && !stopMovingDown && bar.y <= stopPositionDown) {
				stopMovingDown = true; // Stop moving down when reaching the stop position
			} else {
				movingUp = !movingUp; // Reverse the movement direction
				timer = 0; // Reset the timer
				nextInterval = MathUtils.random(1f, 1.2f); // Randomize the interval for the next movement phase
				stopPositionUp = MathUtils.random(40, 212); // Generate new random stop positions
				stopPositionDown = MathUtils.random(40, 212);
				stopMovingUp = false; // Reset stop flags
				stopMovingDown = false;
			}
		}

		// Move the bar based on the current movement direction
		if (fishItem.getRarity() == Rarity.COMMON) {
			if (movingUp && !stopMovingUp) {
				// Move the bar up until it reaches the top or the stop position
				if (bar.y < 170) {
					bar.y += speed * Gdx.graphics.getDeltaTime(); // Move up at the fixed speed
				}
			} else if (!movingUp && !stopMovingDown) {
				// Move the bar down until it reaches the bottom or the stop position
				if (bar.y > 10) {
					bar.y -= speed * Gdx.graphics.getDeltaTime(); // Move down at the fixed speed
				}
			}
		} else if (fishItem.getRarity() == Rarity.RARE) {
			if (movingUp && !stopMovingUp) {
				// Move the bar up until it reaches the top or the stop position
				if (bar.y < 189) {
					bar.y += speed * Gdx.graphics.getDeltaTime(); // Move up at the fixed speed
				}
			} else if (!movingUp && !stopMovingDown) {
				// Move the bar down until it reaches the bottom or the stop position
				if (bar.y > 40) {
					bar.y -= speed * Gdx.graphics.getDeltaTime(); // Move down at the fixed speed
				}
			}
		} else if (fishItem.getRarity() == Rarity.LEGENDARY) {
			if (movingUp && !stopMovingUp) {
				// Move the bar up until it reaches the top or the stop position
				if (bar.y < 212) {
					bar.y += speed * Gdx.graphics.getDeltaTime(); // Move up at the fixed speed
				}
			} else if (!movingUp && !stopMovingDown) {
				// Move the bar down until it reaches the bottom or the stop position
				if (bar.y > 40) {
					bar.y -= speed * Gdx.graphics.getDeltaTime(); // Move down at the fixed speed
				}
			}
		}
	}

	private void basicPlayMovement() {

		if (fishItem.getRarity() == Rarity.COMMON) {
			fishSpeed = 120;
		} else if (fishItem.getRarity() == Rarity.RARE) {
			fishSpeed = 135;
		} else if (fishItem.getRarity() == Rarity.LEGENDARY) {
			fishSpeed = 150;
		}

		// Apply upgrades
		if (hasTackleBox) {
			fishSpeed += 10;
		}

		if (isPressed && fish.y < 240) {

			fish.y += fishSpeed * Gdx.graphics.getDeltaTime(); // Adjust speed as needed
		} else if (!isPressed && fish.y > 40) {
			// Move down until it reaches the bottom
			fish.y -= fishSpeed * Gdx.graphics.getDeltaTime(); // Adjust speed as needed
		}
	}

	// CHECKS THE CONTACT OF FISH AND MAIN BAR
	private void basicCheckFishBarContact() {
		// Check for fish bar contact
		if (fish.overlaps(bar)) {
			reelingSFX.play();
			fishingTimer += Gdx.graphics.getDeltaTime(); // Increment fishing timer
			if (fishingTimer >= 10f) { // Check if contact duration is 12 seconds
				reelingSFX.stop();
				game.setScreen(game.gameScreen); // Exit the application
				fishingTimer = 0;
				if (fishingCallback != null) {
					fishingCallback.onFishingCompleted(true, fishItem);
				}
			}
		} else {
			// Reset timer if there's no contact
			reelingSFX.stop();
			fishingTimer = Math.max(0, fishingTimer - Gdx.graphics.getDeltaTime());

			if (elapsedTime >= timeLimit) {
				reelingSFX.stop();
				game.setScreen(game.gameScreen);
				elapsedTime = 0f;
				if (fishingCallback != null) {
					fishingCallback.onFishingCompleted(false, fishItem);
				}
			}
		}
	}

	// DISPLAYS REMAINING TIME UNTIL COMPLETION
	private void basicRenderRemainingTime() {
		float remainingTime = Math.max(0, fishingTimer); // Calculate remaining time
		String timeText = "Progress: " + String.format("%.0f", remainingTime * 10) + "/100"; // Format time as string

		// Draw time text on the screen
		batch.begin();
		font.draw(batch, timeText, 10, Gdx.graphics.getHeight() - 10);
		font.getData().setScale(5);
		batch.end();
	}

	private void basicRenderTimeLimit() {
		float remainingTime = Math.max(0, timeLimit - elapsedTime);
		String timeLimitText = "Time left: " + String.format("%.0f", remainingTime) + "s";

		batch.begin();
		font.draw(batch, timeLimitText, 10, Gdx.graphics.getHeight() - 100);
		font.getData().setScale(5);
		batch.end();
	}

	private void renderInstructions() {
		batch.begin();
		descFont.draw(
			batch, 
			"Hold M1 to control the red box" + 
			"\nComplete the progress to catch the fish" + 
			"\nDon't let the timer reach ZERO or you'll lose coins!" +
			"\nYou don't lose coins for the first 5 games", 
			10, 
			Gdx.graphics.getHeight() - 240);
		descFont.getData().setScale(1.5f);
		batch.end();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
		game.widthScreen = width;
		game.heightScreen = height;
	}

	@Override
	public void dispose() {
		shapeRenderer.dispose();
		font.dispose();
		batch.dispose();
		stage.dispose();
	}
}
