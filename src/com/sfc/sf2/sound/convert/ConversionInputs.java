/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.sound.convert;

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
    private int inRamPreloadOffset;
    private int[] ymInstruments;
    private boolean ssgEg;
    private int sampleTableOffset;
    private boolean multiBankSampleTableFormat;
    private int[] sampleBankOffsets;
    private String[] targetFolders;
    
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
        inRamPreloadOffset = Integer.parseInt(params[3], 16);
        String[] ymInstrumentsOffsetStrings = params[4].split(",");
        ymInstruments = new int[ymInstrumentsOffsetStrings.length];
        for(int i=0;i<ymInstruments.length;i++){
            ymInstruments[i] = Integer.parseInt(ymInstrumentsOffsetStrings[i], 16);
        }
        ssgEg = Boolean.parseBoolean(params[5]);
        sampleTableOffset = Integer.parseInt(params[6], 16);
        multiBankSampleTableFormat = Boolean.parseBoolean(params[7]);
        String[] sampleBankOffsetStrings = params[8].split(",");
        sampleBankOffsets = new int[sampleBankOffsetStrings.length];
        for(int i=0;i<sampleBankOffsets.length;i++){
            sampleBankOffsets[i] = Integer.parseInt(sampleBankOffsetStrings[i], 16);
        }
        targetFolders = params[9].split(",");
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

    public int getInRamPreloadOffset() {
        return inRamPreloadOffset;
    }

    public void setInRamPreloadOffset(int inRamPreloadOffset) {
        this.inRamPreloadOffset = inRamPreloadOffset;
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

    public String[] getTargetFolders() {
        return targetFolders;
    }

    public void setTargetFolders(String[] targetFolders) {
        this.targetFolders = targetFolders;
    }
    
    
    
}
