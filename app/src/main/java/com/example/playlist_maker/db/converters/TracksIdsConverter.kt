package com.example.playlist_maker.db.converters

import androidx.room.TypeConverter

class TracksIdsConverter {
    @TypeConverter
    fun fromList(list: ArrayList<Long>): String {
        return list.joinToString(",")
    }

    @TypeConverter
    fun toList(data: String): ArrayList<Long> {
        val list = ArrayList<Long>()
        val items = data.split(",").toTypedArray()
        for (item in items) {
            if (item.isNotEmpty()) {
                list.add(item.toLong())
            }
        }
        return list
    }
}