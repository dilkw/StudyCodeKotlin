package com.dilkw.studycodekotlin.mainRecyclerView

import com.dilkw.studycodekotlin.ItemEnum
import java.util.ArrayList
import java.util.HashMap

object PlaceholderContent {

    val ITEMS: MutableList<ItemEnum> = ArrayList()

    val ITEM_MAP: MutableMap<Int, ItemEnum> = HashMap()

    private val COUNT = 25

    init {
        val items = ItemEnum.values()
        for (element in items) {
            addItem(element)
        }
    }

    private fun addItem(item: ItemEnum) {
        ITEMS.add(item)
        ITEM_MAP.put(item.code, item)
    }

    private fun createPlaceholderItem(position: Int): PlaceholderItem {
        return PlaceholderItem(position.toString(), "Item " + position, makeDetails(position))
    }

    private fun makeDetails(position: Int): String {
        val builder = StringBuilder()
        builder.append("Details about Item: ").append(position)
        for (i in 0..position - 1) {
            builder.append("\nMore details information here.")
        }
        return builder.toString()
    }

    data class PlaceholderItem(val id: String, val content: String, val details: String) {
        override fun toString(): String = content
    }
}