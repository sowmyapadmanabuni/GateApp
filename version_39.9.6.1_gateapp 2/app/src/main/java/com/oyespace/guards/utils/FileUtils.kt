package com.oyespace.guards.utils

import java.io.File

fun deleteFile(filePath: String) {

    val file = File(filePath)
    if (file.exists()) {
        file.delete()
    }

}

fun deleteDir(dirPath: String) {

    val file = File(dirPath)

    if (file.isDirectory) {
        for (f in file.listFiles()) {
            deleteDir(f.absolutePath)
        }
    } else {
        deleteFile(dirPath)
    }

}