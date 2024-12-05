/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.sound.convert.io.furnace.file;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 * @author Wiz
 */
public class Pattern {
    
    private String blockId = "PATN";
    private int blockSize = 0;
    private byte subsong = 0;
    private byte channel = 0;
    private short patternIndex = 0;
    private String name = "";
    private byte[] rawData = null;

    Pattern(byte[] data, int startPointer) {
        ByteBuffer bb = ByteBuffer.wrap(data, startPointer, data.length-startPointer);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.position(startPointer);
        blockId = getString(bb, 4);
        blockSize = bb.getInt();
        subsong = bb.get();
        channel = bb.get();
        patternIndex = bb.getShort();
        name = getString(bb);
        rawData = getByteArray(bb, blockSize-1-1-2-name.length()-1);
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

    public String getBlockId() {
        return blockId;
    }

    public void setBlockId(String blockId) {
        this.blockId = blockId;
    }

    public int getBlockSize() {
        return blockSize;
    }

    public void setBlockSize(int blockSize) {
        this.blockSize = blockSize;
    }

    public byte getSubsong() {
        return subsong;
    }

    public void setSubsong(byte subsong) {
        this.subsong = subsong;
    }

    public byte getChannel() {
        return channel;
    }

    public void setChannel(byte channel) {
        this.channel = channel;
    }

    public short getPatternIndex() {
        return patternIndex;
    }

    public void setPatternIndex(short patternIndex) {
        this.patternIndex = patternIndex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getRawData() {
        return rawData;
    }

    public void setRawData(byte[] rawData) {
        this.rawData = rawData;
    }
    
    
}
