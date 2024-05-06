package com.mygdx.quest.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
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
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.mygdx.quest.AnglersQuest;
import com.mygdx.quest.utils.Fish;
import com.mygdx.quest.utils.Fish.Rarity;

public class CommonFishingScreen extends ScreenAdapter{
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

	private Image background;
	private Stage stage;

	final AnglersQuest game;
	float barHeight;


	public CommonFishingScreen(final AnglersQuest game, Fish fishItem) {
		this.game = game;
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
			barHeight = 20;
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
		batch = new SpriteBatch();
	}

	@Override
	public void show() {
		System.out.println("COMMON FISHING SCREEN");

		Texture bgTexture = game.assets.get("assets/images/underwater.png");

        background = new Image(bgTexture);
        background.setFillParent(true);
		background.setPosition(game.widthScreen / 2 - bgTexture.getWidth() / 2, game.heightScreen / 2 - bgTexture.getHeight() / 2);

		stage.clear();
		stage.addActor(background);
	}

	@Override
	public void render(float delta) {
		basicHandleInput();
		basicUpdateBarMovement();
		basicPlayMovement();
		basicCheckFishBarContact();

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
		shapeRenderer.setColor(Color.BLACK);
		shapeRenderer.rect(fish.x, fish.y, fish.width, fish.height);

		shapeRenderer.end();

		basicRenderRemainingTime();
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
		float speed = 90; // Adjust this value as needed for the desired speed

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
	}

	private void basicPlayMovement() {

		float fishSpeed = 120;
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
			fishingTimer += Gdx.graphics.getDeltaTime(); // Increment fishing timer
			if (fishingTimer >= 10f) { // Check if contact duration is 12 seconds
				game.setScreen(game.gameScreen); // Exit the application
				fishingTimer = 0;
			}
		} else {
			// Reset timer if there's no contact
			fishingTimer = Math.max(0, fishingTimer - Gdx.graphics.getDeltaTime());
		}
	}

	// DISPLAYS REMAINING TIME UNTIL COMPLETION
	private void basicRenderRemainingTime() {
		float remainingTime = Math.max(0, 10f - fishingTimer); // Calculate remaining time
		String timeText = "Time: " + String.format("%.1f", remainingTime); // Format time as string

		// Draw time text on the screen
		batch.begin();
		font.draw(batch, timeText, 10, Gdx.graphics.getHeight() - 10);
		font.getData().setScale(5);
		batch.end();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void dispose() {
		shapeRenderer.dispose();
		font.dispose();
		batch.dispose();
		stage.dispose();
	}
}
