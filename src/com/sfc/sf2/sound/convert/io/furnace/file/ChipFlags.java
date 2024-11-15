/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.sound.convert.io.furnace.file;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Wiz
 */
public class ChipFlags {
    
    private String blockId = "FLAG";
    private int size = 0;
    private Map<String, String> dataMap = new HashMap();
    
    public ChipFlags(byte[] data, int startPointer){
        ByteBuffer bb = ByteBuffer.allocate(data.length-startPointer);
        bb.put(data, startPointer, data.length-startPointer);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        int cursor = 0;
        
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
        return dataMap;
    }

    public void setDataMap(Map<String, String> dataMap) {
        this.dataMap = dataMap;
    }
    
    
    
}
