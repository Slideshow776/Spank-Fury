package no.sandramoen.spankfury.actors

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import no.sandramoen.spankfury.utils.BaseGame

class SwapEnemy(x: Float, y: Float, s: Stage, player: Player) : Enemy(x, y, s, player) {
    private val token = "SwapEnemy.kt"
    override var health = 3

    init {
        setSize(BaseGame.WORLD_WIDTH / 14, BaseGame.WORLD_HEIGHT / 3)
        color = Color.YELLOW
        originalColor = Color.YELLOW
        originalWidth = width
        originalHeight = height
        points = 30
    }

    override fun struck(enableSound: Boolean): Boolean { // returns true if enemy died
        if (enableSound) BaseGame.hitSound1!!.play(BaseGame.soundVolume)
        health--
        if (health <= 0)
            return handleDeath()
        swapSide()
        return false
    }

    private fun swapSide() {
        if (x <= player.x) { // if on left
            addAction(Actions.moveBy(2 * width, 0f, .1f)) // move to right side
        } else {
            addAction(Actions.moveBy(-2 * width, 0f, .1f)) // move to left side
        }
        flip()
    }

}