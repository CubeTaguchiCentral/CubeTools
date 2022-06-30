/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.sound.convert.io.cube;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Wiz
 */
public class MusicEntry {
    String name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
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
    
    public String produceAsmOutput(){
        
        StringBuilder sb = new StringBuilder();
        sb.append(name+":"
                + "\n"+"    db 0"
                + "\n"+"    db 0"+(ym6InDacMode?"0":"1")+"h"
                + "\n"+"    db 0"
                + "\n"+"    db "+Integer.toString(ymTimerBValue&0xFF)
                + "\n"+"    dw "+name+"_Channel_0"
                + "\n"+"    dw "+name+"_Channel_1"
                + "\n"+"    dw "+name+"_Channel_2"
                + "\n"+"    dw "+name+"_Channel_3"
                + "\n"+"    dw "+name+"_Channel_4"
                + "\n"+"    dw "+name+"_Channel_5"
                + "\n"+"    dw "+name+"_Channel_6"
                + "\n"+"    dw "+name+"_Channel_7"
                + "\n"+"    dw "+name+"_Channel_8"
                + "\n"+"    dw "+name+"_Channel_9");
            
        sb.append("\n"+name+"_Channel_0:"+produceChannelAsmOutput(ym1ChannelData));
        sb.append("\n"+name+"_Channel_1:"+produceChannelAsmOutput(ym2ChannelData));
        sb.append("\n"+name+"_Channel_2:"+produceChannelAsmOutput(ym3ChannelData));
        sb.append("\n"+name+"_Channel_3:"+produceChannelAsmOutput(ym4ChannelData));
        sb.append("\n"+name+"_Channel_4:"+produceChannelAsmOutput(ym5ChannelData));
        sb.append("\n"+name+"_Channel_5:"+produceChannelAsmOutput(ym6ChannelData));
        sb.append("\n"+name+"_Channel_6:"+produceChannelAsmOutput(psgTone1ChannelData));
        sb.append("\n"+name+"_Channel_7:"+produceChannelAsmOutput(psgTone2ChannelData));
        sb.append("\n"+name+"_Channel_8:"+produceChannelAsmOutput(psgTone3ChannelData));
        sb.append("\n"+name+"_Channel_9:"+produceChannelAsmOutput(psgNoiseChannelData));
            
        return sb.toString();
    }
    
    private String produceChannelAsmOutput(CubeCommand[] ccs){
        StringBuilder sb = new StringBuilder();
        for(CubeCommand cc : ccs){
            sb.append("\n    "+cc.produceAsmOutput());
        }
        return sb.toString();
    }
    
    public byte[] produceBinaryOutput(){
        
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        
        try {
            output.write(0);
            output.write(ym6InDacMode?(byte)0:(byte)1);
            output.write(0);
            output.write(ymTimerBValue);
            
            byte[] ym1ChannelBytes = produceChannelBinaryOutput(ym1ChannelData);
            byte[] ym2ChannelBytes = produceChannelBinaryOutput(ym2ChannelData);
            byte[] ym3ChannelBytes = produceChannelBinaryOutput(ym3ChannelData);
            byte[] ym4ChannelBytes = produceChannelBinaryOutput(ym4ChannelData);
            byte[] ym5ChannelBytes = produceChannelBinaryOutput(ym5ChannelData);
            byte[] ym6ChannelBytes = produceChannelBinaryOutput(ym6ChannelData);
            byte[] psgTone1ChannelBytes = produceChannelBinaryOutput(psgTone1ChannelData);
            byte[] psgTone2ChannelBytes = produceChannelBinaryOutput(psgTone2ChannelData);
            byte[] psgTone3ChannelBytes = produceChannelBinaryOutput(psgTone3ChannelData);
            byte[] psgNoiseChannelBytes = produceChannelBinaryOutput(psgNoiseChannelData);
            
            int ym1ChannelPointer = 24;
            int ym2ChannelPointer = ym1ChannelPointer+ym1ChannelBytes.length;
            int ym3ChannelPointer = ym2ChannelPointer+ym2ChannelBytes.length;
            int ym4ChannelPointer = ym3ChannelPointer+ym3ChannelBytes.length;
            int ym5ChannelPointer = ym4ChannelPointer+ym4ChannelBytes.length;
            int ym6ChannelPointer = ym5ChannelPointer+ym5ChannelBytes.length;
            int psgTone1ChannelPointer = ym6ChannelPointer+ym6ChannelBytes.length;
            int psgTone2ChannelPointer = psgTone1ChannelPointer+psgTone1ChannelBytes.length;
            int psgTone3ChannelPointer = psgTone2ChannelPointer+psgTone2ChannelBytes.length;
            int psgNoiseChannelPointer = psgTone3ChannelPointer+psgTone3ChannelBytes.length;
            
            output.write((byte)(ym1ChannelPointer&0xFF));
            output.write((byte)((ym1ChannelPointer>>8)&0xFF));
            output.write((byte)(ym2ChannelPointer&0xFF));
            output.write((byte)((ym2ChannelPointer>>8)&0xFF));
            output.write((byte)(ym3ChannelPointer&0xFF));
            output.write((byte)((ym3ChannelPointer>>8)&0xFF));
            output.write((byte)(ym4ChannelPointer&0xFF));
            output.write((byte)((ym4ChannelPointer>>8)&0xFF));
            output.write((byte)(ym5ChannelPointer&0xFF));
            output.write((byte)((ym5ChannelPointer>>8)&0xFF));
            output.write((byte)(ym6ChannelPointer&0xFF));
            output.write((byte)((ym6ChannelPointer>>8)&0xFF));
            output.write((byte)(psgTone1ChannelPointer&0xFF));
            output.write((byte)((psgTone1ChannelPointer>>8)&0xFF));
            output.write((byte)(psgTone2ChannelPointer&0xFF));
            output.write((byte)((psgTone2ChannelPointer>>8)&0xFF));
            output.write((byte)(psgTone3ChannelPointer&0xFF));
            output.write((byte)((psgTone3ChannelPointer>>8)&0xFF));
            output.write((byte)(psgNoiseChannelPointer&0xFF));
            output.write((byte)((psgNoiseChannelPointer>>8)&0xFF));
            
            output.write(ym1ChannelBytes);
            output.write(ym2ChannelBytes);
            output.write(ym3ChannelBytes);
            output.write(ym4ChannelBytes);
            output.write(ym5ChannelBytes);
            output.write(ym6ChannelBytes);
            output.write(psgTone1ChannelBytes);
            output.write(psgTone2ChannelBytes);
            output.write(psgTone3ChannelBytes);
            output.write(psgNoiseChannelBytes);
            
        } catch (IOException ex) {
            Logger.getLogger(MusicEntry.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return output.toByteArray();
    }
    
    private byte[] produceChannelBinaryOutput(CubeCommand[] ccs) throws IOException{
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        for(CubeCommand cc : ccs){
           output.write(cc.produceBinaryOutput());
        }
        return output.toByteArray();
    }
    
}
