import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.*

class Window {

    // Init Value
    private val window = JFrame()
    private val panel = JPanel()

    // Set Value
    var fromDbType: Byte = 3
    var toDbType: Byte = 3
    var func: Byte = 1
    var mode: Byte = 1
    var idInsert = false
    val tabNameList = mutableSetOf<String>()

    // Set Value (JTextField)
    val fromDbUrl = JTextField()
    val fromDbSid = JTextField()
    val fromDbName = JTextField()
    val fromDbUser = JTextField()
    val fromDbPass = JPasswordField()
    val toDbUrl = JTextField()
    val toDbSid = JTextField()
    val toDbName = JTextField()
    val toDbUser = JTextField()
    val toDbPass = JPasswordField()
    val record = JTextField()
    val tabName = JTextField()
    val total = JTextField()
    val where = JTextArea()

    // Components
    private val rdBtnFromDbTypeOne = JRadioButton("Oracle DB")
    private val rdBtnFromDbTypeTwo = JRadioButton("SQL Server")
    private val rdBtnFromDbTypeThree = JRadioButton("MySQL")
    private val rdBtnToDbTypeOne = JRadioButton("Oracle DB")
    private val rdBtnToDbTypeTwo = JRadioButton("SQL Server")
    private val rdBtnToDbTypeThree = JRadioButton("MySQL")
    private val rdBtnFuncOne = JRadioButton("Transfer")
    private val rdBtnFuncTwo = JRadioButton("Compare")
    private val rdBtnModeOne = JRadioButton("To File")
    private val rdBtnModeTwo = JRadioButton("To DB")
    private val chBoxSetIdInsert = JCheckBox()
    private val btnAddTabName = JButton("Add")
    private val btnRemoveTabName = JButton("Remove")
    private val btnClearTabName = JButton("Clear")
    val btnStart = JButton("Start")
    val statusBox = JTextArea()

    fun print() {

        // Set Window & Panel
        window.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        window.title = "DBTools"
        window.setSize(600, 700)
        panel.layout = null
        window.contentPane = panel
        // End

        // Set Block 1 (To DB Setter)
        val lblFromDbType = JLabel("From DB Type:")
        lblFromDbType.setBounds(5, 0, 295, 25)
        panel.add(lblFromDbType)

        rdBtnFromDbTypeOne.setBounds(0, 25, 100, 25)
        rdBtnFromDbTypeOne.actionCommand = "from Oracle DB"
        rdBtnFromDbTypeOne.addActionListener(UnionRadioButtonActionListener())
        panel.add(rdBtnFromDbTypeOne)

        rdBtnFromDbTypeTwo.setBounds(100, 25, 100, 25)
        rdBtnFromDbTypeTwo.actionCommand = "from SQL Server"
        rdBtnFromDbTypeTwo.addActionListener(UnionRadioButtonActionListener())
        panel.add(rdBtnFromDbTypeTwo)

        rdBtnFromDbTypeThree.setBounds(200, 25, 100, 25)
        rdBtnFromDbTypeThree.isSelected = true
        rdBtnFromDbTypeThree.actionCommand = "from MySQL"
        rdBtnFromDbTypeThree.addActionListener(UnionRadioButtonActionListener())
        panel.add(rdBtnFromDbTypeThree)

        val lblFromDbUrl = JLabel("From DB URL:")
        lblFromDbUrl.setBounds(5, 50, 145, 25)
        panel.add(lblFromDbUrl)

        val lblFromDbSid = JLabel("From DB SID:")
        lblFromDbSid.setBounds(155, 50, 145, 25)
        panel.add(lblFromDbSid)

        fromDbUrl.setBounds(0, 75, 150, 25)
        panel.add(fromDbUrl)

        fromDbSid.setBounds(150, 75, 150, 25)
        panel.add(fromDbSid)

        val lblFromDbName = JLabel("From DB Name:")
        lblFromDbName.setBounds(5, 100, 295, 25)
        panel.add(lblFromDbName)

        fromDbName.setBounds(0, 125, 300, 25)
        panel.add(fromDbName)

        val lblFromDbUser = JLabel("From DB Username:")
        lblFromDbUser.setBounds(5, 150, 295, 25)
        panel.add(lblFromDbUser)

        fromDbUser.setBounds(0, 175, 300, 25)
        panel.add(fromDbUser)

        val lblFromDbPass = JLabel("From DB Password:")
        lblFromDbPass.setBounds(5, 200, 295, 25)
        panel.add(lblFromDbPass)

        fromDbPass.setBounds(0, 225, 300, 25)
        panel.add(fromDbPass)
        // End

        // Set Block 2 (To DB Setter)
        val lblToDbType = JLabel("To DB Type:")
        lblToDbType.setBounds(5, 250, 295, 25)
        panel.add(lblToDbType)

        rdBtnToDbTypeOne.setBounds(0, 275, 100, 25)
        rdBtnToDbTypeOne.actionCommand = "to Oracle DB"
        rdBtnToDbTypeOne.addActionListener(UnionRadioButtonActionListener())
        panel.add(rdBtnToDbTypeOne)

        rdBtnToDbTypeTwo.setBounds(100, 275, 100, 25)
        rdBtnToDbTypeTwo.actionCommand = "to SQL Server"
        rdBtnToDbTypeTwo.addActionListener(UnionRadioButtonActionListener())
        panel.add(rdBtnToDbTypeTwo)

        rdBtnToDbTypeThree.setBounds(200, 275, 100, 25)
        rdBtnToDbTypeThree.isSelected = true
        rdBtnToDbTypeThree.actionCommand = "to MySQL"
        rdBtnToDbTypeThree.addActionListener(UnionRadioButtonActionListener())
        panel.add(rdBtnToDbTypeThree)

        val lblToDbUrl = JLabel("To DB URL:")
        lblToDbUrl.setBounds(5, 300, 145, 25)
        panel.add(lblToDbUrl)

        val lblToDbSid = JLabel("To DB SID:")
        lblToDbSid.setBounds(155, 300, 145, 25)
        panel.add(lblToDbSid)

        toDbUrl.setBounds(0, 325, 150, 25)
        panel.add(toDbUrl)

        toDbSid.setBounds(150, 325, 150, 25)
        panel.add(toDbSid)

        val lblToDbName = JLabel("To DB Name:")
        lblToDbName.setBounds(5, 350, 295, 25)
        panel.add(lblToDbName)

        toDbName.setBounds(0, 375, 300, 25)
        panel.add(toDbName)

        val lblToDbUser = JLabel("To DB Username:")
        lblToDbUser.setBounds(5, 400, 295, 25)
        panel.add(lblToDbUser)

        toDbUser.setBounds(0, 425, 300, 25)
        panel.add(toDbUser)

        val lblToDbPass = JLabel("To DB Password:")
        lblToDbPass.setBounds(5, 450, 295, 25)
        panel.add(lblToDbPass)

        toDbPass.setBounds(0, 475, 300, 25)
        panel.add(toDbPass)
        // End

        // Set Block 3 (Function Setter & Start Button)
        val lblFunc = JLabel("Function Mode:")
        lblFunc.setBounds(305, 0, 295, 25)
        panel.add(lblFunc)

        rdBtnFuncOne.setBounds(300, 25, 150, 25)
        rdBtnFuncOne.isSelected = true
        rdBtnFuncOne.actionCommand = "Transfer"
        rdBtnFuncOne.addActionListener(UnionRadioButtonActionListener())
        panel.add(rdBtnFuncOne)

        rdBtnFuncTwo.setBounds(450, 25, 150, 25)
        rdBtnFuncTwo.actionCommand = "Compare"
        rdBtnFuncTwo.addActionListener(UnionRadioButtonActionListener())
        panel.add(rdBtnFuncTwo)

        val lblMode = JLabel("Transfer Mode:")
        lblMode.setBounds(305, 50, 295, 25)
        panel.add(lblMode)

        rdBtnModeOne.setBounds(300, 75, 150, 25)
        rdBtnModeOne.isSelected = true
        rdBtnModeOne.actionCommand = "To File"
        rdBtnModeOne.addActionListener(UnionRadioButtonActionListener())
        panel.add(rdBtnModeOne)

        rdBtnModeTwo.setBounds(450, 75, 150, 25)
        rdBtnModeTwo.actionCommand = "To DB"
        rdBtnModeTwo.addActionListener(UnionRadioButtonActionListener())
        panel.add(rdBtnModeTwo)

        chBoxSetIdInsert.setBounds(300, 100, 25, 25)
        chBoxSetIdInsert.addActionListener(SetIdInsert())
        panel.add(chBoxSetIdInsert)

        val lblSetIdInsert = JLabel("SET IDENTITY_INSERT ON (To File Only)")
        lblSetIdInsert.setBounds(325, 100, 270, 25)
        panel.add(lblSetIdInsert)

        val lblRecord = JLabel("Record:")
        lblRecord.setBounds(305, 125, 295, 25)
        panel.add(lblRecord)

        record.setBounds(300, 150, 300, 25)
        panel.add(record)

        val lblTabName = JLabel("Table Name:")
        lblTabName.setBounds(305, 175, 295, 25)
        panel.add(lblTabName)

        tabName.setBounds(300, 200, 300, 25)
        panel.add(tabName)

        btnAddTabName.setBounds(300, 225, 100, 25)
        btnAddTabName.actionCommand = "Add"
        btnAddTabName.addActionListener(TabNameListControl())
        panel.add(btnAddTabName)

        btnRemoveTabName.setBounds(400, 225, 100, 25)
        btnRemoveTabName.actionCommand = "Remove"
        btnRemoveTabName.addActionListener(TabNameListControl())
        panel.add(btnRemoveTabName)

        btnClearTabName.setBounds(500, 225, 100, 25)
        btnClearTabName.actionCommand = "Clear"
        btnClearTabName.addActionListener(TabNameListControl())
        panel.add(btnClearTabName)

        val lblTotalRow = JLabel("Total Row:")
        lblTotalRow.setBounds(305, 250, 295, 25)
        panel.add(lblTotalRow)

        total.setBounds(300, 275, 300, 25)
        panel.add(total)

        val lblQueryConditions = JLabel("Query Conditions:")
        lblQueryConditions.setBounds(305, 300, 295, 25)
        panel.add(lblQueryConditions)

        where.lineWrap = true
        where.wrapStyleWord = true
        val jspWhere = JScrollPane(where)
        jspWhere.setBounds(305, 328, 290, 119)
        panel.add(jspWhere)

        btnStart.setBounds(300, 475, 300, 25)
        panel.add(btnStart)
        // End

        // Set Block 4 (StatusBox)
        statusBox.isEditable = false
        statusBox.lineWrap = true
        statusBox.wrapStyleWord = true
        val jspStatusBox = JScrollPane(statusBox)
        jspStatusBox.setBounds(5, 503, 590, 164)
        panel.add(jspStatusBox)
        // End

        window.isVisible = true
    }

    fun start() {
        rdBtnFromDbTypeOne.isEnabled = false
        rdBtnFromDbTypeTwo.isEnabled = false
        rdBtnFromDbTypeThree.isEnabled = false
        rdBtnToDbTypeOne.isEnabled = false
        rdBtnToDbTypeTwo.isEnabled = false
        rdBtnToDbTypeThree.isEnabled = false
        rdBtnFuncOne.isEnabled = false
        rdBtnFuncTwo.isEnabled = false
        rdBtnModeOne.isEnabled = false
        rdBtnModeTwo.isEnabled = false
        chBoxSetIdInsert.isEnabled = false
        btnAddTabName.isEnabled = false
        btnRemoveTabName.isEnabled = false
        btnClearTabName.isEnabled = false
        btnStart.isEnabled = false
    }

    fun end() {
        rdBtnFromDbTypeOne.isEnabled = true
        rdBtnFromDbTypeTwo.isEnabled = true
        rdBtnFromDbTypeThree.isEnabled = true
        rdBtnToDbTypeOne.isEnabled = true
        rdBtnToDbTypeTwo.isEnabled = true
        rdBtnToDbTypeThree.isEnabled = true
        rdBtnFuncOne.isEnabled = true
        rdBtnFuncTwo.isEnabled = true
        rdBtnModeOne.isEnabled = true
        rdBtnModeTwo.isEnabled = true
        chBoxSetIdInsert.isEnabled = true
        btnAddTabName.isEnabled = true
        btnRemoveTabName.isEnabled = true
        btnClearTabName.isEnabled = true
        btnStart.isEnabled = true
    }

    private inner class UnionRadioButtonActionListener: ActionListener {
        override fun actionPerformed(e: ActionEvent?) {
            when (e?.actionCommand) {

                // From DB Type Change
                "from Oracle DB" -> {
                    fromDbType = 1
                    rdBtnFromDbTypeOne.isSelected = true
                    rdBtnFromDbTypeTwo.isSelected = false
                    rdBtnFromDbTypeThree.isSelected = false
                }
                "from SQL Server" -> {
                    fromDbType = 2
                    rdBtnFromDbTypeOne.isSelected = false
                    rdBtnFromDbTypeTwo.isSelected = true
                    rdBtnFromDbTypeThree.isSelected = false
                }
                "from MySQL" -> {
                    fromDbType = 3
                    rdBtnFromDbTypeOne.isSelected = false
                    rdBtnFromDbTypeTwo.isSelected = false
                    rdBtnFromDbTypeThree.isSelected = true
                }
                // End

                // To DB Type Change
                "to Oracle DB" -> {
                    toDbType = 1
                    rdBtnToDbTypeOne.isSelected = true
                    rdBtnToDbTypeTwo.isSelected = false
                    rdBtnToDbTypeThree.isSelected = false
                }
                "to SQL Server" -> {
                    toDbType = 2
                    rdBtnToDbTypeOne.isSelected = false
                    rdBtnToDbTypeTwo.isSelected = true
                    rdBtnToDbTypeThree.isSelected = false
                }
                "to MySQL" -> {
                    toDbType = 3
                    rdBtnToDbTypeOne.isSelected = false
                    rdBtnToDbTypeTwo.isSelected = false
                    rdBtnToDbTypeThree.isSelected = true
                }
                // End

                // Function Mode Change
                "Transfer" -> {
                    func = 1
                    rdBtnFuncOne.isSelected = true
                    rdBtnFuncTwo.isSelected = false
                }
                "Compare" -> {
                    func = 2
                    rdBtnFuncOne.isSelected = false
                    rdBtnFuncTwo.isSelected = true
                }
                // End

                // Transfer Mode Change
                "To File" -> {
                    mode = 1
                    rdBtnModeOne.isSelected = true
                    rdBtnModeTwo.isSelected = false
                }
                "To DB" -> {
                    mode = 2
                    rdBtnModeOne.isSelected = false
                    rdBtnModeTwo.isSelected = true
                }
                // End

            }
        }
    }

    private inner class SetIdInsert: ActionListener {
        override fun actionPerformed(e: ActionEvent?) {
            idInsert = chBoxSetIdInsert.isSelected
        }
    }

    private inner class TabNameListControl: ActionListener {
        override fun actionPerformed(e: ActionEvent?) {
            when (e?.actionCommand) {
                "Add" -> tabNameList.add(tabName.text)
                "Remove" -> tabNameList.remove(tabName.text)
                "Clear" -> tabNameList.clear()
            }

            statusBox.append("Table Name List: $tabNameList\n")
        }
    }
}