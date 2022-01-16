package com.xingmingyue.generatecode.util

import cn.hutool.core.collection.CollUtil
import cn.hutool.db.DbUtil
import cn.hutool.db.handler.EntityListHandler
import cn.hutool.db.handler.RsHandler
import cn.hutool.db.sql.SqlExecutor
import com.xingmingyue.generatecode.entity.DataSourceEntity
import com.xingmingyue.generatecode.entity.database.DatabaseEntity
import com.xingmingyue.generatecode.entity.database.DatabaseTableEntity
import com.xingmingyue.generatecode.entity.database.DatabaseTableFieldEntity
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import java.sql.Connection
import java.sql.SQLException
import javax.sql.DataSource

/**
 * 数据库工具类
 */
object DatabaseUtil {
    /**
     * 数据源
     */
    private var dataSource: DataSource? = null

    /**
     * 初始化成功
     */
    private var initOk = false

    /**
     * 初始化Hikari数据源
     *
     * @param url             数据库连接
     * @param userName        数据库账号
     * @param password        数据库密码
     * @param driverClassName 数据库驱动
     */
    fun initHikariDataSource(url: String?, userName: String?, password: String?, driverClassName: String?) {
        val hikariDataSource = HikariDataSource()
        hikariDataSource.jdbcUrl = url
        hikariDataSource.username = userName
        hikariDataSource.password = password
        hikariDataSource.driverClassName = driverClassName
        dataSource = hikariDataSource
        initOk = true
    }

    /**
     * 初始化Hikari数据库连接池
     *
     * @param config 配置
     */
    fun initHikariDataSource(config: HikariConfig?) {
        dataSource = HikariDataSource(config)
        initOk = true
    }

    /**
     * 初始化Hikari数据库连接池
     *
     * @param dataSourceEntity 数据源实体
     */
    fun initHikariDataSource(dataSourceEntity: DataSourceEntity) {
        val hikariDataSource = HikariDataSource()
        hikariDataSource.jdbcUrl = dataSourceEntity.url
        hikariDataSource.username = dataSourceEntity.username
        hikariDataSource.password = dataSourceEntity.password
        when (dataSourceEntity.type) {
            "MySQL" -> hikariDataSource.driverClassName = "com.mysql.cj.jdbc.Driver"
            "Oracle" -> hikariDataSource.driverClassName = "oracle.jdbc.OracleDriver"
            else -> Alert(Alert.AlertType.ERROR, "未知类型！", ButtonType.OK).show()
        }
        dataSource = hikariDataSource
        initOk = true
    }

    /**
     * 获取Connection
     *
     * @return Connection对象
     * @throws SQLException 异常
     */
    @get:Throws(SQLException::class)
    val connection: Connection
        get() {
            if (!initOk) {
                throw SQLException("数据库连接池未初始化!!!")
            }
            return dataSource!!.connection
        }

    /**
     * 执行查询语句<br></br>
     * 此方法不会关闭Connection
     *
     * @param <T>        处理结果类型
     * @param connection 数据库连接对象
     * @param sql        查询语句
     * @param rsh        结果集处理对象
     * @param params     参数
     * @return 结果对象
     * @throws SQLException SQL执行异常
    </T> */
    @Throws(SQLException::class)
    fun <T> queryAndCloseCon(connection: Connection?, sql: String?, rsh: RsHandler<T>?, vararg params: Any?): T {
        return try {
            SqlExecutor.query(connection, sql, rsh, *params)
        } finally {
            DbUtil.close(connection)
        }
    }

    /**
     * 忽略数据库名称
     */
    var ignoreDatabaseName: List<String> = CollUtil.newArrayList(
        "sys", "information_schema", "performance_schema", "mysql", "test_data"
    )

    /**
     * 获取当前连接的所有数据库信息
     *
     * @return 所有数据库信息
     */
    @Throws(SQLException::class)
    fun databaseList(vararg ignoreDatabaseNames: String): List<DatabaseEntity> {
        val ignoreDatabaseNameList = CollUtil.newArrayList(*ignoreDatabaseNames)
        val databaseList: MutableList<DatabaseEntity> = ArrayList()
        val connection = connection
        val entityList = queryAndCloseCon(connection, "SHOW DATABASES", EntityListHandler())
        for (entity in entityList) {
            val databaseName = entity.getStr("Database")
            if (!ignoreDatabaseNameList.contains(databaseName)) {
                databaseList.add(DatabaseEntity(databaseName))
            }
        }
        return databaseList
    }

    /**
     * 通过数据库名称查询数据库下的所有表
     *
     * @param databasesName 数据库名称
     * @return 数据库表集合
     */
    @Throws(SQLException::class)
    fun databasesTableList(databasesName: String): List<DatabaseTableEntity> {
        val connection = connection
        val entityList = queryAndCloseCon(
            connection,
            "SELECT TABLE_NAME AS name,TABLE_COMMENT AS logicName FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = ?",
            EntityListHandler(),
            databasesName
        )
        val databaseTableEntityList: MutableList<DatabaseTableEntity> = ArrayList()
        for (entity in entityList) {
            databaseTableEntityList.add(
                DatabaseTableEntity(
                    entity.getOrDefault("name", "") as String,
                    entity.getOrDefault("logicName", "") as String
                )
            )
        }
        return databaseTableEntityList
    }

    /**
     * 通过数据库名，表名查询指定表的所有字段信息
     *
     * @param databasesName 数据库名称
     * @param tableName     表名
     * @return 字段信息
     * @throws SQLException 异常
     */
    @Throws(SQLException::class)
    fun databasesTableFieldList(databasesName: String, tableName: String): List<DatabaseTableFieldEntity> {
        val databaseTableFieldEntityList: MutableList<DatabaseTableFieldEntity> = ArrayList()
        val connection = connection
        val entityList = queryAndCloseCon(
            connection,
            "SELECT COLUMN_NAME,ORDINAL_POSITION,COLUMN_DEFAULT,IS_NULLABLE,DATA_TYPE,CHARACTER_MAXIMUM_LENGTH," +
                    "CHARACTER_OCTET_LENGTH,COLUMN_KEY,COLUMN_COMMENT FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ?",
            EntityListHandler(), databasesName, tableName
        )
        for (entity in entityList) {
            databaseTableFieldEntityList.add(
                DatabaseTableFieldEntity(
                    entity.getStr("COLUMN_NAME"),
                    entity.getOrDefault("COLUMN_COMMENT", "") as String,
                    entity.getOrDefault("COLUMN_COMMENT", "") as String,
                    entity.getOrDefault("DATA_TYPE", "") as String,
                    entity.getOrDefault("COLUMN_KEY", "") as String,
                    entity.getOrDefault("IS_NULLABLE", "") as String
                )
            )
        }
        return databaseTableFieldEntityList
    }

}