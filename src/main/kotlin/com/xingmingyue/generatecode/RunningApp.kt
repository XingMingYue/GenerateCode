package com.xingmingyue.generatecode

import com.xingmingyue.generatecode.util.StyleUtil
import com.xingmingyue.generatecode.view.MainView
import javafx.stage.Stage
import tornadofx.*

fun main() {
    launch<GenerateCodeApp>()
}

class GenerateCodeApp : App(MainView::class) {
    init {
        GlobalData.initData()
    }

    override fun start(stage: Stage) {
        super.start(stage)
        stage.apply {
            minWidth = 827.0
            width = 827.0
            minHeight = 578.0
            height = 578.0
        }
        StyleUtil.setStyle(stage, addListener = true)
    }
}