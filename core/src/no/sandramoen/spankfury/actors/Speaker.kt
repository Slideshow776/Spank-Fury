package no.sandramoen.spankfury.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Align
import no.sandramoen.spankfury.utils.BaseActor
import no.sandramoen.spankfury.utils.BaseGame

class Speaker(x: Float, y: Float, s: Stage) : BaseActor(x, y, s) {
    private val token = "Speaker.kt"
    private var noteX = x
    var renderNotesWithOpacity = 1f

    init {
        // video
        loadImage("speaker")

        // properties
        setSize(
            (BaseGame.WORLD_WIDTH / 10) * BaseGame.scale,
            (BaseGame.WORLD_HEIGHT / 10) * (Gdx.graphics.width.toFloat() / Gdx.graphics.height) * BaseGame.scale
        )

        // animation
        setOrigin(Align.center)
        rotateBy(-1f)
        addAction(
            Actions.forever(
                Actions.parallel(
                    Actions.sequence( // wobbling back and forth
                        Actions.rotateBy(2f, 2f),
                        Actions.rotateBy(-2f, 2f)
                    ),
                    Actions.sequence( // bouncing in scale
                        Actions.scaleTo(1.05f, 1.05f, .5f),
                        Actions.scaleTo(1f, 1f, .5f),
                        Actions.scaleTo(1.05f, 1.05f, .5f),
                        Actions.scaleTo(1f, 1f, .5f),
                        Actions.scaleTo(1.05f, 1.05f, .5f),
                        Actions.scaleTo(1f, 1f, .5f),
                        Actions.scaleTo(1.05f, 1.05f, .5f),
                        Actions.scaleTo(1f, 1f, .5f)
                    ),
                    Actions.sequence( // create notes
                        Actions.run { Note(noteX + 2, y + 2, stage, renderNotesWithOpacity) },
                        Actions.delay(1f),
                        Actions.run { Note(noteX + 2, y + 2, stage, renderNotesWithOpacity) },
                        Actions.delay(1f),
                        Actions.run { Note(noteX + 2, y + 2, stage, renderNotesWithOpacity) }
                    )
                )
            )
        )
    }

    override fun act(dt: Float) {
        super.act(dt)
        noteX = x
    }
}
