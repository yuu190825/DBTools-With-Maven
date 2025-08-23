package com.itoria.dbtools

import java.io.BufferedWriter
import java.io.FileWriter
import java.io.IOException
import javax.swing.JTextArea

class SqlFileWriter(
    private val writerSet: MutableMap<String, Any>,
    private val fileNumber: Int,
    private val sqlStringList: MutableList<String>,
    private val statusBox: JTextArea
): Thread() {
    var error = false

    override fun run() {
        var bw: BufferedWriter? = null

        // Get Value from Set Map
        val tableName = writerSet["TABLE_NAME"] as String
        val from = writerSet["FROM"] as Int
        val to = writerSet["TO"] as Int
        val isIdInsert = writerSet["IS_ID_INSERT"] as Boolean
        // End

        try {
            bw = if (fileNumber > 0) BufferedWriter(FileWriter("$tableName $from-$to-$fileNumber.sql"))
            else {
                if (from in 1..<to) BufferedWriter(FileWriter("$tableName $from-$to-error.sql"))
                else BufferedWriter(FileWriter("$tableName error.sql"))
            }

            if (isIdInsert) bw.write("SET IDENTITY_INSERT $tableName ON;\n")

            for (sqlString in sqlStringList) bw.write("$sqlString\n")

            if (isIdInsert) bw.write("SET IDENTITY_INSERT $tableName OFF;")
        }
        catch (_: IOException) { statusBox.append("IO Error!!!\n"); error = true }
        finally { try { bw?.close() } catch (_: IOException) { statusBox.append("IO Error!!!\n"); error = true } }
    }
}