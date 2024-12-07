/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.sound.convert.io.furnace.file.section;

import com.sfc.sf2.sound.convert.io.furnace.file.FurnaceFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

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
        ByteBuffer bb = ByteBuffer.wrap(data);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        formatMagic = getString(bb, 16);
        formatVersion = bb.getShort();
        reserved1 = bb.getShort();
        songPointer = bb.getInt();
        reserved2 = bb.getLong();
    }

    private byte[] getByteArray(ByteBuffer bb, int length){
        return FurnaceFile.getByteArray(bb, length);
    }

    private int[] getIntArray(ByteBuffer bb, int length){
        return FurnaceFile.getIntArray(bb, length);
    }

    private float[] getFloatArray(ByteBuffer bb, int length){
        return FurnaceFile.getFloatArray(bb, length);
    }

    private String getString(ByteBuffer bb){
        return FurnaceFile.getString(bb);
    }

    private String getString(ByteBuffer bb, int length){
        return FurnaceFile.getString(bb, length);
    }
    
    private int findStringLength(ByteBuffer bb, int cursor){
        return FurnaceFile.findStringLength(bb, cursor);
    }

    private String[] getStringArray(ByteBuffer bb, int length){
        return FurnaceFile.getStringArray(bb, length);
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
