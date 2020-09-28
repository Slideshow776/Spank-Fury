package no.sandramoen.spankfury.screens.gameplay

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
    val mediumSpawnFrequency = MathUtils.random(3f, 6f)

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

        for (enemy: BaseActor in BaseActor.getList(mainStage, Enemy::class.java.canonicalName)) {
            if (player.overlaps(enemy)) {
                player.setSpeed(0f)
                player.preventOverlap(enemy)
                hitEnemy(enemy as Enemy)
            }
        }
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        val worldCoordinates = camera.unproject(Vector3(screenX.toFloat(), screenY.toFloat(),0f))
        if (worldCoordinates.x <= player.x) // touch detected to the left of the player
            player.strike(strikeLeft = true)
        else
            player.strike(strikeLeft = false)

        if (BaseActor.count(mainStage, Enemy::class.java.canonicalName) == 0) { // hitting while no enemies => a miss
            displayMiss()
            return super.touchDown(screenX, screenY, pointer, button)
        }

        var closest = BaseGame.WORLD_WIDTH
        for (enemy: BaseActor in BaseActor.getList(mainStage, Enemy::class.java.canonicalName)) {
            val enemy = enemy as Enemy
            val distance = abs(enemy.x - player.x)
            if (distance < closest)
                closest = distance

            /*if (enemy.width + player.width <= distance) {
                displayMiss()
                break
            }*/
        }

        println("$token $closest, ${BaseGame.WORLD_WIDTH / 3.5}")
        if (closest >= BaseGame.WORLD_WIDTH / 3.5)
            displayMiss()
        return super.touchDown(screenX, screenY, pointer, button)
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
}
