package com.xingmingyue.generatecode.view

import com.xingmingyue.generatecode.view.tab.DatabaseListTab
import com.xingmingyue.generatecode.view.tab.SettingTab
import com.xingmingyue.generatecode.view.tab.TableListTab
import com.xingmingyue.generatecode.view.tab.TemplateListTab
import javafx.scene.control.TabPane
import jfxtras.styles.jmetro.JMetroStyleClass
import tornadofx.*
import kotlin.system.exitProcess

class MainView : View("CodeGenerator") {
    override val root = borderpane {
        styleClass.add(JMetroStyleClass.BACKGROUND)
        top = menubar {
            menu("文件") {
                item("导出数据").action {
                    println("导出数据")
                }
                item("导入数据").action {
                    println("导入数据")
                }
                separator()
                item("设置").action {
                }
                separator()
                item("退出").action { exitProcess(0) }
            }

        }
        center = tabpane {
            tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
            tab("代码模板") { this += find<TemplateListTab>() }
            tab("数据表") { this += find<TableListTab>() }
            tab("数据源") { this += find<DatabaseListTab>() }
            tab("设置") { this += find<SettingTab>() }
        }
    }
}
