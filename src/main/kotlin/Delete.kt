package com.itoria.dbtools

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.sql.Statement
import javax.swing.JTextArea

class Delete(
    private val dbType: Byte,
    private val dbUrl: String,
    private val dbSid: String,
    private val dbName: String,
    private val dbUser: String,
    private val dbPass: String,
    private val tabName: String,
    private val idColName: String,
    private val toDeleteIdList: MutableList<String>,
    private val statusBox: JTextArea
) {
    var error = false

    fun start() {
        var conn: Connection? = null
        var stmt: Statement? = null

        try {
            Class.forName(DbConfig().getJdbcDriver(dbType))
            conn = if (dbType == 1.toByte()) DriverManager.getConnection(
                DbConfig().getDbUrl(dbType, dbUrl, dbSid), dbUser, dbPass)
            else DriverManager.getConnection(DbConfig().getDbUrl(dbType, dbUrl, dbName), dbUser, dbPass)
            stmt = conn?.createStatement()
        } catch (cne: ClassNotFoundException) { statusBox.append("Class Error!!!\n"); error = true }
        catch (sqe: SQLException) { statusBox.append("SQL Error!!!\n"); error = true }

        if (!error) {
            for (id in toDeleteIdList) { // DELETE
                try { stmt?.executeUpdate("DELETE FROM $tabName WHERE $idColName = $id") }
                catch (sqe: SQLException) { statusBox.append("SQL Error!!!\n"); error = true }
            }
        }

        if (!error) {
            try { stmt?.close(); conn?.close() } catch (sqe: SQLException) {
                statusBox.append("SQL Error!!!\n"); error = true
            }
        }
    }
}