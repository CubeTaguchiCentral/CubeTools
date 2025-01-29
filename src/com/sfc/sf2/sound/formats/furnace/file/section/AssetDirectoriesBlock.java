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
public class AssetDirectoriesBlock {
    
    private String blockId = "ADIR";
    private int blockSize = 0;
    private int numberOfDirs = 0;
    private AssetDirectory[] assetDirectories = null;
    
    public AssetDirectoriesBlock(byte[] data, int startPointer){
        ByteBuffer bb = ByteBuffer.wrap(data);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.position(startPointer);    
        blockId = getString(bb, 4);
        blockSize = bb.getInt();    
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
        return blockSize;
    }

    public void setSize(int size) {
        this.blockSize = size;
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
    
    public byte[] toByteArray(){
        ByteBuffer bb = ByteBuffer.allocate(findLength());
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.position(0);
        bb.put(blockId.getBytes(StandardCharsets.UTF_8));
        bb.putInt(findLength()-4-4);
        bb.putInt(numberOfDirs);
        for(int i=0;i<assetDirectories.length;i++){
            bb.put(assetDirectories[i].toByteArray());
        }
        return bb.array();
    }
    
    private int getStringArrayLength(String[] stringArray){
        return FurnaceFile.getStringArrayLength(stringArray);
    }
    
    private byte[] toByteArray(int[] intArray){
        return FurnaceFile.toByteArray(intArray);
    }
    
    private byte[] toByteArray(String[] stringArray){
        return FurnaceFile.toByteArray(stringArray);
    }
    
    public int findLength(){
        return 4+4+4+getAssetDirectoriesLength();
    }
    
    private int getAssetDirectoriesLength(){
        int totalLength = 0;
        for(int i=0;i<assetDirectories.length;i++){
            totalLength += assetDirectories[i].getName().length()+1;
            totalLength += 2;
            totalLength += assetDirectories[i].getNumberOfAssets();
        }
        return totalLength;
    }
    
}
