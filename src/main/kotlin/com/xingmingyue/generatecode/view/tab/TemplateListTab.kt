package com.xingmingyue.generatecode.view.tab

import com.xingmingyue.generatecode.GlobalData
import com.xingmingyue.generatecode.entity.TemplateEntity
import com.xingmingyue.generatecode.entity.TemplateFileEntity
import com.xingmingyue.generatecode.entity.TemplateFileModel
import com.xingmingyue.generatecode.entity.TemplateModel
import com.xingmingyue.generatecode.enums.EntityOperateMode
import com.xingmingyue.generatecode.util.StyleUtil
import com.xingmingyue.generatecode.view.GenerateCodeView
import com.xingmingyue.generatecode.view.base.BaseEntityOperateFragment
import tornadofx.*

/**
 * 模板列表Tab
 */
class TemplateListTab : Fragment() {

    override val root = borderpane {
        top = hbox {
            button("新增").action {
                val templateEntity = TemplateEntity()
                find<CreateOrEditorTemplate>(
                    mapOf(CreateOrEditorTemplate::entity to templateEntity,
                        CreateOrEditorTemplate::mode to EntityOperateMode.CREATE,
                        CreateOrEditorTemplate::cellBack to {
                            GlobalData.templateList.add(0, templateEntity)
                            GlobalData.saveTemplateData()
                        })
                ).openWindow()
            }
            button("导入").action {

            }
        }
        center = tableview<TemplateEntity> {
            items = GlobalData.templateList
            smartResize()
            column("操作", TemplateEntity::nameProperty).apply {
                cellFormat {
                    val template = this@cellFormat.rowItem
                    graphic = hbox {
                        prefWidth = 200.0
                        minWidth = 200.0
                        maxWidth = 200.0
                        button("编辑").action {
                            find<CreateOrEditorTemplate>(
                                mapOf(CreateOrEditorTemplate::entity to template,
                                    CreateOrEditorTemplate::mode to EntityOperateMode.UPDATE,
                                    CreateOrEditorTemplate::cellBack to {
                                        GlobalData.saveTemplateData()
                                    })
                            ).openWindow()
                        }
                        button("删除").action {
                            items.remove(rowItem)
                            GlobalData.saveTemplateData()
                        }
                        button("配置").action {
                            find<ConfigurationTemplate>(mapOf(ConfigurationTemplate::templateEntity to template)).openWindow()
                        }
                        button("生成").action {
                            find<GenerateCodeView>(mapOf(GenerateCodeView::template to template)).openWindow(owner = scene.window)
                        }
                    }
                }
            }.fixedWidth(210.0)
            column("名称", TemplateEntity::nameProperty).apply {
                prefWidth = 100.0
                minWidth = 100.0
            }
            column("备注", TemplateEntity::remarkProperty).remainingWidth()
        }
    }
}


class CreateOrEditorTemplate : BaseEntityOperateFragment<TemplateEntity>("新增模板") {

    override val root = form {
        StyleUtil.setStyle(this)
        if (EntityOperateMode.UPDATE == mode) {
            title = "更新模板"
        }
        val model = TemplateModel()
        model.item = entity
        fieldset {
            field("名称：") {
                textfield(model.name)
            }
            field("备注：") {
                textfield(model.remark)
            }
        }
        add(buttonGroup(model))
    }

}

/**
 * 配置模板
 */
class ConfigurationTemplate : Fragment("配置模板文件") {

    val templateEntity: TemplateEntity by param()

    override val root = borderpane {
        StyleUtil.setStyle(this)
        setPrefSize(728.0, 586.0)
        top = hbox {
            button("新增").action {
                val templateFileEntity = TemplateFileEntity()
                find<CreateOrEditorTemplateFile>(
                    mapOf(CreateOrEditorTemplateFile::entity to templateFileEntity,
                        CreateOrEditorTemplateFile::mode to EntityOperateMode.CREATE,
                        CreateOrEditorTemplateFile::cellBack to {
                            templateEntity.templateFileList.add(0, templateFileEntity)
                            GlobalData.saveTemplateData()
                        })
                ).openWindow()
            }
        }
        center = tableview<TemplateFileEntity> {
            columnResizePolicy = SmartResize.POLICY
            items = templateEntity.templateFileList
            column("操作", TemplateFileEntity::nameProperty).cellFormat {
                val templateFileEntity = this@cellFormat.rowItem
                graphic = hbox {
                    button("编辑").action {
                        find<CreateOrEditorTemplateFile>(
                            mapOf(CreateOrEditorTemplateFile::entity to templateFileEntity,
                                CreateOrEditorTemplateFile::mode to EntityOperateMode.UPDATE,
                                CreateOrEditorTemplateFile::cellBack to {
                                    GlobalData.saveTemplateData()
                                })
                        ).openWindow()
                    }
                    button("删除").action {
                        items.remove(rowItem)
                        GlobalData.saveTemplateData()
                    }
                }
            }
            column("名称", TemplateFileEntity::nameProperty)
            column("文件名称", TemplateFileEntity::fileNameProperty)
            column("备注", TemplateFileEntity::remarkProperty)
            column("输出相对路径", TemplateFileEntity::outputRelativePathProperty)
        }
    }
}


class CreateOrEditorTemplateFile : BaseEntityOperateFragment<TemplateFileEntity>("新增模板文件") {

    override val root = borderpane {
        StyleUtil.setStyle(this)
        setPrefSize(728.0, 586.0)

        val model = TemplateFileModel()
        model.item = entity
        top = form {
            if (EntityOperateMode.UPDATE == mode) {
                title = "更新模板"
            }
            fieldset {
                field("名称：") {
                    textfield(model.name)
                }
                field("文件名称：") {
                    textfield(model.fileName)
                }
                field("备注：") {
                    textfield(model.remark)
                }

                field("输出相对路径：") {
                    textfield(model.outputRelativePath)
                }
            }
        }
        center = textarea(model.content)
        bottom = buttonGroup(model)
    }
}