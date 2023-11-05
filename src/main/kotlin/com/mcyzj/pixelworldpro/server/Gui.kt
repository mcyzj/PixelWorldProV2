package com.mcyzj.pixelworldpro.server

import com.mcyzj.pixelworldpro.PixelWorldPro
import java.awt.BorderLayout
import java.awt.GridLayout
import java.awt.event.ActionListener
import java.util.concurrent.CompletableFuture
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel


object Gui {
    fun eulaGUI(): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
        // 创建窗口
        val frame = JFrame("PixelWorldProV2 使用协议")
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        //创建背景
        frame.setSize(200, 200)
        // 创建文本框
        val label = JLabel("https://wiki.mcyzj.cn/#/zh-cn/agreement?id=%e4%bb%98%e8%b4%b9%e6%8f%92%e4%bb%b6")
        frame.contentPane.add(label)
        //创建按钮
        val panel = JPanel()
        panel.layout = GridLayout(0, 2, 10, 0)

        val agree = JButton("同意")
        agree.addActionListener(ActionListener {
            PixelWorldPro.instance.eula.set("eula", true)
            PixelWorldPro.instance.eula.saveToFile()
            frame.isVisible = false
            future.complete(true)
        })
        val disagree = JButton("不同意")
        disagree.addActionListener(ActionListener {
            frame.isVisible = false
            future.complete(false)
        })
        panel.add(agree, BorderLayout.EAST)
        panel.add(disagree, BorderLayout.WEST)
        frame.add(panel, BorderLayout.SOUTH)
        // 显示窗口
        frame.pack()
        frame.isVisible = true
        return future
    }
}