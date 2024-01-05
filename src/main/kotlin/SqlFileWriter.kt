import java.io.BufferedWriter
import java.io.FileWriter
import java.io.IOException

class SqlFileWriter(
    private val tabName: String,
    private val from: Long,
    private val to: Long,
    private val fileNumber: Int,
    private val idInsert: Boolean,
    private val sqlStringList: MutableList<String>
): Thread() {
    var error = false

    override fun run() {
        var bw: BufferedWriter? = null

        try {
            bw = if (fileNumber > 0) BufferedWriter(FileWriter("${tabName}_${from}_${to}_$fileNumber.sql"))
            else BufferedWriter(FileWriter("${tabName}_error.sql"))

            if (idInsert) bw.write("SET IDENTITY_INSERT $tabName ON;\n")

            for (sqlString in sqlStringList) bw.write("$sqlString\n")

            if (idInsert) bw.write("SET IDENTITY_INSERT $tabName OFF;")
        } catch (ioe: IOException) { error = true } finally {
            try { bw?.close() } catch (ioe: IOException) { error = true } }
    }
}