package com.itoria.dbtools

import java.io.BufferedWriter
import java.io.FileWriter
import java.io.IOException
import javax.swing.JTextArea

class CsvFileWriter(
    private val writerSet: MutableMap<String, Any>,
    private val notInDbAColValueLists: MutableList<MutableList<Any?>>,
    private val addInDbAColValueLists: MutableList<MutableList<Any?>>,
    private val xorInDbAColValueLists: MutableList<MutableList<Any?>>,
    private val statusBox: JTextArea
) {
    var error = false

    fun start() {
        var bw: BufferedWriter? = null

        // Get Value from Set Map
        val dbName = writerSet["DB_NAME"] as String
        val tableName = writerSet["TABLE_NAME"] as String
        // End

        try {
            bw = BufferedWriter(FileWriter("$tableName.csv"))

            bw.write("Not in $dbName:\n")

            for (colValueList in notInDbAColValueLists) {
                bw.write(colValueList[0].toString())

                for (j in 1 until colValueList.size) bw.write(", ${colValueList[j].toString()}")

                bw.newLine()
            }

            bw.newLine()

            bw.write("Add in $dbName:\n")

            for (colValueList in addInDbAColValueLists) {
                bw.write(colValueList[0].toString())

                for (j in 1 until colValueList.size) bw.write(", ${colValueList[j].toString()}")

                bw.newLine()
            }

            bw.newLine()

            bw.write("Not equal in $dbName:\n")

            for (colValueList in xorInDbAColValueLists) {
                bw.write(colValueList[0].toString())

                for (j in 1 until colValueList.size) bw.write(", ${colValueList[j].toString()}")

                bw.newLine()
            }
        }
        catch (_: IOException) { statusBox.append("IO Error!!!\n"); error = true }
        finally { try { bw?.close() } catch (_: IOException) { statusBox.append("IO Error!!!\n"); error = true } }
    }
}