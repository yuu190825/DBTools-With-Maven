package com.itoria.dbtools

class SqlStringCreate(
    private val tabName: String,
    private val idInsert: Boolean,
    private val colNameList: MutableSet<String>,
    private val colValueLists: MutableList<MutableList<Any?>>,
    private val isSync: Boolean
): Thread() {
    val sqlStringList = mutableListOf<String>()

    override fun run() {
        for (colValueList in colValueLists) {
            if (isSync) for (i in 0 until colValueList.size) colValueList[i] = colValueList[i].toString()
                .replace("'", "''")

            val sqlString = if (idInsert) StringBuilder("SET IDENTITY_INSERT $tabName ON; INSERT INTO $tabName(" +
                    colNameList.elementAt(0))
            else StringBuilder("INSERT INTO $tabName(${colNameList.elementAt(0)}")

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

        // DateTime
        for (i in 0 until sqlStringList.size) sqlStringList[i] = sqlStringList[i].replace(
            "[0-9]{5}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}.[0-9]{1,3}".toRegex(),
            "1900-01-01 00:00:00.0")
        // End

    }
}