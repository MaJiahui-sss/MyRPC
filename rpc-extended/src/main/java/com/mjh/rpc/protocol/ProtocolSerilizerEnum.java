package com.mjh.rpc.protocol;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum ProtocolSerilizerEnum {
    JDK((byte) 0,"jdk"),
    JSON((byte) 1,"json"),
    ;
    byte serilizerKey;
    String serilizerName;
    ProtocolSerilizerEnum(byte serilizerKey, String serilizerName) {
        this.serilizerKey = serilizerKey;
        this.serilizerName = serilizerName;
    }

    public byte getSerilizerKey() {
        return serilizerKey;
    }

    public String getSerilizerName() {
        return serilizerName;
    }

    /**
     * 根据key值返回枚举
     * @param serilizerKey
     * @return
     */
    public static ProtocolSerilizerEnum getSerilizerEnum(byte serilizerKey) {
        ProtocolSerilizerEnum[] values = ProtocolSerilizerEnum.values();
        for (ProtocolSerilizerEnum value : values) {
            if (value.serilizerKey == serilizerKey) {
                return value;
            }
        }
        return null;
    }
    public static ProtocolSerilizerEnum getSerilizerEnum(String serilizerName) {
        ProtocolSerilizerEnum[] values = ProtocolSerilizerEnum.values();
        for (ProtocolSerilizerEnum value : values) {
            if (value.serilizerName.equals(serilizerName)) {
                return value;
            }
        }
        return null;
    }


}
