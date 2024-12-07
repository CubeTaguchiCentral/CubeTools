/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.sound.convert.io.furnace.file.section;

import com.sfc.sf2.sound.convert.io.furnace.file.FurnaceFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Wiz
 */
public class ChipFlagsBlock {
    
    private String blockId = "FLAG";
    private int size = 0;
    private Map<String, String> flagMap = new HashMap();
    
    public ChipFlagsBlock(byte[] data, int startPointer){
        ByteBuffer bb = ByteBuffer.wrap(data);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.position(startPointer);    
        String blockId = getString(bb, 4);
        size = bb.getInt();    
        String dataString = getString(bb, size);
        String[] flags = dataString.split("\n");
        for(int i=0;i<flags.length;i++){
            String[] strings = flags[i].split("=");
            String key = strings[0];
            String value = strings[1];
            flagMap.put(key, value);
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

    public Map<String, String> getDataMap() {
        return flagMap;
    }

    public void setDataMap(Map<String, String> dataMap) {
        this.flagMap = dataMap;
    }
    
    
    
}
