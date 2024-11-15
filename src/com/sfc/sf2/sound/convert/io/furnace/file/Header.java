/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.sound.convert.io.furnace.file;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author Wiz
 */
public class Header {
    
    private String formatMagic = "-Furnace module-";
    private short formatVersion = 219;
    private short reserved1 = 0;
    private int songPointer = 0;
    private long reserved2 = 0;
    
    public Header(byte[] data){
        ByteBuffer bb = ByteBuffer.allocate(data.length);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.put(data);
        formatMagic = new String(bb.slice(0, 16).array(), StandardCharsets.UTF_8);
        formatVersion = bb.getShort(16);
        reserved1 = bb.getShort(18);
        songPointer = bb.getInt(20);
        reserved2 = bb.getLong(24);
    }

    public String getFormatMagic() {
        return formatMagic;
    }

    public void setFormatMagic(String formatMagic) {
        this.formatMagic = formatMagic;
    }

    public short getFormatVersion() {
        return formatVersion;
    }

    public void setFormatVersion(short formatVersion) {
        this.formatVersion = formatVersion;
    }

    public short getReserved1() {
        return reserved1;
    }

    public void setReserved1(short reserved1) {
        this.reserved1 = reserved1;
    }

    public int getSongPointer() {
        return songPointer;
    }

    public void setSongPointer(int songPointer) {
        this.songPointer = songPointer;
    }

    public long getReserved2() {
        return reserved2;
    }

    public void setReserved2(long reserved2) {
        this.reserved2 = reserved2;
    }
    
}
