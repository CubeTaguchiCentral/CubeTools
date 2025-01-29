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
public class AssetDirectory {
    
    private String name = "";
    private short numberOfAssets = 0;
    private byte[] assets = null;
    
    public AssetDirectory(byte[] data, int startPointer){
        ByteBuffer bb = ByteBuffer.wrap(data);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.position(startPointer);    
        name = getString(bb);
        numberOfAssets = bb.getShort();    
        assets = getByteArray(bb, numberOfAssets);
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public short getNumberOfAssets() {
        return numberOfAssets;
    }

    public void setNumberOfAssets(short numberOfAssets) {
        this.numberOfAssets = numberOfAssets;
    }

    public byte[] getAssets() {
        return assets;
    }

    public void setAssets(byte[] assets) {
        this.assets = assets;
    }

    public byte[] toByteArray() {
        ByteBuffer bb = ByteBuffer.allocate(findLength());
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.position(0);
        bb.put(name.getBytes(StandardCharsets.UTF_8));
        bb.put((byte)0);
        bb.putShort(numberOfAssets);
        bb.put(assets);
        return bb.array();
    }
    
    public int findLength(){
        return name.length()+1+2+numberOfAssets;
    }
    
}
