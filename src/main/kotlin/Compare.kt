class Compare(
    private val colValueListsA: MutableList<MutableList<Any?>>,
    private val colValueListsB: MutableList<MutableList<Any?>>
) {
    val notInDBA = mutableListOf<MutableList<Any?>>()
    val addInDBA = mutableListOf<MutableList<Any?>>()
    val xorInDBA = mutableListOf<MutableList<Any?>>()

    fun start() {
        val tempColValueListsA = colValueListsA.minus(colValueListsB.toSet())
        val tempColValueListsB = colValueListsB.minus(colValueListsA.toSet())

        val idInTempColValueListsA = mutableListOf<Any?>()
        for (id in tempColValueListsA) idInTempColValueListsA.add(id[0])

        val idInTempColValueListsB = mutableListOf<Any?>()
        for (id in tempColValueListsB) idInTempColValueListsB.add(id[0])

        for (colValueList in tempColValueListsB)
            if (!idInTempColValueListsA.contains(colValueList[0])) notInDBA.add(colValueList)

        for (colValueList in tempColValueListsA)
            if (!idInTempColValueListsB.contains(colValueList[0])) addInDBA.add(colValueList)
            else xorInDBA.add(colValueList)
    }
}