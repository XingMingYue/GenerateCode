package com.xingmingyue.generatecode.entity

import tornadofx.*
import javax.json.JsonObject

class DataSourceEntity(name: String, remark: String, type: String, url: String, username: String, password: String) :
    JsonModel {

    constructor() : this("", "", "", "", "", "")

    constructor(name: String, remark: String, type: String) : this(name, remark, type, "", "", "")

    // 数据源名称
    var name: String by property(name)
    fun nameProperty() = getProperty(DataSourceEntity::name)

    // 备注
    var remark: String by property(remark)
    fun remarkProperty() = getProperty(DataSourceEntity::remark)

    // 数据库类型
    var type: String by property(type)
    fun typeProperty() = getProperty(DataSourceEntity::type)

    // 数据库URL
    var url: String by property(url)
    fun urlProperty() = getProperty(DataSourceEntity::url)

    // 用户名
    var username: String by property(username)
    fun usernameProperty() = getProperty(DataSourceEntity::username)

    // 密码
    var password: String by property(password)
    fun passwordProperty() = getProperty(DataSourceEntity::password)


    override fun toJSON(json: JsonBuilder) {
        with(json) {
            add("name", name)
            add("remark", remark)
            add("type", type)
            add("url", url)
            add("username", username)
            add("password", password)
        }
    }

    override fun updateModel(json: JsonObject) {
        with(json) {
            name = getString("name")
            remark = getString("remark")
            type = getString("type")
            url = getString("url")
            username = getString("username")
            password = getString("password")
        }
    }

    /**
     * Returns a string representation of the object.
     */
    override fun toString(): String {
        return name
    }
}

class DatabaseModel : ItemViewModel<DataSourceEntity>() {
    val name = bind(DataSourceEntity::name)
    val remark = bind(DataSourceEntity::remark)
    val type = bind(DataSourceEntity::type)
    val url = bind(DataSourceEntity::url)
    val username = bind(DataSourceEntity::username)
    val password = bind(DataSourceEntity::password)
}