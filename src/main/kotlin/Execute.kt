package com.itoria.dbtools

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.sql.Statement
import javax.swing.JTextArea

class Execute(
    private val fromDbType: Byte,
    private val fromDbUrl: String,
    private val fromDbSid: String,
    private val fromDbName: String,
    private val fromDbUser: String,
    private val fromDbPass: String,
    private val toDbType: Byte,
    private val toDbUrl: String,
    private val toDbSid: String,
    private val toDbName: String,
    private val toDbUser: String,
    private val toDbPass: String,
    private val func: Byte,
    private val mode: Byte,
    private val idInsert: Boolean,
    private val record: Short,
    private val tabName: String,
    private val where: String,
    private val from: Int,
    private val to: Int,
    private val statusBox: JTextArea
) {
    val colNameList = mutableSetOf<String>()
    val colValueListsA = mutableListOf<MutableList<Any?>>()
    val colValueListsB = mutableListOf<MutableList<Any?>>()
    var warning = 0
    private val errorSqlStringList = mutableListOf<String>()
    var error = false

    init { statusBox.append("$tabName from $from to $to\n") }

    fun start() {
        val sqlStringPackages = mutableListOf<MutableList<String>>()
        val sqlStringCreateList = mutableListOf<SqlStringCreate>()
        val insertIntoList = mutableListOf<InsertInto>()
        val sqlFileWriterList = mutableListOf<SqlFileWriter>()

        val selectA = Select(fromDbType, fromDbUrl, fromDbSid, fromDbName, fromDbUser, fromDbPass, func, record,
            tabName, where, from, to, statusBox)
        val selectB = Select(toDbType, toDbUrl, toDbSid, toDbName, toDbUser, toDbPass, func, record, tabName, where,
            from, to, statusBox)

        selectA.start(); if (func != 1.toByte()) selectB.start()

        if (func == 1.toByte()) {
            try { selectA.join() } catch (ie: InterruptedException) { error = true }

            error = selectA.error
        } else {
            try { selectA.join(); selectB.join() } catch (ie: InterruptedException) { error = true }

            error = selectA.error || selectB.error
        }

        if (!error) {
            if (func == 1.toByte()) {
                statusBox.append("Running SqlStringCreate...\n")

                for (clValuePackage in selectA.colValuePackages) sqlStringCreateList.add(
                    SqlStringCreate(tabName, idInsert, selectA.colNameList, clValuePackage, false))

                for (sqlStringCreate in sqlStringCreateList) sqlStringCreate.start()

                for (sqlStringCreate in sqlStringCreateList) {
                    try { sqlStringCreate.join() } catch (ie: InterruptedException) { error = true }

                    sqlStringPackages.add(sqlStringCreate.sqlStringList)
                }

                if (!error) {
                    if (mode == 2.toByte()) {
                        var conn: Connection? = null
                        var stmt: Statement? = null

                        try {
                            Class.forName(DbConfig().getJdbcDriver(toDbType))
                            conn = if (toDbType == 1.toByte()) DriverManager.getConnection(
                                DbConfig().getDbUrl(toDbType, toDbUrl, toDbSid), toDbUser, toDbPass)
                            else DriverManager.getConnection(DbConfig().getDbUrl(toDbType, toDbUrl, toDbName), toDbUser,
                                toDbPass)
                            stmt = conn?.createStatement()

                            stmt?.executeUpdate("TRUNCATE TABLE $tabName")
                        } catch (e: Exception) {error = true} finally {
                            try { stmt?.close(); conn?.close() } catch (sqe: SQLException) { error = true }
                        }

                        if (!error) {
                            statusBox.append("Running InsertInto...\n")

                            for (sqlStringListPackage in sqlStringPackages) insertIntoList.add(
                                InsertInto(toDbType, toDbUrl, toDbSid, toDbName, toDbUser, toDbPass,
                                    sqlStringListPackage)
                            )

                            for (insertInto in insertIntoList) insertInto.start()

                            for (insertInto in insertIntoList) {
                                try { insertInto.join() } catch (ie: InterruptedException) { error = true }

                                warning += insertInto.warning; if (!error) error = insertInto.error
                                errorSqlStringList.addAll(insertInto.sqlStringListOut)
                            }
                        }

                        if (!error && idInsert) {
                            try {
                                Class.forName(DbConfig().getJdbcDriver(toDbType))
                                conn = DriverManager.getConnection(DbConfig().getDbUrl(toDbType, toDbUrl, toDbName),
                                    toDbUser, toDbPass)
                                stmt = conn?.createStatement()

                                stmt?.executeUpdate("SET IDENTITY_INSERT $tabName OFF")
                            } catch (e: Exception) {error = true} finally {
                                try { stmt?.close(); conn?.close() } catch (sqe: SQLException) { error = true }
                            }
                        }

                        if (!error && warning > 0) {
                            statusBox.append("Running SqlFileWriter...\n")

                            val sqlFileWriter = SqlFileWriter(tabName, from, to, -1, idInsert,
                                errorSqlStringList); sqlFileWriter.start()

                            try { sqlFileWriter.join() } catch (ie: InterruptedException) { error = true }

                            if (!error) error = sqlFileWriter.error
                        }
                    } else {
                        statusBox.append("Running SqlFileWriter...\n")

                        for (i in 0 until sqlStringPackages.size) sqlFileWriterList.add(
                            SqlFileWriter(tabName, from, to, i + 1, idInsert, sqlStringPackages[i]))

                        for (sqlFileWriter in sqlFileWriterList) sqlFileWriter.start()

                        for (sqlFileWriter in sqlFileWriterList) {
                            try { sqlFileWriter.join() } catch (ie: InterruptedException) { error = true }

                            if (!error) error = sqlFileWriter.error
                        }
                    }
                }
            } else {
                if (func == 3.toByte()) colNameList.addAll(selectA.colNameList)
                colValueListsA.addAll(selectA.colValueLists); colValueListsB.addAll(selectB.colValueLists)
            }
        }
    }
}