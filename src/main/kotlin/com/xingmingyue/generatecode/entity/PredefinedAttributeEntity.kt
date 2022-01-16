package com.xingmingyue.generatecode.entity

import tornadofx.*
import java.util.stream.Collectors
import javax.json.JsonObject

/**
 * 预定义属性
 *
 * @author XingMingYue
 */
class PredefinedAttributeEntity(attributeKey: String, type: String, attributeExpression: String) : JsonModel {

    constructor() : this("", "", "")

    var type: String by property(type)
    fun typeProperty() = getProperty(PredefinedAttributeEntity::type)

    var attributeKey: String by property(attributeKey)
    fun attributeKeyProperty() = getProperty(PredefinedAttributeEntity::attributeKey)

    var attributeExpression: String by property(attributeExpression)
    fun attributeExpressionProperty() = getProperty(PredefinedAttributeEntity::attributeExpression)


    override fun toJSON(json: JsonBuilder) {
        with(json) {
            add("type", type)
            add("attributeKey", attributeKey)
            add("attributeExpression", attributeExpression)
        }
    }

    override fun updateModel(json: JsonObject) {
        with(json) {
            type = getString("type")
            attributeKey = getString("attributeKey")
            attributeExpression = getString("attributeExpression")
        }
    }
}

fun List<PredefinedAttributeEntity>.tablePredefinedAttribute(): List<PredefinedAttributeEntity> =
    this.stream().filter { o: PredefinedAttributeEntity -> o.type == "table" }.collect(Collectors.toList())

fun List<PredefinedAttributeEntity>.fieldPredefinedAttribute(): List<PredefinedAttributeEntity> =
    this.stream().filter { o: PredefinedAttributeEntity -> o.type == "field" }.collect(Collectors.toList())