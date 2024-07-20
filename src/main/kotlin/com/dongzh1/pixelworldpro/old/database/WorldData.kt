package com.dongzh1.pixelworldpro.old.database

import java.util.UUID

data class WorldData (
    val worldName: String,
    var worldLevel: String,
    val members: List<UUID>,
    val memberName: List<String>,
    val banPlayers: List<UUID>,
    val banName: List<String>,
    //anyone, member, owner, inviter
    val state: String,
    val createTime: String,
    val lastTime: Long,
    val onlinePlayerNumber: Int,
    val isCreateNether: Boolean,
    val isCreateEnd: Boolean,
    var inviter: List<UUID>,
    var gameRule: HashMap<String, String>,
    var location: HashMap<String, Double>
)
data class PlayerData (
    val joinedWorld: List<UUID>,
    val memberNumber: Int,
    var inviterMsg: List<UUID>
)
data class DimensionData (
    val name: String,
    val creator: String,
    val createUse: String,
    val createMoney: Double,
    val createPoints: Double,
    val barrier: Boolean
)
data class WorldDimensionData (
    val createlist: List<String>,
    val discreatelist: List<String>,
    val seed: String
)
data class RedStone (
    var frequency: Int,
    val time: Int
)