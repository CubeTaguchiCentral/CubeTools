/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.sound.convert.io.furnace.file;

import com.sfc.sf2.sound.convert.io.furnace.file.section.SongInfoBlock;
import com.sfc.sf2.sound.convert.io.furnace.file.section.ChipFlagsBlock;
import com.sfc.sf2.sound.convert.io.furnace.file.section.SampleBlock;
import com.sfc.sf2.sound.convert.io.furnace.file.section.WavetableBlock;
import com.sfc.sf2.sound.convert.io.furnace.file.section.InstrumentBlock;
import com.sfc.sf2.sound.convert.io.furnace.file.section.PatternBlock;
import com.sfc.sf2.sound.convert.io.furnace.file.section.AssetDirectoriesBlock;
import com.sfc.sf2.sound.convert.io.furnace.file.section.Header;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author Wiz
 */
public class FurnaceFile {
    
    private Header header;
    private SongInfoBlock songInfo;
    private ChipFlagsBlock[] chipFlags;
    private AssetDirectoriesBlock[] assetDirectoriesArray;
    private InstrumentBlock[] instruments;
    private WavetableBlock[] wavetables;
    private SampleBlock[] samples;
    private PatternBlock[] patterns;
    
    public FurnaceFile(byte[] data){
        header = new Header(data);
        songInfo = new SongInfoBlock(data, header.getSongPointer());
        int numberOfChips = songInfo.findNumberOfChips();
        chipFlags = new ChipFlagsBlock[32];
        int[] chipFlagPointers = songInfo.getSoundChipFlagPointers();
        for(int i=0;i<numberOfChips;i++){
            if(chipFlagPointers[i]>0){
                chipFlags[i] = new ChipFlagsBlock(data,chipFlagPointers[i]);
            }
        }  
        assetDirectoriesArray = new AssetDirectoriesBlock[3];
        int instrumentDirectoriesPointer = songInfo.getInstrumentDirectoriesPointer();
        assetDirectoriesArray[0] = new AssetDirectoriesBlock(data, instrumentDirectoriesPointer);
        int wavetableDirectoriesPointer = songInfo.getWavetableDirectoriesPointer();
        assetDirectoriesArray[1] = new AssetDirectoriesBlock(data, wavetableDirectoriesPointer);
        int sampleDirectoriesPointer = songInfo.getSampleDirectoriesPointer();
        assetDirectoriesArray[2] = new AssetDirectoriesBlock(data, sampleDirectoriesPointer);
        int instrumentCount = songInfo.getInstrumentCount();
        instruments = new InstrumentBlock[instrumentCount];
        for(int i=0;i<instrumentCount;i++){
            int instrumentPointer = songInfo.getInstrumentPointers()[i];
            if(instrumentPointer>0){
                instruments[i] = new InstrumentBlock(data, instrumentPointer);
            }
        }
        int wavetableCount = songInfo.getWavetableCount();
        wavetables = new WavetableBlock[wavetableCount];
        for(int i=0;i<wavetableCount;i++){
            int wavetablePointer = songInfo.getWavetablePointers()[i];
            if(wavetablePointer>0){
                wavetables[i] = new WavetableBlock(data, wavetablePointer);
            }
        }
        int sampleCount = songInfo.getSampleCount();
        samples = new SampleBlock[sampleCount];
        for(int i=0;i<sampleCount;i++){
            int samplePointer = songInfo.getSamplePointers()[i];
            if(samplePointer>0){
                samples[i] = new SampleBlock(data, samplePointer);
            }
        }
        int patternCount = songInfo.getPatternCount();
        patterns = new PatternBlock[patternCount];
        for(int i=0;i<patternCount;i++){
            int patternPointer = songInfo.getPatternPointers()[i];
            if(patternPointer>0){
                patterns[i] = new PatternBlock(data, patternPointer);
            }
        }
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
        return chipFlags;
    }

    public void setChipFlags(ChipFlagsBlock[] chipFlags) {
        this.chipFlags = chipFlags;
    }

    public AssetDirectoriesBlock[] getAssetDirectoriesArray() {
        return assetDirectoriesArray;
    }

    public void setAssetDirectoriesArray(AssetDirectoriesBlock[] assetDirectoriesArray) {
        this.assetDirectoriesArray = assetDirectoriesArray;
    }

    public InstrumentBlock[] getInstruments() {
        return instruments;
    }

    public void setInstruments(InstrumentBlock[] instruments) {
        this.instruments = instruments;
    }

    public WavetableBlock[] getWavetables() {
        return wavetables;
    }

    public void setWavetables(WavetableBlock[] wavetables) {
        this.wavetables = wavetables;
    }

    public SampleBlock[] getSamples() {
        return samples;
    }

    public void setSamples(SampleBlock[] samples) {
        this.samples = samples;
    }

    public PatternBlock[] getPatterns() {
        return patterns;
    }

    public void setPatterns(PatternBlock[] patterns) {
        this.patterns = patterns;
    }
    
    
    
}
