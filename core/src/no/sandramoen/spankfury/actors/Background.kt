package no.sandramoen.spankfury.actors

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import no.sandramoen.spankfury.utils.BaseActor
import no.sandramoen.spankfury.utils.BaseGame

class Background(s: Stage) {
    private val token = "Background"

    private val background1 = BaseActor(0f, 0f, s)
    private val background2 = BaseActor(0f, 0f, s)
    private val background3 = BaseActor(0f, 0f, s)

    init {
        background1.loadImage("background1")
        background1.setSize(BaseGame.WORLD_WIDTH * 2.5f, BaseGame.WORLD_HEIGHT + 5)

        background2.loadImage("background1")
        background2.setSize(BaseGame.WORLD_WIDTH * 2.5f, BaseGame.WORLD_HEIGHT + 5)
        background2.setPosition(background1.width, 0f)

        background3.loadImage("background1")
        background3.setSize(BaseGame.WORLD_WIDTH * 2.5f, BaseGame.WORLD_HEIGHT + 5)
        background2.setPosition(-background1.width, 0f)
    }

    fun act(player: Player) {
        if (player.x > background1.x && player.x + player.width <= background1.x + background1.width) {
            background2.setPosition(background1.x + background1.width, 0f)
            background3.setPosition(background1.x - background1.width, 0f)
        } else if (player.x > background2.x && player.x + player.width <= background2.x + background2.width) {
            background1.setPosition(background2.x + background2.width, 0f)
            background3.setPosition(background2.x - background2.width, 0f)
        } else if (player.x > background3.x && player.x + player.width <= background3.x + background3.width) {
            background1.setPosition(background3.x + background3.width, 0f)
            background2.setPosition(background3.x - background3.width, 0f)
        }
    }

    fun setOpacity(opacity: Float, duration: Float) {
        background1.addAction(Actions.alpha(opacity, duration))
        background2.addAction(Actions.alpha(opacity, duration))
        background3.addAction(Actions.alpha(opacity, duration))
    }
}