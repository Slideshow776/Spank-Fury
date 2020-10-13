package no.sandramoen.spankfury.screens.gameplay

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import no.sandramoen.spankfury.actors.*
import no.sandramoen.spankfury.utils.BaseActor
import no.sandramoen.spankfury.utils.BaseGame
import no.sandramoen.spankfury.utils.BaseScreen
import no.sandramoen.spankfury.utils.GameUtils
import kotlin.math.abs

class LevelScreen : BaseScreen() {
    private val token = "LevelScreen"
    private lateinit var player: Player

    private lateinit var scoreLabel: Label
    private lateinit var personalBestLabel: Label
    private lateinit var missLabel: Label
    private lateinit var bonusTitleLabel: Label
    private lateinit var bonusLabel: Label
    private lateinit var motivationTitleLabel: Label
    private lateinit var motivationLabel: Label
    private lateinit var healthLabel: Label

    private lateinit var bonusTable: Table
    private lateinit var motivationTable: Table

    private var score = 0
    private var scoreAwarded = 0
    private var bonus = 0
    private var bonusMissModifier = 3
    private var motivationNumber = 20
    private var gameTime = 0f

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

    private lateinit var background: Background

    override fun initialize() {

        // entities
        background = Background(mainStage)
        player = Player(0f, 0f, mainStage)

        // audio
        BaseGame.levelMusic1!!.play()
        BaseGame.levelMusic1!!.volume = BaseGame.musicVolume
        BaseGame.levelMusic1!!.isLooping = true

        // ui
        val scoreTitleLabel = Label("Score", BaseGame.labelStyle)
        scoreTitleLabel.color = Color.RED
        scoreLabel = Label("$score", BaseGame.labelStyle)
        val scoreTable = Table()
        scoreTable.add(scoreTitleLabel).row()
        scoreTable.add(scoreLabel)

        healthLabel = Label("health: ${player.health}", BaseGame.labelStyle)
        healthLabel.setAlignment(Align.center)

        val personalBestTitleLabel = Label("Personal Best", BaseGame.labelStyle)
        personalBestTitleLabel.color = Color.RED
        personalBestLabel = Label("${BaseGame.highScore.toInt()}", BaseGame.labelStyle)
        val personalBestTable = Table()
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
        // bonusTable.addAction(Actions.forever(Actions.rotateBy(1f)))
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
        // motivationTable.addAction(Actions.forever(Actions.rotateBy(1f)))
        motivationTable.add(motivationLabel).row()
        motivationTable.add(motivationTitleLabel)
        val motivationTableTable = Table()
        motivationTableTable.add(motivationTable)

        uiTable.add(scoreTable).top().width(Gdx.graphics.width / 3f)
        uiTable.add(healthLabel).top().width(Gdx.graphics.width / 3f)
        uiTable.add(personalBestTable).top().width(Gdx.graphics.width / 3f).row()
        uiTable.add(bonusTableTable).expand().width(Gdx.graphics.width / 3f).padBottom(Gdx.graphics.height * 1 / 2f)
        uiTable.add(missLabel).expand().width(Gdx.graphics.width / 3f).padBottom(Gdx.graphics.height * 1 / 2f)
        uiTable.add(motivationTableTable).expand().width(Gdx.graphics.width / 3f).padBottom(Gdx.graphics.height * 1 / 2f)
        // uiTable.debug = true
    }

    override fun update(dt: Float) {
        gameTime += dt
        if (controlTimer < controlFrequency) {
            controlTimer += dt
        }

        // spawning
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

        // increasing difficulty
        when {
            gameTime < 30f -> spawnDifficulty = 1.25f
            gameTime < 50f -> spawnDifficulty = 1.5f
            gameTime < 70f -> spawnDifficulty = 1.75f
            gameTime < 90f -> spawnDifficulty = 2f
        }

        // asset updates
        healthLabel.setText("health: ${player.health}")
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {

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
        if (noEnemiesExist()) {
            if (keycode == Keys.LEFT)
                handleMiss(true)
            else if (keycode == Keys.RIGHT)
                handleMiss(false)
            background.act(player)
            return false
        }

        if (keycode == Keys.LEFT) {
            checkEnemyHit(true)
        } else if (keycode == Keys.RIGHT) {
            checkEnemyHit(false)
        }
        background.act(player)
        return false
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
            scoreLabel.setText("$score")
            if (bonus % motivationNumber == 0) setMotivation()
            if (BaseGame.highScore < score) {
                BaseGame.highScore = score.toFloat()
                personalBestLabel.setText("${BaseGame.highScore.toInt()}")
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
}
