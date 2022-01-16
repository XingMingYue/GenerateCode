package com.xingmingyue.generatecode.util

import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Parent
import javafx.stage.Stage
import jfxtras.styles.jmetro.JMetro
import jfxtras.styles.jmetro.JMetroStyleClass
import jfxtras.styles.jmetro.Style
import tornadofx.*

/**
 * 样式工具类
 */
object StyleUtil {

    /**
     * 风格
     */
    private val styleProperty = SimpleObjectProperty(Style.DARK)

    fun setStyle(stage: Stage, addListener: Boolean = false) {
        importStylesheetAndAddListener(addListener, JMetro(stage.scene, styleProperty.get()))
    }

    fun setStyle(parent: Parent, addListener: Boolean = false) {
        importStylesheetAndAddListener(addListener, JMetro(parent, styleProperty.get()))
        parent.styleClass.add(JMetroStyleClass.BACKGROUND)
    }

    private fun importStylesheetAndAddListener(addListener: Boolean, jMetro: JMetro) {
        importStylesheet("/css/cover_jmetro_skin.css")
        if (addListener) {
            styleProperty.addListener { _, _, newValue ->
                jMetro.style = newValue
                importStylesheet("/css/cover_jmetro_skin.css")
            }
        }
    }

}