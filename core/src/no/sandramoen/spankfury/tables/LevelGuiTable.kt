package no.sandramoen.spankfury.tables

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import no.sandramoen.spankfury.utils.BaseGame

class LevelGuiTable: Table() {
    private val token = "LevelGuiTable.kt"
    private var statsScoreLabel: Label
    private var personalBestLabel: Label
    private var personalBestTitleLabel: Label
    private var missLabel: Label
    private var bonusTitleLabel: Label
    private var bonusLabel: Label
    private var motivationTitleLabel: Label
    private var motivationLabel: Label

    private var scoreTable: Table
    private var healthTable: Table
    private var personalBestTable: Table
    private var bonusTable: Table
    private var motivationTable: Table

    private var health1: Image
    private var health2: Image
    private var health3: Image
    private var healths: Array<Image>
    private var healthIndex = 2

    init {
        val scoreTitleLabel = Label("Score", BaseGame.labelStyle)
        scoreTitleLabel.color = Color.RED
        scoreTitleLabel.setFontScale(1.1f)
        statsScoreLabel = Label("0", BaseGame.labelStyle)
        statsScoreLabel.setFontScale(1.1f)
        scoreTable = Table()
        scoreTable.add(scoreTitleLabel).row()
        scoreTable.add(statsScoreLabel).padTop(Gdx.graphics.height * .005f)

        healthTable = Table()
        health1 = Image(BaseGame.textureAtlas!!.findRegion("heart"))
        health1.color.a = 0f
        health2 = Image(BaseGame.textureAtlas!!.findRegion("heart"))
        health2.color.a = 0f
        health3 = Image(BaseGame.textureAtlas!!.findRegion("heart"))
        health3.color.a = 0f
        healths = Array()
        healths.add(health1)
        healths.add(health2)
        healths.add(health3)
        for (i in 0 until healths.size)
            healths[i].addAction(
                Actions.sequence(
                Actions.delay(i / 1.75f + 1f), // bigger number yields faster animations
                Actions.fadeIn(.5f)
            ))
        val healthWidth = Gdx.graphics.width * .045f
        healthTable.add(health1).width(healthWidth).height(healthWidth).padRight(Gdx.graphics.width * .02f)
        healthTable.add(health2).width(healthWidth).height(healthWidth).padRight(Gdx.graphics.width * .02f)
        healthTable.add(health3).width(healthWidth).height(healthWidth)

        personalBestTitleLabel = Label("Personal Best", BaseGame.labelStyle)
        personalBestTitleLabel.color = Color.RED
        personalBestTitleLabel.setFontScale(1.1f)
        personalBestLabel = Label("${BaseGame.highScore}", BaseGame.labelStyle)
        personalBestLabel.setFontScale(1.1f)
        personalBestTable = Table()
        personalBestTable.add(personalBestTitleLabel).row()
        personalBestTable.add(personalBestLabel).padTop(Gdx.graphics.height * .005f)

        bonusLabel = Label("0", BaseGame.labelStyle)
        bonusLabel.setFontScale(3f)
        bonusLabel.color = Color.YELLOW
        bonusLabel.color.a = 0f
        bonusTitleLabel = Label("x BONUS", BaseGame.labelStyle)
        bonusTitleLabel.setFontScale(1.5f)
        bonusTitleLabel.color.a = 0f
        bonusTable = Table()
        bonusTable.isTransform = true
        bonusTable.originX = Gdx.graphics.width / 15f
        bonusTable.originY = Gdx.graphics.height / 15f
        bonusTable.add(bonusLabel).row()
        bonusTable.add(bonusTitleLabel)
        val bonusTableTable = Table()
        bonusTableTable.add(bonusTable)

        missLabel = Label("miss!", BaseGame.labelStyle)
        missLabel.setAlignment(Align.center)
        missLabel.color = Color.RED
        missLabel.color.a = 0f
        missLabel.setFontScale(2f)

        motivationLabel = Label("0", BaseGame.labelStyle)
        motivationLabel.setFontScale(3f)
        motivationLabel.color.a = 0f
        motivationTitleLabel = Label("xRAD", BaseGame.labelStyle)
        motivationTitleLabel.setFontScale(1.5f)
        motivationTitleLabel.color = Color.CYAN
        motivationTitleLabel.color.a = 0f
        motivationTable = Table()
        motivationTable.isTransform = true
        motivationTable.originX = Gdx.graphics.width * 1 / 24f
        motivationTable.originY = Gdx.graphics.height * 1 / 24f
        motivationTable.add(motivationLabel).row()
        motivationTable.add(motivationTitleLabel)
        val motivationTableTable = Table()
        motivationTableTable.add(motivationTable)

        setFillParent(true)
        add(scoreTable).top().width(Gdx.graphics.width / 3f).padTop(Gdx.graphics.height * .01f)
        add(healthTable).top().width(Gdx.graphics.width / 3f).padTop(Gdx.graphics.height * .01f)
        add(personalBestTable).top().width(Gdx.graphics.width / 3f).padTop(Gdx.graphics.height * .01f).row()
        add(bonusTableTable).expand().width(Gdx.graphics.width / 3f).padBottom(Gdx.graphics.height * .5f)
        add(missLabel).expand().width(Gdx.graphics.width / 3f).padBottom(Gdx.graphics.height * .5f)
        add(motivationTableTable).expand().width(Gdx.graphics.width / 3f).padBottom(Gdx.graphics.height * .5f).row()
        // debug = true
    }

    fun handleMiss(bonus: Int) {
        bonusLabel.setText("$bonus")
        animateBonuses(bonusTable, bonusLabel, bonusTitleLabel)
        missLabel.addAction(Actions.sequence(
            Actions.fadeIn(.3f),
            Actions.fadeOut(.3f)
        ))
    }

    fun updateBonus(bonus: Int) {
        bonusLabel.setText("$bonus")
        animateBonuses(bonusTable, bonusLabel, bonusTitleLabel)
    }

    fun setMotivation(bonus: Int) {
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

    fun subtractHealth() {
        healths[healthIndex].addAction(Actions.fadeOut(1f))
        healthIndex--
    }

    fun setScoreLabel(score: Int) { statsScoreLabel.setText("$score") }
    fun setPersonalBestLabel() { personalBestLabel.setText("${BaseGame.highScore}") }

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
}