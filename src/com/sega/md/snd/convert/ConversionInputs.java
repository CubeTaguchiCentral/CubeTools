/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sega.md.snd.convert;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author Wiz
 */
public class ConversionInputs {
    
    private String gameName;
    private String romFilePath;
    private int[] musicBankOffsets;
    private int driverOffset;
    private int pitchEffectsOffset;
    private int ymLevelsOffset;
    private int psgInstruments;
    private int inRamPreloadOffset;
    private int ymTimerBIncrement;
    private int[] ymInstruments;
    private boolean ssgEg;
    private int sampleTableOffset;
    private int sampleCount;
    private boolean multiBankSampleTableFormat;
    private int[] sampleBankOffsets;
    private int sfxOffset;
    private int sfxCount;
    
    public ConversionInputs(String line){
        line = line.trim();
        String[] params = line.split(";");
        gameName = params[0];
        romFilePath = params[1];
        String[] musicBankOffsetStrings = params[2].split(",");
        musicBankOffsets = new int[musicBankOffsetStrings.length];
        for(int i=0;i<musicBankOffsets.length;i++){
            musicBankOffsets[i] = Integer.parseInt(musicBankOffsetStrings[i], 16);
        }
        driverOffset = Integer.parseInt(params[3], 16);
        pitchEffectsOffset = Integer.parseInt(params[4], 16);
        ymLevelsOffset = Integer.parseInt(params[5], 16);
        psgInstruments = Integer.parseInt(params[6], 16);
        inRamPreloadOffset = Integer.parseInt(params[7], 16);
        ymTimerBIncrement = Integer.parseInt(params[8], 16);
        String[] ymInstrumentsOffsetStrings = params[9].split(",");
        ymInstruments = new int[ymInstrumentsOffsetStrings.length];
        for(int i=0;i<ymInstruments.length;i++){
            ymInstruments[i] = Integer.parseInt(ymInstrumentsOffsetStrings[i], 16);
        }
        ssgEg = Boolean.parseBoolean(params[10]);
        sampleTableOffset = Integer.parseInt(params[11], 16);
        sampleCount = Integer.parseInt(params[12], 10);
        multiBankSampleTableFormat = Boolean.parseBoolean(params[13]);
        String[] sampleBankOffsetStrings = params[14].split(",");
        sampleBankOffsets = new int[sampleBankOffsetStrings.length];
        for(int i=0;i<sampleBankOffsets.length;i++){
            sampleBankOffsets[i] = Integer.parseInt(sampleBankOffsetStrings[i], 16);
        }
        if(params.length>15){
            sfxOffset = Integer.parseInt(params[15], 16);
            sfxCount = Integer.parseInt(params[16], 10);
        }
    }
    
    public static ConversionInputs[] importConversionInputs(String filePath){
        ConversionInputs[] cis = null;
        List<ConversionInputs> ciList = new ArrayList();            
        File file = new File(filePath);
        try{
            Scanner scan = new Scanner(file);
            while(scan.hasNext()){
                String line = scan.nextLine();
                if(!line.startsWith("#")){
                    ciList.add(new ConversionInputs(line));
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        cis = ciList.toArray(new ConversionInputs[0]);
        return cis;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getRomFilePath() {
        return romFilePath;
    }

    public void setRomFilePath(String romFilePath) {
        this.romFilePath = romFilePath;
    }

    public int[] getMusicBankOffsets() {
        return musicBankOffsets;
    }

    public void setMusicBankOffsets(int[] musicBankOffsets) {
        this.musicBankOffsets = musicBankOffsets;
    }

    public int getDriverOffset() {
        return driverOffset;
    }

    public void setDriverOffset(int driverOffset) {
        this.driverOffset = driverOffset;
    }

    public int getPitchEffectsOffset() {
        return pitchEffectsOffset;
    }

    public void setPitchEffectsOffset(int pitchEffectsOffset) {
        this.pitchEffectsOffset = pitchEffectsOffset;
    }

    public int getYmLevelsOffset() {
        return ymLevelsOffset;
    }

    public void setYmLevelsOffset(int ymLevelsOffset) {
        this.ymLevelsOffset = ymLevelsOffset;
    }

    public int getPsgInstruments() {
        return psgInstruments;
    }

    public void setPsgInstruments(int psgInstruments) {
        this.psgInstruments = psgInstruments;
    }

    public int getInRamPreloadOffset() {
        return inRamPreloadOffset;
    }

    public void setInRamPreloadOffset(int inRamPreloadOffset) {
        this.inRamPreloadOffset = inRamPreloadOffset;
    }

    public int getYmTimerBIncrement() {
        return ymTimerBIncrement;
    }

    public void setYmTimerBIncrement(int ymTimerBIncrement) {
        this.ymTimerBIncrement = ymTimerBIncrement;
    }

    public int[] getYmInstruments() {
        return ymInstruments;
    }

    public void setYmInstruments(int[] ymInstruments) {
        this.ymInstruments = ymInstruments;
    }

    public boolean isSsgEg() {
        return ssgEg;
    }

    public void setSsgEg(boolean ssgEg) {
        this.ssgEg = ssgEg;
    }

    public int getSampleTableOffset() {
        return sampleTableOffset;
    }

    public void setSampleTableOffset(int sampleTableOffset) {
        this.sampleTableOffset = sampleTableOffset;
    }

    public int getSampleCount() {
        return sampleCount;
    }

    public void setSampleCount(int sampleCount) {
        this.sampleCount = sampleCount;
    }

    public boolean isMultiBankSampleTableFormat() {
        return multiBankSampleTableFormat;
    }

    public void setMultiBankSampleTableFormat(boolean multiBankSampleTableFormat) {
        this.multiBankSampleTableFormat = multiBankSampleTableFormat;
    }

    public int[] getSampleBankOffsets() {
        return sampleBankOffsets;
    }

    public void setSampleBankOffsets(int[] sampleBankOffsets) {
        this.sampleBankOffsets = sampleBankOffsets;
    }

    public int getSfxOffset() {
        return sfxOffset;
    }

    public void setSfxOffset(int sfxOffset) {
        this.sfxOffset = sfxOffset;
    }

    public int getSfxCount() {
        return sfxCount;
    }

    public void setSfxCount(int sfxCount) {
        this.sfxCount = sfxCount;
    }
    
    
    
}
