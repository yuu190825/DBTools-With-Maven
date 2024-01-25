package com.itoria.dbtools

import java.io.BufferedWriter
import java.io.FileWriter
import java.io.IOException
import javax.swing.JTextArea

class SqlFileWriter(
    private val tabName: String,
    private val from: Int,
    private val to: Int,
    private val fileNumber: Int,
    private val idInsert: Boolean,
    private val sqlStringList: MutableList<String>,
    private val statusBox: JTextArea
): Thread() {
    var error = false

    override fun run() {
        var bw: BufferedWriter? = null

        try {
            bw = if (fileNumber >= 0) BufferedWriter(FileWriter("${tabName}_${from}_${to}_$fileNumber.sql"))
            else {
                if (from >= 0 && to >= 0) BufferedWriter(FileWriter("${tabName}_${from}_${to}_error.sql"))
                else BufferedWriter(FileWriter("${tabName}_error.sql"))
            }

            if (idInsert) bw.write("SET IDENTITY_INSERT $tabName ON;\n")

            for (sqlString in sqlStringList) bw.write("$sqlString\n")

            if (idInsert) bw.write("SET IDENTITY_INSERT $tabName OFF;")
        } catch (ioe: IOException) { statusBox.append("IO Error!!!\n"); error = true } finally {
            try { bw?.close() } catch (ioe: IOException) { statusBox.append("IO Error!!!\n"); error = true }
        }
    }
}