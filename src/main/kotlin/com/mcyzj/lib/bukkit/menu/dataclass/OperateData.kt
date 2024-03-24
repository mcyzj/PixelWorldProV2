package com.mcyzj.lib.bukkit.menu.dataclass

data class OperateData (
    val leftClick: Boolean,
    val rightClick: Boolean,
    val type: String,
    val flag: ArrayList<String>,
    val value: String
)