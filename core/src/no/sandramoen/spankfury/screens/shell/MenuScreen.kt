package no.sandramoen.spankfury.screens.shell

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Interpolation
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
import no.sandramoen.spankfury.screens.gameplay.LevelScreen
import no.sandramoen.spankfury.utils.*
import kotlin.math.abs

class MenuScreen : BaseScreen() {
    private var token = "MenuScreen.kt"
    private var time = 0f
    private var disableTime = 1f
    private var pressOverlay = true
    private var mainStageDelay = BaseActor(0f, 0f, mainStage)

    // foreground - title
    private lateinit var titleTable: Table
    private lateinit var titleTitle1: BaseActor
    private lateinit var titleTitle2: BaseActor
    private lateinit var titleTitle3: BaseActor
    private lateinit var titleTouchToStartLabel: Label
    private lateinit var titleMadeByLabel: Label
    private lateinit var titleLeftButtonImage: Image
    private lateinit var titleLeftButtonLabel: Label
    private lateinit var titleRightButtonImage: Image
    private lateinit var titleRightButtonLabel: Label
    private lateinit var screenTransition: ScreenTransition

    // foreground - menu
    private lateinit var menuTable: Table
    private lateinit var startButton: TextButton
    private lateinit var optionsButton: TextButton
    private lateinit var exitButton: TextButton

    // foreground - options
    private lateinit var optionsTable: Table
    private lateinit var optionsLabel: Label
    private lateinit var optionsMusicSlider: Slider
    private lateinit var optionsSoundSlider: Slider
    private lateinit var optionsVibrationCheckBox: CheckBox
    private lateinit var optionsUseGPSCheckBox: CheckBox
    private lateinit var optionsBackButton: TextButton
    private lateinit var optionsShowScore: TextButton

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
        token = "MenuScreen.kt"

        // audio
        GameUtils.playAndLoopRandomMusic()

        mainStage.addListener(object : ActorGestureListener() {
            override fun tap(event: InputEvent?, x: Float, y: Float, count: Int, button: Int) {
                if (!pressOverlay && optionsTable.color.a != 1f && mainStageDelay.actions.size == 0) {
                    pressOverlay = true
                    changeToTitleOverlay()
                } else if (time >= disableTime && pressOverlay) {
                    pressOverlay = false
                    changeToMenuOverlay()
                    mainStageDelay.addAction(Actions.delay(1f))
                }
            }
        })

        // foreground - Title ---------------------------------------------------------------
        titleTitle1 = BaseActor(0f, 0f, mainStage)
        titleTitle1.actions
        titleTitle1.loadImage("title1")
        titleTitle1.setSize(Gdx.graphics.width.toFloat() * .48f, Gdx.graphics.height.toFloat() * .35f)

        titleTitle2 = BaseActor(0f, 0f, mainStage)
        titleTitle2.loadImage("title2")
        titleTitle2.setSize(Gdx.graphics.width.toFloat() * .48f, Gdx.graphics.height.toFloat() * .35f)

        titleTitle3 = BaseActor(0f, 0f, mainStage)
        titleTitle3.loadImage("title3")
        titleTitle3.setSize(Gdx.graphics.width.toFloat() * .3f, Gdx.graphics.height.toFloat() * .15f)
        titleTitle3.color.a = 0f
        animateTitle()

        val titleTitleTable = Table()
        titleTitleTable.add(titleTitle1)
        titleTitleTable.add(titleTitle2).row()
        titleTitleTable.add(titleTitle3).colspan(2).top()
        // titleTitleTable.debug = true

        titleTouchToStartLabel = Label("Touch to Play!", BaseGame.labelStyle)
        titleTouchToStartLabel.setAlignment(Align.center)
        titleTouchToStartLabel.setFontScale(.8f)
        GameUtils.pulseLabel(titleTouchToStartLabel)

        titleMadeByLabel = Label("made by Sandra Moen 2020", BaseGame.labelStyle)
        titleMadeByLabel.setFontScale(.5f)
        titleMadeByLabel.setAlignment(Align.center)
        titleMadeByLabel.color = Color.DARK_GRAY

        // arcade buttons
        var arcadeButtonWidth = Gdx.graphics.width * .14f
        var arcadeButtonHeight = Gdx.graphics.height * .12f

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
        titleTable.add(titleTitleTable).expand().fill().colspan(3).padBottom(Gdx.graphics.height * .1f).row()
        titleTable.add(leftButtonTable).width(arcadeButtonWidth).height(arcadeButtonHeight).bottom()
        titleTable.add(titleTouchToStartLabel).width(Gdx.graphics.width * .33f)
        titleTable.add(rightButtonTable).width(arcadeButtonWidth).height(arcadeButtonHeight).bottom().row()
        titleTable.add(titleMadeByLabel).bottom().colspan(3).padTop(Gdx.graphics.height * .2f)
            .padBottom(Gdx.graphics.height * .01f)
        // titleTable.debug = true

        // foreground - Menu ---------------------------------------------------------------
        startButton = TextButton("Start", BaseGame.textButtonStyle)
        startButton.color = BaseGame.green
        startButton.touchable = Touchable.disabled
        startButton.addListener(object : ActorGestureListener() {
            override fun tap(event: InputEvent?, x: Float, y: Float, count: Int, button: Int) {
                startTheGame()
            }
        })

        optionsButton = TextButton("Options", BaseGame.textButtonStyle)
        optionsButton.color = BaseGame.yellow
        optionsButton.touchable = Touchable.disabled
        optionsButton.addListener(object : ActorGestureListener() {
            override fun tap(event: InputEvent?, x: Float, y: Float, count: Int, button: Int) {
                BaseGame.clickSound!!.play(BaseGame.soundVolume)
                changeToOptionsOverlay()
            }
        })

        exitButton = TextButton("Quit", BaseGame.textButtonStyle)
        exitButton.color = BaseGame.red
        exitButton.touchable = Touchable.disabled
        exitButton.addListener(object : ActorGestureListener() {
            override fun tap(event: InputEvent?, x: Float, y: Float, count: Int, button: Int) {
                BaseGame.clickSound!!.play(BaseGame.soundVolume)
                screenTransition.fadeInAndExit()
            }
        })

        menuTable = Table()
        menuTable.setFillParent(true)
        menuTable.color.a = 0f
        menuTable.add(startButton).width(Gdx.graphics.width * .25f).height(Gdx.graphics.height * .125f)
            .padBottom(Gdx.graphics.height * .04f).padTop(Gdx.graphics.height * .25f).row()
        menuTable.add(optionsButton).width(Gdx.graphics.width * .25f).height(Gdx.graphics.height * .125f)
            .padBottom(Gdx.graphics.height * .04f).row()
        menuTable.add(exitButton).width(Gdx.graphics.width * .25f).height(Gdx.graphics.height * .125f)
        // menuTable.debug = true

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
                BaseGame.hit1Sound!!.play(BaseGame.soundVolume)
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
                BaseGame.clickSound!!.play(BaseGame.soundVolume)
                if (BaseGame.vibrations) Gdx.input.vibrate(100)
                GameUtils.saveGameState()
            }
        })
        optionsVibrationCheckBox.isTransform = true
        optionsVibrationCheckBox.image.setScaling(Scaling.fill)
        optionsVibrationCheckBox.imageCell.size(optionsWidgetWidth * .125f)
        optionsVibrationCheckBox.label.setFontScale(3.5f)
        optionsVibrationCheckBox.setOrigin(optionsWidgetWidth / 2, optionsWidgetHeight / 2)

        // google play services
        optionsUseGPSCheckBox = CheckBox("Google Play Services", BaseGame.skin)
        optionsUseGPSCheckBox.touchable = Touchable.disabled
        optionsUseGPSCheckBox.isChecked = !BaseGame.disableGPS
        optionsUseGPSCheckBox.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                BaseGame.disableGPS = !BaseGame.disableGPS
                BaseGame.clickSound!!.play(BaseGame.soundVolume)
                GameUtils.saveGameState()

                if (!BaseGame.disableGPS) {
                    BaseGame.gps!!.signIn()
                    BaseGame.gps!!.submitScore(BaseGame.highScore)
                    optionsShowScore.touchable = Touchable.enabled
                    optionsShowScore.color.a = 1f
                } else {
                    BaseGame.gps!!.signOut()
                    optionsShowScore.touchable = Touchable.disabled
                    optionsShowScore.color.a = .75f
                }
            }
        })
        optionsUseGPSCheckBox.isTransform = true
        optionsUseGPSCheckBox.image.setScaling(Scaling.fill)
        optionsUseGPSCheckBox.imageCell.size(optionsWidgetWidth * .125f)
        optionsUseGPSCheckBox.label.setFontScale(3.5f)
        optionsUseGPSCheckBox.setOrigin(optionsWidgetWidth / 2, optionsWidgetHeight / 2)

        // show Google Play Services leaderboard
        optionsShowScore = TextButton("Show Leaderboard", BaseGame.textButtonStyle)
        optionsShowScore.label.setFontScale(.75f)
        optionsShowScore.color = Color.GRAY
        optionsShowScore.touchable = Touchable.disabled
        optionsShowScore.addListener(object : ActorGestureListener() {
            override fun tap(event: InputEvent?, x: Float, y: Float, count: Int, button: Int) {
                BaseGame.clickSound!!.play(BaseGame.soundVolume)
                if (BaseGame.gps!!.isSignedIn()) BaseGame.gps!!.getLeaderboard()
            }
        })

        // back button
        optionsBackButton = TextButton("Back", BaseGame.textButtonStyle)
        optionsBackButton.color = BaseGame.yellow
        optionsBackButton.touchable = Touchable.disabled
        optionsBackButton.addListener(object : ActorGestureListener() {
            override fun tap(event: InputEvent?, x: Float, y: Float, count: Int, button: Int) {
                BaseGame.clickSound!!.play(BaseGame.soundVolume)
                changeToMenuOverlay()
            }
        })

        optionsTable = Table()
        optionsTable.setFillParent(true)
        optionsTable.color.a = 0f
        optionsTable.add(optionsLabel).colspan(2).padBottom(Gdx.graphics.height * .05f).row()
        optionsTable.add(optionsMusicSliderContainer).width(optionsWidgetWidth * 5 / 6).height(optionsWidgetHeight)
        optionsTable.add(Label("Music", BaseGame.labelStyle)).width(optionsWidgetWidth * 1 / 6)
            .padLeft(Gdx.graphics.width * .11f).row()
        optionsTable.add(optionsSoundSliderContainer).width(optionsWidgetWidth * 5 / 6).height(optionsWidgetHeight)
        optionsTable.add(Label("Sounds", BaseGame.labelStyle)).width(optionsWidgetWidth * 1 / 6)
            .padLeft(Gdx.graphics.width * .11f).row()
        if (Gdx.app.type == Application.ApplicationType.Android) {
            optionsTable.add(optionsVibrationCheckBox).width(optionsWidgetWidth).height(optionsWidgetHeight).colspan(2)
                .row()
            optionsTable.add(optionsUseGPSCheckBox).width(optionsWidgetWidth).height(optionsWidgetHeight).colspan(2)
                .row()
            optionsTable.add(optionsShowScore).width(optionsWidgetWidth * 1.15f).height(optionsWidgetHeight).colspan(2)
                .padTop(Gdx.graphics.height * .01f)
                .row()
        }
        optionsTable.add(optionsBackButton).width(Gdx.graphics.width * .25f).height(Gdx.graphics.height * .125f)
            .colspan(2).padTop(Gdx.graphics.height * .05f)
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
        stack.add(optionsTable)
        stack.add(screenTransition.blackOverLay)
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

    override fun keyDown(keycode: Int): Boolean {
        if (keycode == Keys.BACK || keycode == Keys.ESCAPE) {
            screenTransition.blackOverLay.addAction(Actions.sequence(
                Actions.fadeIn(1f),
                Actions.run {
                    super.dispose()
                    Gdx.app.exit()
                }
            ))
        } else if (keycode == Keys.ENTER) {
            if (pressOverlay) {
                pressOverlay = false
                changeToMenuOverlay()
            } else startTheGame()
        }

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
                enemy.struck()
                player.hit(distance, enableSound = false)
                background.act(player)
                playerHitTime = 0f
                if (distance < 0) pressArcadeButton(titleLeftButtonImage, true)
                else pressArcadeButton(titleRightButtonImage, false)
                break
            }
        }
    }

    private fun pressArcadeButton(button: Image, left: Boolean) {
        button.drawable = SpriteDrawable(Sprite(BaseGame.textureAtlas!!.findRegion("arcade-button-pressed")))
        if (left) titleLeftButtonLabel.color = Color.WHITE
        else titleRightButtonLabel.color = Color.WHITE
        button.addAction(Actions.sequence(
            Actions.delay(playerHitFrequency),
            Actions.run {
                titleLeftButtonLabel.color = Color.GRAY
                titleRightButtonLabel.color = Color.GRAY
                button.drawable = SpriteDrawable(Sprite(BaseGame.textureAtlas!!.findRegion("arcade-button-unpressed")))
            }
        ))
    }

    private fun changeToMenuOverlay() {
        titleTable.color.a = 0f
        menuTable.color.a = 1f
        titleTitle1.isVisible = true
        titleTitle2.isVisible = true
        titleTitle3.isVisible = false
        optionsTable.color.a = 0f
        GameUtils.enableActorsWithDelay(startButton)
        GameUtils.enableActorsWithDelay(optionsButton)
        GameUtils.enableActorsWithDelay(exitButton)
        optionsMusicSlider.touchable = Touchable.disabled
        optionsSoundSlider.touchable = Touchable.disabled
        optionsBackButton.touchable = Touchable.disabled
        optionsShowScore.touchable = Touchable.disabled
        optionsVibrationCheckBox.touchable = Touchable.disabled
        optionsUseGPSCheckBox.touchable = Touchable.disabled
    }

    private fun changeToTitleOverlay() {
        titleTable.color.a = 1f
        menuTable.color.a = 0f
        titleTitle1.isVisible = true
        titleTitle2.isVisible = true
        titleTitle3.isVisible = false
        optionsTable.color.a = 0f
        startButton.clearActions() // buttons are enabled with delay, need to cancel delay
        startButton.touchable = Touchable.disabled
        optionsButton.clearActions() // buttons are enabled with delay, need to cancel delay
        optionsButton.touchable = Touchable.disabled
        exitButton.clearActions() // buttons are enabled with delay, need to cancel delay
        exitButton.touchable = Touchable.disabled
        optionsMusicSlider.touchable = Touchable.disabled
        optionsSoundSlider.touchable = Touchable.disabled
        optionsBackButton.touchable = Touchable.disabled
        optionsShowScore.touchable = Touchable.disabled
        optionsVibrationCheckBox.touchable = Touchable.disabled
        optionsUseGPSCheckBox.touchable = Touchable.disabled
    }

    private fun changeToOptionsOverlay() {
        titleTable.color.a = 0f
        menuTable.color.a = 0f
        titleTitle1.isVisible = false
        titleTitle2.isVisible = false
        titleTitle3.isVisible = false
        optionsTable.color.a = 1f
        GameUtils.enableActorsWithDelay(optionsMusicSlider)
        GameUtils.enableActorsWithDelay(optionsSoundSlider)
        GameUtils.enableActorsWithDelay(optionsBackButton)
        if (BaseGame.disableGPS) {
            optionsShowScore.color.a = .75f
        } else {
            optionsShowScore.color.a = 1f
            GameUtils.enableActorsWithDelay(optionsShowScore)
        }
        GameUtils.enableActorsWithDelay(optionsVibrationCheckBox)
        GameUtils.enableActorsWithDelay(optionsUseGPSCheckBox)
        startButton.touchable = Touchable.disabled
        optionsButton.touchable = Touchable.disabled
        exitButton.touchable = Touchable.disabled
    }

    private fun animateTitle() {
        // first sequence
        titleTitle1.setOrigin(Align.left)
        titleTitle1.addAction(
            Actions.sequence(
                Actions.delay(1f),
                Actions.scaleTo(.25f, 1f, .25f),
                Actions.run { BaseGame.whipCrackSound!!.play(BaseGame.soundVolume) },
                Actions.delay(.25f),
                Actions.scaleTo(1.3f, 1f, .125f, Interpolation.pow5In),
                Actions.scaleTo(1f, 1f, .125f)
            )
        )

        // second sequence
        titleTitle2.setOrigin(Align.right)
        titleTitle2.addAction(
            Actions.sequence(
                Actions.delay(1.625f),
                Actions.scaleTo(.75f, 1f, .125f, Interpolation.circleOut),
                Actions.scaleTo(1f, 1f, 1f, Interpolation.bounceOut),
                Actions.run { titleTitle2.setOrigin(Align.center) },
                Actions.parallel(
                    Actions.color(Color.RED, 2f, Interpolation.pow3OutInverse),
                    Actions.sequence(
                        Actions.scaleTo(.98f, .98f, .4f),
                        Actions.scaleTo(1f, 1f, .4f),
                        Actions.scaleTo(.98f, .98f, .4f),
                        Actions.scaleTo(1f, 1f, .4f),
                        Actions.scaleTo(.98f, .98f, .4f),
                        Actions.scaleTo(1f, 1f, .4f),
                        Actions.scaleTo(.98f, .98f, .4f),
                        Actions.scaleTo(1f, 1f, .4f),
                        Actions.scaleTo(.98f, .98f, .4f),
                        Actions.scaleTo(1f, 1f, .4f),
                        Actions.scaleTo(.98f, .98f, .4f),
                        Actions.scaleTo(1f, 1f, .4f),
                        Actions.scaleTo(.98f, .98f, .4f),
                        Actions.scaleTo(1f, 1f, .4f)
                    )
                ),
                Actions.color(Color.WHITE, 1f)
            )
        )

        // third sequence
        titleTitle3.addAction(
            Actions.sequence(
                Actions.delay(4f),
                Actions.run { if (titleTitle3.isVisible) BaseGame.titlePowerUpSound!!.play(BaseGame.soundVolume) },
                Actions.alpha(1f, 1.5f, Interpolation.bounceIn),
                Actions.delay(5f),
                Actions.fadeOut(1.5f)
            )
        )
    }

    private fun startTheGame() {
        GameUtils.stopAllMusic()
        BaseGame.clickSound!!.play(BaseGame.soundVolume)
        screenTransition.fadeIn()
        screenTransition.blackOverLay.addAction(Actions.after(Actions.run {
            BaseGame.setActiveScreen(LevelScreen())
        }))
    }
}
