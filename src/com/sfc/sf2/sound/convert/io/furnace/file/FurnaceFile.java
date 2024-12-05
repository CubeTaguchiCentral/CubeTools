/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.sound.convert.io.furnace.file;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author Wiz
 */
public class FurnaceFile {
    
    private Header header;
    private SongInfo songInfo;
    private ChipFlags[] chipFlags;
    private AssetDirectories[] assetDirectoriesArray;
    private Instrument[] instruments;
    private Wavetable[] wavetables;
    private Sample[] samples;
    private Pattern[] patterns;
    
    public FurnaceFile(byte[] data){
        //ByteBuffer bb = ByteBuffer.allocate(data.length);
        header = new Header(data);
        songInfo = new SongInfo(data, header.getSongPointer());
        int numberOfChips = songInfo.findNumberOfChips();
        chipFlags = new ChipFlags[32];
        int[] chipFlagPointers = songInfo.getSoundChipFlagPointers();
        for(int i=0;i<numberOfChips;i++){
            if(chipFlagPointers[i]>0){
                chipFlags[i] = new ChipFlags(data,chipFlagPointers[i]);
            }
        }  
        assetDirectoriesArray = new AssetDirectories[3];
        int instrumentDirectoriesPointer = songInfo.getInstrumentDirectoriesPointer();
        assetDirectoriesArray[0] = new AssetDirectories(data, instrumentDirectoriesPointer);
        int wavetableDirectoriesPointer = songInfo.getWavetableDirectoriesPointer();
        assetDirectoriesArray[1] = new AssetDirectories(data, wavetableDirectoriesPointer);
        int sampleDirectoriesPointer = songInfo.getSampleDirectoriesPointer();
        assetDirectoriesArray[2] = new AssetDirectories(data, sampleDirectoriesPointer);
        int instrumentCount = songInfo.getInstrumentCount();
        instruments = new Instrument[instrumentCount];
        for(int i=0;i<instrumentCount;i++){
            int instrumentPointer = songInfo.getInstrumentPointers()[i];
            if(instrumentPointer>0){
                instruments[i] = new Instrument(data, instrumentPointer);
            }
        }
        int wavetableCount = songInfo.getWavetableCount();
        wavetables = new Wavetable[wavetableCount];
        for(int i=0;i<wavetableCount;i++){
            int wavetablePointer = songInfo.getWavetablePointers()[i];
            if(wavetablePointer>0){
                wavetables[i] = new Wavetable(data, wavetablePointer);
            }
        }
        int sampleCount = songInfo.getSampleCount();
        samples = new Sample[sampleCount];
        for(int i=0;i<sampleCount;i++){
            int samplePointer = songInfo.getSamplePointers()[i];
            if(samplePointer>0){
                samples[i] = new Sample(data, samplePointer);
            }
        }
        int patternCount = songInfo.getPatternCount();
        patterns = new Pattern[patternCount];
        for(int i=0;i<patternCount;i++){
            int patternPointer = songInfo.getPatternPointers()[i];
            if(patternPointer>0){
                patterns[i] = new Pattern(data, patternPointer);
            }
        }
        int i = 0;
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

    public SongInfo getSongInfo() {
        return songInfo;
    }

    public void setSongInfo(SongInfo songInfo) {
        this.songInfo = songInfo;
    }

    public ChipFlags[] getChipFlags() {
        return chipFlags;
    }

    public void setChipFlags(ChipFlags[] chipFlags) {
        this.chipFlags = chipFlags;
    }

    public AssetDirectories[] getAssetDirectoriesArray() {
        return assetDirectoriesArray;
    }

    public void setAssetDirectoriesArray(AssetDirectories[] assetDirectoriesArray) {
        this.assetDirectoriesArray = assetDirectoriesArray;
    }

    public Instrument[] getInstruments() {
        return instruments;
    }

    public void setInstruments(Instrument[] instruments) {
        this.instruments = instruments;
    }

    public Wavetable[] getWavetables() {
        return wavetables;
    }

    public void setWavetables(Wavetable[] wavetables) {
        this.wavetables = wavetables;
    }

    public Sample[] getSamples() {
        return samples;
    }

    public void setSamples(Sample[] samples) {
        this.samples = samples;
    }

    public Pattern[] getPatterns() {
        return patterns;
    }

    public void setPatterns(Pattern[] patterns) {
        this.patterns = patterns;
    }
    
    
    
}
