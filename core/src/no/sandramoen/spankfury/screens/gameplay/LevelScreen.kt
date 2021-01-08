package no.sandramoen.spankfury.screens.gameplay

import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener
import no.sandramoen.spankfury.actors.*
import no.sandramoen.spankfury.screens.shell.MenuScreen
import no.sandramoen.spankfury.tables.LevelGameOverTable
import no.sandramoen.spankfury.tables.LevelGuiTable
import no.sandramoen.spankfury.tables.LevelPauseTable
import no.sandramoen.spankfury.utils.*
import kotlin.math.abs

class LevelScreen : BaseScreen() {
    private val token = "LevelScreen"
    private lateinit var player: Player
    private var playerHealth = player.health

    private lateinit var guiTable: LevelGuiTable
    private lateinit var pauseTable: LevelPauseTable
    private lateinit var gameOverTable: LevelGameOverTable
    private lateinit var gameOverLabelTable: Table

    private lateinit var screenTransition: ScreenTransition

    private var score = 0
    private var scoreAwarded = 0
    private var bonus = 0
    private var bonusMissModifier = .8f
    private var motivationNumber = 20
    private var gameTime = 0f
    private var pause = false
    private var overlayDuration = .125f
    private var playing = true
    private var enemySpeed = 20f
    private var enemyHittingDelay = 1f

    private var spawnDifficulty = 0f
    private var easySpawnTimer = 0f
    private val easySpawnFrequency = 3f
    private var mediumSpawnTimer = 0f
    private val mediumSpawnFrequency = 4f
    private var swapSpawnTimer = 0f
    private val swapSpawnFrequency = 12f
    private var hardSpawnTimer = 0f
    private val hardSpawnFrequency = 15f

    private var controlFrequency = 1f
    private var controlTimer = controlFrequency
    private var backOffTimer = BaseGame.backOffFrequency
    private var backOff = false

    private lateinit var background: Background

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
        guiTable = LevelGuiTable()

        // pause menu overlay

        pauseTable = LevelPauseTable()
        pauseTable.menuTextButton.addListener(object : ActorGestureListener() {
            override fun tap(event: InputEvent?, x: Float, y: Float, count: Int, button: Int) {
                screenTransition.blackOverLay.addAction(Actions.sequence(
                    Actions.fadeIn(1f),
                    Actions.run { BaseGame.setActiveScreen(MenuScreen()) }
                ))
            }
        })
        pauseTable.continueTextButton.addListener(object : ActorGestureListener() {
            override fun tap(event: InputEvent?, x: Float, y: Float, count: Int, button: Int) {
                changeToPlayOverlay()
            }
        })
        pauseTable.exitTextButton.addListener(object : ActorGestureListener() {
            override fun tap(event: InputEvent?, x: Float, y: Float, count: Int, button: Int) {
                screenTransition.fadeInAndExit()
            }
        })

        // game over overlay
        gameOverTable = LevelGameOverTable()

        // black transition overlay
        screenTransition = ScreenTransition()
        screenTransition.fadeOut()

        // game over label
        val gameOverLabel = Label("GAME OVER!", BaseGame.labelStyle)
        gameOverLabel.setFontScale(4f)
        GameUtils.pulseLabel(gameOverLabel, .125f, .5f)
        gameOverLabelTable = Table()
        gameOverLabelTable.color.a = 0f
        gameOverLabelTable.add(gameOverLabel)

        val stack = Stack()
        stack.setFillParent(true)
        stack.add(guiTable)
        stack.add(gameOverLabelTable)
        stack.add(pauseTable)
        stack.add(gameOverTable)
        stack.add(screenTransition.blackOverLay)
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
            gameTime < 30f -> {
                enemySpeed = 25f
                enemyHittingDelay = 1f
                spawnDifficulty = 1.5f
            }
            gameTime < 60f -> {
                enemySpeed = 27f
                enemyHittingDelay = .8f
                spawnDifficulty = 1.75f
            }
            gameTime < 120f -> {
                enemySpeed = 30f
                enemyHittingDelay = .75f
                spawnDifficulty = 2.0f
            }
            gameTime < 180f -> {
                enemySpeed = 35f
                enemyHittingDelay = .5f
                spawnDifficulty = 2.5f
            }
            gameTime < 240f -> {
                enemySpeed = 40f
                enemyHittingDelay = .25f
                spawnDifficulty = 3.0f
            }
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

        if (keycode == Keys.ENTER && gameOverTable.color.a == 1f) {
            screenTransition.blackOverLay.addAction(
                Actions.sequence(
                    Actions.fadeIn(1f),
                    Actions.run { BaseGame.setActiveScreen(LevelScreen()) }
                ))
        } else if (keycode == Keys.ENTER && pauseTable.color.a == 1f)
            changeToPlayOverlay()

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
        for (baseActor: BaseActor in BaseActor.getList(mainStage, BaseEnemy::class.java.canonicalName)) {
            val enemy = baseActor as BaseEnemy
            enemy.handleBackOff(backOff)
        }
    }

    // Checks if enemy may be hit or not, and triggers appropriate player, enemy and UI behaviour
    private fun checkEnemyHit(hitLeft: Boolean) {
        var hitBaseEnemy: BaseEnemy? = null
        var distance = 0f

        // detect if we hit someone
        for (baseActor: BaseActor in BaseActor.getList(mainStage, BaseEnemy::class.java.canonicalName)) {
            val enemy = baseActor as BaseEnemy

            if (enemy.health <= 0) // dead enemies should not be counted
                continue

            // measure the distance from player
            distance = enemy.x - player.x
            val inRange = enemy.width + player.width > abs(distance)
            val onLeftSide = enemy.x <= player.x

            // detect enemies in range
            if ((hitLeft && inRange && onLeftSide) || (!hitLeft && inRange && !onLeftSide)) {
                hitBaseEnemy = enemy
                break
            }
        }

        // implement consequences of a hit or miss
        if (hitBaseEnemy == null) {
            handleMiss(hitLeft)
        } else if (hitBaseEnemy.health > 0) {
            player.hit(distance)
            hitEnemy(hitBaseEnemy)
        }
    }

    private fun handleMiss(left: Boolean) {
        if (controlTimer >= controlFrequency) {
            if (left) player.hit(-20f)
            else player.hit(20f)

            touchIsDisabled()
            if (bonus >= bonusMissModifier) bonus = (bonus * bonusMissModifier).toInt()
            else bonus = 0
            guiTable.handleMiss(bonus)
        }
    }

    private fun hitEnemy(baseEnemy: BaseEnemy) {
        if (baseEnemy.struck()) { // if this kills the enemy
            scoreAwarded = baseEnemy.points * (bonus + 1)
            bonus++
            guiTable.updateBonus(bonus)
            score += scoreAwarded
            guiTable.setScoreLabel(score)
            if (bonus % motivationNumber == 0)
                guiTable.setMotivation(bonus)
            if (BaseGame.highScore < score) {
                BaseGame.highScore = score
                guiTable.setPersonalBestLabel()
                GameUtils.saveGameState()
            }
            val tempLabel = ScoreLabel(mainStage, "+$scoreAwarded")
            tempLabel.scaleBy(-.6f)
            tempLabel.centerAtActor(baseEnemy)
        }
    }

    private fun noEnemiesExist(): Boolean {
        return BaseActor.count(mainStage, BaseEnemy::class.java.canonicalName) == 0
    }

    private fun touchIsDisabled(): Boolean { // disallowing player spamming
        if (controlTimer < controlFrequency)
            return true
        controlTimer = 0f
        return false
    }

    private fun changeToGameOverOverlay() {
        gameOverLabelTable.color.a = 0f
        gameOverTable.updateHighScoreTable()
        gameOverTable.setScoreLabel(score)
        gameOverTable.enableButtons()
        gameOverTable.fadeIn(overlayDuration)

        // fade background elements
        background.setOpacity(.1f, overlayDuration)
        guiTable.addAction(Actions.alpha(.1f, overlayDuration))

        // disable entities
        disableEntities()
    }

    private fun changeToPauseOverlay() {
        pause = true
        pauseTable.fadeInAndEnable(overlayDuration)

        // fade background elements
        background.setOpacity(.1f, overlayDuration)
        guiTable.addAction(Actions.alpha(.1f, overlayDuration))


        // disable entities
        disableEntities()
    }

    private fun changeToPlayOverlay() {
        pause = false

        // fade out pause menu
        pauseTable.fadeOutAndDisable(overlayDuration)

        // fade in background elements
        background.setOpacity(1f, overlayDuration)
        guiTable.addAction(Actions.alpha(1f, overlayDuration))

        // enable entities
        player.pause = false
        player.addAction(Actions.color(Color.WHITE, overlayDuration))
        for (baseActor: BaseActor in BaseActor.getList(mainStage, BaseEnemy::class.java.canonicalName)) {
            val enemy = baseActor as BaseEnemy
            enemy.pause = false
            enemy.addAction(Actions.color(enemy.originalColor, overlayDuration))
        }
    }

    private fun subtractHealth() {
        playerHealth = player.health
        if (playerHealth >= 0)
            guiTable.subtractHealth(playerHealth)
    }

    private fun spawn(dt: Float) {
        easySpawnTimer += dt
        if (easySpawnTimer >= easySpawnFrequency / spawnDifficulty
            && BaseActor.count(mainStage, EasyEnemy::class.java.canonicalName) <= 8
        ) {
            EasyEnemy(0f, 0f, mainStage, player, enemySpeed, enemyHittingDelay)
            easySpawnTimer = 0f
        }
        mediumSpawnTimer += dt
        if (mediumSpawnTimer >= mediumSpawnFrequency / spawnDifficulty
            && BaseActor.count(mainStage, MediumEnemy::class.java.canonicalName) <= 8
            && gameTime > 30f
        ) {
            MediumEnemy(0f, 0f, mainStage, player, enemySpeed, enemyHittingDelay)
            mediumSpawnTimer = 0f
        }
        swapSpawnTimer += dt
        if (swapSpawnTimer >= swapSpawnFrequency / spawnDifficulty
            && BaseActor.count(mainStage, SwapEnemy::class.java.canonicalName) <= 8
            && gameTime > 40f
        ) {
            SwapEnemy(0f, 0f, mainStage, player, enemySpeed, enemyHittingDelay)
            swapSpawnTimer = 0f
        }
        hardSpawnTimer += dt
            if (hardSpawnTimer >= hardSpawnFrequency / spawnDifficulty
                && BaseActor.count(mainStage, HardEnemy::class.java.canonicalName) <= 8
            && gameTime > 60f
        ) {
            HardEnemy(0f, 0f, mainStage, player, enemySpeed, enemyHittingDelay)
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
        for (baseActor: BaseActor in BaseActor.getList(mainStage, BaseEnemy::class.java.canonicalName)) {
            baseActor.addAction(Actions.sequence(
                Actions.color(Color.BLACK, overlayDuration),
                Actions.run { baseActor.pause = true }
            ))
        }
    }
}
