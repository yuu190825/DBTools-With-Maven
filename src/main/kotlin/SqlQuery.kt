// SQL Query in Oracle DB(1)
private const val SELECT_COLUMN_NAME_IN_ORACLE_DB = "SELECT COLUMN_NAME FROM ALL_TAB_COLUMNS WHERE OWNER ="
private const val SELECT_COLUMN_NAME_FROM_IN_ORACLE_DB = "AND TABLE_NAME ="
private const val SELECT_COLUMN_NAME_ORDER_BY_IN_ORACLE_DB = "ORDER BY COLUMN_ID"
private const val SELECT_IN_ORACLE_DB = "SELECT * FROM (SELECT ROWNUM AS NUM, T.* FROM"
private const val SELECT_ONE_LIMIT_IN_ORACLE_DB = "WHERE NUM = 1"
private const val SELECT_ALL_FROM_IN_ORACLE_DB = "WHERE NUM >="
private const val SELECT_ALL_TO_IN_ORACLE_DB = "AND NUM <="
private const val SELECT_ALL_ORDER_BY_IN_ORACLE_DB = "ORDER BY"

// SQL Query in SQL Server(2)
private const val SELECT_COLUMN_NAME_IN_SQL_SERVER = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS " +
        "WHERE TABLE_CATALOG ="
private const val SELECT_COLUMN_NAME_FROM_IN_SQL_SERVER = "AND TABLE_NAME ="
private const val SELECT_COLUMN_NAME_ORDER_BY_IN_SQL_SERVER = "ORDER BY ORDINAL_POSITION"
private const val SELECT_ONE_IN_SQL_SERVER = "SELECT TOP 1 * FROM"
private const val SELECT_ALL_ORDER_BY_IN_SQL_SERVER = "SELECT * FROM (SELECT ROW_NUMBER() OVER(ORDER BY"
private const val SELECT_ALL_IN_SQL_SERVER = "AS NUM, * FROM"
private const val SELECT_ALL_FROM_IN_SQL_SERVER = "A WHERE NUM >="
private const val SELECT_ALL_TO_IN_SQL_SERVER = "AND NUM <="

// SQL Query in MySQL(3)
private const val SELECT_COLUMN_NAME_IN_MYSQL = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS " +
        "WHERE TABLE_SCHEMA ="
private const val SELECT_COLUMN_NAME_FROM_IN_MYSQL = "AND TABLE_NAME ="
private const val SELECT_COLUMN_NAME_ORDER_BY_IN_MYSQL = "ORDER BY ORDINAL_POSITION"
private const val SELECT_IN_MYSQL = "SELECT * FROM"
private const val SELECT_ONE_LIMIT_IN_MYSQL = "LIMIT 1"
private const val SELECT_ALL_ORDER_BY_IN_MYSQL = "ORDER BY"
private const val SELECT_ALL_LIMIT_IN_MYSQL = "LIMIT"

class SqlQuery {
    fun getSelectColumnNameQuery(type: Byte, dbName: String, tabName: String): String {
        return if (type.toInt() == 1) "$SELECT_COLUMN_NAME_IN_ORACLE_DB '$dbName' " +
                "$SELECT_COLUMN_NAME_FROM_IN_ORACLE_DB '$tabName' $SELECT_COLUMN_NAME_ORDER_BY_IN_ORACLE_DB"
        else if (type.toInt() == 2) "$SELECT_COLUMN_NAME_IN_SQL_SERVER '$dbName' " +
                "$SELECT_COLUMN_NAME_FROM_IN_SQL_SERVER '$tabName' $SELECT_COLUMN_NAME_ORDER_BY_IN_SQL_SERVER"
        else "$SELECT_COLUMN_NAME_IN_MYSQL '$dbName' $SELECT_COLUMN_NAME_FROM_IN_MYSQL '$tabName' " +
                SELECT_COLUMN_NAME_ORDER_BY_IN_MYSQL
    }

    fun getSelectOneQuery(type: Byte, tabName: String): String {
        return if (type.toInt() == 1) "$SELECT_IN_ORACLE_DB $tabName T) $SELECT_ONE_LIMIT_IN_ORACLE_DB"
        else if (type.toInt() == 2) "$SELECT_ONE_IN_SQL_SERVER $tabName"
        else "$SELECT_IN_MYSQL $tabName $SELECT_ONE_LIMIT_IN_MYSQL"
    }

    fun getSelectAllQuery(type: Byte, tabName: String, where: String, id: String, from: Long, to: Long): String {
        return if (type.toInt() == 1) "$SELECT_IN_ORACLE_DB $tabName T $where) $SELECT_ALL_FROM_IN_ORACLE_DB $from " +
                "$SELECT_ALL_TO_IN_ORACLE_DB $to $SELECT_ALL_ORDER_BY_IN_ORACLE_DB $id"
        else if (type.toInt() == 2) "$SELECT_ALL_ORDER_BY_IN_SQL_SERVER $id) $SELECT_ALL_IN_SQL_SERVER $tabName " +
                "$where) $SELECT_ALL_FROM_IN_SQL_SERVER $from $SELECT_ALL_TO_IN_SQL_SERVER $to"
        else "$SELECT_IN_MYSQL $tabName $where $SELECT_ALL_ORDER_BY_IN_MYSQL $id $SELECT_ALL_LIMIT_IN_MYSQL $from,$to"
    }
}