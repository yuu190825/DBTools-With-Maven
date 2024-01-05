// SQL Config in Oracle DB(1)
private const val ORACLE_JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver"
private const val ORACLE_DB_URL = "jdbc:oracle:thin:@"
private const val ORACLE_DB_NAME = ":1521:"

// SQL Config in SQL Server(2)
private const val MS_JDBC_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver"
private const val MS_DB_URL = "jdbc:sqlserver://"
private const val MS_DB_NAME = ":1433;encrypt=false;databaseName="

// SQL Config in MySQL(3)
private const val MYSQL_JDBC_DRIVER = "com.mysql.cj.jdbc.Driver"
private const val MYSQL_DB_URL = "jdbc:mysql://"
private const val MYSQL_DB_NAME = "/"

class DbConfig {
    fun getJdbcDriver(dbType: Byte): String {
        return if (dbType.toInt() == 1) ORACLE_JDBC_DRIVER
        else if (dbType.toInt() == 2) MS_JDBC_DRIVER
        else MYSQL_JDBC_DRIVER
    }

    fun getDbUrl(dbType: Byte, dbUrl: String, dbName: String): String {
        return if (dbType.toInt() == 1) ORACLE_DB_URL + dbUrl + ORACLE_DB_NAME + dbName
        else if (dbType.toInt() == 2) MS_DB_URL + dbUrl + MS_DB_NAME + dbName
        else MYSQL_DB_URL + dbUrl + MYSQL_DB_NAME + dbName
    }
}