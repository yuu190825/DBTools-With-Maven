package com.itoria.dbtools

class SqlStringCreator(
    private val createSet: MutableMap<String, Any>,
    private val colNameList: MutableSet<String>,
    private val colValueLists: MutableList<MutableList<Any?>>
): Thread() {
    val sqlStringList = mutableListOf<String>()

    override fun run() {

        // Get Value from Set Map
        val tableName = createSet["TABLE_NAME"] as String
        val isIdInsert = createSet["IS_ID_INSERT"] as Boolean
        val isSync = createSet["IS_SYNC"] as Boolean
        // End

        for (colValueList in colValueLists) {
            if (isSync)
                for (i in 0 until colValueList.size)
                    colValueList[i] = colValueList[i].toString().replace("'", "''")

            val sqlString = if (isIdInsert) StringBuilder("SET IDENTITY_INSERT $tableName ON; " +
                    "INSERT INTO $tableName(${colNameList.elementAt(0)}")
            else StringBuilder("INSERT INTO $tableName(${colNameList.elementAt(0)}")

            for (i in 1 until colNameList.size) sqlString.append(", ${colNameList.elementAt(i)}")

            if (isSync) {
                if (colValueList[0] == null) sqlString.append(") VALUES(NULL")
                else sqlString.append(") VALUES('${colValueList[0]}'")
            } else {
                if (colValueList[0]?.equals("NULL") == true) sqlString.append(") VALUES(NULL")
                else sqlString.append(") VALUES('${colValueList[0]}'")
            }

            for (i in 1 until colValueList.size) {
                if (isSync) {
                    if (colValueList[i] == null) sqlString.append(", NULL")
                    else sqlString.append(", '${colValueList[i]}'")
                } else {
                    if (colValueList[i]?.equals("NULL") == true) sqlString.append(", NULL")
                    else sqlString.append(", '${colValueList[i]}'")
                }
            }

            sqlStringList.add(sqlString.append(");").toString())
        }

        // DateTime Replace
        for (i in 0 until sqlStringList.size) sqlStringList[i] = sqlStringList[i].replace(
            "[0-9]{5}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}.[0-9]{1,3}".toRegex(),
            "1900-01-01 00:00:00.0")
    }
}