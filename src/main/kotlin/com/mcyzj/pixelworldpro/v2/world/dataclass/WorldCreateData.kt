package com.mcyzj.pixelworldpro.v2.world.dataclass

import java.util.*
import kotlin.collections.HashMap

data class WorldCreateData(
    //世界主人
    var owner: UUID,
    //世界名称
    var name: String,
    //世界权限表
    var permission: HashMap<String, HashMap<String, String>>,
    //玩家权限表
    var player: HashMap<UUID, String>,
    //世界维度表
    //var dimension: HashMap<String, WorldDimensionData>
)
