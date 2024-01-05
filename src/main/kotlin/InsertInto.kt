import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.sql.Statement

class InsertInto(
    private val dbType: Byte,
    private val dbUrl: String,
    private val dbName: String,
    private val dbUser: String,
    private val dbPass: String,
    private val sqlStringListIn: MutableList<String>
): Thread() {
    var warning = 0
    val sqlStringListOut = mutableListOf<String>()
    var error = false

    override fun run() {
        var conn: Connection? = null
        var stmt: Statement? = null

        try {
            Class.forName(DbConfig().getJdbcDriver(dbType))
            conn = DriverManager.getConnection(DbConfig().getDbUrl(dbType, dbUrl, dbName), dbUser, dbPass)
            stmt = conn?.createStatement()
        } catch (e: Exception) { error = true }

        if (!error) {
            for (sqlString in sqlStringListIn) {
                try { stmt?.executeUpdate(sqlString) } catch (sqe: SQLException) { // INSERT INTO
                    warning++
                    sqlStringListOut.add(sqlString)
                }
            }

            try {
                stmt?.close()
                conn?.close()
            } catch (sqe: SQLException) { error = true }
        }
    }
}