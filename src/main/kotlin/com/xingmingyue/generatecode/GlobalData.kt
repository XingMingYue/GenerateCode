package com.xingmingyue.generatecode

import cn.hutool.core.io.FileUtil
import com.xingmingyue.generatecode.config.DataFilePath
import com.xingmingyue.generatecode.entity.DataSourceEntity
import com.xingmingyue.generatecode.entity.PredefinedAttributeEntity
import com.xingmingyue.generatecode.entity.TemplateEntity
import com.xingmingyue.generatecode.entity.database.DatabaseTableEntity
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.stage.FileChooser
import javafx.stage.Stage
import tornadofx.*
import java.io.File

/**
 * 数据工具类
 */
object GlobalData {

    /**
     * 模板列表
     */
    val templateList: ObservableList<TemplateEntity> = FXCollections.observableArrayList()

    /**
     * 内部列表
     */
    val internalTableEntityList: ObservableList<DatabaseTableEntity> = FXCollections.observableArrayList()

    /**
     * 数据源列表
     */
    val dataSourceList: ObservableList<DataSourceEntity> = FXCollections.observableArrayList()

    /**
     * 预定义属性列表
     */
    val predefinedAttributeList: ObservableList<PredefinedAttributeEntity> = FXCollections.observableArrayList()

    /**
     * 初始化数据
     */
    fun initData() {
        // 模板
        initData(DataFilePath.templateFilePath, templateList)

        // 内部表
        initData(DataFilePath.internalTableFilePath, internalTableEntityList)

        // 数据源
        initData(DataFilePath.dataSourceListFilePath, dataSourceList)

        // 预定义属性
        initData(DataFilePath.predefinedAttributeListFilePath, predefinedAttributeList)
    }

    /**
     * 初始化数据
     */
    private inline fun <reified T : JsonModel> initData(filePath: String, list: ObservableList<T>) {
        val dataStr = FileUtil.readUtf8String(FileUtil.touch(filePath))
        if (dataStr != null && dataStr.isNotEmpty()) {
            list.addAll(dataStr.byteInputStream().toJSONArray().toModel())
        }
    }

    /**
     * 导入数据
     */
    inline fun <reified T : JsonModel> importData(list: ObservableList<T>, extensions: String) {
        val fileChooser = FileChooser()
        fileChooser.title = "请选择文件"
        fileChooser.extensionFilters.add(FileChooser.ExtensionFilter("导入到...", extensions))
        val file = fileChooser.showOpenDialog(Stage()) ?: return
        val string = FileUtil.readUtf8String(file) ?: return
        list.addAll(string.byteInputStream().toJSONArray().toModel())
    }

    /**
     * 导出数据
     */
    inline fun <reified T : JsonModel> exportData(list: ObservableList<T>, extensions: String) {
        val fileChooser = FileChooser()
        fileChooser.extensionFilters.add(FileChooser.ExtensionFilter("导出到...", extensions))
        val file: File = fileChooser.showSaveDialog(Stage()) ?: return
        if (file.exists()) {
            // 文件已存在，则删除文件
            file.delete()
        }
        // 写入文件
        FileUtil.writeUtf8String(list.toJSON().toString(), file)
    }

    /**
     * 保存模板数据
     */
    fun saveTemplateData() {
        saveList(DataFilePath.templateFilePath, templateList)
    }

    /**
     * 保存内部表数据
     */
    fun saveInternalTableData() {
        saveList(DataFilePath.internalTableFilePath, internalTableEntityList)
    }

    /**
     * 保存数据源配置
     */
    fun saveDataSourceList() {
        saveList(DataFilePath.dataSourceListFilePath, dataSourceList)
    }

    /**
     * 保存数据源配置
     */
    fun savePredefinedAttributeList() {
        saveList(DataFilePath.predefinedAttributeListFilePath, predefinedAttributeList)
    }

    /**
     * 保存数据
     */
    private fun <T : JsonModel> saveList(filePath: String, list: ObservableList<T>) {
        FileUtil.writeUtf8String(list.toJSON().toString(), FileUtil.touch(filePath))
    }
}