package com.xingmingyue.generatecode.entity.database

import tornadofx.*

/**
 * 数据库实体
 */
class DatabaseEntity(name: String) {

    /**
     * 数据库名称
     */
    var name: String by property(name)
    fun nameProperty() = getProperty(DatabaseEntity::name)

    /**
     * 数据库表
     */
    var tableList: List<DatabaseTableEntity>? = null
        private set

    override fun toString(): String {
        return name
    }

    fun setTableList(tableList: List<DatabaseTableEntity>?): DatabaseEntity {
        this.tableList = tableList
        return this
    }
}