package no.sandramoen.spankfury.screens.gameplay

import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import no.sandramoen.spankfury.actors.EasyEnemy
import no.sandramoen.spankfury.actors.Enemy
import no.sandramoen.spankfury.actors.MediumEnemy
import no.sandramoen.spankfury.actors.Player
import no.sandramoen.spankfury.utils.BaseActor
import no.sandramoen.spankfury.utils.BaseGame
import no.sandramoen.spankfury.utils.BaseScreen
import kotlin.math.abs

class LevelScreen : BaseScreen() {
    private val token = "LevelScreen"
    private lateinit var player: Player

    private lateinit var scoreLabel: Label
    private lateinit var missLabel: Label

    private var score: Int = 0
    var easySpawnTimer = 0f
    private val easySpawnFrequency = MathUtils.random(2f, 3f)
    var mediumSpawnTimer = 0f
    private val mediumSpawnFrequency = MathUtils.random(3f, 6f)

    override fun initialize() {
        player = Player(0f, 0f, mainStage)

        // ui
        scoreLabel = Label("score: $score", BaseGame.labelStyle)
        missLabel = Label("miss!", BaseGame.labelStyle)
        missLabel.color.a = 0f
        uiTable.add(scoreLabel).expandY().top().row()
        uiTable.add(missLabel).expandY().bottom()
        // uiTable.debug = true
    }

    override fun update(dt: Float) {
        easySpawnTimer += dt
        mediumSpawnTimer += dt
        if (easySpawnTimer >= easySpawnFrequency) {
            EasyEnemy(0f, 0f, mainStage, player)
            easySpawnTimer = 0f
        }
        if (mediumSpawnTimer >= mediumSpawnFrequency) {
            MediumEnemy(0f, 0f, mainStage, player)
            mediumSpawnTimer = 0f
        }
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (enemiesExist()) return false

        // check which way player is hitting
        val worldCoordinates = camera.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0f))
        val hitLeft = (worldCoordinates.x <= player.x) // touch detected to the left of the player

        checkEnemyHit(hitLeft)
        return false
    }


    override fun keyDown(keycode: Int): Boolean { // desktop controls
        if (enemiesExist()) return false

        if (keycode == Keys.LEFT) {
            checkEnemyHit(true)
        } else if (keycode == Keys.RIGHT) {
            checkEnemyHit(false)
        }
        return false
    }

    /*
    * Core gameplay:
    * Checks if enemy may be hit or not, and triggers appropriate player, enemy and UI behaviour
    * */
    private fun checkEnemyHit(hitLeft: Boolean) {
        println("$token: checkEnemyHit()")
        for (enemy: BaseActor in BaseActor.getList(mainStage, Enemy::class.java.canonicalName)) {
            val enemy = enemy as Enemy
            if (!enemy.alive) break

            val distance = enemy.x - player.x
            val inRange = enemy.width + player.width > abs(distance)
            val onLeftSide = enemy.x <= player.x

            if ((hitLeft && inRange && onLeftSide) || (!hitLeft && inRange && !onLeftSide)) {
                player.hit(distance)
                hitEnemy(enemy)
                break
            } else {
                displayMiss()
            }
        }
    }

    private fun displayMiss() {
        missLabel.addAction(Actions.sequence(
                Actions.fadeIn(.2f),
                Actions.fadeOut(.2f)
        ))
    }

    private fun hitEnemy(enemy: Enemy) {
        if (enemy.hit()) { // if this kills the enemy
            score++
            scoreLabel.setText("score: $score")
        }
    }

    private fun enemiesExist(): Boolean {
        return (BaseActor.count(mainStage, Enemy::class.java.canonicalName) == 0)
    }
}
