package com.linhei.ddnsdnspod.utils;

import org.apache.commons.io.FileUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

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

    /**
     * 更新yml配置文件
     *
     * @param key   key
     * @param value value
     */
    public void updateConfigProperty(String key, String value, File file) throws IOException {
        String yamlContent = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        // 解析 YAML 内容为 Map 对象
        Yaml yaml = new Yaml();
        Map<String, Object> yamlMap = yaml.load(yamlContent);

        // 递归修改指定键的值
        updatePropertyValue(yamlMap, key, value);

        // 转换为更新后的 YAML 字符串
        String updatedYamlContent = yaml.dump(yamlMap);

        FileUtils.writeStringToFile(file, updatedYamlContent, StandardCharsets.UTF_8);
    }

    @SuppressWarnings("unchecked")
    private void updatePropertyValue(Map<String, Object> yamlMap, String key, String value) {
        String[] split = key.split("\\.");
        String mapKey = split[0];
        if (split.length == 1)
            // 最后一层键，直接修改值或新建键值对
            yamlMap.put(mapKey, value);
        else
            // 非最后一层键，递归调用更新子层或新建子层
            if (yamlMap.containsKey(mapKey)) {
                Object childObject = yamlMap.get(mapKey);
                if (childObject instanceof Map) {
                    Map<String, Object> childMap = (Map<String, Object>) childObject;
                    updatePropertyValue(childMap, key.substring(key.indexOf('.') + 1), value);
                }
            } else {
                Map<String, Object> childMap = new LinkedHashMap<>();
                yamlMap.put(mapKey, childMap);
                updatePropertyValue(childMap, key.substring(key.indexOf('.') + 1), value);
            }

    }
}
