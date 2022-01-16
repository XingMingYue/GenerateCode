package com.xingmingyue.generatecode.entity.database

import tornadofx.*
import javax.json.JsonObject

/**
 * 数据库表字段实体
 */
class DatabaseTableFieldEntity(
    name: String,
    logicName: String,
    remark: String,
    type: String,
    primaryKey: String,
    notNull: String,
    queryType: String
) : JsonModel {

    constructor() : this("", "", "", "", "", "", "")

    constructor(
        name: String,
        logicName: String,
        remark: String,
        type: String,
        primaryKey: String,
        notNull: String
    ) : this(name, logicName, remark, type, primaryKey, notNull, "")

    // 字段名称
    var name: String by property(name)
    fun nameProperty() = getProperty(DatabaseTableFieldEntity::name)

    // 逻辑名称（中文名称）
    var logicName: String by property(logicName)
    fun logicNameProperty() = getProperty(DatabaseTableFieldEntity::logicName)

    // 字段描述
    var remark: String by property(remark)
    fun remarkProperty() = getProperty(DatabaseTableFieldEntity::remark)

    // 字段类型
    var type: String by property(type)
    fun typeProperty() = getProperty(DatabaseTableFieldEntity::type)

    // 是主键
    var primaryKey: String by property(primaryKey)
    fun primaryKeyProperty() = getProperty(DatabaseTableFieldEntity::primaryKey)

    // 不为Null
    var notNull: String by property(notNull)
    fun notNullProperty() = getProperty(DatabaseTableFieldEntity::notNull)

    // 查询类型
    var queryType: String by property(queryType)
    fun queryTypeProperty() = getProperty(DatabaseTableFieldEntity::queryType)


    override fun toJSON(json: JsonBuilder) {
        with(json) {
            add("name", name)
            add("logicName", logicName)
            add("remark", remark)
            add("type", type)
            add("primaryKey", primaryKey)
            add("notNull", notNull)
            add("queryType", queryType)
        }
    }

    override fun updateModel(json: JsonObject) {
        with(json) {
            name = getString("name", "")
            logicName = getString("logicName", "")
            remark = getString("remark", "")
            type = getString("type", "")
            primaryKey = getString("primaryKey", "")
            notNull = getString("notNull", "")
            queryType = getString("queryType", "")
        }
    }

}