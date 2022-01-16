package com.xingmingyue.generatecode.entity.database

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.*
import javax.json.JsonObject

/**
 * 数据库表实体
 */
class DatabaseTableEntity(name: String, logicName: String, remark: String) : JsonModel {

    constructor() : this("", "", "")
    constructor(name: String, logicName: String) : this(name, logicName, "")

    // 表名
    var name: String by property(name)
    fun nameProperty() = getProperty(DatabaseTableEntity::name)

    // 表逻辑名（中文名称）
    var logicName: String by property(logicName)
    fun logicNameProperty() = getProperty(DatabaseTableEntity::logicName)

    // 备注
    var remark: String by property(remark)
    fun remarkProperty() = getProperty(DatabaseTableEntity::remark)

    /**
     * 预定义属性
     */
    val predefinedAttribute = LinkedHashMap<String, Any?>()

    /**
     * 表字段
     */
    var fieldList: ObservableList<DatabaseTableFieldEntity> = FXCollections.observableArrayList()

    override fun toJSON(json: JsonBuilder) {
        with(json) {
            add("name", name)
            add("logicName", logicName)
            add("remark", remark)
            add("fieldList", fieldList.toJSON())
        }
    }

    override fun updateModel(json: JsonObject) {
        with(json) {
            name = getString("name")
            logicName = getString("logicName", "")
            remark = getString("remark", "")
            fieldList.addAll(getJsonArray("fieldList").toModel())
        }
    }

    override fun toString(): String {
        return name
    }
}