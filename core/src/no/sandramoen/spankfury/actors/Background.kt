package no.sandramoen.spankfury.actors

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Array
import no.sandramoen.spankfury.utils.BaseActor
import no.sandramoen.spankfury.utils.BaseGame

class Background(stage: Stage) {
    private val token = "Background"

    // background
    private val background1 = BaseActor(0f, 0f, stage)
    private val background2 = BaseActor(0f, 0f, stage)
    private val background3 = BaseActor(0f, 0f, stage)

    // props
    private var candle1 = Candle(12f, 43f, stage)
    private var candle2 = Candle(43f, 45f, stage)
    private var candle3 = Candle(65f, 42.75f, stage)
    private var candle4 = Candle(95f, 44f, stage)
    private var candle5 = Candle(204.5f, 60f, stage)
    private var speaker1 = Speaker(1f, 76.5f, stage)
    private var speaker2 = Speaker(91f, 76.5f, stage)

    private var propList: Array<BaseActor> = Array<BaseActor>()

    private var backgroundWidth = BaseGame.WORLD_WIDTH * 2.5f

    init {
        // props
        propList.add(candle1)
        propList.add(candle2)
        propList.add(candle3)
        propList.add(candle4)
        propList.add(candle5)
        propList.add(speaker1)
        propList.add(speaker2)
        for (actor in propList) actor.zIndex = 3

        // backgrounds
        background1.loadImage("background1")
        background1.setSize(backgroundWidth, BaseGame.WORLD_HEIGHT + 5)

        background2.loadImage("background1")
        background2.setSize(backgroundWidth, BaseGame.WORLD_HEIGHT + 5)
        background2.setPosition(background1.width, 0f)

        background3.loadImage("background1")
        background3.setSize(backgroundWidth, BaseGame.WORLD_HEIGHT + 5)
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

        for (actor in propList) moveActors(actor, player)
    }

    fun setOpacity(opacity: Float, duration: Float) {
        background1.addAction(Actions.alpha(opacity, duration))
        background2.addAction(Actions.alpha(opacity, duration))
        background3.addAction(Actions.alpha(opacity, duration))
        for (actor in propList) actor.addAction(Actions.alpha(opacity, duration))
        speaker1.renderNotesWithOpacity = opacity
        speaker2.renderNotesWithOpacity = opacity
    }

    private fun moveActors(actor: BaseActor, player: Player) {
        actor.zIndex = 3
        if (actor.x + backgroundWidth / 2 < player.x) {
            actor.x += backgroundWidth
        } else if (actor.x - backgroundWidth / 2 > player.x) {
            actor.x -= backgroundWidth
        }
    }
}