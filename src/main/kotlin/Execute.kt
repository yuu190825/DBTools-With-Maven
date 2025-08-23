package com.itoria.dbtools

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.sql.Statement
import javax.swing.JTextArea

class Execute(
    executeSet: MutableMap<String, Any>,
    private val from: Int,
    private val to: Int,
    private val fromDbSet: MutableMap<String, Any>,
    private val toDbSet: MutableMap<String, Any>,
    private val statusBox: JTextArea
) {
    val colNameList = mutableSetOf<String>()
    val dbAColValueLists = mutableListOf<MutableList<Any?>>()
    val dbBColValueLists = mutableListOf<MutableList<Any?>>()
    var warning = 0
    private val errorSqlStringList = mutableListOf<String>()
    var error = false

    // Get Value from Set Map (Execute Set)
    private val func = executeSet["FUNC"] as Int
    private val mode = executeSet["MODE"] as Int
    private val isIdInsert = executeSet["IS_ID_INSERT"] as Boolean
    private val record = executeSet["RECORD"] as Int
    private val tableName = executeSet["TABLE_NAME"] as String
    private val where = executeSet["WHERE"] as String
    // End

    init { statusBox.append("$tableName from $from to $to\n") }

    fun start() {
        val sqlStringLists = mutableListOf<MutableList<String>>()
        val sqlStringCreatorList = mutableListOf<SqlStringCreator>()
        val insertIntoList = mutableListOf<InsertInto>()
        val sqlFileWriterList = mutableListOf<SqlFileWriter>()

        // Get Value from Set Map
        val toDbType = toDbSet["DB_TYPE"] as Int
        val toDbUrl = toDbSet["DB_URL"] as String
        val toDbSid = toDbSet["DB_SID"] as String
        val toDbName = toDbSet["DB_NAME"] as String
        val toDbUser = toDbSet["DB_USER"] as String
        val toDbPass = toDbSet["DB_PASS"] as String
        // End

        // Put Value to Set Map
        val selectSet = mutableMapOf<String, Any>()
        selectSet["FUNC"] = func
        selectSet["RECORD"] = record
        selectSet["TABLE_NAME"] = tableName
        selectSet["WHERE"] = where
        selectSet["FROM"] = from
        selectSet["TO"] = to

        val createSet = mutableMapOf<String, Any>()
        createSet["TABLE_NAME"] = tableName
        createSet["IS_ID_INSERT"] = isIdInsert

        val writerSet = mutableMapOf<String, Any>()
        writerSet["TABLE_NAME"] = tableName
        writerSet["FROM"] = from
        writerSet["TO"] = to
        writerSet["IS_ID_INSERT"] = isIdInsert
        // End

        val selectA = Select(selectSet, fromDbSet, statusBox)
        val selectB = Select(selectSet, toDbSet, statusBox)

        selectA.start()
        if (func != 1) selectB.start()

        if (func == 1) {
            try { selectA.join() }
            catch (_: InterruptedException) { statusBox.append("Thread Error!!!\n"); error = true }

            error = selectA.error
        } else {
            try { selectA.join(); selectB.join() }
            catch (_: InterruptedException) { statusBox.append("Thread Error!!!\n"); error = true }

            error = selectA.error || selectB.error
        }

        if (!error) {
            if (func == 1) {
                statusBox.append("Running SqlStringCreator...\n")

                // Put Value to Set Map (Create Set)
                createSet["IS_SYNC"] = true
                // End

                for (colValueLists in selectA.colValuePackage)
                    sqlStringCreatorList.add(SqlStringCreator(createSet, selectA.colNameList, colValueLists))

                for (sqlStringCreator in sqlStringCreatorList) sqlStringCreator.start()

                for (sqlStringCreator in sqlStringCreatorList) {
                    try { sqlStringCreator.join() }
                    catch (_: InterruptedException) { statusBox.append("Thread Error!!!\n"); error = true }

                    sqlStringLists.add(sqlStringCreator.sqlStringList)
                }

                if (!error) {
                    if (mode == 2) {
                        var conn: Connection? = null
                        var stmt: Statement? = null

                        statusBox.append("Doing TRUNCATE TABLE...\n")

                        try {
                            Class.forName(DbConfig().getJdbcDriver(toDbType))
                            conn = if (toDbType == 1) {
                                DriverManager.getConnection(
                                    DbConfig().getDbUrl(toDbType, toDbUrl, toDbSid),
                                    toDbUser, toDbPass
                                )
                            } else {
                                DriverManager.getConnection(
                                    DbConfig().getDbUrl(toDbType, toDbUrl, toDbName), toDbUser, toDbPass
                                )
                            }
                            stmt = conn?.createStatement()

                            stmt?.executeUpdate("TRUNCATE TABLE $tableName")
                        }
                        catch (_: ClassNotFoundException) { statusBox.append("Class Error!!!\n"); error = true }
                        catch (_: SQLException) { statusBox.append("SQL Error!!!\n"); error = true }
                        finally {
                            try { stmt?.close(); conn?.close() }
                            catch (_: SQLException) { statusBox.append("SQL Error!!!\n"); error = true }
                        }

                        if (!error) {
                            statusBox.append("Running InsertInto...\n")

                            for (sqlStringList in sqlStringLists)
                                insertIntoList.add(InsertInto(toDbSet, sqlStringList, statusBox))

                            for (insertInto in insertIntoList) insertInto.start()

                            for (insertInto in insertIntoList) {
                                try { insertInto.join() }
                                catch (_: InterruptedException) { statusBox.append("Thread Error!!!\n"); error = true }

                                if (!error) {
                                    warning += insertInto.warning
                                    errorSqlStringList.addAll(insertInto.errorSqlStringList)
                                    error = insertInto.error
                                }
                            }
                        }

                        if (!error && isIdInsert) {
                            statusBox.append("Doing SET IDENTITY_INSERT OFF...\n")

                            try {
                                Class.forName(DbConfig().getJdbcDriver(toDbType))
                                conn = DriverManager.getConnection(
                                    DbConfig().getDbUrl(toDbType, toDbUrl, toDbName),
                                    toDbUser, toDbPass
                                )
                                stmt = conn?.createStatement()

                                stmt?.executeUpdate("SET IDENTITY_INSERT $tableName OFF")
                            }
                            catch (_: ClassNotFoundException) { statusBox.append("Class Error!!!\n"); error = true }
                            catch (_: SQLException) { statusBox.append("SQL Error!!!\n"); error = true }
                            finally {
                                try { stmt?.close(); conn?.close() }
                                catch (_: SQLException) { statusBox.append("SQL Error!!!\n"); error = true }
                            }
                        }

                        if (!error && warning > 0) {
                            statusBox.append("Running SqlFileWriter...\n")

                            val sqlFileWriter = SqlFileWriter(writerSet, 0, errorSqlStringList, statusBox)
                            sqlFileWriter.start()

                            try { sqlFileWriter.join() }
                            catch (_: InterruptedException) { statusBox.append("Thread Error!!!\n"); error = true }

                            if (!error) error = sqlFileWriter.error
                        }
                    } else {
                        statusBox.append("Running SqlFileWriter...\n")

                        for (i in 0 until sqlStringLists.size) {
                            sqlFileWriterList.add(
                                SqlFileWriter(writerSet, i + 1, sqlStringLists[i], statusBox)
                            )
                        }

                        for (sqlFileWriter in sqlFileWriterList) sqlFileWriter.start()

                        for (sqlFileWriter in sqlFileWriterList) {
                            try { sqlFileWriter.join() }
                            catch (_: InterruptedException) { statusBox.append("Thread Error!!!\n"); error = true }

                            if (!error) error = sqlFileWriter.error
                        }
                    }
                }
            } else {
                if (func == 3) colNameList.addAll(selectA.colNameList)
                dbAColValueLists.addAll(selectA.colValueLists)
                dbBColValueLists.addAll(selectB.colValueLists)
            }
        }
    }
}