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
    private val step: Byte,
    private val record: Short,
    private val tabName: String,
    private val where: String,
    private val from: Long,
    private val to: Long,
    private val statusBox: JTextArea
): Thread() {
    val colNameList = mutableSetOf<String>()
    private val toDeleteColNameList = mutableSetOf<String>()
    var colValueListsA = mutableListOf<MutableList<Any?>>()
    val colValueListsB = mutableListOf<MutableList<Any?>>()
    val colValuePackages = mutableListOf<MutableList<MutableList<Any?>>>()
    var error = false

    override fun run() {
        var conn: Connection? = null
        var stmt: Statement? = null
        var rs: ResultSet? = null

        try {
            Class.forName(DbConfig().getJdbcDriver(dbType))
            conn = if (dbType.toInt() == 1) DriverManager.getConnection(
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
                for (i in 1 .. metadata.columnCount) if (metadata.getColumnTypeName(i).equals("BLOB") ||
                    metadata.getColumnTypeName(i).equals("timestamp") ||
                    metadata.getColumnTypeName(i).equals("varbinary"))
                    toDeleteColNameList.add(metadata.getColumnName(i))
            }

            for (colName in toDeleteColNameList) colNameList.remove(colName)
            // End

            // SELECT * FROM TABLE
            statusBox.append("Getting COLUMN_VALUE...\n")

            var finalWhere = ""
            if (dbType.toInt() == 1) for (colName in colNameList) finalWhere = where.replace(colName,
                "T.$colName", true)
            else finalWhere = where

            rs = stmt.executeQuery(
                SqlQuery().getSelectAllQuery(dbType, tabName, finalWhere, colNameList.elementAt(0), from, to))

            while (rs.next()) {
                val colValueList = mutableListOf<Any?>()
                for (colName in colNameList) {
                    if (func.toInt() == 1) {
                        try { colValueList.add(rs.getTimestamp(colName)) } catch (e: Exception) {
                            colValueList.add(rs.getString(colName).replace("'", "''")) } // '
                    } else {
                        if (rs.getObject(colName) == null) colValueList.add("NULL") else {
                            try { colValueList.add(rs.getTimestamp(colName)) } catch (e: Exception) {
                                colValueList.add(rs.getString(colName)) }
                        }
                    }
                }

                if (func.toInt() == 1) colValueListsA.add(colValueList) else {
                    if (step.toInt() == 1) colValueListsA.add(colValueList) else colValueListsB.add(colValueList) }

                if (func.toInt() == 1 && colValueListsA.size.toShort() == record) {
                    colValuePackages.add(colValueListsA)
                    colValueListsA = mutableListOf()
                }
            }

            if (func.toInt() == 1 && colValueListsA.isNotEmpty()) colValuePackages.add(colValueListsA)
            // End

        } catch (e: Exception) { error = true } finally {
            try {
                rs?.close()
                stmt?.close()
                conn?.close()
            } catch (sqe: SQLException) { error = true }
        }
    }
}