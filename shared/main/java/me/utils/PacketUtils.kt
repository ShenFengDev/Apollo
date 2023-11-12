package me.utils;

import net.ccbluex.liquidbounce.api.minecraft.network.IPacket
import net.ccbluex.liquidbounce.utils.MinecraftInstance
import net.minecraft.network.Packet
import net.minecraft.network.play.INetHandlerPlayServer
import net.minecraft.network.play.client.CPacketPlayerTryUseItem
import net.minecraft.util.EnumHand

object PacketUtils : MinecraftInstance() {
    private val packets = ArrayList<Packet<INetHandlerPlayServer>>()

    @JvmStatic
    fun handleSendPacket(packet: Packet<*>): Boolean {
        if (packets.contains(packet)) {
            packets.remove(packet)
            return true
        }
        return false
    }
    @JvmStatic
    fun sendTryUseItem() {
        mc2.connection!!.sendPacket(CPacketPlayerTryUseItem(EnumHand.MAIN_HAND))
        mc2.connection!!.sendPacket(CPacketPlayerTryUseItem(EnumHand.OFF_HAND))
    }
    @JvmStatic
    fun sendPacketNoEvent(packet: Packet<INetHandlerPlayServer>) {
        packets.add(packet)
        mc2.connection!!.sendPacket(packet)
//        mc.netHandler.addToSendQueue(packet as IPacket)
    }

    @JvmStatic
    fun getPacketType(packet: Packet<*>): PacketType {
        val className=packet.javaClass.simpleName
        if(className.startsWith("C",ignoreCase = true)){
                return PacketType.CLIENTSIDE
        }else if(className.startsWith("S",ignoreCase = true)){
                return PacketType.SERVERSIDE
        }
        return PacketType.UNKNOWN
    }

    enum class PacketType {
        SERVERSIDE,
        CLIENTSIDE,
        UNKNOWN
    }
}