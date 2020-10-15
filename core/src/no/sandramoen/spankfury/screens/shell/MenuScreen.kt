package no.sandramoen.spankfury.screens.shell

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable
import com.badlogic.gdx.utils.Align
import no.sandramoen.spankfury.actors.*
import no.sandramoen.spankfury.screens.gameplay.LevelScreen
import no.sandramoen.spankfury.utils.BaseActor
import no.sandramoen.spankfury.utils.BaseGame
import no.sandramoen.spankfury.utils.BaseScreen
import no.sandramoen.spankfury.utils.GameUtils
import kotlin.math.abs

class MenuScreen : BaseScreen() {
    private val token = "MenuScreen.kt"
    private var time = 0f
    private var disableTime = 1f

    // foreground
    private lateinit var titleLabel: Label
    private lateinit var touchToStartLabel: Label
    private lateinit var madeByLabel: Label
    private lateinit var leftButtonImage: Image
    private lateinit var leftButtonLabel: Label
    private lateinit var rightButtonImage: Image
    private lateinit var rightButtonLabel: Label
    private lateinit var blackOverlay: Image

    // background
    private lateinit var background: Background
    private lateinit var player: Player
    private var playerHitFrequency = .25f
    private var playerHitTime = 1f
    private var spawnDifficulty = 2f
    private var easySpawnTimer = 0f
    private val easySpawnFrequency = MathUtils.random(2f, 5f)
    private var mediumSpawnTimer = 0f
    private val mediumSpawnFrequency = MathUtils.random(3f, 6f)
    private var swapSpawnTimer = 0f
    private val swapSpawnFrequency = MathUtils.random(10f, 13f)
    private var hardSpawnTimer = 0f
    private val hardSpawnFrequency = MathUtils.random(13f, 18f)

    override fun initialize() {
        // audio
        BaseGame.levelMusic1!!.play()
        BaseGame.levelMusic1!!.volume = BaseGame.musicVolume
        BaseGame.levelMusic1!!.isLooping = true

        // foreground
        val uiTable = Table()
        uiTable.setFillParent(true)

        titleLabel = Label("Spank Fury!", BaseGame.labelStyle)
        titleLabel.setFontScale(4f)
        titleLabel.color = Color.RED
        titleLabel.setAlignment(Align.center)

        touchToStartLabel = Label("Touch to Play!", BaseGame.labelStyle)
        touchToStartLabel.setAlignment(Align.center)
        touchToStartLabel.setFontScale(.8f)
        GameUtils.pulseLabel(touchToStartLabel)

        madeByLabel = Label("made by Sandra Moen 2020", BaseGame.labelStyle)
        madeByLabel.setFontScale(.5f)
        madeByLabel.setAlignment(Align.center)
        madeByLabel.color = Color.DARK_GRAY

        // buttons
        var buttonWidth = Gdx.graphics.width * .125f
        var buttonHeight = Gdx.graphics.height * .1f
        leftButtonImage = Image(BaseGame.textureAtlas!!.findRegion("arcade-button-unpressed"))
        leftButtonLabel = Label("Left attack", BaseGame.labelStyle)
        leftButtonLabel.setFontScale(.5f)
        leftButtonLabel.color = Color.GRAY
        val leftButtonTable = Table()
        leftButtonTable.add(leftButtonImage).row()
        leftButtonTable.add(leftButtonLabel)

        rightButtonImage = Image(BaseGame.textureAtlas!!.findRegion("arcade-button-unpressed"))
        rightButtonLabel = Label("Right attack", BaseGame.labelStyle)
        rightButtonLabel.setFontScale(.5f)
        rightButtonLabel.color = Color.GRAY
        val rightButtonTable = Table()
        rightButtonTable.add(rightButtonImage).row()
        rightButtonTable.add(rightButtonLabel)

        uiTable.add(titleLabel).expand().colspan(3).row()
        uiTable.add(leftButtonTable).width(buttonWidth).height(buttonHeight).bottom()
        uiTable.add(touchToStartLabel).width(Gdx.graphics.width * .33f)
        uiTable.add(rightButtonTable).width(buttonWidth).height(buttonHeight).bottom().row()
        uiTable.add(madeByLabel).bottom().colspan(3).padTop(Gdx.graphics.height * .2f).padBottom(Gdx.graphics.height * .01f)
        // uiTable.debug = true

        // background
        background = Background(mainStage)
        player = Player(0f, 0f, mainStage)

        // black transition overlay
        blackOverlay = Image(BaseGame.textureAtlas!!.findRegion("whitePixel"))
        blackOverlay.color = Color.BLACK
        blackOverlay.touchable = Touchable.childrenOnly
        blackOverlay.setSize(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        blackOverlay.addAction(Actions.fadeOut(1f))

        val stack = Stack()
        stack.setFillParent(true)
        stack.add(uiTable)
        stack.add(blackOverlay)
        uiStage.addActor(stack)
    }

    override fun update(dt: Float) {
        if (time < Float.MAX_VALUE)
            time += dt

        if (playerHitTime < playerHitFrequency)
            playerHitTime += dt
        spawnEnemies(dt)
        playPlayer()
    }


    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (time >= disableTime) fadeToLevelScreen()
        return false
    }

    override fun keyDown(keycode: Int): Boolean {
        if (keycode == Keys.BACK || keycode == Keys.ESCAPE) {
            blackOverlay.addAction(Actions.sequence(
                    Actions.fadeIn(1f),
                    Actions.run {
                        super.dispose()
                        Gdx.app.exit()
                    }
            ))
        }

        if (time >= disableTime) fadeToLevelScreen()
        return false
    }

    private fun spawnEnemies(dt: Float) {
        easySpawnTimer += dt
        if (easySpawnTimer >= easySpawnFrequency / spawnDifficulty
                && BaseActor.count(mainStage, EasyEnemy::class.java.canonicalName) <= 8
                && BaseActor.count(mainStage, EasyEnemy::class.java.canonicalName) < 5
        ) {
            EasyEnemy(0f, 0f, mainStage, player)
            easySpawnTimer = 0f
        }
        mediumSpawnTimer += dt
        if (mediumSpawnTimer >= mediumSpawnFrequency / spawnDifficulty
                && BaseActor.count(mainStage, MediumEnemy::class.java.canonicalName) <= 8
                && BaseActor.count(mainStage, MediumEnemy::class.java.canonicalName) < 4
        ) {
            MediumEnemy(0f, 0f, mainStage, player)
            mediumSpawnTimer = 0f
        }
        swapSpawnTimer += dt
        if (swapSpawnTimer >= swapSpawnFrequency / spawnDifficulty
                && BaseActor.count(mainStage, SwapEnemy::class.java.canonicalName) <= 8
                && BaseActor.count(mainStage, SwapEnemy::class.java.canonicalName) < 4
        ) {
            SwapEnemy(0f, 0f, mainStage, player)
            swapSpawnTimer = 0f
        }
        hardSpawnTimer += dt
        if (hardSpawnTimer >= hardSpawnFrequency / spawnDifficulty
                && BaseActor.count(mainStage, HardEnemy::class.java.canonicalName) <= 8
                && BaseActor.count(mainStage, HardEnemy::class.java.canonicalName) < 4
        ) {
            HardEnemy(0f, 0f, mainStage, player)
            hardSpawnTimer = 0f
        }
    }

    private fun playPlayer() {
        // detect if player may hit someone
        for (baseActor: BaseActor in BaseActor.getList(mainStage, Enemy::class.java.canonicalName)) {
            val enemy = baseActor as Enemy

            if (enemy.health <= 0) // dead enemies should not be counted
                continue

            // measure the distance from player
            var distance = enemy.x - player.x
            val inRange = enemy.width + player.width > abs(distance)

            // detect enemies in range
            if (inRange && playerHitTime >= playerHitFrequency) {
                enemy.struck(enableSound = false)
                player.hit(distance)
                background.act(player)
                playerHitTime = 0f
                if (distance < 0) pressButton(leftButtonImage)
                else pressButton(rightButtonImage)
                break
            }
        }
    }

    private fun pressButton(button: Image) {
        button.drawable = SpriteDrawable(Sprite(BaseGame.textureAtlas!!.findRegion("arcade-button-pressed")))
        button.addAction(Actions.sequence(
                Actions.delay(playerHitFrequency),
                Actions.run {
                    button.drawable = SpriteDrawable(Sprite(BaseGame.textureAtlas!!.findRegion("arcade-button-unpressed")))
                }
        ))
    }

    private fun fadeToLevelScreen() {
        BaseGame.levelMusic1!!.stop()
        blackOverlay.addAction(Actions.sequence(
                Actions.fadeIn(1f),
                Actions.run { BaseGame.setActiveScreen(LevelScreen()) }
        ))
    }
}
