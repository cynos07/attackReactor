package kr.studygram.attackReactor.core;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by cynos07 on 2017-05-14.
 */

public class AttackReactorMain {
    public static void main(String[] args) {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            System.out.println("===============================");
            System.out.println("호스트명: "+localHost.getHostName());
            System.out.println("ip주소  : "+localHost.getHostAddress());
            System.out.println("MAC주소 : "+getMacAddress(localHost.getLocalHost()));
            System.out.println("===============================");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        Thread attackListener = new Thread(AttackListener.getInstance());
        attackListener.start();
    }

    private static String getMacAddress(InetAddress ip) throws SocketException {
        NetworkInterface network = NetworkInterface.getByInetAddress(ip);
        byte[] mac = network.getHardwareAddress();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mac.length; i++) {
            sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
        }
        return sb.toString();
    }
}