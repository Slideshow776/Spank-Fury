package no.sandramoen.spankfury.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Widget

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
            BaseGame.prefs!!.putBoolean("vibrations", BaseGame.vibrations)
            BaseGame.prefs!!.putFloat("musicVolume", BaseGame.musicVolume)
            BaseGame.prefs!!.putFloat("soundVolume", BaseGame.soundVolume)
            BaseGame.prefs!!.flush()
        }

        fun loadGameState() {
            BaseGame.prefs = Gdx.app.getPreferences("spankFuryGameState")
            BaseGame.highScore = BaseGame.prefs!!.getFloat("highScore")
            BaseGame.vibrations = BaseGame.prefs!!.getBoolean("vibrations")
            BaseGame.musicVolume = BaseGame.prefs!!.getFloat("musicVolume")
            BaseGame.soundVolume = BaseGame.prefs!!.getFloat("soundVolume")
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

        fun setMusicVolume() {
            BaseGame.levelMusic1!!.volume = BaseGame.musicVolume
        }

        fun enableActorsWithDelay(actor: Actor) {
            actor.addAction(Actions.sequence(
                    Actions.delay(.5f),
                    Actions.run { actor.touchable = Touchable.enabled }
            ))
        }
    }
}
