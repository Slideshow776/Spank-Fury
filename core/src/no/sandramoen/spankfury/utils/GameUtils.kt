package no.sandramoen.spankfury.utils

import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label

class GameUtils {
    private val token = "GameUtils.kt"
    companion object {
        fun isTouchDownEvent(e: Event): Boolean { // Custom type checker
            return e is InputEvent && e.type == InputEvent.Type.touchDown
        }

        fun saveGameState() {
            try {
                BaseGame.prefs!!.putFloat("highScore", BaseGame.highScore)
            } catch (error: Error) {
                BaseGame.prefs!!.putFloat("highScore", Float.MAX_VALUE)
            }
            BaseGame.prefs!!.flush()
        }

        fun pulseLabel(label: Label, lowestAlpha: Float = .5f, duration: Float = .5f) {
            label.addAction(Actions.forever(Actions.sequence(
                    Actions.alpha(1f, .5f),
                    Actions.alpha(.5f, .5f)
            )))
        }

        fun setMusicVolumeAndPlay(music: Music?, volume: Float) {
            music!!.volume = volume
            music!!.play()
        }
    }
}
