package com.xingmingyue.generatecode.util

import javafx.stage.DirectoryChooser
import javafx.stage.FileChooser
import javafx.stage.Stage
import java.awt.Desktop
import java.io.File
import java.io.IOException

/**
 * 系统文件工具类
 *
 * @author XingMingYue
 */
object SystemFileUtil {
    /**
     * 打开文件夹
     * @param folder 文件夹
     */
    fun openFolder(folder: File?) {
        try {
            Desktop.getDesktop().open(folder)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * 选择文件夹
     *
     * @param title            标题
     * @param initialDirectory 初始目录
     * @return 选中的文件夹
     */
    fun selectFolder(title: String?, initialDirectory: File? = null): File? {
        val directoryChooser = DirectoryChooser()
        directoryChooser.title = title
        directoryChooser.initialDirectory = initialDirectory
        return directoryChooser.showDialog(Stage())
    }

    /**
     * 选择文件
     *
     * @param title            标题
     * @param initialDirectory 初始目录
     * @return 选中的文件夹
     */
    fun selectFile(
        title: String?,
        initialDirectory: String? = null,
        vararg extensionFilters: FileChooser.ExtensionFilter
    ): File {
        val fileChooser = FileChooser()
        fileChooser.title = title
        if (initialDirectory != null) {
            fileChooser.initialDirectory = File(initialDirectory)
        }
        fileChooser.extensionFilters.addAll(extensionFilters)
        return fileChooser.showOpenDialog(Stage())
    }

}