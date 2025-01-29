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
public class WavetableBlock {
    
    private String blockId = "WAVE";
    private int blockSize = 0;
    private String name = "";
    private int width = 0;
    private int reserved = 0;
    private int height = 0;
    private int[] data = null;

    public WavetableBlock(byte[] data, int wavetablePointer) {
        ByteBuffer bb = ByteBuffer.wrap(data);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.position(wavetablePointer);  
        String blockId = getString(bb, 4);
        blockSize = bb.getInt();      
        name = getString(bb);
        width = bb.getInt();
        reserved = bb.getInt();
        height = bb.getInt();
        data = getByteArray(bb, blockSize-name.length()-1-4-4-4);
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

    public int getSize() {
        return blockSize;
    }

    public void setSize(int size) {
        this.blockSize = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getReserved() {
        return reserved;
    }

    public void setReserved(int reserved) {
        this.reserved = reserved;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int[] getData() {
        return data;
    }

    public void setData(int[] data) {
        this.data = data;
    }
    
    public byte[] toByteArray(){
        ByteBuffer bb = ByteBuffer.allocate(findLength());
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.position(0);
        bb.put(blockId.getBytes(StandardCharsets.UTF_8));
        bb.putInt(findLength()-4-4);
        bb.put(name.getBytes(StandardCharsets.UTF_8));
        bb.put((byte)0);
        bb.putInt(width);
        bb.putInt(reserved);
        bb.putInt(height);
        bb.put(toByteArray(data));
        return bb.array();
    }
    
    private byte[] toByteArray(int[] intArray){
        return FurnaceFile.toByteArray(intArray);
    }
    
    public int findLength(){
        return 4+4+name.length()+1+4+4+4+data.length*4;
    }
    
    
}
