/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.sound.convert.io.cube;

/**
 *
 * @author Wiz
 */
public class MusicEntry {
    
    boolean ym6InDacMode = false;
    byte ymTimerBValue = 0;
    CubeCommand[] ym1ChannelData;
    CubeCommand[] ym2ChannelData;
    CubeCommand[] ym3ChannelData;
    CubeCommand[] ym4ChannelData;
    CubeCommand[] ym5ChannelData;
    CubeCommand[] ym6ChannelData;
    CubeCommand[] psgTone1ChannelData;
    CubeCommand[] psgTone2ChannelData;
    CubeCommand[] psgTone3ChannelData;
    CubeCommand[] psgNoiseChannelData;

    public boolean isYm6InDacMode() {
        return ym6InDacMode;
    }

    public void setYm6InDacMode(boolean ym6InDacMode) {
        this.ym6InDacMode = ym6InDacMode;
    }

    public byte getYmTimerBValue() {
        return ymTimerBValue;
    }

    public void setYmTimerBValue(byte ymTimerBValue) {
        this.ymTimerBValue = ymTimerBValue;
    }

    public CubeCommand[] getYm1ChannelData() {
        return ym1ChannelData;
    }

    public void setYm1ChannelData(CubeCommand[] ym1ChannelData) {
        this.ym1ChannelData = ym1ChannelData;
    }

    public CubeCommand[] getYm2ChannelData() {
        return ym2ChannelData;
    }

    public void setYm2ChannelData(CubeCommand[] ym2ChannelData) {
        this.ym2ChannelData = ym2ChannelData;
    }

    public CubeCommand[] getYm3ChannelData() {
        return ym3ChannelData;
    }

    public void setYm3ChannelData(CubeCommand[] ym3ChannelData) {
        this.ym3ChannelData = ym3ChannelData;
    }

    public CubeCommand[] getYm4ChannelData() {
        return ym4ChannelData;
    }

    public void setYm4ChannelData(CubeCommand[] ym4ChannelData) {
        this.ym4ChannelData = ym4ChannelData;
    }

    public CubeCommand[] getYm5ChannelData() {
        return ym5ChannelData;
    }

    public void setYm5ChannelData(CubeCommand[] ym5ChannelData) {
        this.ym5ChannelData = ym5ChannelData;
    }

    public CubeCommand[] getYm6ChannelData() {
        return ym6ChannelData;
    }

    public void setYm6ChannelData(CubeCommand[] ym6ChannelData) {
        this.ym6ChannelData = ym6ChannelData;
    }

    public CubeCommand[] getPsgTone1ChannelData() {
        return psgTone1ChannelData;
    }

    public void setPsgTone1ChannelData(CubeCommand[] psgTone1ChannelData) {
        this.psgTone1ChannelData = psgTone1ChannelData;
    }

    public CubeCommand[] getPsgTone2ChannelData() {
        return psgTone2ChannelData;
    }

    public void setPsgTone2ChannelData(CubeCommand[] psgTone2ChannelData) {
        this.psgTone2ChannelData = psgTone2ChannelData;
    }

    public CubeCommand[] getPsgTone3ChannelData() {
        return psgTone3ChannelData;
    }

    public void setPsgTone3ChannelData(CubeCommand[] psgTone3ChannelData) {
        this.psgTone3ChannelData = psgTone3ChannelData;
    }

    public CubeCommand[] getPsgNoiseChannelData() {
        return psgNoiseChannelData;
    }

    public void setPsgNoiseChannelData(CubeCommand[] psgNoiseChannelData) {
        this.psgNoiseChannelData = psgNoiseChannelData;
    }
    
    
}
