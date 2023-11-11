package com.mcyzj.pixelworldpro.expansion.core.gui

import com.mcyzj.pixelworldpro.PixelWorldPro
import com.mcyzj.pixelworldpro.data.dataclass.BasicCharMap
import com.mcyzj.pixelworldpro.world.Local
import com.xbaimiao.easylib.bridge.replacePlaceholder
import com.xbaimiao.easylib.module.chat.BuiltInConfiguration
import com.xbaimiao.easylib.module.utils.colored
import com.xbaimiao.easylib.xseries.XItemStack
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

class WorldList(val player: Player) {
    //默认配置文件
    private val config = Core.getWorldListConfig()

    //列表格子对应的UUID
    private val listMap = mutableMapOf<Int, UUID>()

    //从数据库获取的世界数据开始位置
    private var start = 0

    //是否为最后一页
    private var isLastPage = false

    //是否为第一次打开
    private var isFirst = true

    //获取list对应的格子
    private var intList = mutableListOf<Int>()
    private fun build(page: Int = 1, isTrust: Boolean = false, gui: String = "WorldList.yml"): BasicCharMap {
        if (PixelWorldPro.instance.config.getBoolean("debug")){
            Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 构建玩家世界列表菜单")
        }
        val basicCharMap = Core.buildBaseGui(gui, player)
        val basic = basicCharMap.basic
        val charMap = basicCharMap.charMap
        val configCustom = BuiltInConfiguration("gui/$gui")
        var listChar: Char? = null
        //获取intList
        intList = mutableListOf()
        for (guiData in charMap) {
            if (guiData.value.type == "List") {
                listChar = guiData.key
                basic.getSlots(guiData.key).forEach {
                    intList.add(it)
                }
                break
            }
        }
        //获取isLastPage,start,isFirst,listMap，填充changelist格子和page格子
        if (isFirst) {
            isFirst = false
            for (guiData in charMap) {
                if (guiData.value.type == "Page") {
                    val item = basic.items[guiData.key]?:continue
                    val meta = item.itemMeta
                    meta.setDisplayName(meta.displayName.replace("{page}", page.toString()))
                    val lore = meta.lore?.map { it.replace("{page}", page.toString()) }?.toMutableList()
                    meta.lore = lore
                    item.itemMeta = meta
                    basic.set(guiData.key, item)
                    continue
                }
                if (guiData.value.type == "ChangeList") {
                    if (guiData.value.value == "trust") {
                        fillListMap(player, true)
                        basic.set(
                            guiData.key,
                            Core.buildItem(
                                config.getConfigurationSection("ChangeList.trust")!!,
                                player
                            ) ?: continue
                        )
                    } else {
                        fillListMap(player, false)
                        basic.set(
                            guiData.key,
                            Core.buildItem(
                                config.getConfigurationSection("ChangeList.public")!!,
                                player
                            ) ?: continue
                        )
                    }
                }
            }
        } else {
            fillListMap(player, isTrust)
            for (guiData in charMap) {
                if (guiData.value.type == "ChangeList") {
                    if (isTrust) {
                        basic.set(
                            guiData.key,
                            Core.buildItem(
                                config.getConfigurationSection("ChangeList.trust")!!,
                                player
                            ) ?: continue
                        )
                    } else {
                        basic.set(
                            guiData.key,
                            Core.buildItem(
                                config.getConfigurationSection("ChangeList.public")!!,
                                player
                            ) ?: continue
                        )
                    }
                    break
                }
            }
        }
        //填充list格子
        for (list in listMap){
            if (listChar == null) {
                break
            }
            val worldData = PixelWorldPro.databaseApi.getWorldData(list.value) ?: continue
            val worldOwner = Bukkit.getOfflinePlayer(list.value)
            val listConfig = configCustom.getConfigurationSection("items.$listChar")!!
            val name = listConfig.getString("name")
            val lore = listConfig.getStringList("lore")
            val skull = listConfig.getString("skull")
            listConfig.set("name", listConfig.getString("name")?.replacePlaceholder(worldOwner).colored())

            if (list.value == player.uniqueId)
                listConfig.set("name",listConfig.getString("name")?.
                replace("{role}",config.getStringColored("List.role.owner")))
            if (worldData.player[player.uniqueId] == "BlackList")
                listConfig.set("name",listConfig.getString("name")?.
                replace("{role}",config.getStringColored("List.role.ban")))
            if (worldData.player[player.uniqueId] == "Member")
                listConfig.set("name",listConfig.getString("name")?.
                replace("{role}",config.getStringColored("List.role.member")))
            listConfig.set("name",listConfig.getString("name")?.
            replace("{role}",config.getStringColored("List.role.visitor")))

            listConfig.set("lore", listConfig.getStringList("lore").replacePlaceholder(worldOwner).colored())

            if (list.value == player.uniqueId)
                listConfig.set("lore",listConfig.getStringList("lore").map {
                    it.replace("{role}",config.getStringColored("List.role.owner"))
                })
            if (worldData.player[player.uniqueId] == "BlackList")
                listConfig.set("lore",listConfig.getStringList("lore").map {
                    it.replace("{role}",config.getStringColored("List.role.ban"))
                })
            if (worldData.player[player.uniqueId] == "Member")
                listConfig.set("lore",listConfig.getStringList("lore").map {
                    it.replace("{role}",config.getStringColored("List.role.member"))
                })
            listConfig.set("lore",listConfig.getStringList("lore").map {
                it.replace("{role}",config.getStringColored("List.role.visitor"))
            })

            listConfig.set("skull", listConfig.getString("skull")?.replacePlaceholder(worldOwner).colored())
            val item = XItemStack.deserialize(listConfig)
            listConfig.set("name", name)
            listConfig.set("lore", lore)
            listConfig.set("skull", skull)
            basic.set(list.key, item)
        }
        return BasicCharMap(basic, charMap)
    }

    fun open(page: Int = 1, isTrust: Boolean = false, gui: String = "WorldList.yml") {
        if (PixelWorldPro.instance.config.getBoolean("debug")){
            Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 打开第 $page 页玩家世界列表菜单")
        }
        val basicCharMap = build(page, isTrust, gui)
        val basic = basicCharMap.basic
        val charMap = basicCharMap.charMap
        basic.openAsync()
        basic.onClick {
            it.isCancelled = true
        }
        if (PixelWorldPro.instance.config.getBoolean("debug")){
            Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 打开玩家世界列表菜单")
        }
        for (guiData in charMap) {
            basic.onClick(guiData.key) {
                //执行命令
                if (guiData.value.commands != null) {
                    Core.runCommand(player, guiData.value.commands!!)
                }
                when (guiData.value.type) {
                    "ChangeList" -> {
                        open(1, !isTrust, gui)
                    }

                    "List" -> {
                        val slot = it.rawSlot
                        val uuid = listMap[slot] ?: return@onClick
                        val data = PixelWorldPro.databaseApi.getWorldData(uuid) ?: return@onClick
                        Local.tpWorldId(player, data.id)
                    }
                    "Page" -> {
                        if (PixelWorldPro.instance.config.getBoolean("debug")){
                            Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 判断翻页类型")
                        }
                        when (guiData.value.value) {
                            "next" -> {
                                if (!isLastPage) {
                                    if (PixelWorldPro.instance.config.getBoolean("debug")){
                                        Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 打开下一页玩家世界列表菜单")
                                    }
                                    start = intList.size * page
                                    open(page + 1, isTrust, gui)
                                }else{
                                    if (PixelWorldPro.instance.config.getBoolean("debug")){
                                        Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 打开下一页玩家世界列表菜单失败：已经是最后一页了")
                                    }
                                }
                            }

                            "back" -> {
                                if (page == 1) {
                                    if (PixelWorldPro.instance.config.getBoolean("debug")){
                                        Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 打开前一页玩家世界列表菜单失败：已经是第一页了")
                                    }
                                    return@onClick
                                }
                                if (PixelWorldPro.instance.config.getBoolean("debug")){
                                    Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 打开前一页玩家世界列表菜单")
                                }
                                start = intList.size * (page - 2)
                                open(page - 1, isTrust, gui)
                            }
                        }
                    }
                    else -> {
                    }
                }

            }
        }

    }

    private fun fillListMap(player: Player, isTrust: Boolean) {
        listMap.clear()
        val uuidList = mutableListOf<UUID>()
        val worldList = PixelWorldPro.databaseApi.getWorldUUIDList(start, intList.size + 1) as MutableList<UUID>
        if (worldList.isEmpty()) {
            if (PixelWorldPro.instance.config.getBoolean("debug")) {
                Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 构建世界列表尾页：worldlist为空")
            }
            isLastPage = true
            return
        }
        if (worldList.size > intList.size) {
            //数据超过这一页能显示的内容，不是最后一页，截取本页显示内容
            for (slot in intList) {
                listMap[slot] = worldList.first()
                worldList.removeAt(0)
            }
            isLastPage = false
        } else {
            //数据没超过这一页能显示的内容
            if (PixelWorldPro.instance.config.getBoolean("debug")) {
                Bukkit.getConsoleSender().sendMessage("§aPixelWorldPro 构建世界列表尾页：数据量无法超过菜单显示数量")
                Bukkit.getConsoleSender().sendMessage("uuidList长度：${worldList.size}  菜单长度：${intList.size}")
            }
            isLastPage = true
            listMap.clear()
            for (slot in intList) {
                listMap[slot] = worldList.first()
                worldList.removeAt(0)
                if (worldList.isEmpty()) {
                    break
                }
            }
        }
    }
}