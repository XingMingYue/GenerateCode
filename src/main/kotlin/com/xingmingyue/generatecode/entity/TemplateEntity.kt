package com.xingmingyue.generatecode.entity

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.*
import javax.json.JsonObject


/**
 * 模板实体
 */
class TemplateEntity(name: String, remark: String) : JsonModel {

    constructor() : this("", "")

    var name: String by property(name)
    fun nameProperty() = getProperty(TemplateEntity::name)

    var remark: String by property(remark)
    fun remarkProperty() = getProperty(TemplateEntity::remark)

    var templateFileList: ObservableList<TemplateFileEntity> = FXCollections.observableArrayList()

    override fun toJSON(json: JsonBuilder) {
        with(json) {
            add("name", name)
            add("remark", remark)
            add("templateFileList", templateFileList.toJSON())
        }
    }

    override fun updateModel(json: JsonObject) {
        with(json) {
            name = getString("name")
            remark = getString("remark")
            val templateFileListJsonArray = getJsonArray("templateFileList")
            if (templateFileListJsonArray != null) {
                templateFileList.addAll(templateFileListJsonArray.toModel())
            }
        }
    }
}

class TemplateModel : ItemViewModel<TemplateEntity>() {
    val name = bind(TemplateEntity::name)
    val remark = bind(TemplateEntity::remark)
}

/**
 * 模板文件实体
 */
class TemplateFileEntity(name: String, fileName: String, remark: String, content: String, outputRelativePath: String) :
    JsonModel {
    constructor() : this("", "", "")
    constructor(name: String, fileName: String, remark: String) : this(name, fileName, remark, "", "")

    /**
     * 名称
     */
    var name: String by property(name)
    fun nameProperty() = getProperty(TemplateFileEntity::name)

    /**
     * 文件名称
     */
    var fileName: String by property(fileName)
    fun fileNameProperty() = getProperty(TemplateFileEntity::fileName)

    /**
     * 备注
     */
    var remark: String by property(remark)
    fun remarkProperty() = getProperty(TemplateFileEntity::remark)

    /**
     * 内容
     */
    var content: String by property(content)
    fun contentProperty() = getProperty(TemplateFileEntity::content)

    /**
     * 输出相对路径
     */
    var outputRelativePath: String by property(outputRelativePath)
    fun outputRelativePathProperty() = getProperty(TemplateFileEntity::outputRelativePath)


    override fun toJSON(json: JsonBuilder) {
        with(json) {
            add("name", name)
            add("fileName", fileName)
            add("remark", remark)
            add("content", content)
            add("outputRelativePath", outputRelativePath)
        }
    }

    override fun updateModel(json: JsonObject) {
        with(json) {
            name = getString("name")
            fileName = getString("fileName")
            remark = getString("remark")
            content = getString("content")
            outputRelativePath = getString("outputRelativePath")
        }
    }
}


class TemplateFileModel : ItemViewModel<TemplateFileEntity>() {
    val name = bind(TemplateFileEntity::name)
    val fileName = bind(TemplateFileEntity::fileName)
    val remark = bind(TemplateFileEntity::remark)
    val content = bind(TemplateFileEntity::content)
    val outputRelativePath = bind(TemplateFileEntity::outputRelativePath)
}