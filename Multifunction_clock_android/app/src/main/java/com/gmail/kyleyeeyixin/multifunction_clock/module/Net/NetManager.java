package com.gmail.kyleyeeyixin.multifunction_clock.module.Net;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Created by yunnnn on 2016/3/14.
 */
public class NetManager {


    public static void sendUDPdata(byte data[]) {
        try {
            DatagramSocket datagramSocket = new DatagramSocket();
            InetAddress serverAddress = InetAddress.getByName("255.255.255.255");
            DatagramPacket datagramPacket = new DatagramPacket(data, data.length, serverAddress, 8080);
            datagramSocket.send(datagramPacket);
            datagramSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] receiveUDPdata() {
        try {
            DatagramSocket socket = new DatagramSocket(8080);
            //2、创建数据包，用于接收内容。
            byte[] buf = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            //3、接收数据
            socket.receive(packet);
            System.out.println(packet.getAddress().getHostAddress() + ":" + packet.getPort());
            //System.out.println(packet.getData().toString());
            //以上语句打印信息错误，因为getData()返回byte[]类型数据，直接toString会将之序列化，而不是提取字符。应该使用以下方法：
            System.out.println(new String(packet.getData(), 0, packet.getLength()));
            //4、关闭连接。
            socket.close();
            return buf;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }
}
