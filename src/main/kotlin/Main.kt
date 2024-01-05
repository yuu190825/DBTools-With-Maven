import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.JOptionPane

// Main Config
private const val DEFAULT_RECORD: Short = 5000
private const val MAX: Long = 500000

// Window Init
private val window = Window()

fun main() {
    StartButton().init()
    window.print()
}

private class StartButton {
    fun init() { window.btnStart.addActionListener(Click()) }

    private inner class Click: ActionListener { override fun actionPerformed(e: ActionEvent?) { Start().start() } }

    private inner class Start {
        fun start() {
            val colValueListsA = mutableListOf<MutableList<Any?>>()
            val colValueListsB = mutableListOf<MutableList<Any?>>()
            var warning = 0
            var error = false

            window.start()

            val record = try { window.record.text.toShort() } catch (nfe: NumberFormatException) { DEFAULT_RECORD }
            val total = try { window.total.text.toLong() } catch (nfe: NumberFormatException) { 0 }

            if (window.tabNameList.isNotEmpty()) {
                for (tabName in window.tabNameList) {
                    if (!error) {
                        var from: Long = if (window.fromDbType.toInt() == 3) 0 else 1
                        var to: Long = MAX

                        while (from <= total) {
                            val execute = Execute(window.fromDbType, window.fromDbUrl.text, window.fromDbSid.text,
                                window.fromDbName.text, window.fromDbUser.text, String(window.fromDbPass.password),
                                window.toDbType, window.toDbUrl.text, window.toDbSid.text, window.toDbName.text,
                                window.toDbUser.text, String(window.toDbPass.password), window.func, window.idInsert,
                                record, tabName, window.where.text, from, to, window.statusBox)

                            execute.start(window.mode)

                            warning += execute.warning
                            error = execute.error

                            if (!error) {
                                if (window.func.toInt() == 2) {
                                    colValueListsA.addAll(execute.colValueListsA)
                                    colValueListsB.addAll(execute.colValueListsB)
                                }

                                from += MAX
                                if (window.fromDbType.toInt() != 3) to += MAX
                            } else break
                        }

                        if (!error && window.func.toInt() == 2) {
                            val compare = Compare(colValueListsA, colValueListsB)

                            compare.start()

                            val csvFileWriter = CsvFileWriter(window.fromDbName.text, tabName, compare.notInDBA,
                                compare.addInDBA, compare.xorInDBA)

                            csvFileWriter.start()

                            error = csvFileWriter.error
                        }
                    }
                }
            }

            if (!error) {
                if (warning == 0) JOptionPane.showMessageDialog(null, "ウルトラハッピー",
                    "Success", JOptionPane.INFORMATION_MESSAGE)
                else JOptionPane.showMessageDialog(null, "はっぷっぷー (Error:$warning)",
                    "Not Success", JOptionPane.WARNING_MESSAGE)
            } else JOptionPane.showMessageDialog(null, "めちょっく", "Error",
                JOptionPane.ERROR_MESSAGE)

            window.end()
        }
    }
}