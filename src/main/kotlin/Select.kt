package com.itoria.dbtools

import java.sql.*
import javax.swing.JTextArea

class Select(
    private val dbType: Byte,
    private val dbUrl: String,
    private val dbSid: String,
    private val dbName: String,
    private val dbUser: String,
    private val dbPass: String,
    private val func: Byte,
    private val record: Short,
    private val tabName: String,
    private val where: String,
    private val from: Int,
    private val to: Int,
    private val statusBox: JTextArea
): Thread() {
    val colNameList = mutableSetOf<String>()
    private val toDeleteColNameList = mutableSetOf<String>()
    var colValueLists = mutableListOf<MutableList<Any?>>()
    val colValuePackages = mutableListOf<MutableList<MutableList<Any?>>>()
    var error = false

    override fun run() {
        var conn: Connection? = null
        var stmt: Statement? = null
        var rs: ResultSet? = null

        try {
            Class.forName(DbConfig().getJdbcDriver(dbType))
            conn = if (dbType == 1.toByte()) DriverManager.getConnection(
                DbConfig().getDbUrl(dbType, dbUrl, dbSid), dbUser, dbPass)
            else DriverManager.getConnection(DbConfig().getDbUrl(dbType, dbUrl, dbName), dbUser, dbPass)
            stmt = conn.createStatement()

            // SELECT COLUMN_NAME FROM TABLE
            statusBox.append("Getting COLUMN_NAME...\n")

            rs = stmt.executeQuery(SqlQuery().getSelectColumnNameQuery(dbType, dbName, tabName))

            while (rs.next()) colNameList.add(rs.getString("COLUMN_NAME"))
            // End

            // Check DataType
            statusBox.append("Checking DataType...\n")

            rs = stmt.executeQuery(SqlQuery().getSelectOneQuery(dbType, tabName))

            while (rs.next()) {
                val metadata = rs.metaData
                for (i in 1..metadata.columnCount) {
                    if (
                        metadata.getColumnTypeName(i).equals("BLOB") ||
                        metadata.getColumnTypeName(i).equals("timestamp") ||
                        metadata.getColumnTypeName(i).equals("varbinary")
                    ) toDeleteColNameList.add(metadata.getColumnName(i))
                }
            }

            for (colName in toDeleteColNameList) colNameList.remove(colName)
            // End

            // SELECT * FROM TABLE
            statusBox.append("Getting COLUMN_VALUE...\n")

            var finalWhere = ""
            if (dbType == 1.toByte()) for (colName in colNameList) finalWhere = where.replace(colName,
                "T.$colName", true)
            else finalWhere = where

            rs = stmt.executeQuery(
                SqlQuery().getSelectAllQuery(dbType, tabName, finalWhere, colNameList.elementAt(0), from, to))

            while (rs.next()) {
                val colValueList = mutableListOf<Any?>()
                for (colName in colNameList) {
                    if (func == 1.toByte()) {
                        try { colValueList.add(rs.getTimestamp(colName)) } catch (e: Exception) {
                            colValueList.add(rs.getString(colName).replace("'", "''")) // '
                        }
                    } else {
                        if (rs.getObject(colName) == null) colValueList.add("NULL") else {
                            try { colValueList.add(rs.getTimestamp(colName)) } catch (e: Exception) {
                                colValueList.add(rs.getString(colName))
                            }
                        }
                    }
                }

                colValueLists.add(colValueList)

                if (func == 1.toByte() && colValueLists.size.toShort() == record) {
                    colValuePackages.add(colValueLists); colValueLists = mutableListOf()
                }
            }

            if (func == 1.toByte() && colValueLists.isNotEmpty()) colValuePackages.add(colValueLists)
            // End

        } catch (cne: ClassNotFoundException) { statusBox.append("Class Error!!!\n"); error = true }
        catch (sqe: SQLException) { statusBox.append("SQL Error!!!\n"); error = true }
        finally {
            try { rs?.close(); stmt?.close(); conn?.close() } catch (sqe: SQLException) {
                statusBox.append("SQL Error!!!\n"); error = true
            }
        }
    }
}