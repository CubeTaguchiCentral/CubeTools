/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.sound.formats.furnace.file.section;

import com.sfc.sf2.sound.formats.furnace.file.FurnaceFile;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Wiz
 */
public class ChipFlagsBlock {
    
    private String blockId = "FLAG";
    private int blockSize = 0;
    private byte[] rawData = null;
    private Map<String, String> flagMap = new TreeMap();
    
    public ChipFlagsBlock(byte[] data, int startPointer){
        ByteBuffer bb = ByteBuffer.wrap(data);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.position(startPointer);    
        String blockId = getString(bb, 4);
        blockSize = bb.getInt();    
        rawData = getByteArray(bb, blockSize);
        String[] flags =  new String(rawData, StandardCharsets.UTF_8).split("\n");
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
        return blockSize;
    }

    public void setSize(int size) {
        this.blockSize = size;
    }

    public Map<String, String> getDataMap() {
        return flagMap;
    }

    public void setDataMap(Map<String, String> dataMap) {
        this.flagMap = dataMap;
    }
    
    public byte[] toByteArray(){
        if(rawData==null){
            rawData = produceRawData();
        }
        ByteBuffer bb = ByteBuffer.allocate(findLength());
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.position(0);
        bb.put(blockId.getBytes(StandardCharsets.UTF_8));
        bb.putInt(findLength()-4-4);  
        bb.put(rawData);
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
    
    public byte[] produceRawData(){
        byte[] bytes = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        boolean firstEntry = true;
        for(Entry e: flagMap.entrySet()){
            try {
                if(!firstEntry){
                    baos.write("\n".getBytes(StandardCharsets.UTF_8));
                }
                baos.write(((String)e.getKey()).getBytes(StandardCharsets.UTF_8));
                baos.write("=".getBytes(StandardCharsets.UTF_8));
                baos.write(((String)e.getValue()).getBytes(StandardCharsets.UTF_8));
            } catch (IOException ex) {
                Logger.getLogger(ChipFlagsBlock.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        baos.write((byte)0);
        bytes = baos.toByteArray();
        return bytes;
    }
    
    public int findLength(){
        return 4+4+rawData.length+1;
    }
    
    
    
}
