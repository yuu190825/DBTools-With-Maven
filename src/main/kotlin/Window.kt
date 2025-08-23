package com.itoria.dbtools

import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.*
import javax.swing.text.DefaultCaret

class Window {

    // Init Value
    private val window = JFrame()
    private val panel = JPanel()

    // Set Value
    var fromDbType = 3
    var toDbType = 3
    var func = 1
    var mode = 1
    var record = 5000
    var isIdInsert = false
    val tableNameList = mutableSetOf<String>()

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
    private val tfRecord = JTextField()
    val tableName = JTextField()
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
    private val rdBtnFuncThree = JRadioButton("Sync")
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
        window.title = "DBTools"; window.setSize(600, 700); window.isResizable = false
        window.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        panel.layout = null; window.contentPane = panel
        // End

        // Set Block 1 (From DB Setter)
        val lblFromDbType = JLabel("From DB Type:")
        lblFromDbType.setBounds(5, 0, 295, 25); panel.add(lblFromDbType)

        rdBtnFromDbTypeOne.actionCommand = "from Oracle DB"
        rdBtnFromDbTypeOne.addActionListener(UnionRadioButtonActionListener())
        rdBtnFromDbTypeOne.setBounds(0, 25, 100, 25); panel.add(rdBtnFromDbTypeOne)

        rdBtnFromDbTypeTwo.actionCommand = "from SQL Server"
        rdBtnFromDbTypeTwo.addActionListener(UnionRadioButtonActionListener())
        rdBtnFromDbTypeTwo.setBounds(100, 25, 100, 25); panel.add(rdBtnFromDbTypeTwo)

        rdBtnFromDbTypeThree.actionCommand = "from MySQL"
        rdBtnFromDbTypeThree.addActionListener(UnionRadioButtonActionListener())
        rdBtnFromDbTypeThree.isSelected = true
        rdBtnFromDbTypeThree.setBounds(200, 25, 100, 25); panel.add(rdBtnFromDbTypeThree)

        val lblFromDbUrl = JLabel("From DB URL:")
        lblFromDbUrl.setBounds(5, 50, 145, 25); panel.add(lblFromDbUrl)

        val lblFromDbSid = JLabel("From DB SID:")
        lblFromDbSid.setBounds(155, 50, 145, 25); panel.add(lblFromDbSid)

        fromDbUrl.setBounds(0, 75, 150, 25); panel.add(fromDbUrl)

        fromDbSid.setBounds(150, 75, 150, 25); panel.add(fromDbSid)

        val lblFromDbName = JLabel("From DB Name:")
        lblFromDbName.setBounds(5, 100, 295, 25); panel.add(lblFromDbName)

        fromDbName.setBounds(0, 125, 300, 25); panel.add(fromDbName)

        val lblFromDbUser = JLabel("From DB Username:")
        lblFromDbUser.setBounds(5, 150, 295, 25); panel.add(lblFromDbUser)

        fromDbUser.setBounds(0, 175, 300, 25); panel.add(fromDbUser)

        val lblFromDbPass = JLabel("From DB Password:")
        lblFromDbPass.setBounds(5, 200, 295, 25); panel.add(lblFromDbPass)

        fromDbPass.setBounds(0, 225, 300, 25); panel.add(fromDbPass)
        // End

        // Set Block 2 (To DB Setter)
        val lblToDbType = JLabel("To DB Type:")
        lblToDbType.setBounds(5, 250, 295, 25); panel.add(lblToDbType)

        rdBtnToDbTypeOne.actionCommand = "to Oracle DB"
        rdBtnToDbTypeOne.addActionListener(UnionRadioButtonActionListener())
        rdBtnToDbTypeOne.setBounds(0, 275, 100, 25); panel.add(rdBtnToDbTypeOne)

        rdBtnToDbTypeTwo.actionCommand = "to SQL Server"
        rdBtnToDbTypeTwo.addActionListener(UnionRadioButtonActionListener())
        rdBtnToDbTypeTwo.setBounds(100, 275, 100, 25); panel.add(rdBtnToDbTypeTwo)

        rdBtnToDbTypeThree.actionCommand = "to MySQL"
        rdBtnToDbTypeThree.addActionListener(UnionRadioButtonActionListener())
        rdBtnToDbTypeThree.isSelected = true
        rdBtnToDbTypeThree.setBounds(200, 275, 100, 25); panel.add(rdBtnToDbTypeThree)

        val lblToDbUrl = JLabel("To DB URL:")
        lblToDbUrl.setBounds(5, 300, 145, 25); panel.add(lblToDbUrl)

        val lblToDbSid = JLabel("To DB SID:")
        lblToDbSid.setBounds(155, 300, 145, 25); panel.add(lblToDbSid)

        toDbUrl.setBounds(0, 325, 150, 25); panel.add(toDbUrl)

        toDbSid.setBounds(150, 325, 150, 25); panel.add(toDbSid)

        val lblToDbName = JLabel("To DB Name:")
        lblToDbName.setBounds(5, 350, 295, 25); panel.add(lblToDbName)

        toDbName.setBounds(0, 375, 300, 25); panel.add(toDbName)

        val lblToDbUser = JLabel("To DB Username:")
        lblToDbUser.setBounds(5, 400, 295, 25); panel.add(lblToDbUser)

        toDbUser.setBounds(0, 425, 300, 25); panel.add(toDbUser)

        val lblToDbPass = JLabel("To DB Password:")
        lblToDbPass.setBounds(5, 450, 295, 25); panel.add(lblToDbPass)

        toDbPass.setBounds(0, 475, 300, 25); panel.add(toDbPass)
        // End

        // Set Block 3 (Function Setter & Start Button)
        val lblFunc = JLabel("Function Mode:")
        lblFunc.setBounds(305, 0, 295, 25); panel.add(lblFunc)

        rdBtnFuncOne.actionCommand = "Transfer"; rdBtnFuncOne.addActionListener(UnionRadioButtonActionListener())
        rdBtnFuncOne.isSelected = true
        rdBtnFuncOne.setBounds(300, 25, 100, 25); panel.add(rdBtnFuncOne)

        rdBtnFuncTwo.actionCommand = "Compare"; rdBtnFuncTwo.addActionListener(UnionRadioButtonActionListener())
        rdBtnFuncTwo.setBounds(400, 25, 100, 25); panel.add(rdBtnFuncTwo)

        rdBtnFuncThree.actionCommand = "Synchronize"; rdBtnFuncThree.addActionListener(UnionRadioButtonActionListener())
        rdBtnFuncThree.setBounds(500, 25, 100, 25); panel.add(rdBtnFuncThree)

        val lblMode = JLabel("Transfer Mode:")
        lblMode.setBounds(305, 50, 295, 25); panel.add(lblMode)

        rdBtnModeOne.actionCommand = "To File"; rdBtnModeOne.addActionListener(UnionRadioButtonActionListener())
        rdBtnModeOne.isSelected = true
        rdBtnModeOne.setBounds(300, 75, 150, 25); panel.add(rdBtnModeOne)

        rdBtnModeTwo.actionCommand = "To DB"; rdBtnModeTwo.addActionListener(UnionRadioButtonActionListener())
        rdBtnModeTwo.setBounds(450, 75, 150, 25); panel.add(rdBtnModeTwo)

        chBoxSetIdInsert.addActionListener(SetIdInsert())
        chBoxSetIdInsert.isEnabled = false
        chBoxSetIdInsert.setBounds(300, 100, 25, 25); panel.add(chBoxSetIdInsert)

        val lblSetIdInsert = JLabel("SET IDENTITY_INSERT ON")
        lblSetIdInsert.setBounds(325, 100, 270, 25); panel.add(lblSetIdInsert)

        val lblRecord = JLabel("Record:")
        lblRecord.setBounds(305, 125, 295, 25); panel.add(lblRecord)

        tfRecord.setBounds(300, 150, 300, 25); panel.add(tfRecord)

        val lblTabName = JLabel("Table Name:")
        lblTabName.setBounds(305, 175, 295, 25); panel.add(lblTabName)

        tableName.setBounds(300, 200, 300, 25); panel.add(tableName)

        btnAddTabName.actionCommand = "Add"; btnAddTabName.addActionListener(TabNameListControl())
        btnAddTabName.setBounds(300, 225, 100, 25); panel.add(btnAddTabName)

        btnRemoveTabName.actionCommand = "Remove"; btnRemoveTabName.addActionListener(TabNameListControl())
        btnRemoveTabName.setBounds(400, 225, 100, 25); panel.add(btnRemoveTabName)

        btnClearTabName.actionCommand = "Clear"; btnClearTabName.addActionListener(TabNameListControl())
        btnClearTabName.setBounds(500, 225, 100, 25); panel.add(btnClearTabName)

        val lblTotalRow = JLabel("Total Row:")
        lblTotalRow.setBounds(305, 250, 295, 25); panel.add(lblTotalRow)

        total.setBounds(300, 275, 300, 25); panel.add(total)

        val lblQueryConditions = JLabel("Query Conditions:")
        lblQueryConditions.setBounds(305, 300, 295, 25); panel.add(lblQueryConditions)

        where.lineWrap = true; where.wrapStyleWord = true
        val jspWhere = JScrollPane(where)
        jspWhere.setBounds(305, 328, 290, 119); panel.add(jspWhere)

        btnStart.setBounds(300, 475, 300, 25); panel.add(btnStart)
        // End

        // Set Block 4 (StatusBox)
        statusBox.isEditable = false; statusBox.lineWrap = true; statusBox.wrapStyleWord = true
        val dcStatusBox = statusBox.caret as DefaultCaret
        dcStatusBox.updatePolicy = DefaultCaret.ALWAYS_UPDATE
        val jspStatusBox = JScrollPane(statusBox)
        jspStatusBox.setBounds(5, 503, 590, 164); panel.add(jspStatusBox)
        // End

        window.isVisible = true
    }

    fun start() {
        record = try { tfRecord.text.toInt() } catch (_: NumberFormatException) { 5000 }

        rdBtnFromDbTypeOne.isEnabled = false
        rdBtnFromDbTypeTwo.isEnabled = false
        rdBtnFromDbTypeThree.isEnabled = false

        rdBtnToDbTypeOne.isEnabled = false; rdBtnToDbTypeTwo.isEnabled = false; rdBtnToDbTypeThree.isEnabled = false

        rdBtnFuncOne.isEnabled = false; rdBtnFuncTwo.isEnabled = false

        rdBtnModeOne.isEnabled = false; rdBtnModeTwo.isEnabled = false

        chBoxSetIdInsert.isEnabled = false

        btnAddTabName.isEnabled = false; btnRemoveTabName.isEnabled = false; btnClearTabName.isEnabled = false

        btnStart.isEnabled = false
    }

    fun end() {
        rdBtnFromDbTypeOne.isEnabled = true; rdBtnFromDbTypeTwo.isEnabled = true; rdBtnFromDbTypeThree.isEnabled = true

        rdBtnToDbTypeOne.isEnabled = true; rdBtnToDbTypeTwo.isEnabled = true; rdBtnToDbTypeThree.isEnabled = true

        rdBtnFuncOne.isEnabled = true; rdBtnFuncTwo.isEnabled = true

        rdBtnModeOne.isEnabled = true; rdBtnModeTwo.isEnabled = true

        chBoxSetIdInsert.isEnabled = true

        btnAddTabName.isEnabled = true; btnRemoveTabName.isEnabled = true; btnClearTabName.isEnabled = true

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

                    isIdInsert = false; chBoxSetIdInsert.isEnabled = false
                }
                "to SQL Server" -> {
                    toDbType = 2

                    rdBtnToDbTypeOne.isSelected = false
                    rdBtnToDbTypeTwo.isSelected = true
                    rdBtnToDbTypeThree.isSelected = false

                    isIdInsert = chBoxSetIdInsert.isSelected; chBoxSetIdInsert.isEnabled = true
                }
                "to MySQL" -> {
                    toDbType = 3

                    rdBtnToDbTypeOne.isSelected = false
                    rdBtnToDbTypeTwo.isSelected = false
                    rdBtnToDbTypeThree.isSelected = true

                    isIdInsert = false; chBoxSetIdInsert.isEnabled = false
                }
                // End

                // Function Mode Change
                "Transfer" -> {
                    func = 1

                    rdBtnFuncOne.isSelected = true; rdBtnFuncTwo.isSelected = false; rdBtnFuncThree.isSelected = false
                }
                "Compare" -> {
                    func = 2

                    rdBtnFuncOne.isSelected = false; rdBtnFuncTwo.isSelected = true; rdBtnFuncThree.isSelected = false
                }
                "Synchronize" -> {
                    func = 3

                    rdBtnFuncOne.isSelected = false; rdBtnFuncTwo.isSelected = false; rdBtnFuncThree.isSelected = true
                }
                // End

                // Transfer Mode Change
                "To File" -> {
                    mode = 1

                    rdBtnModeOne.isSelected = true; rdBtnModeTwo.isSelected = false
                }
                "To DB" -> {
                    mode = 2

                    rdBtnModeOne.isSelected = false; rdBtnModeTwo.isSelected = true
                }
                // End

            }
        }
    }

    private inner class SetIdInsert: ActionListener {
        override fun actionPerformed(e: ActionEvent?) {
            isIdInsert = chBoxSetIdInsert.isSelected
        }
    }

    private inner class TabNameListControl: ActionListener {
        override fun actionPerformed(e: ActionEvent?) {
            when (e?.actionCommand) {
                "Add" -> tableNameList.add(tableName.text)
                "Remove" -> tableNameList.remove(tableName.text)
                "Clear" -> tableNameList.clear()
            }

            statusBox.append("Table Name List: $tableNameList\n")
        }
    }
}