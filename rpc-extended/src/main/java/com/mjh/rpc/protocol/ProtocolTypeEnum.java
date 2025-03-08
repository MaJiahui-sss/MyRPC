package com.mjh.rpc.protocol;

public enum ProtocolTypeEnum {
    REQUEST((byte) 0),
    RESPONSE((byte) 1),
    HEART_BEAT((byte)2),
    OTHER((byte)3);
    ;
    byte key ;

    ProtocolTypeEnum(byte key) {
        this.key = key;
    }
    public static ProtocolTypeEnum getProtocolType(int key) {
        for (ProtocolTypeEnum protocolTypeEnum : ProtocolTypeEnum.values()) {
            if (protocolTypeEnum.key == key) {
                return protocolTypeEnum;
            }
        }
        return null;
    }

    public byte getKey() {
        return key;
    }
}
