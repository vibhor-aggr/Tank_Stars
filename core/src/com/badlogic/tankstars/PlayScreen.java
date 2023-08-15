package com.badlogic.tankstars;

//import java.util.Iterator;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
//import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
//import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
//import com.badlogic.gdx.graphics.g2d.Sprite;
//import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
//import com.badlogic.gdx.math.Vector3;
//import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
//import com.badlogic.tankstars.MyTextInputListener;
//import com.badlogic.tankstars.TankStars;
//import com.badlogic.tankstars.LoadingScreen;
//import com.badlogic.tankstars.MainMenuScreen;
//import com.badlogic.tankstars.TankSelectScreen;
import java.io.Serializable;

// Assumptions:
// 1. plain is assumeed to be straight line
// 2. bullet is assumed to be spherical with rotated view same as original view
public class PlayScreen implements Screen, Serializable {
    //transient private final TankStars ts;
    public transient TankStars ts;
    private transient ShapeRenderer shapeRenderer;
    public transient boolean isRestore = false;

    long lastGamePausedTime = 0;
    long lastBulletTime = 0;
    long lastCongratsTime = 0;
    long lastAirDropTime = 0;
    long lastP1BUpgradeTime = 0;
    long lastP2BUpgradeTime = 0;

    private Rectangle plain;

    private int p1TankType;
    private int p1BType;
    private Rectangle p1Tank;
    private Rectangle p1TankN;
    private Rectangle p1Bullet;
    private Rectangle p1Health;
    private Rectangle p1Fuel;
    private Rectangle p1Power;
    private Rectangle p1Angle;
    private float p1HealthValue;
    private float p1FuelValue;
    private float p1BAngle;
    private float p1BVelocity;

    private int p2TankType;
    private int p2BType;
    private Rectangle p2Tank;
    private Rectangle p2TankN;
    private Rectangle p2Bullet;
    private Rectangle p2Health;
    private Rectangle p2Fuel;
    private Rectangle p2Power;
    private Rectangle p2Angle;
    private float p2HealthValue;
    private float p2FuelValue;
    private float p2BAngle;
    private float p2BVelocity;

    boolean p1Active;

    //Array<Rectangle> airDrops;

    float plainH = 150;
    float tH = 100;
    float tW = 150;
    float bNH = 0;
    float bNW = 0;
    //float bNH = 25;
    //float bNW = 100;
    float bH = 25;
    float bW = 25;
    float p1TCx = 50 + tW/2;
    float p1TCy = plainH + tH/2;
    float p2TCx;
    float p2TCy = plainH + tH/2;

    // temporary variables related to shoting
    boolean inShotMode = false;
    boolean isHit = false;
    int currBDispCnt = 0;
    float bxstep = 0.0f;
    float bystep = 0.0f;
    boolean gameOver = false;
    boolean isPaused;
    boolean p1BulletUpgrade;
    boolean p2BulletUpgrade;

    public PlayScreen(final TankStars ts) {
        this.ts = ts;
        this.shapeRenderer = new ShapeRenderer();
        initValues();
    }

    @Override
    public void render(float delta) {
        // clear the screen with a dark blue color. The
        // arguments to clear are the red, green
        // blue and alpha component in the range [0,1]
        // of the color to be used to clear the screen.
        ScreenUtils.clear(0, 0, 0.2f, 1);

        // tell the camera to update its matrices.
        ts.camera.update();

        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera.
        ts.batch.setProjectionMatrix(ts.camera.combined);

        //todo: show a picture highlighting keys mapping for the game

        // begin a new batch and draw the screen
        ts.batch.begin();
        ts.batch.draw(ts.backgroundTexture, 0, 0, ts.camera.viewportWidth, ts.camera.viewportHeight);

        if (isPaused) {
          //todo: may be have a "Game Paused !!!" image instead of text
          // have a blinking affect for this text displayed
          //if (TimeUtils.nanoTime() - lastGamePausedTime > 1000000000) {
          //ts.font.draw(ts.batch, "!!! Game Paused !!!", ts.camera.viewportWidth/2-20, ts.camera.viewportHeight-50);
          Texture pauseImage = ts.assets.get("img/PSPause.png", Texture.class);
          ts.batch.draw(pauseImage, ts.camera.viewportWidth/2-200, ts.camera.viewportHeight-100, 400, 100);
          //  lastGamePausedTime = TimeUtils.nanoTime();
          //}
        }

        // display menu
        Texture menuImage = ts.assets.get("img/PSMenu.png", Texture.class);
        ts.batch.draw(menuImage, ts.camera.viewportWidth-300, ts.camera.viewportHeight-150, 300, 150);

        if (p1BulletUpgrade) {
          if (TimeUtils.nanoTime() - lastP1BUpgradeTime < 50*1000000000) {
            ts.font.draw(ts.batch, "!!! Player 1 Weapon Upgraded !!!", ts.camera.viewportWidth/2-20, ts.camera.viewportHeight-50);
            p1BulletUpgrade = false;
          }
        }
        if (p2BulletUpgrade) {
          if (TimeUtils.nanoTime() - lastP2BUpgradeTime < 50*1000000000) {
            ts.font.draw(ts.batch, "!!! Player 2 Weapon Upgraded !!!", ts.camera.viewportWidth/2-20, ts.camera.viewportHeight-75);
            p2BulletUpgrade = false;
          }
        }

        Texture p1TankImage = ts.assets.get("img/PSP1Tank1.png", Texture.class);;
        if (p1TankType == 1) {
          p1TankImage = ts.assets.get("img/PSP1Tank1.png", Texture.class);
        } else if (p1TankType == 2) {
          p1TankImage = ts.assets.get("img/PSP1Tank2.png", Texture.class);
        } else if (p1TankType == 3) {
          p1TankImage = ts.assets.get("img/PSP1Tank3.png", Texture.class);
        }
        Texture p2TankImage = ts.assets.get("img/PSP2Tank1.png", Texture.class);
        if (p2TankType == 1) {
          p2TankImage = ts.assets.get("img/PSP2Tank1.png", Texture.class);
        } else if (p2TankType == 2) {
          p2TankImage = ts.assets.get("img/PSP2Tank2.png", Texture.class);
        } else if (p2TankType == 3) {
          p2TankImage = ts.assets.get("img/PSP2Tank3.png", Texture.class);
        }

        if (TimeUtils.nanoTime() - lastAirDropTime > 50*1000000000) {
          spawnAirDrop();
        }

        /*
        Texture adImage = ts.assets.get("img/PSAirDrop.png", Texture.class);
        for (Rectangle airDrop : airDrops) {
          ts.batch.draw(adImage, airDrop.x, airDrop.y);
        }
        */

        ts.font.draw(ts.batch, "Health: ", 50, 600+p1Health.height/2);
        ts.font.draw(ts.batch, "Health: ", ts.camera.viewportWidth-300, 600+p2Health.height/2);
        if (p1Active) {
          ts.font.draw(ts.batch, "Fuel: ", 50, 500+p1Fuel.height/2);
          ts.font.draw(ts.batch, "Power: ", 50, 400+p1Power.height/2);
          ts.font.draw(ts.batch, "Angle: ", 50, 300+p1Angle.height/2);
        } else {
          ts.font.draw(ts.batch, "Fuel: ", ts.camera.viewportWidth-300, 500+p2Fuel.height/2);
          ts.font.draw(ts.batch, "Power: ", ts.camera.viewportWidth-300, 400+p2Power.height/2);
          ts.font.draw(ts.batch, "Angle: ", ts.camera.viewportWidth-300, 300+p2Angle.height/2);
        }
        ts.batch.draw(p1TankImage, p1Tank.x, p1Tank.y, p1Tank.width, p1Tank.height);
        /*
        Texture tN1Image = ts.assets.get("img/PSP1TankNozzle.JPG", Texture.class);
        Sprite spr = new Sprite(tN1Image);
        spr.setSize(p1TankN.width, p1TankN.height);
        spr.setOrigin(p1TankN.x, p1TankN.y+p1TankN.height/2);
        //spr.setRotation(p1BAngle/360f);
        spr.setRotation((360f - p1BAngle)/360f);
        spr.draw(ts.batch);
        //ts.batch.draw(tN1Image, p1TankN.x, p1TankN.y, p1TankN.width, p1TankN.height);
        spr.setRotation(0);
        spr.setOrigin(0, 0);
        */

        ts.batch.draw(p2TankImage, p2Tank.x, p2Tank.y, p2Tank.width, p2Tank.height);
        /*
        Texture tN2Image = ts.assets.get("img/PSP2TankNozzle.JPG", Texture.class);
        Sprite spr2 = new Sprite(tN2Image);
        spr2.setSize(p2TankN.width, p2TankN.height);
        spr2.setOrigin(p2TankN.x+p2TankN.width, p2TankN.y+p2TankN.height/2);
        //ts.setRotation((180f - p2BAngle)/360f);
        spr2.setRotation(p2BAngle/360f);
        spr2.draw(ts.batch);
        //ts.batch.draw(tN2Image, p2TankN.x, p2TankN.y, p2TankN.width, p2TankN.height);
        spr2.setRotation(0);
        spr2.setOrigin(0, 0);
        */

        if (inShotMode) {
          showBullet();
          if (!inShotMode) {
            isHit = false;
            // make other player active
            if (p1HealthValue > 0 && p2HealthValue > 0) {
              p1Active = !p1Active;
              p1FuelValue = 200;
              p2FuelValue = 200;
            }
          }
        }

        // flag Game Over/Won message if health reduces below 0
        if (p1HealthValue <= 0) {  // player p2 won, game is over
          gameOver = true;
          //if (TimeUtils.nanoTime() - lastCongratsTime > 1000000000) {
            //ts.font.draw(ts.batch, "!!! Congratulations Player 2 - You have WON !!!", ts.camera.viewportWidth/2-20, ts.camera.viewportHeight-50);
            Texture wonImage = ts.assets.get("img/PSP2Won.png", Texture.class);
            ts.batch.draw(wonImage, ts.camera.viewportWidth/2-300, ts.camera.viewportHeight-100, 600, 100);
          //  lastCongratsTime = TimeUtils.nanoTime();
          //}
          /*
          if (TimeUtils.nanoTime() - lastBulletTime > 20*1000000000) {
            ts.setScreen(ts.mainMenuScreen);
          }
          */
        } else if (p2HealthValue <= 0) {  // player p1 won, game is over
          gameOver = true;
          //if (TimeUtils.nanoTime() - lastCongratsTime > 1000000000) {
            //ts.font.draw(ts.batch, "!!! Congratulations Player 1 - You have WON !!!", ts.camera.viewportWidth/2-20, ts.camera.viewportHeight-50);
            Texture wonImage = ts.assets.get("img/PSP1Won.png", Texture.class);
            ts.batch.draw(wonImage, ts.camera.viewportWidth/2-300, ts.camera.viewportHeight-100, 600, 100);
          //  lastCongratsTime = TimeUtils.nanoTime();
          //}
          /*
          if (TimeUtils.nanoTime() - lastBulletTime > 20*1000000000) {
            ts.setScreen(ts.mainMenuScreen);
          }
          */
        }
        ts.batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        // create plan surface for tanks movement
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rect(plain.x, plain.y, plain.width, plain.height);
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rect(p1Health.x, p1Health.y, p1Health.width, p1Health.height);
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rect(p2Health.x, p2Health.y, p2Health.width, p2Health.height);
        if (p1Active) {
          shapeRenderer.setColor(Color.ORANGE);
		  //shapeRenderer.rect(p1Fuel.x, p1Fuel.y, p1Fuel.width, p1Fuel.height);
          shapeRenderer.rect(p1Fuel.x, p1Fuel.y, p1FuelValue, p1Fuel.height);
          shapeRenderer.setColor(Color.BLUE);
          //shapeRenderer.rect(p1Power.x, p1Power.y, p1Power.width, p1Power.height);
          shapeRenderer.rect(p1Power.x, p1Power.y, p1BVelocity, p1Power.height);
          shapeRenderer.rect(p1Angle.x, p1Angle.y, p1BAngle, p1Angle.height);
        } else {
          shapeRenderer.setColor(Color.ORANGE);
          //shapeRenderer.rect(p2Fuel.x, p2Fuel.y, p2Fuel.width, p2Fuel.height);
          shapeRenderer.rect(p2Fuel.x, p2Fuel.y, p2FuelValue, p2Fuel.height);
          shapeRenderer.setColor(Color.BLUE);
          //shapeRenderer.rect(p2Power.x, p2Power.y, p2Power.width, p2Power.height);
          shapeRenderer.rect(p2Power.x, p2Power.y, p2BVelocity, p2Power.height);
          shapeRenderer.rect(p2Angle.x, p2Angle.y, p2BAngle, p2Angle.height);
        }
        shapeRenderer.end();

        // process user input
        if (gameOver) {
          // no key to be processed except Exit/New Game key if game is already over
          if (Gdx.input.isKeyPressed(Keys.E)) {
            ts.setScreen(ts.mainMenuScreen);
          } else if (Gdx.input.isKeyPressed(Keys.N)) {  // start new game
             show();
          }
        } else if (isPaused) {
          // process no keys if game is paused except for "R" (resume key)
          if (Gdx.input.isKeyPressed(Keys.R)) {
            isPaused = false;
          }
        } else if (Gdx.input.isKeyPressed(Keys.P)) {
          isPaused = true;
        } else if (inShotMode) {
          // no keys are honored in shot mode except for "Paused" and "Resume" keys
        }
        else if (Gdx.input.isKeyPressed(Keys.LEFT)) {
          // play the tank move sound
          Sound moveSound = ts.assets.get("sound/PSTankMove.wav", Sound.class);
          moveSound.play();
          if (p1Active && p1FuelValue > 0) {
            float orgx = p1Tank.x;
            p1Tank.x -= 200 * Gdx.graphics.getDeltaTime();
            if (p1Tank.x < 0) p1Tank.x = 0.0f;
            float deltax = Math.abs(p1Tank.x - orgx);

            p1TankN.x -= deltax;
            p1FuelValue -= 1;
            if (p1FuelValue < 0) p1FuelValue = 0;
            p1Fuel.width = p1FuelValue;
          } else if (!p1Active && p2FuelValue > 0) {
            float orgx = p2Tank.x;
            p2Tank.x -= 200 * Gdx.graphics.getDeltaTime();
            if (p1Tank.x + tW + bNW > p2Tank.x - bNW) {
              p2Tank.x = p1Tank.x + tW + 2*bNW;
            }
            float deltax = Math.abs(p2Tank.x - orgx);
            p2TankN.x -= deltax;
            p2FuelValue -= 1;
            if (p2FuelValue < 0) p2FuelValue = 0;
            p2Fuel.width = p2FuelValue;
          }
        } else if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
          // play the tank move sound
          Sound moveSound = ts.assets.get("sound/PSTankMove.wav", Sound.class);
          moveSound.play();
          if (p1Active && p1FuelValue > 0) {
            float orgx = p1Tank.x;
            p1Tank.x += 200 * Gdx.graphics.getDeltaTime();
            if (p1Tank.x + tW + bNW > p2Tank.x - bNW) {
              p1Tank.x = p2Tank.x - tW - 2*bNW;
            }
            float deltax = Math.abs(p1Tank.x - orgx);
            p1TankN.x += deltax;
            p1FuelValue -= 1;
            if (p1FuelValue < 0) p1FuelValue = 0;
            p1Fuel.width = p1FuelValue;
          } else if (!p1Active && p2FuelValue > 0) {
            float orgx = p2Tank.x;
            p2Tank.x += 200 * Gdx.graphics.getDeltaTime();
            if (p2Tank.x + tW > ts.camera.viewportWidth) {
              p2Tank.x = ts.camera.viewportWidth - tW;
            }
            float deltax = Math.abs(p2Tank.x - orgx);
            p2TankN.x += deltax;
            p2FuelValue -= 1;
            if (p2FuelValue < 0) p2FuelValue = 0;
            p2Fuel.width = p2FuelValue;
          }
        } else if (Gdx.input.isKeyPressed(Keys.UP)) {
          if (p1Active) {
            p1BAngle += 200 * Gdx.graphics.getDeltaTime();
            if (p1BAngle > 90) p1BAngle = 90;
          } else {
            p2BAngle += 200 * Gdx.graphics.getDeltaTime();
            if (p2BAngle > 90) p2BAngle = 90;
          }
        } else if (Gdx.input.isKeyPressed(Keys.DOWN)) {
          if (p1Active) {
            p1BAngle -= 200 * Gdx.graphics.getDeltaTime();
            if (p1BAngle < 0) p1BAngle = 0;
          } else {
            p2BAngle -= 200 * Gdx.graphics.getDeltaTime();
            if (p2BAngle < 0) p2BAngle = 0;
          }
        } else if (Gdx.input.isKeyPressed(Keys.E)) {
          ts.setScreen(ts.mainMenuScreen);
        } else if (Gdx.input.isKeyPressed(Keys.S)) {
          ts.saveGame("tankStars.ts");
          /*
          //save current state
          ts.setInputText("");
          MyTextInputListener listener = new MyTextInputListener(ts);
          Gdx.input.getTextInput(listener, "Specify file to Save Game", "", "");
          //Gdx.input.getTextInput(listener, "Specify Saved Game file to resume", "Initial Textfield Value", "Hint Value");
          if (!ts.getInputText().isEmpty()) {
            ts.saveGame(ts.getInputText());
          }
          */
        } else if (Gdx.input.isKeyPressed(Keys.I /*Keys.PLUS*/)) {
          // increase velocity
          if (p1Active) {
            p1BVelocity += 200 * Gdx.graphics.getDeltaTime();
            if (p1BVelocity > 200) p1BVelocity = 200;
            p1Power.width = p1BVelocity;
          } else {
            p2BVelocity += 200 * Gdx.graphics.getDeltaTime();
            if (p2BVelocity > 200) p2BVelocity = 200;
            p2Power.width = p2BVelocity;
          }
        } else if (Gdx.input.isKeyPressed(Keys.D /*Keys.MINUS*/)) {
          // decrease velocity
          if (p1Active) {
            p1BVelocity -= 200 * Gdx.graphics.getDeltaTime();
            if (p1BVelocity < 0) p1BVelocity = 0;
            p1Power.width = p1BVelocity;
          } else {
            p2BVelocity -= 200 * Gdx.graphics.getDeltaTime();
            if (p2BVelocity < 0) p2BVelocity = 0;
            p2Power.width = p2BVelocity;
          }
        } else if (Gdx.input.isKeyPressed(Keys.F)) {  // bullet shot
          inShotMode = true;
          if (p1Active && p1HealthValue > 0 || !p1Active && p2HealthValue > 0) {
            inShotMode = true;
            currBDispCnt = 0;
            //lastBulletTime = TimeUtils.nanoTime();

            float shotxrange = p1Active ? p1BVelocity * MathUtils.cos(p1BAngle/360f) : p2BVelocity * MathUtils.cos(p2BAngle/360f);
            float shotyrange = p1Active ? p1BVelocity * MathUtils.sin(p1BAngle/360f) : p2BVelocity * MathUtils.sin(p2BAngle/360f);
            bxstep = 15*shotxrange/20;
            bystep = 15*shotyrange/10;
          }
        } else if (Gdx.input.isKeyPressed(Keys.N)) {  // start new game
          show();
        }

        // move the airDrops, remove any that are beneath the bottom edge of
        // the plain or that hit the tank. In the later case we upgrade
        // its player bullet and add a sound effect.
        /*
        Iterator<Rectangle> iter = airDrops.iterator();
        while (iter.hasNext()) {
          Rectangle airDrop = iter.next();
          airDrop.y -= 200 * Gdx.graphics.getDeltaTime();
          if (airDrop.y + 25 < plain.y)
            iter.remove();
          if (airDrop.overlaps(p1Tank) || airDrop.overlaps(p1TankN)) {
            p1BType++;
            if (p1BType > 3) p1BType = 3;
            //airDropSound.play();
            iter.remove();
            p1BulletUpgrade = true;
            lastP1BUpgradeTime = TimeUtils.nanoTime();
          } else if (airDrop.overlaps(p2Tank) || airDrop.overlaps(p2TankN)) {
            p2BType++;
            if (p2BType > 3) p2BType = 3;
            //airDropSound.play();
            iter.remove();
            p2BulletUpgrade = true;
            lastP2BUpgradeTime = TimeUtils.nanoTime();
          }
        }
        */
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
        Gdx.graphics.setTitle("Tank Stars Game");
        if (this.shapeRenderer == null) {
          this.shapeRenderer = new ShapeRenderer();
        }
        shapeRenderer.setProjectionMatrix(ts.camera.combined);
        if (!isRestore) {
          initValues();
        }
        isRestore = false;
       
        // start the playback of the background music
        // when the screen is shown
        //Music tsMusic = ts.assets.get("sound/PSMusic.mp3", Music.class);
        //tsMusic.setLooping(true);
        //tsMusic.play();
        if (p1TankType == -1) {
          p1TankType = ts.getPlayer1Tank();
        } else {
          ts.setPlayer1Tank(p1TankType);
        }
        if (p2TankType == -1) {
          p2TankType = ts.getPlayer2Tank();
        } else {
          ts.setPlayer2Tank(p2TankType);
        }

        lastGamePausedTime = 0;
        lastBulletTime = 0;
        lastCongratsTime = 0;
        lastAirDropTime = 0;
        lastP1BUpgradeTime = 0;
        lastP2BUpgradeTime = 0;

        // reset temporary variables
        inShotMode = false;
        isHit = false;
        currBDispCnt = 0;
        bxstep = 0.0f;
        bystep = 0.0f;
        gameOver = false;
        p1BulletUpgrade = false;
        p2BulletUpgrade = false;
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }

    private void showBullet() {
      // select player bullet
      Texture p1BImage = ts.assets.get("img/PSP1Bullet1.png", Texture.class);
      if (p1BType == 1) {
        p1BImage = ts.assets.get("img/PSP1Bullet1.png", Texture.class);
      } else if (p1BType == 2) {
        p1BImage = ts.assets.get("img/PSP1Bullet2.png", Texture.class);
      } else if (p1BType == 3) {
        p1BImage = ts.assets.get("img/PSP1Bullet3.png", Texture.class);
      }
      Texture p2BImage = ts.assets.get("img/PSP2Bullet1.png", Texture.class);
      if (p2BType == 1) {
        p2BImage = ts.assets.get("img/PSP2Bullet1.png", Texture.class);
      } else if (p2BType == 2) {
        p2BImage = ts.assets.get("img/PSP2Bullet2.png", Texture.class);
      } else if (p2BType == 3) {
        p2BImage = ts.assets.get("img/PSP2Bullet3.png", Texture.class);
      }

      if (currBDispCnt == 0) {
        // play the shot sound
        Sound shotSound = ts.assets.get("sound/PSShot.wav", Sound.class);
        shotSound.play();
        // set initial x & y co-ordinates of the bullet
        if (p1Active) {
          p1Bullet.x = p1Tank.x + tW + bNW * MathUtils.cos(p1BAngle/360f); 
          p1Bullet.y = p1Tank.y + tH + bNW * MathUtils.sin(p1BAngle/360f);
        } else {
          p2Bullet.x = p2Tank.x - bNW * MathUtils.cos(p2BAngle/360f); 
          p2Bullet.y = p2Tank.y + tH + bNW * MathUtils.sin(p2BAngle/360f);
        }
        currBDispCnt++;
        lastBulletTime = TimeUtils.nanoTime();
      } else if (!isPaused) {
        if (TimeUtils.nanoTime() - lastBulletTime > 100000000) {
          if (isHit) {  // there is a hit already, no need to proceed ahead
            inShotMode = false;
            return;
          }
          // increment x and y coordinates
          currBDispCnt++;
          if (p1Active) {
            p1Bullet.x += bxstep;
            p1Bullet.y = currBDispCnt > 10 ? p1Bullet.y - bystep : p1Bullet.y + bystep;
          } else {
            p2Bullet.x -= bxstep;
            p2Bullet.y = currBDispCnt > 10 ? p2Bullet.y - bystep : p2Bullet.y + bystep;
          }
          lastBulletTime = TimeUtils.nanoTime();
        }
      }
      if (p1Active) {
        ts.batch.draw(p1BImage, p1Bullet.x, p1Bullet.y, p1Bullet.width, p1Bullet.height);
      } else {
        ts.batch.draw(p2BImage, p2Bullet.x, p2Bullet.y, p2Bullet.width, p2Bullet.height);
      }

      // check if it hits the target
      // if yes,
      // - reduce health of the target, flag Game Over/Won message if health reduces below 0
      // - move the target in the direction of the hit
      // - play the hit sound, make other player active
      // - inShotMode is over, and bullet is no longer visible
      // check if it hits the ground, if yes, then the last 2 things are to be done
      if (p1Active) {
        if (p1Bullet.overlaps(p2Tank) || p1Bullet.overlaps(p2TankN)) {  // target is hit
          isHit = true;
          //todo: reduce target health by variable amount
          p2HealthValue -= 10;
          if (p2HealthValue < 0) p2HealthValue = 0;
          p2Health.width = p2HealthValue;

          // move the target in the direction of the hit
          float orgx = p2Tank.x;
          p2Tank.x += 200 * Gdx.graphics.getDeltaTime();
          if (p2Tank.x + tW > ts.camera.viewportWidth) {
            p2Tank.x = ts.camera.viewportWidth - tW;
          }
          float deltax = Math.abs(p2Tank.x - orgx);
          p2TankN.x += deltax;
        } else if (p1Bullet.overlaps(p1Tank) || p1Bullet.overlaps(p1TankN)) {  // self hit
          isHit = true;
          //todo: reduce self health by variable amount
          p1HealthValue -= 10;
          if (p1HealthValue < 0) p1HealthValue = 0;
          p1Health.width = p1HealthValue;
        } else if (p1Bullet.overlaps(plain)) { // ground hit
          isHit = true;
        } else if (p1Bullet.x > ts.camera.viewportWidth) { // outside screen area
          isHit = true;
        }
      } else {
        if (p2Bullet.overlaps(p1Tank) || p2Bullet.overlaps(p1TankN)) {  // target is hit
          isHit = true;
          //todo: reduce target health by variable amount
          p1HealthValue -= 10;
          if (p1HealthValue < 0) p1HealthValue = 0;
          p1Health.width = p1HealthValue;

          // move the target in the direction of the hit
          float orgx = p1Tank.x;
          p1Tank.x -= 200 * Gdx.graphics.getDeltaTime();
          if (p1Tank.x < 0) p1Tank.x = 0;
          float deltax = Math.abs(p1Tank.x - orgx);
          p1TankN.x -= deltax;
        } else if (p2Bullet.overlaps(p2Tank) || p2Bullet.overlaps(p2TankN)) {  // self hit
          isHit = true;
          //todo: reduce self health by variable amount
          p2HealthValue -= 10;
          if (p2HealthValue < 0) p2HealthValue = 0;
          p2Health.width = p2HealthValue;
        } else if (p2Bullet.overlaps(plain)) { // ground hit
          isHit = true;
        } else if (p2Bullet.x < 0) { // outside screen area
          isHit = true;
        }
      }
      if (isHit) {
        Sound hitSound = ts.assets.get("sound/PSHit.wav", Sound.class);
        hitSound.play();
      }
    }

    private void initValues() {
        plain = new Rectangle();
        plain.x = 0; plain.y = 0; plain.width = ts.camera.viewportWidth; plain.height = plainH;

        p1TankType = -1;
        p1BType = 1;

        p1Tank = new Rectangle();
        p1Tank.x = p1TCx - tW/2;
        p1Tank.y = p1TCy - tH/2;
        p1Tank.width = tW;
        p1Tank.height = tH;

        p1TankN = new Rectangle();
        p1TankN.x = p1Tank.x + tW;
        p1TankN.y = p1Tank.y + tH - bNH/2;
        p1TankN.width = bNW;
        p1TankN.height = bNH;

        p1Bullet = new Rectangle();
        p1Bullet.x = p1TankN.x + bNW;
        p1Bullet.y = p1TankN.y + bNH/2 - bH/2;
        p1Bullet.width = bW;
        p1Bullet.height = bH;

        p1Health = new Rectangle();
        p1Fuel = new Rectangle();
        p1Power = new Rectangle();
        p1Angle = new Rectangle();
        p1HealthValue = 200; p1FuelValue = 200;
        p1BAngle = 0; p1BVelocity = 50;
        p1Health.x = 100; p1Health.y = 600; p1Health.width = p1HealthValue; p1Health.height = 50;
        p1Fuel.x = 100; p1Fuel.y = 500; p1Fuel.width = p1FuelValue; p1Fuel.height = 50;
        p1Power.x = 100; p1Power.y = 400; p1Power.width = p1BVelocity; p1Power.height = 50;
        p1Angle.x = 100; p1Angle.y = 300; p1Angle.width = p1BAngle; p1Angle.height = 50;

        p2TCx = ts.camera.viewportWidth - tW/2 - 50;
        p2TankType = -1;
        p2BType = 1;

        p2Tank = new Rectangle();
        p2Tank.x = p2TCx - tW/2;
        p2Tank.y = p2TCy - tH/2;
        p2Tank.width = tW;
        p2Tank.height = tH;

        p2TankN = new Rectangle();
        p2TankN.x = p2Tank.x - bNW;
        p2TankN.y = p2Tank.y + tH - bNH/2;
        p2TankN.width = bNW;
        p2TankN.height = bNH;

        p2Bullet = new Rectangle();
        p2Bullet.x = p2TankN.x - bW;
        p2Bullet.y = p2TankN.y + bNH/2 - bH/2;
        p2Bullet.width = bW;
        p2Bullet.height = bH;

        p2Health = new Rectangle();
        p2Fuel = new Rectangle();
        p2Power = new Rectangle();
        p2Angle = new Rectangle();
        p2HealthValue = 200; p2FuelValue = 200;
        p2BAngle = 0; p2BVelocity = 50;
        p2Health.x = ts.camera.viewportWidth - 250; p2Health.y = 600; p2Health.width = p2HealthValue; p2Health.height = 50;
        p2Fuel.x = ts.camera.viewportWidth - 250; p2Fuel.y = 500; p2Fuel.width = p2FuelValue; p2Fuel.height = 50;
        p2Power.x = ts.camera.viewportWidth - 250; p2Power.y = 400; p2Power.width = p2BVelocity; p2Power.height = 50;
        p2Angle.x =  ts.camera.viewportWidth - 250; p2Angle.y = 300; p2Angle.width = p2BAngle; p2Angle.height = 50;

        p1Active = true;
        isPaused = false;

        // create the airdrops array and spawn the first airdrop
        //airDrops = new Array<Rectangle>();
        //spawnAirDrop();
    }
    private void spawnAirDrop()  {
        return;
        // airdrop for weapon upgrade
        /*
        Rectangle airDrop = new Rectangle();
        airDrop.x = MathUtils.random(0, ts.camera.viewportWidth - 64);
        airDrop.y = ts.camera.viewportHeight - 50;
        airDrop.width = 50;
        airDrop.height = 50;
        airDrops.add(airDrop);
        lastAirDropTime = TimeUtils.nanoTime();
        */
    }
}
