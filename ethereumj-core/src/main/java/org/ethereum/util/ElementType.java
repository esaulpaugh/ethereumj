package org.ethereum.util;

import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;

/**
 * Created by esaulpaugh on 1/19/17.
 */
public enum ElementType {

    SINGLE_BYTE(0, false),
    ITEM_SHORT(0x80, true), // 0x80 + 55 = 0xb7
    ITEM_LONG(0xb7, false), // 0xb8 + 8 = 0xc0
    LIST_SHORT(0xc0, true), // 0xc0 + 55 = 0xf7
    LIST_LONG(0xf7, false);

    private final int offset;
    private final boolean isShort;

    ElementType(int offset, boolean isShort) {
        this.offset = offset;
        this.isShort = isShort;
    }

    public int getOffset() {
        return offset;
    }

    public boolean isShort() {
        return isShort;
    }

    public static ElementType type(byte byteZero) {
        int intVal = byteZero & 0xFF;
        if(intVal < 0x80)
            return SINGLE_BYTE;
        if(intVal <= 0xb7)
            return ITEM_SHORT;
        if(intVal < 0xc0)
            return ITEM_LONG;
        if(intVal <= 0xf7)
            return LIST_SHORT;
        return LIST_LONG;
    }

    public static void testPrint() {
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < 256; i++) {
            byte value = (byte) i;
            sb.append(HexBin.encode(new byte[]{value})).append(" ");
            sb.append(ElementType.type(value)).append("\n");
        }

        System.out.println(sb.toString());
    }

}
