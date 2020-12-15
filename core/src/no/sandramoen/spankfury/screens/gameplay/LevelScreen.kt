package no.sandramoen.spankfury.screens.gameplay

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import no.sandramoen.spankfury.actors.*
import no.sandramoen.spankfury.screens.shell.MenuScreen
import no.sandramoen.spankfury.tables.LevelGameOverTable
import no.sandramoen.spankfury.utils.BaseActor
import no.sandramoen.spankfury.utils.BaseGame
import no.sandramoen.spankfury.utils.BaseScreen
import no.sandramoen.spankfury.utils.GameUtils
import kotlin.math.abs

class LevelScreen : BaseScreen() {
    private val token = "LevelScreen"
    private lateinit var player: Player
    private var playerHealth = player.health

    private lateinit var statsScoreLabel: Label
    private lateinit var personalBestLabel: Label
    private lateinit var personalBestTitleLabel: Label
    private lateinit var missLabel: Label
    private lateinit var bonusTitleLabel: Label
    private lateinit var bonusLabel: Label
    private lateinit var motivationTitleLabel: Label
    private lateinit var motivationLabel: Label

    private lateinit var menuTextButton: TextButton
    private lateinit var continueTextButton: TextButton
    private lateinit var exitTextButton: TextButton

    private lateinit var bonusTable: Table
    private lateinit var motivationTable: Table
    private lateinit var scoreTable: Table
    private lateinit var personalBestTable: Table
    private lateinit var healthTable: Table
    private lateinit var statsTable: Table
    private lateinit var pauseTable: Table
    private lateinit var gameOverTable: LevelGameOverTable
    private lateinit var gameOverLabelTable: Table

    private lateinit var blackOverlay: Image

    private var score = 0
    private var scoreAwarded = 0
    private var bonus = 0
    private var bonusMissModifier = 3
    private var motivationNumber = 20
    private var gameTime = 0f
    private var pause = false
    private var overlayDuration = .125f
    private var playing = true
    private lateinit var highscores: ArrayList<Pair<String, Int>>

    private var spawnDifficulty = 1f
    private var easySpawnTimer = 0f
    private val easySpawnFrequency = MathUtils.random(2f, 5f)
    private var mediumSpawnTimer = 0f
    private val mediumSpawnFrequency = MathUtils.random(3f, 6f)
    private var swapSpawnTimer = 0f
    private val swapSpawnFrequency = MathUtils.random(10f, 13f)
    private var hardSpawnTimer = 0f
    private val hardSpawnFrequency = MathUtils.random(13f, 18f)

    private var controlFrequency = 1f
    private var controlTimer = controlFrequency
    private var backOffTimer = BaseGame.backOffFrequency
    private var backOff = false

    private lateinit var background: Background

    private lateinit var health1: Image
    private lateinit var health2: Image
    private lateinit var health3: Image
    private lateinit var healths: Array<Image>

    override fun initialize() {
        // game state
        BaseGame.tempo = 1f // reset

        // entities
        background = Background(mainStage)
        player = Player(0f, 0f, mainStage)
        playerHealth = player.health

        // audio
        BaseGame.levelMusic1!!.play()
        BaseGame.levelMusic1!!.volume = BaseGame.musicVolume
        BaseGame.levelMusic1!!.isLooping = true

        // ui
        val scoreTitleLabel = Label("Score", BaseGame.labelStyle)
        scoreTitleLabel.color = Color.RED
        statsScoreLabel = Label("$score", BaseGame.labelStyle)
        scoreTable = Table()
        scoreTable.add(scoreTitleLabel).row()
        scoreTable.add(statsScoreLabel)

        healthTable = Table()
        health1 = Image(BaseGame.textureAtlas!!.findRegion("heart"))
        health1.color.a = 0f
        health2 = Image(BaseGame.textureAtlas!!.findRegion("heart"))
        health2.color.a = 0f
        health3 = Image(BaseGame.textureAtlas!!.findRegion("heart"))
        health3.color.a = 0f
        healths = Array()
        healths.add(health1)
        healths.add(health2)
        healths.add(health3)
        for (i in 0 until healths.size)
            healths[i].addAction(Actions.sequence(
                    Actions.delay(i / 2.5f),
                    Actions.fadeIn(.5f)
            ))
        val healthWidth = Gdx.graphics.width * .035f
        healthTable.add(health1).width(healthWidth).height(healthWidth).padRight(Gdx.graphics.width * .02f)
        healthTable.add(health2).width(healthWidth).height(healthWidth).padRight(Gdx.graphics.width * .02f)
        healthTable.add(health3).width(healthWidth).height(healthWidth)

        personalBestTitleLabel = Label("Personal Best", BaseGame.labelStyle)
        personalBestTitleLabel.color = Color.RED
        personalBestLabel = Label("${BaseGame.highScore.toInt()}", BaseGame.labelStyle)
        personalBestTable = Table()
        personalBestTable.add(personalBestTitleLabel).row()
        personalBestTable.add(personalBestLabel)

        bonusLabel = Label("$bonus", BaseGame.labelStyle)
        bonusLabel.setFontScale(2f)
        bonusLabel.color = Color.YELLOW
        bonusLabel.color.a = 0f
        bonusTitleLabel = Label("x BONUS", BaseGame.labelStyle)
        bonusTitleLabel.setFontScale(1f)
        bonusTitleLabel.color.a = 0f
        bonusTable = Table()
        bonusTable.isTransform = true
        bonusTable.originX = Gdx.graphics.width / 15f
        bonusTable.originY = Gdx.graphics.height / 15f
        bonusTable.add(bonusLabel).row()
        bonusTable.add(bonusTitleLabel)
        val bonusTableTable = Table()
        bonusTableTable.add(bonusTable)

        missLabel = Label("miss!", BaseGame.labelStyle)
        missLabel.setAlignment(Align.center)
        missLabel.color = Color.RED
        missLabel.color.a = 0f
        missLabel.setFontScale(1.25f)

        motivationLabel = Label("$motivationNumber", BaseGame.labelStyle)
        motivationLabel.setFontScale(2f)
        motivationLabel.color.a = 0f
        motivationTitleLabel = Label("xRAD", BaseGame.labelStyle)
        motivationTitleLabel.setFontScale(1f)
        motivationTitleLabel.color = Color.CYAN
        motivationTitleLabel.color.a = 0f
        motivationTable = Table()
        motivationTable.isTransform = true
        motivationTable.originX = Gdx.graphics.width * 1 / 24f
        motivationTable.originY = Gdx.graphics.height * 1 / 24f
        motivationTable.add(motivationLabel).row()
        motivationTable.add(motivationTitleLabel)
        val motivationTableTable = Table()
        motivationTableTable.add(motivationTable)

        statsTable = Table()
        statsTable.setFillParent(true)
        statsTable.add(scoreTable).top().width(Gdx.graphics.width / 3f)
        statsTable.add(healthTable).top().width(Gdx.graphics.width / 3f)
        statsTable.add(personalBestTable).top().width(Gdx.graphics.width / 3f).row()
        statsTable.add(bonusTableTable).expand().width(Gdx.graphics.width / 3f).padBottom(Gdx.graphics.height * .5f)
        statsTable.add(missLabel).expand().width(Gdx.graphics.width / 3f).padBottom(Gdx.graphics.height * .5f)
        statsTable.add(motivationTableTable).expand().width(Gdx.graphics.width / 3f).padBottom(Gdx.graphics.height * .5f).row()
        // statsTable.debug = true

        // pause menu overlay
        menuTextButton = TextButton("Menu", BaseGame.textButtonStyle)
        menuTextButton.color = Color.YELLOW
        menuTextButton.touchable = Touchable.disabled
        menuTextButton.addListener(object : ActorGestureListener() {
            override fun tap(event: InputEvent?, x: Float, y: Float, count: Int, button: Int) {
                blackOverlay.addAction(Actions.sequence(
                        Actions.fadeIn(1f),
                        Actions.run { BaseGame.setActiveScreen(MenuScreen()) }
                ))
            }
        })
        continueTextButton = TextButton("Continue", BaseGame.textButtonStyle)
        continueTextButton.color = Color.GREEN
        continueTextButton.touchable = Touchable.disabled
        continueTextButton.addListener(object : ActorGestureListener() {
            override fun tap(event: InputEvent?, x: Float, y: Float, count: Int, button: Int) {
                changeToPlayOverlay()
            }
        })
        exitTextButton = TextButton("Quit", BaseGame.textButtonStyle)
        exitTextButton.color = Color.RED
        exitTextButton.touchable = Touchable.disabled
        exitTextButton.addListener(object : ActorGestureListener() {
            override fun tap(event: InputEvent?, x: Float, y: Float, count: Int, button: Int) {
                blackOverlay.addAction(Actions.sequence(
                        Actions.fadeIn(1f),
                        Actions.run { Gdx.app.exit() }
                ))
            }
        })
        pauseTable = Table()
        pauseTable.color.a = 0f
        pauseTable.add(menuTextButton).expand().padBottom(Gdx.graphics.height * 1 / 4f).right()
        pauseTable.add(continueTextButton).expand().padBottom(Gdx.graphics.height * 1 / 4f)
        pauseTable.add(exitTextButton).expand().padBottom(Gdx.graphics.height * 1 / 4f).left()

        // game over overlay
        gameOverTable = LevelGameOverTable()

        // black transition overlay
        blackOverlay = Image(BaseGame.textureAtlas!!.findRegion("whitePixel"))
        blackOverlay.color = Color.BLACK
        blackOverlay.touchable = Touchable.childrenOnly
        blackOverlay.setSize(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        blackOverlay.addAction(Actions.fadeOut(1f))

        // game over label
        val gameOverLabel = Label("GAME OVER!", BaseGame.labelStyle)
        gameOverLabel.setFontScale(4f)
        GameUtils.pulseLabel(gameOverLabel, .125f, .5f)
        gameOverLabelTable = Table()
        gameOverLabelTable.color.a = 0f
        gameOverLabelTable.add(gameOverLabel)

        val stack = Stack()
        stack.setFillParent(true)
        stack.add(statsTable)
        stack.add(gameOverLabelTable)
        stack.add(pauseTable)
        stack.add(gameOverTable)
        stack.add(blackOverlay)
        uiStage.addActor(stack)
    }

    override fun update(dt: Float) {
        if (pause) return
        gameTime += dt
        if (controlTimer < controlFrequency) controlTimer += dt

        // enemy back off
        enemyBackOff(dt)

        // spawning
        if (player.health > 0) spawn(dt)
        else if (playing) gameOver()

        // increasing difficulty
        when {
            gameTime < 30f -> spawnDifficulty = 1.25f
            gameTime < 50f -> spawnDifficulty = 1.5f
            gameTime < 70f -> spawnDifficulty = 1.75f
            gameTime < 90f -> spawnDifficulty = 2f
        }

        // ui update
        if (playerHealth != player.health) subtractHealth()
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {

        if (pause) return false

        // check which way player is hitting
        val worldCoordinates = camera.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0f))
        val hitLeft = (worldCoordinates.x <= player.x) // touch detected to the left of the player

        if (noEnemiesExist()) {
            handleMiss(hitLeft)
            background.act(player)
            return false
        }

        checkEnemyHit(hitLeft)
        background.act(player)
        return false
    }

    override fun keyDown(keycode: Int): Boolean { // desktop controls
        if (keycode == Keys.BACK || keycode == Keys.ESCAPE) {
            if (pause)
                BaseGame.setActiveScreen(MenuScreen())
            else
                changeToPauseOverlay()
        }

        if (pause) return false

        if (noEnemiesExist()) {
            if (keycode == Keys.LEFT || keycode == Keys.A)
                handleMiss(true)
            else if (keycode == Keys.RIGHT || keycode == Keys.D)
                handleMiss(false)
            background.act(player)
            return false
        }

        if (keycode == Keys.LEFT || keycode == Keys.A) {
            checkEnemyHit(true)
        } else if (keycode == Keys.RIGHT || keycode == Keys.D) {
            checkEnemyHit(false)
        }
        background.act(player)
        return false
    }

    private fun enemyBackOff(dt: Float) {
        // player health change
        if (playerHealth != player.health && player.health > 0) {
            backOffTimer = 0f
            backOff = true
            setEnemyBackOff(true)
        }

        // back off
        if (backOffTimer < BaseGame.backOffFrequency) {
            backOffTimer += dt
        } else if (backOff) {
            backOffTimer = BaseGame.backOffFrequency
            backOff = false
            setEnemyBackOff(false)
        }
    }

    private fun setEnemyBackOff(backOff: Boolean) {
        for (baseActor: BaseActor in BaseActor.getList(mainStage, Enemy::class.java.canonicalName)) {
            val enemy = baseActor as Enemy
            enemy.handleBackOff(backOff)
        }
    }

    // Checks if enemy may be hit or not, and triggers appropriate player, enemy and UI behaviour
    private fun checkEnemyHit(hitLeft: Boolean) {
        var hitEnemy: Enemy? = null
        var distance = 0f

        // detect if we hit someone
        for (baseActor: BaseActor in BaseActor.getList(mainStage, Enemy::class.java.canonicalName)) {
            val enemy = baseActor as Enemy

            if (enemy.health <= 0) // dead enemies should not be counted
                continue

            // measure the distance from player
            distance = enemy.x - player.x
            val inRange = enemy.width + player.width > abs(distance)
            val onLeftSide = enemy.x <= player.x

            // detect enemies in range
            if ((hitLeft && inRange && onLeftSide) || (!hitLeft && inRange && !onLeftSide)) {
                hitEnemy = enemy
                break
            }
        }

        // implement consequences of a hit or miss
        if (hitEnemy == null) {
            handleMiss(hitLeft)
        } else if (hitEnemy.health > 0) {
            player.hit(distance)
            hitEnemy(hitEnemy)
        }
    }

    private fun handleMiss(left: Boolean) {
        if (controlTimer >= controlFrequency) {
            if (left)
                player.hit(-20f)
            else
                player.hit(20f)
            touchIsDisabled()
            if (bonus >= bonusMissModifier) bonus -= bonusMissModifier
            else bonus = 0
            bonusLabel.setText("$bonus")
            animateBonuses(bonusTable, bonusLabel, bonusTitleLabel)
            missLabel.addAction(Actions.sequence(
                    Actions.fadeIn(.2f),
                    Actions.fadeOut(.2f)
            ))
        }
    }

    private fun hitEnemy(enemy: Enemy) {
        if (enemy.struck()) { // if this kills the enemy
            scoreAwarded = enemy.points * (bonus + 1)
            bonus++
            bonusLabel.setText("$bonus")
            animateBonuses(bonusTable, bonusLabel, bonusTitleLabel)
            score += scoreAwarded
            statsScoreLabel.setText("$score")
            if (bonus % motivationNumber == 0) setMotivation()
            if (BaseGame.highScore < score) {
                BaseGame.highScore = score
                personalBestLabel.setText("${BaseGame.highScore}")
                GameUtils.saveGameState()
            }
            val tempLabel = ScoreLabel(mainStage, "+$scoreAwarded")
            tempLabel.scaleBy(-.6f)
            tempLabel.centerAtActor(enemy)
        }
    }

    private fun noEnemiesExist(): Boolean {
        return (BaseActor.count(mainStage, Enemy::class.java.canonicalName) == 0)
    }

    private fun setMotivation() {
        animateBonuses(motivationTable, motivationLabel, motivationTitleLabel)
        motivationLabel.setText("$bonus")
        var motivation = "Great!"
        when (bonus) {
            20 -> motivation = "Nice!"
            40 -> motivation = "Cool!"
            60 -> motivation = "Wow!"
            80 -> motivation = "No way!"
            100 -> motivation = "Fierce!"
            200 -> motivation = "Awesome!"
            220 -> motivation = "Sayonara!"
            240 -> motivation = "Piece of cake!"
            260 -> motivation = "Yippee Ki Yay!"
            280 -> motivation = "Dodge this!"
            300 -> motivation = "Astounding!"
            320 -> motivation = "Staggering!"
            340 -> motivation = "Extraordinary!"
            360 -> motivation = "Assimilate this!"
            380 -> motivation = "Unbelievable!"
            400 -> motivation = "Breathtaking!"
            420 -> motivation = "Epic!"
            440 -> motivation = "Legendary!"
        }
        motivationTitleLabel.setText("$motivation")
    }

    private fun animateBonuses(table: Table, label: Label, titleLabel: Label) {
        animateLabel(label)
        animateLabel(titleLabel)
        table.addAction(Actions.sequence(
                Actions.scaleTo(.5f, .5f, 0f),
                Actions.scaleTo(1f, 1f, .7f, Interpolation.bounceOut)
        ))
    }

    private fun animateLabel(label: Label) {
        label.clearActions()
        label.color.a = 0f
        label.addAction(Actions.parallel(
                Actions.forever(Actions.sequence(
                        Actions.alpha(.5f, .2f),
                        Actions.alpha(1f, .2f)
                )),
                Actions.sequence(
                        Actions.delay(2f),
                        Actions.run {
                            label.clearActions()
                            label.color.a = 0f
                        }
                )
        ))
    }

    private fun touchIsDisabled(): Boolean { // disallowing player spamming
        if (controlTimer < controlFrequency)
            return true
        controlTimer = 0f
        return false
    }

    private fun changeToGameOverOverlay() {
        gameOverTable.updateHighScoreTable()
        gameOverLabelTable.color.a = 0f
        gameOverTable.setScoreLabel(score)

        gameOverTable.enableButtons()

        // fade in game over menu
        gameOverTable.fadeIn(overlayDuration)

        // fade background elements
        background.setOpacity(.1f, overlayDuration)
        statsTable.addAction(Actions.alpha(.1f, overlayDuration))

        // disable entities
        disableEntities()
    }

    private fun changeToPauseOverlay() {
        pause = true
        pauseTable.addAction(Actions.fadeIn(overlayDuration))

        // fade in pause menu
        menuTextButton.addAction(Actions.fadeIn(overlayDuration))
        continueTextButton.addAction(Actions.fadeIn(overlayDuration))
        exitTextButton.addAction(Actions.fadeIn(overlayDuration))

        // fade background elements
        background.setOpacity(.1f, overlayDuration)
        statsTable.addAction(Actions.alpha(.1f, overlayDuration))

        // enable menu buttons
        GameUtils.enableActorsWithDelay(menuTextButton)
        GameUtils.enableActorsWithDelay(continueTextButton)
        GameUtils.enableActorsWithDelay(exitTextButton)

        // disable entities
        disableEntities()
    }

    private fun changeToPlayOverlay() {
        pause = false

        // fade out pause menu
        pauseTable.addAction(Actions.fadeOut(overlayDuration))

        // fade in background elements
        background.setOpacity(1f, overlayDuration)
        statsTable.addAction(Actions.alpha(1f, overlayDuration))

        // disable menu buttons
        menuTextButton.touchable = Touchable.disabled
        continueTextButton.touchable = Touchable.disabled
        exitTextButton.touchable = Touchable.disabled

        // enable entities
        player.pause = false
        player.addAction(Actions.color(Color.WHITE, overlayDuration))
        for (baseActor: BaseActor in BaseActor.getList(mainStage, Enemy::class.java.canonicalName)) {
            val enemy = baseActor as Enemy
            enemy.pause = false
            enemy.addAction(Actions.color(enemy.originalColor, overlayDuration))
        }
    }

    private fun subtractHealth() {
        playerHealth = player.health
        if (playerHealth >= 0)
            healths[playerHealth].addAction(Actions.fadeOut(1f))
    }

    private fun spawn(dt: Float) {
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
                && gameTime > 30f
                && BaseActor.count(mainStage, MediumEnemy::class.java.canonicalName) < 4
        ) {
            MediumEnemy(0f, 0f, mainStage, player)
            mediumSpawnTimer = 0f
        }
        swapSpawnTimer += dt
        if (swapSpawnTimer >= swapSpawnFrequency / spawnDifficulty
                && BaseActor.count(mainStage, SwapEnemy::class.java.canonicalName) <= 8
                && gameTime > 40f
                && BaseActor.count(mainStage, SwapEnemy::class.java.canonicalName) < 4
        ) {
            SwapEnemy(0f, 0f, mainStage, player)
            swapSpawnTimer = 0f
        }
        hardSpawnTimer += dt
        if (hardSpawnTimer >= hardSpawnFrequency / spawnDifficulty
                && BaseActor.count(mainStage, HardEnemy::class.java.canonicalName) <= 8
                && gameTime > 60f
                && BaseActor.count(mainStage, HardEnemy::class.java.canonicalName) < 4
        ) {
            HardEnemy(0f, 0f, mainStage, player)
            hardSpawnTimer = 0f
        }
    }

    private fun gameOver() {
        playing = false
        pause = true
        gameOverLabelTable.color.a = 1f
        gameOverLabelTable.addAction(Actions.sequence(
                Actions.delay(2f),
                Actions.run { changeToGameOverOverlay() }
        ))
    }

    private fun disableEntities() {
        player.addAction(Actions.sequence(
                Actions.color(Color.BLACK, overlayDuration),
                Actions.run { player.pause = true }
        ))
        for (baseActor: BaseActor in BaseActor.getList(mainStage, Enemy::class.java.canonicalName)) {
            baseActor.addAction(Actions.sequence(
                    Actions.color(Color.BLACK, overlayDuration),
                    Actions.run { baseActor.pause = true }
            ))
        }
    }
}
