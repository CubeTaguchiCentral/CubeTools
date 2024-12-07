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
public class AssetDirectoriesBlock {
    
    private String blockId = "ADIR";
    private int size = 0;
    private int numberOfDirs = 0;
    private AssetDirectory[] assetDirectories = null;
    
    public AssetDirectoriesBlock(byte[] data, int startPointer){
        ByteBuffer bb = ByteBuffer.wrap(data);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.position(startPointer);    
        blockId = getString(bb, 4);
        size = bb.getInt();    
        numberOfDirs = bb.getInt();
        assetDirectories = new AssetDirectory[numberOfDirs];
        for(int i=0;i<numberOfDirs;i++){
            assetDirectories[i] = new AssetDirectory(data, bb.position());
            bb.position(bb.position()+assetDirectories[i].getNumberOfAssets()+2+assetDirectories[i].getName().length()+1);
        }
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
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getNumberOfDirs() {
        return numberOfDirs;
    }

    public void setNumberOfDirs(int numberOfDirs) {
        this.numberOfDirs = numberOfDirs;
    }

    public AssetDirectory[] getAssets() {
        return assetDirectories;
    }

    public void setAssets(AssetDirectory[] assets) {
        this.assetDirectories = assets;
    }
    
}
