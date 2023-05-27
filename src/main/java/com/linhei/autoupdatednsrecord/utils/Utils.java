package com.linhei.autoupdatednsrecord.utils;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 工具类
 *
 * @author linhei
 */
public class Utils {

    /**
     * 是否为合法的IPV4地址
     *
     * @param ipAddress ipAddr
     * @return 是否合法
     */
    public boolean isValidIpv4Address(String ipAddress) {
        if (ipAddress == null || ipAddress.isEmpty()) return false;
        String[] parts = ipAddress.split("\\.");
        if (parts.length != 4) return false;
        for (String part : parts)
            try {
                int value = Integer.parseInt(part);
                if (value < 0 || value > 255) return false;
            } catch (NumberFormatException e) {
                return false;
            }

        return true;
    }

    /**
     * 是否为合法的IPv6地址
     *
     * @param ipAddress ipAddr
     * @return 是否合法
     */
    public boolean isValidIpv6Address(String ipAddress) {
        if (ipAddress == null || ipAddress.isEmpty()) return false;
        try {
            InetAddress inetAddress = InetAddress.getByName(ipAddress);
            return inetAddress instanceof Inet6Address;
        } catch (UnknownHostException e) {
            return false;
        }
    }
}
