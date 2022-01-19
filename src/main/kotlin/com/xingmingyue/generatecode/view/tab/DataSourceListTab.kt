package com.xingmingyue.generatecode.view.tab

import com.xingmingyue.generatecode.GlobalData
import com.xingmingyue.generatecode.entity.DataSourceEntity
import com.xingmingyue.generatecode.entity.DatabaseModel
import com.xingmingyue.generatecode.enums.EntityOperateMode
import com.xingmingyue.generatecode.util.StyleUtil
import com.xingmingyue.generatecode.view.base.BaseEntityOperateFragment
import javafx.collections.FXCollections
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import tornadofx.*
import java.sql.DriverManager

/**
 * 数据库列表Tab
 */
class DatabaseListTab : Fragment() {

    override val root = borderpane {
        top = hbox {
            button("新增").action {
                val dataSourceEntity = DataSourceEntity()
                find<EditorDatabase>(
                    mapOf(
                        EditorDatabase::entity to dataSourceEntity,
                        EditorDatabase::mode to EntityOperateMode.CREATE,
                        EditorDatabase::cellBack to {
                            GlobalData.dataSourceList.add(dataSourceEntity)
                            GlobalData.saveDataSourceList()
                        }
                    )
                ).openWindow()
            }
            button("导入").action {
                GlobalData.importData(GlobalData.dataSourceList,"*.datasource")
            }
            button("导出").action {
                GlobalData.exportData(GlobalData.dataSourceList,"*.datasource")
            }
        }
        center = tableview<DataSourceEntity> {
            items = GlobalData.dataSourceList
            columnResizePolicy = SmartResize.POLICY
            column("操作", DataSourceEntity::nameProperty).apply {
                cellFormat {
                    val cf = this@cellFormat
                    graphic = hbox {
                        button("编辑").action {
                            find<EditorDatabase>(
                                mapOf(
                                    EditorDatabase::entity to cf.rowItem,
                                    EditorDatabase::mode to EntityOperateMode.UPDATE,
                                    EditorDatabase::cellBack to {
                                        GlobalData.saveDataSourceList()
                                    }
                                )
                            ).openWindow()
                        }
                        button("删除").action { items.remove(rowItem) }
                    }
                }
            }.fixedWidth(140.0)
            column("名称", DataSourceEntity::nameProperty).prefWidth(200.0)
            column("类型", DataSourceEntity::typeProperty).prefWidth(100.0)
            column("备注", DataSourceEntity::remarkProperty)
        }
    }
}

class EditorDatabase : BaseEntityOperateFragment<DataSourceEntity>("新增数据源") {

    override val root = form {
        if (EntityOperateMode.UPDATE == mode) {
            this@EditorDatabase.title = "编辑数据源"
        }
        StyleUtil.setStyle(this)
        val model = DatabaseModel()
        model.item = entity
        fieldset {
            field("名称：") {
                textfield(model.name)
            }
            field("备注：") {
                textfield(model.remark)
            }
            field("类型：") {
                combobox(model.type, FXCollections.observableArrayList("MySQL", "Oracle"))
            }
            field("URL：") {
                textfield(model.url)
            }
            field("账号：") {
                textfield(model.username)
            }
            field("密码：") {
                passwordfield(model.password)
            }
        }
        hbox {
            button("关闭").action { close() }
            var commitText = "新增"
            if (EntityOperateMode.UPDATE == mode) {
                commitText = "保存"
                button("重置").action { model.rollback() }
            }
            button("测试").action {
                try {
                    when (model.type.value) {
                        "MySQL" -> Class.forName("com.mysql.cj.jdbc.Driver")
                        "Oracle" -> Class.forName("oracle.jdbc.OracleDriver")
                        else -> Alert(Alert.AlertType.ERROR, "未知类型！", ButtonType.OK).show()
                    }
                    DriverManager.getConnection(
                        model.url.value,
                        model.username.value,
                        model.password.value
                    ).close()
                    Alert(Alert.AlertType.NONE, "(*^▽^*)链接成功！", ButtonType.OK).show()
                } catch (e: Exception) {
                    Alert(Alert.AlertType.ERROR, e.message, ButtonType.OK).show()
                }

            }
            button(commitText) {
                enableWhen(model.dirty)
                action {
                    model.commit()
                    close()
                    cellBack()
                }
            }
        }
    }

}