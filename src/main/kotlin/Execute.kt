import javax.swing.JTextArea

class Execute(
    private val fromDbType: Byte,
    private val fromDbUrl: String,
    private val fromDbSid: String,
    private val fromDbName: String,
    private val fromDbUser: String,
    private val fromDbPass: String,
    private val toDbType: Byte,
    private val toDbUrl: String,
    private val toDbSid: String,
    private val toDbName: String,
    private val toDbUser: String,
    private val toDbPass: String,
    private val func: Byte,
    private val idInsert: Boolean,
    private val record: Short,
    private val tabName: String,
    private val where: String,
    private val from: Long,
    private val to: Long,
    private val statusBox: JTextArea
) {
    val colValueListsA = mutableListOf<MutableList<Any?>>()
    val colValueListsB = mutableListOf<MutableList<Any?>>()
    var warning = 0
    private val errorSqlStringList = mutableListOf<String>()
    var error = false

    init { statusBox.append("$tabName from $from to $to\n") }

    fun start(mode: Byte) {
        val sqlStringPackages = mutableListOf<MutableList<String>>()
        val sqlStringCreateList = mutableListOf<SqlStringCreate>()
        val insertIntoList = mutableListOf<InsertInto>()
        val sqlFileWriterList = mutableListOf<SqlFileWriter>()

        val selectA = Select(fromDbType, fromDbUrl, fromDbSid, fromDbName, fromDbUser, fromDbPass, func, 1,
            record, tabName, where, from, to, statusBox)
        val selectB = Select(toDbType, toDbUrl, toDbSid, toDbName, toDbUser, toDbPass, func, 2, record, tabName,
            where, from, to, statusBox)

        selectA.start()
        if (func.toInt() == 2) selectB.start()

        if (func.toInt() == 1) {
            try { selectA.join() } catch (ie: InterruptedException) { error = true }

            error = selectA.error
        } else {
            try {
                selectA.join()
                selectB.join()
            } catch (ie: InterruptedException) { error = true }

            error = selectA.error || selectB.error
        }

        if (!error) {
            if (func.toInt() == 1) {
                statusBox.append("Running SqlStringCreate...\n")

                for (clValuePackage in selectA.colValuePackages) sqlStringCreateList.add(
                    SqlStringCreate(tabName, selectA.colNameList, clValuePackage))

                for (sqlStringCreateThread in sqlStringCreateList) sqlStringCreateThread.start()

                for (sqlStringCreateThread in sqlStringCreateList) {
                    try { sqlStringCreateThread.join() } catch (ie: InterruptedException) { error = true }

                    sqlStringPackages.add(sqlStringCreateThread.sqlStringList)
                }

                if (!error) {
                    if (mode.toInt() == 2) {
                        statusBox.append("Running InsertInto...\n")

                        for (sqlStringListPackage in sqlStringPackages) insertIntoList.add(
                            InsertInto(toDbType, toDbUrl, toDbName, toDbUser, toDbPass, sqlStringListPackage))

                        for (insertIntoThread in insertIntoList) insertIntoThread.start()

                        for (insertIntoThread in insertIntoList) {
                            try { insertIntoThread.join() } catch (ie: InterruptedException) { error = true }

                            warning += insertIntoThread.warning
                            errorSqlStringList.addAll(insertIntoThread.sqlStringListOut)
                            if (!error) error = insertIntoThread.error
                        }

                        if (!error && warning > 0) {
                            statusBox.append("Running SqlFileWriter...\n")

                            val sqlFileWriterThread = SqlFileWriter(tabName, from, to, -1, false,
                                errorSqlStringList)

                            sqlFileWriterThread.start()

                            try { sqlFileWriterThread.join() } catch (ie: InterruptedException) { error = true }
                        }
                    } else {
                        statusBox.append("Running SqlFileWriter...\n")

                        for (i in 0..<sqlStringPackages.size) sqlFileWriterList.add(
                            SqlFileWriter(tabName, from, to, i + 1, idInsert, sqlStringPackages[i]))

                        for (sqlFileWriterThread in sqlFileWriterList) sqlFileWriterThread.start()

                        for (sqlFileWriterThread in sqlFileWriterList) {
                            try { sqlFileWriterThread.join() } catch (ie: InterruptedException) { error = true }

                            if (!error) error = sqlFileWriterThread.error
                        }
                    }
                }
            } else {
                colValueListsA.addAll(selectA.colValueListsA)
                colValueListsB.addAll(selectB.colValueListsB)
            }
        }
    }
}