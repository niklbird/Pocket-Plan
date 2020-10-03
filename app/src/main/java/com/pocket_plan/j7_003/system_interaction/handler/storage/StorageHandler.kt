package com.pocket_plan.j7_003.system_interaction.handler.storage

import android.content.Context
import com.pocket_plan.j7_003.MainActivity
import com.google.gson.Gson
import java.io.File

class StorageHandler {

    companion object {
        var files = HashMap<StorageId, File>()

        fun saveAsJsonToFile(file: File?, any: Any) = file?.writeText(Gson().toJson(any))

        fun createFile(identifier: StorageId, fileName: String) {
            files[identifier] =
                setStorageLocation(fileName, MainActivity.act)

            if (files[identifier]?.exists() == null || files[identifier]?.exists() == false) {
                files[identifier]?.createNewFile()
            }
        }

        fun createJsonFile(identifier: StorageId, context: Context = MainActivity.act,
                           text: String = "[]") {
            files[identifier] =
                setStorageLocation(
                    identifier.s,
                    context
                )

            if (files[identifier]?.exists() == null || files[identifier]?.exists() == false) {
                files[identifier]?.writeText(text)
            }
        }

        private fun setStorageLocation(fileName: String, context: Context): File =
            File(context.filesDir, fileName)
    }
}