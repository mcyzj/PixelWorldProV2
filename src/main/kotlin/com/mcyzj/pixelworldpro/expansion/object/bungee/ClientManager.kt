package com.mcyzj.pixelworldpro.expansion.`object`.bungee

import com.mcyzj.pixelworldpro.api.interfaces.event.bungee.Client

object ClientManager {
    private val listenMap = HashMap<String, ArrayList<Client>>()
    fun register(client: Client, type: List<String>){
        for (listen in type) {
            val listenList = listenMap[listen]
            if (listenList == null) {
                listenMap[listen] = arrayListOf(client)
            } else {
                if (client !in listenList){
                    listenList.add(client)
                    listenMap[listen] = listenList
                }
            }
        }
    }

    fun getListener(type: String): ArrayList<Client>? {
        return listenMap[type]
    }
}