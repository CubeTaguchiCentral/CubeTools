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
    private AssetDirectory[] assetDirectories;
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
        int instrumentDirectoriesPointer = songInfo.getInstrumentDirectoriesPointer();
        
        int wavetableDirectoriesPointer = songInfo.getWavetableDirectoriesPointer();
        int sampleDirectoriesPointer = songInfo.getSampleDirectoriesPointer();
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

    public AssetDirectory[] getAssetDirectories() {
        return assetDirectories;
    }

    public void setAssetDirectories(AssetDirectory[] assetDirectories) {
        this.assetDirectories = assetDirectories;
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
