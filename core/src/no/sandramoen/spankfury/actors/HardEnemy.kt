package no.sandramoen.spankfury.actors

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import no.sandramoen.spankfury.utils.BaseGame

class HardEnemy(x: Float, y: Float, s: Stage, player: Player) : Enemy(x, y, s, player) {
    private val token = "HardEnemy.kt"
    override var health = 4

    init {
        setSize(BaseGame.WORLD_WIDTH / 9, BaseGame.WORLD_HEIGHT / 2.5f)
        color = Color.PURPLE
        originalWidth = width
        originalHeight = height
        points = 40
    }
}