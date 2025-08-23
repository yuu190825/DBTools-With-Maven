package com.itoria.dbtools

class Compare(
    private val dbAColValueLists: MutableList<MutableList<Any?>>,
    private val dbBColValueLists: MutableList<MutableList<Any?>>
) {
    val notInDbAColValueLists = mutableListOf<MutableList<Any?>>()
    val addInDbAColValueLists = mutableListOf<MutableList<Any?>>()
    val xorInDbAColValueLists = mutableListOf<MutableList<Any?>>()

    fun start() {
        val tempColValueListsA = dbAColValueLists.minus(dbBColValueLists.toSet())
        val tempColValueListsB = dbBColValueLists.minus(dbAColValueLists.toSet())

        val idInTempColValueListsA = mutableListOf<Any?>()
        for (colValueList in tempColValueListsA) idInTempColValueListsA.add(colValueList[0])

        val idInTempColValueListsB = mutableListOf<Any?>()
        for (colValueList in tempColValueListsB) idInTempColValueListsB.add(colValueList[0])

        for (colValueList in tempColValueListsB)
            if (!idInTempColValueListsA.contains(colValueList[0])) notInDbAColValueLists.add(colValueList)

        for (colValueList in tempColValueListsA)
            if (!idInTempColValueListsB.contains(colValueList[0])) addInDbAColValueLists.add(colValueList)
            else xorInDbAColValueLists.add(colValueList)
    }
}