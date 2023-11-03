package com.mcyzj.pixelworldpro.dataclass

import com.xbaimiao.easylib.module.chat.BuiltInConfiguration
import com.xbaimiao.easylib.module.ui.Basic

data class GuiData(
    var type:String?,
    var value:String?,
    var commands:List<String>?
)
data class BasicCharMap(
    var basic: Basic,
    var charMap:HashMap<Char, GuiData>,
)
data class TypeValue(
    var config: BuiltInConfiguration,
    var charMap:HashMap<Char,GuiData>
)