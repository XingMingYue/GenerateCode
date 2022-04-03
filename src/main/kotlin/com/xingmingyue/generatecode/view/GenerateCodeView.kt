package com.xingmingyue.generatecode.view

import cn.hutool.core.io.FileUtil
import com.xingmingyue.generatecode.GlobalData
import com.xingmingyue.generatecode.entity.DataSourceEntity
import com.xingmingyue.generatecode.entity.TemplateEntity
import com.xingmingyue.generatecode.entity.TemplateFileEntity
import com.xingmingyue.generatecode.entity.database.DatabaseEntity
import com.xingmingyue.generatecode.entity.database.DatabaseTableEntity
import com.xingmingyue.generatecode.entity.tablePredefinedAttribute
import com.xingmingyue.generatecode.enums.OutputMethod
import com.xingmingyue.generatecode.util.FreemarkerUtil
import com.xingmingyue.generatecode.util.StyleUtil
import com.xingmingyue.generatecode.util.SystemFileUtil
import com.xingmingyue.generatecode.util.DatabaseUtil
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import tornadofx.*
import java.io.File


class GenerateCodeView : Fragment("生成代码") {

    val template: TemplateEntity by param()

    // 数据库集合
    private val databaseList = FXCollections.observableArrayList<DatabaseEntity>()

    // 表集合
    private val tableList = FXCollections.observableArrayList<DatabaseTableEntity>()

    // 选择的数据源
    private val selectedDataSource = SimpleObjectProperty<DataSourceEntity>()

    // 选择的数据库
    private val selectedDatabase = SimpleObjectProperty<DatabaseEntity>()

    // 输出方式
    private var outputMethod = OutputMethod.POP_UP_DISPLAY

    override val root = borderpane {
        StyleUtil.setStyle(this)
        center = form {
            prefWidth = 400.0
            prefHeight = 300.0

            val selectedTableSource = SimpleStringProperty("数据表")

            // 选择的数据表
            val selectedTable = SimpleObjectProperty<DatabaseTableEntity>()

            fieldset {
                field("模板名称：") {
                    text(template.name)
                }
                field("表来源：") {
                    val tableSourceObList = FXCollections.observableArrayList("数据表", "数据源")
                    combobox(selectedTableSource, tableSourceObList).apply {
                        tableList.addAll(GlobalData.internalTableEntityList)
                        valueProperty().addListener { _, _, newValue ->
                            if ("数据表" == newValue) {
                                tableList.clear()
                                tableList.addAll(GlobalData.internalTableEntityList)
                            } else {
                                tableList.clear()
                            }
                        }
                    }
                }
                field("数据源：") {
                    combobox(selectedDataSource, GlobalData.dataSourceList).apply {
                        valueProperty().addListener { _, _, newValue ->
                            DatabaseUtil.initHikariDataSource(newValue)
                            databaseList.clear()
                            databaseList.addAll(DatabaseUtil.databaseList())
                        }
                    }
                    this.hide()
                    selectedTableSource.addListener { _, _, newValue ->
                        if ("数据源" == newValue) {
                            this.show()
                        } else {
                            this.hide()
                        }
                    }
                }

                field("数据库：") {
                    combobox(selectedDatabase, databaseList).apply {
                        valueProperty().addListener { _, _, newValue ->
                            // 清空旧列表，查询添加所有新数据表集合
                            tableList.clear()
                            tableList.addAll(DatabaseUtil.databasesTableList(newValue.name))
                        }
                    }
                    this.hide()
                    selectedTableSource.addListener { _, _, newValue ->
                        if ("数据源" == newValue) {
                            this.show()
                        } else {
                            this.hide()
                        }
                    }
                }
                field("数据表：") {
                    combobox(selectedTable, tableList).apply {
                        visibleRowCount = 15
                    }
                }
                field("输出方式：") {
                    togglegroup {
                        radiobutton("写入文件").apply { userData = OutputMethod.WRITE_FILE }
                        radiobutton("弹窗显示").apply { userData = OutputMethod.POP_UP_DISPLAY }
                        selectedToggleProperty().addListener { _, _, newValue ->
                            outputMethod = newValue.userData as OutputMethod
                        }
                    }
                }
            }
            bottom = hbox {
                button("关闭").action { close() }
                button("确定").action {
                    println(selectedTableSource.get())
                    var selectFolder: File? = null
                    if (OutputMethod.WRITE_FILE == outputMethod) {
                        selectFolder =
                            (SystemFileUtil.selectFolder("生成目录", File(File("").absolutePath)) ?: return@action)
                    }
                    val dataMap: MutableMap<String, Any> = HashMap()
                    val tableEntity = selectedTable.get()
                    dataMap["table"] = tableEntity
                    if (selectFolder != null) {
                        dataMap["_selectFolderAbsolutePath"] = selectFolder.absolutePath
                    }
                    GlobalData.predefinedAttributeList.tablePredefinedAttribute().forEach {
                        tableEntity.predefinedAttribute[it.attributeKey] = FreemarkerUtil.process(
                            it.attributeExpression,
                            dataMap
                        )
                    }
                    val templateFileOutputList = ArrayList<TemplateFileEntity>()
                    for (templateFileEntity in template.templateFileList) {
                        val fileName =
                            FreemarkerUtil.process(templateFileEntity.fileName, dataMap)
                        val fileContent =
                            FreemarkerUtil.process(templateFileEntity.content, dataMap)
                        val outputRelativePath =
                            FreemarkerUtil.process(templateFileEntity.outputRelativePath, dataMap)
                        templateFileOutputList.add(
                            TemplateFileEntity(
                                templateFileEntity.name,
                                fileName,
                                templateFileEntity.remark,
                                fileContent,
                                outputRelativePath
                            )
                        )
                    }

                    if (OutputMethod.WRITE_FILE == outputMethod) {
                        if (selectFolder != null) {
                            for (templateFileEntity in templateFileOutputList) {
                                val path =
                                    selectFolder.absolutePath + File.separator + templateFileEntity.outputRelativePath + File.separator + templateFileEntity.fileName
                                // 写数据
                                FileUtil.writeUtf8String(templateFileEntity.content, path)
                            }
                        }
                        Alert(Alert.AlertType.NONE, "(*^▽^*)代码已生成！", ButtonType.OK).show()
                    } else {
                        find<TemplateFileOutputListView>(
                            mapOf(
                                TemplateFileOutputListView::templateFileOutputList to templateFileOutputList
                            )
                        ).openWindow()
                    }
                    close()
                }
            }
        }
    }
}

/**
 * 模板文件输出查看
 */
class TemplateFileOutputListView : Fragment("模板文件输出") {

    val templateFileOutputList: ArrayList<TemplateFileEntity> by param()

    private val objectProperty = TemplateFileEntity()

    override val root = borderpane {
        StyleUtil.setStyle(this)
        setPrefSize(728.0, 586.0)
        left = listview<TemplateFileEntity> {
            items = templateFileOutputList.asObservable()
            cellFormat {
                styleClass.addAll("table-cell")
                text = this.item.fileName
            }
            selectionModel.selectedItemProperty().addListener { _, _, newValue ->
                objectProperty.name = newValue.name
                objectProperty.fileName = newValue.fileName
                objectProperty.remark = newValue.remark
                objectProperty.content = newValue.content
                objectProperty.outputRelativePath = newValue.outputRelativePath
            }
        }
        center = borderpane {
            top = form {
                fieldset {
                    field("名称：") {
                        textfield(objectProperty.nameProperty())
                    }
                    field("文件名称：") {
                        textfield(objectProperty.fileNameProperty())
                    }
                    field("备注：") {
                        textfield(objectProperty.remarkProperty())
                    }
                    field("输出相对路径：") {
                        textfield(objectProperty.outputRelativePathProperty())
                    }
                }
            }
            center = textarea(objectProperty.contentProperty())
        }
    }
}

