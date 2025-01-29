/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.sound.formats.furnace.file;

import com.sfc.sf2.sound.formats.furnace.file.section.SongInfoBlock;
import com.sfc.sf2.sound.formats.furnace.file.section.ChipFlagsBlock;
import com.sfc.sf2.sound.formats.furnace.file.section.SampleBlock;
import com.sfc.sf2.sound.formats.furnace.file.section.WavetableBlock;
import com.sfc.sf2.sound.formats.furnace.file.section.InstrumentBlock;
import com.sfc.sf2.sound.formats.furnace.file.section.PatternBlock;
import com.sfc.sf2.sound.formats.furnace.file.section.AssetDirectoriesBlock;
import com.sfc.sf2.sound.formats.furnace.file.section.Header;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author Wiz
 */
public class FurnaceFile {
    
    private Header header;
    private SongInfoBlock songInfo;
    private ChipFlagsBlock[] chipFlagsBlocks;
    private AssetDirectoriesBlock[] assetDirectoriesBlocks;
    private InstrumentBlock[] instrumentBlocks;
    private WavetableBlock[] wavetableBlocks;
    private SampleBlock[] sampleBlocks;
    private PatternBlock[] patternBlocks;
    
    public FurnaceFile(byte[] data){
        header = new Header(data);
        songInfo = new SongInfoBlock(data, header.getSongInfoPointer());
        int numberOfChips = songInfo.findNumberOfChips();
        chipFlagsBlocks = new ChipFlagsBlock[32];
        int[] chipFlagPointers = songInfo.getSoundChipFlagPointers();
        for(int i=0;i<numberOfChips;i++){
            if(chipFlagPointers[i]>0){
                chipFlagsBlocks[i] = new ChipFlagsBlock(data,chipFlagPointers[i]);
            }
        }  
        assetDirectoriesBlocks = new AssetDirectoriesBlock[3];
        int instrumentDirectoriesPointer = songInfo.getInstrumentDirectoriesPointer();
        assetDirectoriesBlocks[0] = new AssetDirectoriesBlock(data, instrumentDirectoriesPointer);
        int wavetableDirectoriesPointer = songInfo.getWavetableDirectoriesPointer();
        assetDirectoriesBlocks[1] = new AssetDirectoriesBlock(data, wavetableDirectoriesPointer);
        int sampleDirectoriesPointer = songInfo.getSampleDirectoriesPointer();
        assetDirectoriesBlocks[2] = new AssetDirectoriesBlock(data, sampleDirectoriesPointer);
        int instrumentCount = songInfo.getInstrumentCount();
        instrumentBlocks = new InstrumentBlock[instrumentCount];
        for(int i=0;i<instrumentCount;i++){
            int instrumentPointer = songInfo.getInstrumentPointers()[i];
            if(instrumentPointer>0){
                instrumentBlocks[i] = new InstrumentBlock(data, instrumentPointer);
            }
        }
        int wavetableCount = songInfo.getWavetableCount();
        wavetableBlocks = new WavetableBlock[wavetableCount];
        for(int i=0;i<wavetableCount;i++){
            int wavetablePointer = songInfo.getWavetablePointers()[i];
            if(wavetablePointer>0){
                wavetableBlocks[i] = new WavetableBlock(data, wavetablePointer);
            }
        }
        int sampleCount = songInfo.getSampleCount();
        sampleBlocks = new SampleBlock[sampleCount];
        for(int i=0;i<sampleCount;i++){
            int samplePointer = songInfo.getSamplePointers()[i];
            if(samplePointer>0){
                sampleBlocks[i] = new SampleBlock(data, samplePointer);
            }
        }
        int patternCount = songInfo.getPatternCount();
        patternBlocks = new PatternBlock[patternCount];
        for(int i=0;i<patternCount;i++){
            int patternPointer = songInfo.getPatternPointers()[i];
            if(patternPointer>0){
                patternBlocks[i] = new PatternBlock(data, patternPointer);
            }
        }
        int i=0;
    }

    public static byte[] getByteArray(ByteBuffer bb, int length){
        byte[] bytes = new byte[length];
        for(int i=0;i<bytes.length;i++){
            bytes[i] = bb.get();
        }
        return bytes;
    }

    public static int[] getIntArray(ByteBuffer bb, int length){
        int[] ints = new int[length];
        for(int i=0;i<ints.length;i++){
            ints[i] = bb.getInt();
        }
        return ints;
    }

    public static float[] getFloatArray(ByteBuffer bb, int length){
        float[] floats = new float[length];
        for(int i=0;i<floats.length;i++){
            floats[i] = bb.getFloat();
        }
        return floats;
    }

    public static String getString(ByteBuffer bb){
        int length = findStringLength(bb, bb.position());
        byte[] workingBytes = new byte[length];
        bb.get(workingBytes, 0, length);
        bb.position(bb.position()+1);
        return new String(workingBytes, StandardCharsets.UTF_8);
    }

    public static String getString(ByteBuffer bb, int length){
        byte[] workingBytes = new byte[length];
        bb.get(workingBytes, 0, length);
        return new String(workingBytes, StandardCharsets.UTF_8);
    }
    
    public static int findStringLength(ByteBuffer bb, int cursor){
        int length = 0;
        while(bb.get(cursor+length)!=0){
            length++;
        }
        return length;
    }    
    
    public static int getStringArrayLength(String[] stringArray){
        int totalLength = 0;
        for(int i=0;i<stringArray.length;i++){
            totalLength += stringArray[i].length()+1;
        }
        return totalLength;
    }
    
    public static byte[] toByteArray(int[] intArray){
        ByteBuffer bb = ByteBuffer.allocate(intArray.length*4);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.position(0);
        for(int i=0;i<intArray.length;i++){
            bb.putInt(intArray[i]);
        }
        return bb.array();
    }
    
    public static byte[] toByteArray(String[] stringArray){
        ByteBuffer bb = ByteBuffer.allocate(getStringArrayLength(stringArray));
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.position(0);
        for(int i=0;i<stringArray.length;i++){
            bb.put(stringArray[i].getBytes(StandardCharsets.UTF_8));
            bb.put((byte)0);
        }
        return bb.array();
    }

    public static String[] getStringArray(ByteBuffer bb, int length){
        String[] strings = new String[length];
        for(int i=0;i<length;i++){
            int workingLength = findStringLength(bb, bb.position());
            byte[] workingBytes = new byte[workingLength];
            bb.get(workingBytes, 0, workingLength);
            bb.position(bb.position()+1);
            strings[i] = new String(workingBytes, StandardCharsets.UTF_8);
        }
        return strings;
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public SongInfoBlock getSongInfo() {
        return songInfo;
    }

    public void setSongInfo(SongInfoBlock songInfo) {
        this.songInfo = songInfo;
    }

    public ChipFlagsBlock[] getChipFlags() {
        return chipFlagsBlocks;
    }

    public void setChipFlags(ChipFlagsBlock[] chipFlags) {
        this.chipFlagsBlocks = chipFlags;
    }

    public AssetDirectoriesBlock[] getAssetDirectoriesArray() {
        return assetDirectoriesBlocks;
    }

    public void setAssetDirectoriesArray(AssetDirectoriesBlock[] assetDirectoriesArray) {
        this.assetDirectoriesBlocks = assetDirectoriesArray;
    }

    public InstrumentBlock[] getInstruments() {
        return instrumentBlocks;
    }

    public void setInstruments(InstrumentBlock[] instruments) {
        this.instrumentBlocks = instruments;
    }

    public WavetableBlock[] getWavetables() {
        return wavetableBlocks;
    }

    public void setWavetables(WavetableBlock[] wavetables) {
        this.wavetableBlocks = wavetables;
    }

    public SampleBlock[] getSamples() {
        return sampleBlocks;
    }

    public void setSamples(SampleBlock[] samples) {
        this.sampleBlocks = samples;
    }

    public PatternBlock[] getPatterns() {
        return patternBlocks;
    }

    public void setPatterns(PatternBlock[] patterns) {
        this.patternBlocks = patterns;
    }
    
    public byte[] toByteArray(){
        
        updatePointers();
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        baos.writeBytes(header.toByteArray());
        baos.writeBytes(songInfo.toByteArray());
        for(int i=0;i<chipFlagsBlocks.length;i++){
            if(chipFlagsBlocks[i]!=null){
                baos.writeBytes(chipFlagsBlocks[i].toByteArray());
            }
        }
        for(int i=0;i<chipFlagsBlocks.length;i++){
            if(chipFlagsBlocks[i]!=null){
                baos.writeBytes(chipFlagsBlocks[i].toByteArray());
            }
        }
        for(int i=0;i<assetDirectoriesBlocks.length;i++){
            if(assetDirectoriesBlocks[i]!=null){
                baos.writeBytes(assetDirectoriesBlocks[i].toByteArray());
            }
        }
        for(int i=0;i<instrumentBlocks.length;i++){
            if(instrumentBlocks[i]!=null){
                baos.writeBytes(instrumentBlocks[i].toByteArray());
            }
        }
        for(int i=0;i<wavetableBlocks.length;i++){
            if(wavetableBlocks[i]!=null){
                baos.writeBytes(wavetableBlocks[i].toByteArray());
            }
        }
        for(int i=0;i<sampleBlocks.length;i++){
            if(sampleBlocks[i]!=null){
                baos.writeBytes(sampleBlocks[i].toByteArray());
            }
        }
        for(int i=0;i<patternBlocks.length;i++){
            if(patternBlocks[i]!=null){
                baos.writeBytes(patternBlocks[i].toByteArray());
            }
        }  
        
        return baos.toByteArray();
    }
    
    public void updatePointers(){
        int nextPointer = header.findLength();
        header.setSongInfoPointer(nextPointer);
        int numberOfChips = songInfo.findNumberOfChips();
        songInfo.setPatternCount(patternBlocks.length);
        songInfo.setOrdersLength((short)(patternBlocks.length/10));
        nextPointer += songInfo.findLength();
        for(int i=0;i<numberOfChips;i++){
            if(chipFlagsBlocks[i]!=null){
                songInfo.getSoundChipFlagPointers()[i] = nextPointer;
                nextPointer += chipFlagsBlocks[i].findLength();
            }
        } 
        songInfo.setInstrumentDirectoriesPointer(nextPointer);
        nextPointer += assetDirectoriesBlocks[0].findLength();
        songInfo.setWavetableDirectoriesPointer(nextPointer);
        nextPointer += assetDirectoriesBlocks[1].findLength();
        songInfo.setSampleDirectoriesPointer(nextPointer);
        nextPointer += assetDirectoriesBlocks[2].findLength();
        for(int i=0;i<instrumentBlocks.length;i++){
            if(instrumentBlocks[i]!=null){
                songInfo.getInstrumentPointers()[i] = nextPointer;
                nextPointer += instrumentBlocks[i].findLength();
            }
        } 
        for(int i=0;i<wavetableBlocks.length;i++){
            if(wavetableBlocks[i]!=null){
                songInfo.getWavetablePointers()[i] = nextPointer;
                nextPointer += wavetableBlocks[i].findLength();
            }
        } 
        for(int i=0;i<sampleBlocks.length;i++){
            if(sampleBlocks[i]!=null){
                songInfo.getSamplePointers()[i] = nextPointer;
                nextPointer += sampleBlocks[i].findLength();
            }
        } 
        
        int[] newPatternPointers = new int[patternBlocks.length];
        for(int i=0;i<patternBlocks.length;i++){
            if(patternBlocks[i]!=null){
                newPatternPointers[i] = nextPointer;
                nextPointer += patternBlocks[i].findLength();
            }
        }
        songInfo.setPatternPointers(newPatternPointers);
        
    }
    
}
