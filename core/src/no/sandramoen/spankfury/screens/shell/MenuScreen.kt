package no.sandramoen.spankfury.screens.shell

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Scaling
import no.sandramoen.spankfury.actors.*
import no.sandramoen.spankfury.utils.*
import kotlin.math.abs

class MenuScreen : BaseScreen() {
    private val token = "MenuScreen.kt"
    private var time = 0f
    private var disableTime = 1f
    private var pressOverlay = true

    // foreground - title
    private lateinit var titleTable: Table
    private lateinit var titleTitleLabel: Label
    private lateinit var titleTouchToStartLabel: Label
    private lateinit var titleMadeByLabel: Label
    private lateinit var titleLeftButtonImage: Image
    private lateinit var titleLeftButtonLabel: Label
    private lateinit var titleRightButtonImage: Image
    private lateinit var titleRightButtonLabel: Label
    private lateinit var screenTransition: ScreenTransition

    // foreground - menu
    private lateinit var menuTable: Table
    private lateinit var menuTitleLabel: Label
    private lateinit var startButton: TextButton
    private lateinit var optionsButton: TextButton
    private lateinit var exitButton: TextButton

    // foreground - options
    private lateinit var optionsTable: Table
    private lateinit var optionsLabel: Label
    private lateinit var optionsMusicSlider: Slider
    private lateinit var optionsSoundSlider: Slider
    private lateinit var optionsVibrationCheckBox: CheckBox
    private lateinit var optionsBackButton: TextButton

    // background
    private lateinit var background: Background
    private lateinit var player: Player
    private var playerHitFrequency = .25f
    private var playerHitTime = 1f
    private var spawnDifficulty = .8f
    private var easySpawnTimer = 0f
    private val easySpawnFrequency = 3f
    private var mediumSpawnTimer = 0f
    private val mediumSpawnFrequency = 5f
    private var swapSpawnTimer = 0f
    private val swapSpawnFrequency = 12f
    private var hardSpawnTimer = 0f
    private val hardSpawnFrequency = 15f

    override fun initialize() {
        // audio
        BaseGame.levelMusic1!!.play()
        BaseGame.levelMusic1!!.volume = BaseGame.musicVolume
        BaseGame.levelMusic1!!.isLooping = true

        // foreground - Title ---------------------------------------------------------------
        titleTitleLabel = Label("Spank Fury!", BaseGame.labelStyle)
        titleTitleLabel.setFontScale(4f)
        titleTitleLabel.color = Color.RED
        titleTitleLabel.setAlignment(Align.center)

        titleTouchToStartLabel = Label("Touch to Play!", BaseGame.labelStyle)
        titleTouchToStartLabel.setAlignment(Align.center)
        titleTouchToStartLabel.setFontScale(.8f)
        GameUtils.pulseLabel(titleTouchToStartLabel)

        titleMadeByLabel = Label("made by Sandra Moen 2020", BaseGame.labelStyle)
        titleMadeByLabel.setFontScale(.5f)
        titleMadeByLabel.setAlignment(Align.center)
        titleMadeByLabel.color = Color.DARK_GRAY

        // arcade buttons
        var arcadeButtonWidth = Gdx.graphics.width * .125f
        var arcadeButtonHeight = Gdx.graphics.height * .1f

        titleLeftButtonImage = Image(BaseGame.textureAtlas!!.findRegion("arcade-button-unpressed"))
        titleLeftButtonLabel = Label("Left attack", BaseGame.labelStyle)
        titleLeftButtonLabel.setFontScale(.5f)
        titleLeftButtonLabel.color = Color.GRAY
        val leftButtonTable = Table()
        leftButtonTable.add(titleLeftButtonImage).row()
        leftButtonTable.add(titleLeftButtonLabel)

        titleRightButtonImage = Image(BaseGame.textureAtlas!!.findRegion("arcade-button-unpressed"))
        titleRightButtonLabel = Label("Right attack", BaseGame.labelStyle)
        titleRightButtonLabel.setFontScale(.5f)
        titleRightButtonLabel.color = Color.GRAY
        val rightButtonTable = Table()
        rightButtonTable.add(titleRightButtonImage).row()
        rightButtonTable.add(titleRightButtonLabel)

        titleTable = Table()
        titleTable.setFillParent(true)
        titleTable.add(titleTitleLabel).expand().colspan(3).row()
        titleTable.add(leftButtonTable).width(arcadeButtonWidth).height(arcadeButtonHeight).bottom()
        titleTable.add(titleTouchToStartLabel).width(Gdx.graphics.width * .33f)
        titleTable.add(rightButtonTable).width(arcadeButtonWidth).height(arcadeButtonHeight).bottom().row()
        titleTable.add(titleMadeByLabel).bottom().colspan(3).padTop(Gdx.graphics.height * .2f).padBottom(Gdx.graphics.height * .01f)

        // foreground - Menu ---------------------------------------------------------------
        menuTitleLabel = Label("Spank Fury!", BaseGame.labelStyle)
        menuTitleLabel.setFontScale(3f)
        menuTitleLabel.color = Color.RED
        menuTitleLabel.setAlignment(Align.center)

        startButton = TextButton("Start", BaseGame.textButtonStyle)
        startButton.touchable = Touchable.disabled
        startButton.addListener(object : ActorGestureListener() {
            override fun tap(event: InputEvent?, x: Float, y: Float, count: Int, button: Int) {
                BaseGame.levelMusic1!!.stop()
                screenTransition.fadeIn()
            }
        })

        optionsButton = TextButton("Options", BaseGame.textButtonStyle)
        optionsButton.touchable = Touchable.disabled
        optionsButton.addListener(object : ActorGestureListener() {
            override fun tap(event: InputEvent?, x: Float, y: Float, count: Int, button: Int) {
                changeToOptionsOverlay()
            }
        })

        exitButton = TextButton("Quit", BaseGame.textButtonStyle)
        exitButton.touchable = Touchable.disabled
        exitButton.addListener(object : ActorGestureListener() {
            override fun tap(event: InputEvent?, x: Float, y: Float, count: Int, button: Int) {
                screenTransition.fadeInAndExit()
            }
        })

        menuTable = Table()
        menuTable.setFillParent(true)
        menuTable.color.a = 0f
        menuTable.add(menuTitleLabel).padBottom(Gdx.graphics.height * .125f).row()
        menuTable.add(startButton).padBottom(Gdx.graphics.height * .02f).row()
        menuTable.add(optionsButton).padBottom(Gdx.graphics.height * .02f).row()
        menuTable.add(exitButton)

        // foreground - Options ---------------------------------------------------------------
        optionsLabel = Label("Options", BaseGame.labelStyle)
        optionsLabel.setFontScale(2f)
        optionsLabel.setAlignment(Align.center)
        optionsLabel.color = Color.YELLOW

        val optionsWidgetWidth = Gdx.graphics.width * .25f // value must be pre-determined for scaling
        val optionsWidgetHeight = Gdx.graphics.height * .1f // value must be pre-determined for scaling
        val optionsSliderScale = .0055f * Gdx.graphics.height // makes sure scale is device adjustable-ish

        // music
        optionsMusicSlider = Slider(0f, 1f, .1f, false, BaseGame.skin)
        optionsMusicSlider.touchable = Touchable.disabled
        optionsMusicSlider.value = BaseGame.musicVolume
        optionsMusicSlider.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                BaseGame.musicVolume = optionsMusicSlider.value
                GameUtils.setMusicVolume()
                GameUtils.saveGameState()
            }
        })
        val optionsMusicSliderContainer = Container(optionsMusicSlider)
        optionsMusicSliderContainer.isTransform = true
        optionsMusicSliderContainer.setOrigin((optionsWidgetWidth * 5 / 6) / 2, optionsWidgetHeight / 2)
        optionsMusicSliderContainer.setScale(optionsSliderScale)

        // sound
        optionsSoundSlider = Slider(0f, 1f, .1f, false, BaseGame.skin)
        optionsSoundSlider.touchable = Touchable.disabled
        optionsSoundSlider.value = BaseGame.soundVolume
        optionsSoundSlider.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                BaseGame.soundVolume = optionsSoundSlider.value
                BaseGame.hitSound1!!.play(BaseGame.soundVolume)
                GameUtils.saveGameState()
            }
        })
        val optionsSoundSliderContainer = Container(optionsSoundSlider)
        optionsSoundSliderContainer.isTransform = true
        optionsSoundSliderContainer.setOrigin((optionsWidgetWidth * 5 / 6) / 2, optionsWidgetHeight / 2)
        optionsSoundSliderContainer.setScale(optionsSliderScale)

        // vibration
        optionsVibrationCheckBox = CheckBox("Vibrations", BaseGame.skin)
        optionsVibrationCheckBox.touchable = Touchable.disabled
        optionsVibrationCheckBox.isChecked = BaseGame.vibrations
        optionsVibrationCheckBox.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                BaseGame.vibrations = !BaseGame.vibrations
                if (BaseGame.vibrations) Gdx.input.vibrate(100)
                BaseGame.hitSound1!!.play(BaseGame.soundVolume)
                GameUtils.saveGameState()
            }
        })
        optionsVibrationCheckBox.isTransform = true
        optionsVibrationCheckBox.image.setScaling(Scaling.fill)
        optionsVibrationCheckBox.imageCell.size(optionsWidgetWidth * .125f)
        optionsVibrationCheckBox.label.setFontScale(3f)
        optionsVibrationCheckBox.setOrigin(optionsWidgetWidth / 2, optionsWidgetHeight / 2)
        if (Gdx.app.type == Application.ApplicationType.Desktop) optionsVibrationCheckBox.color.a = 0f

        // back button
        optionsBackButton = TextButton("Back", BaseGame.textButtonStyle)
        optionsBackButton.touchable = Touchable.disabled
        optionsBackButton.addListener(object : ActorGestureListener() {
            override fun tap(event: InputEvent?, x: Float, y: Float, count: Int, button: Int) {
                changeToMenuOverlay()
            }
        })

        optionsTable = Table()
        optionsTable.setFillParent(true)
        optionsTable.color.a = 0f
        optionsTable.add(optionsLabel).colspan(2).padBottom(Gdx.graphics.height * .05f).row()
        optionsTable.add(optionsMusicSliderContainer).width(optionsWidgetWidth * 5 / 6).height(optionsWidgetHeight)
        optionsTable.add(Label("Music", BaseGame.labelStyle)).width(optionsWidgetWidth * 1 / 6).padLeft(Gdx.graphics.width * .1f).row()
        optionsTable.add(optionsSoundSliderContainer).width(optionsWidgetWidth * 5 / 6).height(optionsWidgetHeight)
        optionsTable.add(Label("Sounds", BaseGame.labelStyle)).width(optionsWidgetWidth * 1 / 6).padLeft(Gdx.graphics.width * .1f).row()
        optionsTable.add(optionsVibrationCheckBox).width(optionsWidgetWidth).height(optionsWidgetHeight).colspan(2).row()
        optionsTable.add(optionsBackButton).width(optionsWidgetWidth).colspan(2)
        // optionsTable.debug = true

        // background
        background = Background(mainStage)
        player = Player(0f, 0f, mainStage)

        // black transition overlay
        screenTransition = ScreenTransition()
        screenTransition.fadeOut()

        val stack = Stack()
        stack.setFillParent(true)
        stack.add(titleTable)
        stack.add(menuTable)
        stack.add(screenTransition.blackOverLay)
        stack.add(optionsTable)
        uiStage.addActor(stack)

        // titleTable.debug = true
        // menuTable.debug = true
        // optionsTable.debug = true
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
        if (time >= disableTime && pressOverlay) {
            changeToMenuOverlay()
            pressOverlay = false
        }
        return false
    }

    override fun keyDown(keycode: Int): Boolean {
        if (keycode == Keys.BACK || keycode == Keys.ESCAPE) {
            screenTransition.blackOverLay.addAction(Actions.sequence(
                    Actions.fadeIn(1f),
                    Actions.run {
                        super.dispose()
                        Gdx.app.exit()
                    }
            ))
        }

        if (time >= disableTime) changeToMenuOverlay()
        return false
    }

    private fun spawnEnemies(dt: Float) {
        easySpawnTimer += dt
        if (easySpawnTimer >= easySpawnFrequency / spawnDifficulty
                && BaseActor.count(mainStage, EasyEnemy::class.java.canonicalName) <= 8
                && BaseActor.count(mainStage, EasyEnemy::class.java.canonicalName) < 5
        ) {
            EasyEnemy(0f, 0f, mainStage, player, 25f, 1f)
            easySpawnTimer = 0f
        }
        mediumSpawnTimer += dt
        if (mediumSpawnTimer >= mediumSpawnFrequency / spawnDifficulty
                && BaseActor.count(mainStage, MediumEnemy::class.java.canonicalName) <= 8
                && BaseActor.count(mainStage, MediumEnemy::class.java.canonicalName) < 4
        ) {
            MediumEnemy(0f, 0f, mainStage, player, 25f, 1f)
            mediumSpawnTimer = 0f
        }
        swapSpawnTimer += dt
        if (swapSpawnTimer >= swapSpawnFrequency / spawnDifficulty
                && BaseActor.count(mainStage, SwapEnemy::class.java.canonicalName) <= 8
                && BaseActor.count(mainStage, SwapEnemy::class.java.canonicalName) < 4
        ) {
            SwapEnemy(0f, 0f, mainStage, player, 25f, 1f)
            swapSpawnTimer = 0f
        }
        hardSpawnTimer += dt
        if (hardSpawnTimer >= hardSpawnFrequency / spawnDifficulty
                && BaseActor.count(mainStage, HardEnemy::class.java.canonicalName) <= 8
                && BaseActor.count(mainStage, HardEnemy::class.java.canonicalName) < 4
        ) {
            HardEnemy(0f, 0f, mainStage, player, 25f, 1f)
            hardSpawnTimer = 0f
        }
    }

    private fun playPlayer() {
        // detect if player may hit someone
        for (baseActor: BaseActor in BaseActor.getList(mainStage, BaseEnemy::class.java.canonicalName)) {
            val enemy = baseActor as BaseEnemy

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
                if (distance < 0) pressButton(titleLeftButtonImage)
                else pressButton(titleRightButtonImage)
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

    private fun changeToMenuOverlay() {
        titleTable.color.a = 0f
        menuTable.color.a = 1f
        optionsTable.color.a = 0f
        GameUtils.enableActorsWithDelay(startButton)
        GameUtils.enableActorsWithDelay(optionsButton)
        GameUtils.enableActorsWithDelay(exitButton)
        optionsMusicSlider.touchable = Touchable.disabled
        optionsSoundSlider.touchable = Touchable.disabled
        optionsBackButton.touchable = Touchable.disabled
        optionsVibrationCheckBox.touchable = Touchable.disabled
    }

    private fun changeToOptionsOverlay() {
        titleTable.color.a = 0f
        menuTable.color.a = 0f
        optionsTable.color.a = 1f
        GameUtils.enableActorsWithDelay(optionsMusicSlider)
        GameUtils.enableActorsWithDelay(optionsSoundSlider)
        GameUtils.enableActorsWithDelay(optionsBackButton)
        GameUtils.enableActorsWithDelay(optionsVibrationCheckBox)
        startButton.touchable = Touchable.disabled
        optionsButton.touchable = Touchable.disabled
        exitButton.touchable = Touchable.disabled
    }
}
