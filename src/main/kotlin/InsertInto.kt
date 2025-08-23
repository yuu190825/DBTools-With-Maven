package com.itoria.dbtools

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.sql.Statement
import javax.swing.JTextArea

class InsertInto(
    private val dbSet: MutableMap<String, Any>,
    private val sqlStringList: MutableList<String>,
    private val statusBox: JTextArea
): Thread() {
    var warning = 0
    val errorSqlStringList = mutableListOf<String>()
    var error = false

    override fun run() {
        var conn: Connection? = null
        var stmt: Statement? = null

        // Get Value from Set Map
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
            stmt = conn?.createStatement()
        }
        catch (_: ClassNotFoundException) { statusBox.append("Class Error!!!\n"); error = true }
        catch (_: SQLException) { statusBox.append("SQL Error!!!\n"); error = true }

        if (!error) {
            for (sqlString in sqlStringList) {
                try { stmt?.executeUpdate(sqlString) } // INSERT INTO
                catch (_: SQLException) { warning++; errorSqlStringList.add(sqlString) }
            }

            try { stmt?.close(); conn?.close() }
            catch (_: SQLException) { statusBox.append("SQL Error!!!\n"); error = true }
        }
    }
}