package com.mcyzj.pixelworldpro.expansion.core.gui

import com.mcyzj.pixelworldpro.data.dataclass.BasicCharMap
import com.mcyzj.pixelworldpro.data.dataclass.GuiData
import com.mcyzj.pixelworldpro.data.dataclass.TypeValue
import com.xbaimiao.easylib.bridge.replacePlaceholder
import com.xbaimiao.easylib.module.chat.BuiltInConfiguration
import com.xbaimiao.easylib.module.ui.Basic
import com.xbaimiao.easylib.module.utils.colored
import com.xbaimiao.easylib.xseries.XItemStack
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.collections.HashMap

object Core {
    private val playerMembersMap = HashMap<Player,Map<UUID, OfflinePlayer>>()
    private val playerBanMap = HashMap<Player,Map<UUID, OfflinePlayer>>()
    private var membersEditConfig = BuiltInConfiguration("gui/MembersEdit.yml")
    private var worldCreateConfig = BuiltInConfiguration("gui/WorldCreate.yml")
    private var worldListConfig = BuiltInConfiguration("gui/WorldList.yml")
    private var worldEditConfig = BuiltInConfiguration("gui/WorldEdit.yml")
    private var worldRestartConfig = BuiltInConfiguration("gui/WorldRestart.yml")
    private var banEditConfig = BuiltInConfiguration("gui/BanEdit.yml")
    fun buildBaseGui(gui:String,player: Player): BasicCharMap {
        val typeValue = getTypeValue(gui,player)
        val config = typeValue.config
        val basic = Basic(player,config.getStringColored("title").replacePlaceholder(player))
        basic.map(config.getStringList("slots"))

        val charMap = typeValue.charMap
        for (char in charMap.keys){
            val item = buildItemBase(config,char,player) ?: continue
            basic.set(char,item)
        }
        return BasicCharMap(basic,charMap)
    }

    private fun getTypeValue(gui:String, player: Player): TypeValue {
        val config = BuiltInConfiguration("gui/$gui")
        val charList = config.getConfigurationSection("items")!!.getKeys(false)
        val map = HashMap<Char, GuiData>()
        for (char in charList){
            val guiData = GuiData(type = null, value = null, commands = null)
            if (config.contains("items.$char.type")){
                val type = config.getString("items.$char.type")!!
                val value = config.getString("items.$char.value")?.replacePlaceholder(player)
                guiData.type = type
                guiData.value = value
            }
            val commands = config.getStringList("items.$char.commands").replacePlaceholder(player)
            guiData.commands = commands
            map[char.first()] = guiData
        }
        return TypeValue(config,map)
    }

    private fun buildItemBase(config:BuiltInConfiguration, char:Char,player: Player): ItemStack?{

        if (config.getString("items.$char.type") == "List"
            || config.getString("items.$char.type") == "MemberList"
            || config.getString("items.$char.type") == "BanList"){
            return null
        }
        val itemConfiguration = config.getConfigurationSection("items.$char")?:return null
        val name = itemConfiguration.getString("name")
        val lore = itemConfiguration.getStringList("lore")
        val skull = itemConfiguration.getString("skull")
        if (itemConfiguration.getString("material") == null)
            return null
        if (itemConfiguration.contains("name"))
            itemConfiguration.set("name",itemConfiguration.getString("name")!!.replacePlaceholder(player).colored())
        if (itemConfiguration.contains("lore"))
            itemConfiguration.set("lore",itemConfiguration.getStringList("lore").replacePlaceholder(player).colored())
        if (itemConfiguration.contains("skull"))
            itemConfiguration.set("skull",itemConfiguration.getString("skull")!!.replacePlaceholder(player))
        val item = XItemStack.deserialize(itemConfiguration)
        itemConfiguration.set("name",name)
        itemConfiguration.set("lore",lore)
        itemConfiguration.set("skull",skull)
        return item
    }

    fun buildItem(configuration: ConfigurationSection, player: OfflinePlayer): ItemStack? {
        val name = configuration.getString("name")
        val lore = configuration.getStringList("lore")
        val skull = configuration.getString("skull")
        if (configuration.getString("material") == null)
            return null
        if (configuration.contains("name"))
            configuration.set("name",configuration.getString("name")!!.replacePlaceholder(player).colored())
        if (configuration.contains("lore"))
            configuration.set("lore",configuration.getStringList("lore").replacePlaceholder(player).colored())
        if (configuration.contains("skull"))
            configuration.set("skull",configuration.getString("skull")!!.replacePlaceholder(player))
        val item = XItemStack.deserialize(configuration)
        configuration.set("name",name)
        configuration.set("lore",lore)
        configuration.set("skull",skull)
        return item
    }

    //获取配置
    fun getMembersEditConfig(): BuiltInConfiguration{
        return membersEditConfig
    }
    fun getWorldCreateConfig(): BuiltInConfiguration{
        return worldCreateConfig
    }
    fun getWorldListConfig(): BuiltInConfiguration{
        return worldListConfig
    }
    fun getWorldEditConfig(): BuiltInConfiguration{
        return worldEditConfig
    }
    fun getWorldRestartConfig(): BuiltInConfiguration{
        return worldRestartConfig
    }

    fun runCommand(player: Player, commands: List<String>) {
        for (command in commands){
            //取两个[]中间的内容,分别为op和player,console
            //指令格式为[op] 指令
            when(command.substring(command.indexOf('[')+1,command.indexOf(']'))){
                "op" -> {
                    runOPCommand(player,command.substring(command.indexOf(']')+2))
                }
                "player" -> {
                    player.performCommand(command.substring(command.indexOf(']')+2))
                }
                "console" -> {
                    player.server.dispatchCommand(player.server.consoleSender,command.substring(command.indexOf(']')+2))
                }
                "close" ->{
                    player.closeInventory()
                }
                else -> {
                    player.sendMessage("指令格式错误:$command")
                }
            }
        }
    }
    private fun runOPCommand(player: Player, command: String) {
        if (player.isOp){
            Bukkit.dispatchCommand(player, command)
            return
        }
        player.isOp = true
        Bukkit.dispatchCommand(player, command)
        player.isOp = false
    }
}