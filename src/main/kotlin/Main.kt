package com.itoria.dbtools

import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.JOptionPane

// Main Config
private const val MAX = 500000

// Window Init
private val window = Window()

fun main() { StartButton().init(); window.print() }

private class StartButton {
    fun init() { window.btnStart.addActionListener(Click()) }

    private class Click: ActionListener {
        override fun actionPerformed(e: ActionEvent?) { Work().start() }
    }

    private class Work {
        fun start() {
            var warning = 0
            var error = false

            window.start()

            // Put Value to Set Map
            val executeSet = mutableMapOf<String, Any>()
            executeSet["FUNC"] = window.func
            executeSet["MODE"] = window.mode
            executeSet["IS_ID_INSERT"] = window.isIdInsert
            executeSet["RECORD"] = window.record
            executeSet["WHERE"] = window.where.text

            val cfWriterSet = mutableMapOf<String, Any>()
            cfWriterSet["DB_NAME"] = window.fromDbName.text

            val createSet = mutableMapOf<String, Any>()
            createSet["IS_ID_INSERT"] = window.isIdInsert

            val sfWriterSet = mutableMapOf<String, Any>()
            sfWriterSet["IS_ID_INSERT"] = window.isIdInsert

            val fromDbSet = mutableMapOf<String, Any>()
            fromDbSet["DB_TYPE"] = window.fromDbType
            fromDbSet["DB_URL"] = window.fromDbUrl.text
            fromDbSet["DB_SID"] = window.fromDbSid.text
            fromDbSet["DB_NAME"] = window.fromDbName.text
            fromDbSet["DB_USER"] = window.fromDbUser.text
            fromDbSet["DB_PASS"] = String(window.fromDbPass.password)

            val toDbSet = mutableMapOf<String, Any>()
            toDbSet["DB_TYPE"] = window.toDbType
            toDbSet["DB_URL"] = window.toDbUrl.text
            toDbSet["DB_SID"] = window.toDbSid.text
            toDbSet["DB_NAME"] = window.toDbName.text
            toDbSet["DB_USER"] = window.toDbUser.text
            toDbSet["DB_PASS"] = String(window.toDbPass.password)
            // End

            val total = try { window.total.text.toInt() } catch (_: NumberFormatException) { 1 }

            if (window.tableNameList.isNotEmpty()) {
                for (tableName in window.tableNameList) {
                    val colNameList = mutableSetOf<String>()
                    val dbAColValueLists = mutableListOf<MutableList<Any?>>()
                    val dbBColValueLists = mutableListOf<MutableList<Any?>>()

                    if (!error) {

                        // Put Value to Set Map (Execute Set)
                        executeSet["TABLE_NAME"] = tableName
                        // End

                        var from = if (window.fromDbType == 3) 0 else 1
                        var to = MAX

                        while (from <= total) {
                            val execute = Execute(executeSet, from, to, fromDbSet, toDbSet, window.statusBox)
                            execute.start()

                            warning += execute.warning; error = execute.error

                            if (!error) {
                                if (window.func != 1) {
                                    colNameList.addAll(execute.colNameList)
                                    dbAColValueLists.addAll(execute.dbAColValueLists)
                                    dbBColValueLists.addAll(execute.dbBColValueLists)
                                }

                                from += MAX; if (window.fromDbType != 3) to += MAX
                            } else break
                        }
                    }

                    if (!error && window.func != 1) {
                        window.statusBox.append("Running Compare...\n")

                        val compare = Compare(dbAColValueLists, dbBColValueLists); compare.start()

                        if (window.func == 2) {
                            window.statusBox.append("Running CsvFileWriter...\n")

                            // Put Value to Set Map (Csv File Writer Set)
                            cfWriterSet["TABLE_NAME"] = tableName
                            // End

                            val csvFileWriter = CsvFileWriter(cfWriterSet, compare.notInDbAColValueLists,
                                compare.addInDbAColValueLists, compare.xorInDbAColValueLists, window.statusBox)
                            csvFileWriter.start()

                            error = csvFileWriter.error
                        } else {
                            val toDeleteIdList = mutableListOf<String>()
                            val toInsertSqlStringList = mutableListOf<String>()
                            val errorSqlStringList = mutableListOf<String>()

                            for (colValueList in compare.notInDbAColValueLists) {
                                try { val id = colValueList[0].toString().toInt(); toDeleteIdList.add(id.toString()) }
                                catch (_: NumberFormatException) { toDeleteIdList.add("'${colValueList[0]}'") }
                            }

                            for (colValueList in compare.xorInDbAColValueLists) {
                                try { val id = colValueList[0].toString().toInt(); toDeleteIdList.add(id.toString()) }
                                catch (_: NumberFormatException) { toDeleteIdList.add("'${colValueList[0]}'") }
                            }

                            window.statusBox.append("Running Delete...\n")

                            // Put Value to Set Map (Delete Set)
                            val deleteSet = mutableMapOf<String, Any>()
                            deleteSet["TABLE_NAME"] = tableName
                            deleteSet["ID_COL_NAME"] = colNameList.elementAt(0)
                            // End

                            val delete = Delete(deleteSet, toDbSet, toDeleteIdList, window.statusBox); delete.start()

                            error = delete.error

                            if (!error) {
                                window.statusBox.append("Running SqlStringCreate...\n")

                                // Put Value to Set Map (Create Set)
                                createSet["TABLE_NAME"] = tableName
                                createSet["IS_SYNC"] = true
                                // End

                                val sqlStringCreateAdd = SqlStringCreator(createSet, colNameList,
                                    compare.addInDbAColValueLists)
                                val sqlStringCreateXor = SqlStringCreator(createSet, colNameList,
                                    compare.xorInDbAColValueLists)

                                sqlStringCreateAdd.start(); sqlStringCreateXor.start()

                                try { sqlStringCreateAdd.join(); sqlStringCreateXor.join() }
                                catch (_: InterruptedException) {
                                    window.statusBox.append("Thread Error!!!\n"); error = true
                                }

                                if (!error) {
                                    toInsertSqlStringList.addAll(sqlStringCreateAdd.sqlStringList)
                                    toInsertSqlStringList.addAll(sqlStringCreateXor.sqlStringList)
                                }
                            }

                            if (!error) {
                                window.statusBox.append("Running InsertInto...\n")

                                val insertInto = InsertInto(toDbSet, toInsertSqlStringList, window.statusBox)
                                insertInto.start()

                                try { insertInto.join() }
                                catch (_: InterruptedException) {
                                    window.statusBox.append("Thread Error!!!\n"); error = true
                                }

                                if (!error) {
                                    warning += insertInto.warning
                                    errorSqlStringList.addAll(insertInto.errorSqlStringList)
                                    error = insertInto.error
                                }
                            }

                            if (!error && warning > 0) {
                                window.statusBox.append("Running SqlFileWriter...\n")

                                // Put Value to Set Map (Sql File Writer Set)
                                sfWriterSet["TABLE_NAME"] = tableName
                                sfWriterSet["FROM"] = 0
                                sfWriterSet["TO"] = 0
                                // End

                                val sqlFileWriter = SqlFileWriter(sfWriterSet, 0, errorSqlStringList,
                                    window.statusBox)
                                sqlFileWriter.start()

                                try { sqlFileWriter.join() }
                                catch (_: InterruptedException) {
                                    window.statusBox.append("Thread Error!!!\n"); error = true
                                }

                                if (!error) error = sqlFileWriter.error
                            }
                        }
                    }
                }
            }

            if (!error) {
                if (warning == 0) JOptionPane.showMessageDialog(null, "ウルトラハッピー",
                    "Success", JOptionPane.INFORMATION_MESSAGE)
                else JOptionPane.showMessageDialog(null, "はっぷっぷー (Error:$warning)",
                    "Not Success", JOptionPane.WARNING_MESSAGE)
            } else JOptionPane.showMessageDialog(null, "めちょっく", "Error",
                JOptionPane.ERROR_MESSAGE)

            window.end()
        }
    }
}