package com.xingmingyue.generatecode.view

import jfxtras.styles.jmetro.JMetro
import jfxtras.styles.jmetro.JMetroStyleClass
import jfxtras.styles.jmetro.Style
import tornadofx.*

class MainView : View("CodeGenerator") {
    override val root = hbox {
        JMetro(this, Style.DARK)
        styleClass.add(JMetroStyleClass.BACKGROUND)
        importStylesheet("/css/cover_jmetro_skin.css")
        label("Hello World")
    }
}
