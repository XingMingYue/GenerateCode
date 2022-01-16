package com.xingmingyue.generatecode.view.base

import com.xingmingyue.generatecode.enums.EntityOperateMode
import javafx.scene.layout.HBox
import tornadofx.*
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

abstract class BaseEntityOperateFragment<T>(titleName: String) : Fragment(titleName) {

    private fun <T> param(): ReadOnlyProperty<Component, T> = object : ReadOnlyProperty<Component, T> {
        override fun getValue(thisRef: Component, property: KProperty<*>): T {
            val param = thisRef.params[property.name]
            if (param == null) {
                throw IllegalStateException("param for name [$property.name] has not been set")
            } else {
                @Suppress("UNCHECKED_CAST")
                return param as T
            }
        }
    }

    val entity: T by param()

    val mode: EntityOperateMode by param()

    val cellBack: () -> Unit by param()

    fun buttonGroup(model: ItemViewModel<T>): HBox {
        return hbox {
            button("关闭").action { close() }
            if (EntityOperateMode.UPDATE == mode) {
                button("重置").action { model.rollback() }
            }
            button(
                if (EntityOperateMode.UPDATE == mode) {
                    "更新"
                } else {
                    "新增"
                }
            ) {
                enableWhen(model.dirty)
                action {
                    model.commit()
                    cellBack()
                    close()
                }
            }
        }
    }
}