package com.itoria.dbtools

import java.sql.*
import javax.swing.JTextArea

class Select(
    private val selectSet: MutableMap<String, Any>,
    private val dbSet: MutableMap<String, Any>,
    private val statusBox: JTextArea
): Thread() {
    val colNameList = mutableSetOf<String>()
    private val toDeleteColNameList = mutableSetOf<String>()
    var colValueLists = mutableListOf<MutableList<Any?>>()
    val colValuePackage = mutableListOf<MutableList<MutableList<Any?>>>()
    var error = false

    override fun run() {
        var conn: Connection? = null
        var stmt: Statement? = null
        var rs: ResultSet? = null

        // Get Value from Set Map
        val func = selectSet["FUNC"] as Int
        val record = selectSet["RECORD"] as Int
        val tableName = selectSet["TABLE_NAME"] as String
        val where = selectSet["WHERE"] as String
        val from = selectSet["FROM"] as Int
        val to = selectSet["TO"] as Int

        val dbType = dbSet["DB_TYPE"] as Int
        val dbUrl = dbSet["DB_URL"] as String
        val dbSid = dbSet["DB_SID"] as String
        val dbName = dbSet["DB_NAME"] as String
        val dbUser = dbSet["DB_USER"] as String
        val dbPass = dbSet["DB_PASS"] as String
        // End

        try {
            Class.forName(DbConfig().getJdbcDriver(dbType))
            conn = if (dbType == 1) {
                DriverManager.getConnection(
                    DbConfig().getDbUrl(dbType, dbUrl, dbSid), dbUser, dbPass
                )
            } else
                DriverManager.getConnection(DbConfig().getDbUrl(dbType, dbUrl, dbName), dbUser, dbPass)
            stmt = conn.createStatement()

            // SELECT COLUMN_NAME FROM TABLE
            statusBox.append("Getting COLUMN_NAME...\n")

            rs = stmt.executeQuery(SqlQuery().getSelectColumnNameQuery(dbType, dbName, tableName))

            while (rs.next()) colNameList.add(rs.getString("COLUMN_NAME"))
            // End

            // Check DataType
            statusBox.append("Checking DataType...\n")

            rs = stmt.executeQuery(SqlQuery().getSelectOneQuery(dbType, tableName))

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
            if (dbType == 1)
                for (colName in colNameList)
                    finalWhere = where.replace(colName, "T.$colName", true)
            else finalWhere = where

            rs = stmt.executeQuery(
                SqlQuery().getSelectAllQuery(dbType, tableName, finalWhere,
                    colNameList.elementAt(0), from, to)
            )

            while (rs.next()) {
                val colValueList = mutableListOf<Any?>()
                for (colName in colNameList) {
                    if (func == 1) {
                        try { colValueList.add(rs.getTimestamp(colName)) }
                        catch (_: Exception) {
                            // ' Replace
                            colValueList.add(rs.getString(colName).replace("'", "''"))
                        }
                    } else {
                        if (rs.getObject(colName) == null) colValueList.add("NULL")
                        else {
                            try { colValueList.add(rs.getTimestamp(colName)) }
                            catch (_: Exception) { colValueList.add(rs.getString(colName)) }
                        }
                    }
                }

                colValueLists.add(colValueList)

                if (func == 1 && colValueLists.size == record) {
                    colValuePackage.add(colValueLists); colValueLists = mutableListOf()
                }
            }

            if (func == 1 && colValueLists.isNotEmpty()) colValuePackage.add(colValueLists)
            // End

        }
        catch (_: ClassNotFoundException) { statusBox.append("Class Error!!!\n"); error = true }
        catch (_: SQLException) { statusBox.append("SQL Error!!!\n"); error = true }
        finally {
            try { rs?.close(); stmt?.close(); conn?.close() }
            catch (_: SQLException) { statusBox.append("SQL Error!!!\n"); error = true }
        }
    }
}