package com.itoria.dbtools

import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.JOptionPane

// Main Config
private const val DEFAULT_RECORD: Short = 5000
private const val MAX = 500000

// Window Init
private val window = Window()

fun main() { StartButton().init(); window.print() }

private class StartButton {
    fun init() { window.btnStart.addActionListener(Click()) }

    private inner class Click: ActionListener { override fun actionPerformed(e: ActionEvent?) { Start().start() } }

    private inner class Start {
        fun start() {
            var warning = 0
            var error = false

            window.start()

            val record = try { window.record.text.toShort() } catch (nfe: NumberFormatException) { DEFAULT_RECORD }
            val total = try { window.total.text.toInt() } catch (nfe: NumberFormatException) { 1 }

            if (window.tabNameList.isNotEmpty()) {
                for (tabName in window.tabNameList) {
                    val colNameList = mutableSetOf<String>()
                    val colValueListsA = mutableListOf<MutableList<Any?>>()
                    val colValueListsB = mutableListOf<MutableList<Any?>>()

                    if (!error) {
                        var from = if (window.fromDbType == 3.toByte()) 0 else 1
                        var to = MAX

                        while (from <= total) {
                            val execute = Execute(window.fromDbType, window.fromDbUrl.text, window.fromDbSid.text,
                                window.fromDbName.text, window.fromDbUser.text, String(window.fromDbPass.password),
                                window.toDbType, window.toDbUrl.text, window.toDbSid.text, window.toDbName.text,
                                window.toDbUser.text, String(window.toDbPass.password), window.func, window.mode,
                                window.idInsert, record, tabName, window.where.text, from, to, window.statusBox)
                            execute.start()

                            warning += execute.warning; error = execute.error

                            if (!error) {
                                if (window.func != 1.toByte()) {
                                    colNameList.addAll(execute.colNameList)
                                    colValueListsA.addAll(execute.colValueListsA)
                                    colValueListsB.addAll(execute.colValueListsB)
                                }

                                from += MAX; if (window.fromDbType != 3.toByte()) to += MAX
                            } else break
                        }
                    }

                    if (!error && window.func != 1.toByte()) {
                        window.statusBox.append("Running Compare...\n")

                        val compare = Compare(colValueListsA, colValueListsB); compare.start()

                        if (window.func == 2.toByte()) {
                            window.statusBox.append("Running CsvFileWriter...\n")

                            val csvFileWriter = CsvFileWriter(window.fromDbName.text, tabName, compare.notInDBA,
                                compare.addInDBA, compare.xorInDBA); csvFileWriter.start()

                            error = csvFileWriter.error
                        } else {
                            val toDeleteIdList = mutableListOf<String>()
                            val toInsertSqlStringList = mutableListOf<String>()
                            val errorSqlStringList = mutableListOf<String>()

                            for (colValueList in compare.notInDBA) {
                                try { val id = colValueList[0].toString().toInt(); toDeleteIdList.add(id.toString()) }
                                catch (e: Exception) { toDeleteIdList.add("'${colValueList[0]}'") }
                            }

                            for (colValueList in compare.xorInDBA) {
                                try { val id = colValueList[0].toString().toInt(); toDeleteIdList.add(id.toString()) }
                                catch (e: Exception) { toDeleteIdList.add("'${colValueList[0]}'") }
                            }

                            window.statusBox.append("Running Delete...\n")

                            val delete = Delete(window.toDbType, window.toDbUrl.text, window.toDbSid.text,
                                window.toDbName.text, window.toDbUser.text, String(window.toDbPass.password),
                                tabName, colNameList.elementAt(0), toDeleteIdList); delete.start()

                            error = delete.error

                            if (!error) {
                                window.statusBox.append("Running SqlStringCreate...\n")

                                val sqlStringCreateAdd = SqlStringCreate(tabName, window.idInsert, colNameList,
                                    compare.addInDBA, true)
                                val sqlStringCreateXor = SqlStringCreate(tabName, window.idInsert, colNameList,
                                    compare.xorInDBA, true)

                                sqlStringCreateAdd.start(); sqlStringCreateXor.start()

                                try { sqlStringCreateAdd.join(); sqlStringCreateXor.join() }
                                catch (ie: InterruptedException) { error = true }

                                toInsertSqlStringList.addAll(sqlStringCreateAdd.sqlStringList)
                                toInsertSqlStringList.addAll(sqlStringCreateXor.sqlStringList)
                            }

                            if (!error) {
                                window.statusBox.append("Running InsertInto...\n")

                                val insertInto = InsertInto(window.toDbType, window.toDbUrl.text,
                                    window.toDbSid.text, window.toDbName.text, window.toDbUser.text,
                                    String(window.toDbPass.password), toInsertSqlStringList); insertInto.start()

                                try { insertInto.join() } catch (ie: InterruptedException) { error = true }

                                warning += insertInto.warning; if (!error) error = insertInto.error
                                errorSqlStringList.addAll(insertInto.sqlStringListOut)
                            }

                            if (!error && warning > 0) {
                                window.statusBox.append("Running SqlFileWriter...\n")

                                val sqlFileWriter = SqlFileWriter(tabName, -1, -1, -1,
                                    window.idInsert, errorSqlStringList); sqlFileWriter.start()

                                try { sqlFileWriter.join() } catch (ie: InterruptedException) { error = true }

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