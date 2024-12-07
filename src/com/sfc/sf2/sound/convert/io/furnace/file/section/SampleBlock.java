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
public class SampleBlock {
    
    private String blockId = "SMPL";
    private int blockSize = 0;
    private String name = "";
    private int length = 0;
    private int compatibilityRate = 0;
    private short reserved1 = 0;
    private short reserved2 = 0;
    private byte depth = 8;
    private byte reserved3;
    private short c4Rate = 0;
    private int loopPoint = -1;
    private byte[] rawData = null;

    public SampleBlock(byte[] data, int samplePointer) {
        ByteBuffer bb = ByteBuffer.wrap(data);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.position(samplePointer);  
        String blockId = getString(bb, 4);
        blockSize = bb.getInt();      
        name = getString(bb);
        length = bb.getInt();
        compatibilityRate = bb.getInt();
        reserved1 = bb.getShort();
        reserved2 = bb.getShort();
        depth = bb.get();
        reserved3 = bb.get();
        c4Rate = bb.getShort();
        loopPoint = bb.getInt();
        rawData = getByteArray(bb, length);
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getCompatibilityRate() {
        return compatibilityRate;
    }

    public void setCompatibilityRate(int compatibilityRate) {
        this.compatibilityRate = compatibilityRate;
    }

    public short getReserved1() {
        return reserved1;
    }

    public void setReserved1(short reserved1) {
        this.reserved1 = reserved1;
    }

    public short getReserved2() {
        return reserved2;
    }

    public void setReserved2(short reserved2) {
        this.reserved2 = reserved2;
    }

    public byte getDepth() {
        return depth;
    }

    public void setDepth(byte depth) {
        this.depth = depth;
    }

    public byte getReserved3() {
        return reserved3;
    }

    public void setReserved3(byte reserved3) {
        this.reserved3 = reserved3;
    }

    public short getC4Rate() {
        return c4Rate;
    }

    public void setC4Rate(short c4Rate) {
        this.c4Rate = c4Rate;
    }

    public int getLoopPoint() {
        return loopPoint;
    }

    public void setLoopPoint(int loopPoint) {
        this.loopPoint = loopPoint;
    }

    public byte[] getData() {
        return rawData;
    }

    public void setData(byte[] data) {
        this.rawData = data;
    }
    
}
