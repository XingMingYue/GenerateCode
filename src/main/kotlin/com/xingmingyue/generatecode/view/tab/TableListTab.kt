package com.xingmingyue.generatecode.view.tab

import com.xingmingyue.generatecode.GlobalData
import com.xingmingyue.generatecode.entity.DataSourceEntity
import com.xingmingyue.generatecode.entity.database.DatabaseEntity
import com.xingmingyue.generatecode.entity.database.DatabaseTableEntity
import com.xingmingyue.generatecode.util.DatabaseUtil
import com.xingmingyue.generatecode.util.StyleUtil
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.scene.control.SelectionMode
import tornadofx.*

class TableListTab : Fragment() {

    override val root = borderpane {
        top = hbox {
            button("新增").action {

            }
            button("导入").action {
                GlobalData.importData(GlobalData.internalTableEntityList, "*.table")
            }
            button("导出").action {
                GlobalData.exportData(GlobalData.internalTableEntityList, "*.table")
            }
            button("SQL解析").action {

            }
            button("数据源导入").action {
                find<ImportTableFromDataSource>().openWindow()
            }
            button("PDMan导入").action {

            }
            button("CHINER导入").action {

            }

        }
        center = tableview<DatabaseTableEntity> {
            items = GlobalData.internalTableEntityList
            columnResizePolicy = SmartResize.POLICY
            column("操作", DatabaseTableEntity::nameProperty).apply {
                cellFormat {
                    val cf = this@cellFormat
                    graphic = hbox {
                        button("编辑").action {
                            println(cf.rowItem.name)
                        }
                        button("删除").action { items.remove(rowItem) }
                    }
                }
            }.fixedWidth(140.0)
            column("表名", DatabaseTableEntity::nameProperty).prefWidth(200.0)
            column("中文名称", DatabaseTableEntity::logicNameProperty)
        }
    }
}

class ImportTableFromDataSource : Fragment("从数据源导入") {

    override val root = hbox {
        StyleUtil.setStyle(this)
        prefWidth = 586.0
        // 数据库列表
        val databaseList = FXCollections.observableList(ArrayList<DatabaseEntity>())

        // 表列表
        val tableList = FXCollections.observableList(ArrayList<DatabaseTableEntity>())

        // 选择数据源
        val selectedDataSource = SimpleObjectProperty<DataSourceEntity>()

        // 选择数据库
        val selectedDatabase = SimpleObjectProperty<DatabaseEntity>()

        // 选择表
        val selectedTableList = FXCollections.observableList(ArrayList<DatabaseTableEntity>())

        form {
            fieldset {
                field("数据源：") {
                    combobox(selectedDataSource, GlobalData.dataSourceList).apply {
                        // 选择的数据源变更时，查询覆盖数据库列表
                        valueProperty().addListener { _, _, newValue ->
                            DatabaseUtil.initHikariDataSource(newValue)
                            databaseList.clear()
                            databaseList.addAll(DatabaseUtil.databaseList())
                        }
                    }
                }
                field("数据库：") {
                    combobox(selectedDatabase, databaseList).apply {
                        valueProperty().addListener { _, _, newValue ->
                            // 选择的数据库变更时，查询覆盖表列表
                            tableList.clear()
                            tableList.addAll(DatabaseUtil.databasesTableList(newValue.name))
                        }
                    }
                }
                field("数据表：") {
                    tableview<DatabaseTableEntity> {
                        prefWidth = 500.0
                        columnResizePolicy = SmartResize.POLICY
                        // 允许多选
                        selectionModel.selectionMode = SelectionMode.MULTIPLE
                        items = tableList
                        column("表名", DatabaseTableEntity::nameProperty).prefWidth(200.0)
                        column("中文名称", DatabaseTableEntity::logicNameProperty)
                        selectionModel.selectedItems.addListener(ListChangeListener {
                            selectedTableList.clear()
                            selectedTableList.addAll(selectionModel.selectedItems)
                        })
                    }
                }
                hbox {
                    button("关闭").action { close() }
                    button("确定").apply {
                        isDisable = true
                        selectedTableList.addListener(ListChangeListener {
                            isDisable = selectedTableList.isEmpty()
                        })
                        action {
                            for (databaseTableEntity in selectedTableList) {
                                databaseTableEntity.fieldList.addAll(
                                    DatabaseUtil.databasesTableFieldList(
                                        selectedDatabase.get().name,
                                        databaseTableEntity.name
                                    )
                                )
                            }
                            GlobalData.internalTableEntityList.addAll(selectedTableList)
                            GlobalData.saveInternalTableData()
                            close()
                        }
                    }
                }
            }
        }
    }
}
