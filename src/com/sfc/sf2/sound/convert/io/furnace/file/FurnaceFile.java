/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.sound.convert.io.furnace.file;

/**
 *
 * @author Wiz
 */
public class FurnaceFile {
    
    private Header header;
    private SongInfo songInfo;
    private ChipFlags chipFlags;
    private AssetDirectory[] assetDirectories;
    private Instrument[] instruments;
    private Wavetable[] wavetables;
    private Sample[] samples;
    private Pattern[] patterns;
    
    public FurnaceFile(byte[] data){
        header = new Header(data);
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

    public ChipFlags getChipFlags() {
        return chipFlags;
    }

    public void setChipFlags(ChipFlags chipFlags) {
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
