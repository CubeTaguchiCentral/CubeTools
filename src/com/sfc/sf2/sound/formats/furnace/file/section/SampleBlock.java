/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.sound.formats.furnace.file.section;

import com.sfc.sf2.sound.formats.furnace.file.FurnaceFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author Wiz
 */
public class SampleBlock {
    
    private String blockId = "SMP2";
    private int blockSize = 0;
    private String name = "";
    private int length = 0;
    private int compatibilityRate = 0;
    private int c4Rate = 0;
    private byte depth = 8;
    private byte reserved1 = 0;
    private byte reserved2 = 0;
    private byte reserved3 = 0;
    private int loopStart = -1;
    private int loopEnd = -1;
    private byte[] samplePresenceBitfield = null;
    private byte[] rawData = null;

    public SampleBlock(byte[] data, int samplePointer) {
        ByteBuffer bb = ByteBuffer.wrap(data);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.position(samplePointer);  
        blockId = getString(bb, 4);
        blockSize = bb.getInt();      
        name = getString(bb);
        length = bb.getInt();
        compatibilityRate = bb.getInt();
        c4Rate = bb.getInt();
        depth = bb.get();
        reserved1 = bb.get();
        reserved2 = bb.get();
        reserved3 = bb.get();
        loopStart = bb.getInt();
        loopEnd = bb.getInt();
        samplePresenceBitfield = getByteArray(bb, 16);
        rawData = getByteArray(bb, length*2);
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

    public byte getReserved1() {
        return reserved1;
    }

    public void setReserved1(byte reserved1) {
        this.reserved1 = reserved1;
    }

    public byte getReserved2() {
        return reserved2;
    }

    public void setReserved2(byte reserved2) {
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

    public int getC4Rate() {
        return c4Rate;
    }

    public void setC4Rate(int c4Rate) {
        this.c4Rate = c4Rate;
    }

    public int getLoopStart() {
        return loopStart;
    }

    public void setLoopStart(int loopStart) {
        this.loopStart = loopStart;
    }

    public int getLoopEnd() {
        return loopEnd;
    }

    public void setLoopEnd(int loopEnd) {
        this.loopEnd = loopEnd;
    }

    public byte[] getRawData() {
        return rawData;
    }

    public void setRawData(byte[] rawData) {
        this.rawData = rawData;
    }

    public byte[] getSamplePresenceBitfield() {
        return samplePresenceBitfield;
    }

    public void setSamplePresenceBitfield(byte[] samplePresenceBitfield) {
        this.samplePresenceBitfield = samplePresenceBitfield;
    }

    public byte[] getData() {
        return rawData;
    }

    public void setData(byte[] data) {
        this.rawData = data;
    }
    
    public byte[] toByteArray(){
        ByteBuffer bb = ByteBuffer.allocate(findLength());
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.position(0);
        bb.put(blockId.getBytes(StandardCharsets.UTF_8));
        bb.putInt(findLength()-4-4);
        bb.put(name.getBytes(StandardCharsets.UTF_8));
        bb.put((byte)0);
        bb.putInt(length);
        bb.putInt(compatibilityRate);
        bb.putInt(c4Rate);
        bb.put(depth);
        bb.put(reserved1);
        bb.put(reserved2);
        bb.put(reserved3);
        bb.putInt(loopStart);
        bb.putInt(loopEnd);
        bb.put(samplePresenceBitfield);
        bb.put(rawData);
        return bb.array();
    }
    
    public int findLength(){
        return 4+4+name.length()+1+4+4+4+1+1+1+1+4+4+16+rawData.length;
    }
    
}
