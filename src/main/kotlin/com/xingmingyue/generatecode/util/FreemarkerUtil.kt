package com.xingmingyue.generatecode.util

import cn.hutool.core.util.StrUtil
import freemarker.cache.TemplateLoader
import freemarker.template.Configuration
import freemarker.template.SimpleScalar
import freemarker.template.TemplateException
import freemarker.template.TemplateMethodModelEx
import java.io.IOException
import java.io.Reader
import java.io.StringReader
import java.io.StringWriter
import java.util.*

/**
 * freemarker工具类
 *
 * @author XingMingYue
 */
object FreemarkerUtil {

    /**
     * createConfiguration
     */
    private val CONFIGURATION = createConfiguration()

    /**
     * 模板内容转换
     *
     * @param templateContent 模板内容
     * @param dataModel       数据
     * @return 转换后的结果
     */
    fun process(templateContent: String?, dataModel: Any?): String {
        if (Objects.isNull(templateContent)) {
            return StrUtil.EMPTY
        }
        val writer = StringWriter()
        return try {
            CONFIGURATION.getTemplate(templateContent).process(dataModel, writer)
            writer.toString()
        } catch (e: IOException) {
            throw RuntimeException("模板转换异常", e)
        } catch (e: TemplateException) {
            throw RuntimeException("模板转换异常", e)
        }
    }

    /**
     * 创建 Configuration
     *
     * @return Configuration
     */
    private fun createConfiguration(): Configuration {
        val configuration = Configuration(Configuration.VERSION_2_3_28)
        configuration.localizedLookup = false
        configuration.defaultEncoding = "UTF-8"
        configuration.templateLoader = SimpleStringTemplateLoader()
        configuration.isAPIBuiltinEnabled = true
        configuration.setSharedVariable("setOutputPath", SetOutputPathMethod())
        configuration.setSharedVariable("toCamelCase", ToCamelCaseMethod())
        configuration.setSharedVariable("removeTablePrefix", RemoveTablePrefixMethod())
        return configuration
    }

    /**
     * [TemplateLoader] 简单的字符串模板加载器<br></br>
     * 用于直接获取字符串模板
     *
     * @author XingMingYue
     */
    class SimpleStringTemplateLoader : TemplateLoader {
        /**
         * 查询模板
         *
         * @param name 在这直接给模板内容，故而直接返回
         * @return 模板内容
         */
        override fun findTemplateSource(name: String): Any {
            return name
        }

        override fun getLastModified(templateSource: Any): Long {
            return System.currentTimeMillis()
        }

        override fun getReader(templateSource: Any, encoding: String): Reader {
            return StringReader(templateSource.toString())
        }

        override fun closeTemplateSource(templateSource: Any) {
            //忽略
        }
    }

    /**
     * 通过角色编码获取角色下的用户编码
     */
    private class SetOutputPathMethod : TemplateMethodModelEx {
        override fun exec(list: List<*>): Any {
            val roleCodeList: MutableList<String> = ArrayList()
            for (item in list) {
                roleCodeList.add(item.toString())
            }
            return SimpleScalar(java.lang.String.join(",", roleCodeList))
        }
    }

    /**
     * 转换为驼峰方法
     */
    private class ToCamelCaseMethod : TemplateMethodModelEx {
        override fun exec(strList: List<*>): Any {
            val list: MutableList<String> = ArrayList()
            for (item in strList) {
                //如果表名不包含下划线_则全转换为小写
                var str = item.toString()
                if (!str.contains(StrUtil.UNDERLINE)) {
                    str = str.lowercase(Locale.getDefault())
                }
                //转换为驼峰命名法
                list.add(StrUtil.toCamelCase(str))
            }
            return SimpleScalar(java.lang.String.join(",", list))
        }
    }

    /**
     * 删除表前缀
     */
    private class RemoveTablePrefixMethod : TemplateMethodModelEx {
        var prefixList: List<String> = listOf("T_S_", "T_R_", "T_B_")
        override fun exec(tableNameList: List<*>): Any {
            return if (tableNameList.isNotEmpty()) {
                var str = tableNameList[0].toString()
                for (prefix in prefixList) {
                    str = StrUtil.removePrefix(str, prefix)
                }
                SimpleScalar(str)
            } else {
                SimpleScalar(null)
            }
        }
    }
}