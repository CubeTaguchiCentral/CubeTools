/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.sound.convert.io.cube;

import com.sfc.sf2.sound.convert.io.cube.channel.*;
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
    CubeChannel[] channels = new CubeChannel[10];

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

    public CubeChannel[] getChannels() {
        return channels;
    }

    public void setChannels(CubeChannel[] channels) {
        this.channels = channels;
    }
    
    public MusicEntry(byte [] data, int entryOffset, int baseOffset){
        if(data[entryOffset+1]==0){
            ym6InDacMode = true;
        }else{
            ym6InDacMode = false;
        }
        ymTimerBValue = data[entryOffset+3];
        int channelOffset = baseOffset + (((data[entryOffset+4 + 2*0 + 1])&0xFF)<<8) + ((data[entryOffset+4 + 2*0])&0xFF);
        channels[0] = new YmChannel(data, channelOffset);
        channelOffset = baseOffset + (((data[entryOffset+4 + 2*1 + 1])&0xFF)<<8) + ((data[entryOffset+4 + 2*1])&0xFF);
        channels[1] = new YmChannel(data,channelOffset);
        channelOffset = baseOffset + (((data[entryOffset+4 + 2*2 + 1])&0xFF)<<8) + ((data[entryOffset+4 + 2*2])&0xFF);
        channels[2] = new YmChannel(data,channelOffset);
        channelOffset = baseOffset + (((data[entryOffset+4 + 2*3 + 1])&0xFF)<<8) + ((data[entryOffset+4 + 2*3])&0xFF);
        channels[3] = new YmChannel(data, channelOffset);
        channelOffset = baseOffset + (((data[entryOffset+4 + 2*4 + 1])&0xFF)<<8) + ((data[entryOffset+4 + 2*4])&0xFF);
        channels[4] = new YmChannel(data, channelOffset);
        channelOffset = baseOffset + (((data[entryOffset+4 + 2*5 + 1])&0xFF)<<8) + ((data[entryOffset+4 + 2*5])&0xFF);
        if(isYm6InDacMode()){
            channels[5] = new DacChannel(data, channelOffset);
        }else{
            channels[5] = new YmChannel(data, channelOffset);
        }
        channelOffset = baseOffset + (((data[entryOffset+4 + 2*6 + 1])&0xFF)<<8) + ((data[entryOffset+4 + 2*6])&0xFF);
        channels[6] = new PsgToneChannel(data, channelOffset);
        channelOffset = baseOffset + (((data[entryOffset+4 + 2*7 + 1])&0xFF)<<8) + ((data[entryOffset+4 + 2*7])&0xFF);
        channels[7] = new PsgToneChannel(data, channelOffset);
        channelOffset = baseOffset + (((data[entryOffset+4 + 2*8 + 1])&0xFF)<<8) + ((data[entryOffset+4 + 2*8])&0xFF);
        channels[8] = new PsgToneChannel(data, channelOffset);
        channelOffset = baseOffset + (((data[entryOffset+4 + 2*9 + 1])&0xFF)<<8) + ((data[entryOffset+4 + 2*9])&0xFF);
        channels[9] = new PsgNoiseChannel(data, channelOffset);
    }
    
    public String produceAsmOutput(){
        StringBuilder sb = new StringBuilder();
        sb.append(name+":"
                + "\n"+"    db 0"
                + "\n"+"    db "+(ym6InDacMode?"0":"1")
                + "\n"+"    db 0"
                + "\n"+"    db "+Integer.toString(ymTimerBValue&0xFF));
        for(int i=0;i<10;i++){
            sb.append("\n"+"    dw "+name+"_Channel_"+i);
        }
        for(int i=0;i<10;i++){
            sb.append("\n"+name+"_Channel_"+i+":"+channels[i].produceAsmOutput());
        }            
        return sb.toString();
    }
    
    public byte[] produceBinaryOutput(int baseOffset){
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            output.write(0);
            output.write(ym6InDacMode?(byte)0:(byte)1);
            output.write(0);
            output.write(ymTimerBValue);
            byte[][] channelOutputBytes = new byte[10][];
            for(int i=0;i<10;i++){
                channelOutputBytes[i] = channels[i].produceBinaryOutput();
            }
            int[] channelPointers = new int[10];
            channelPointers[0] = baseOffset+1+1+1+1+2*10;
            for(int i=1;i<10;i++){
                channelPointers[i] = channelPointers[i-1]+channelOutputBytes[i-1].length;
            }
            for(int i=0;i<10;i++){
                output.write((byte)(channelPointers[i]&0xFF));
                output.write((byte)((channelPointers[i]>>8)&0xFF));
            }
            for(int i=0;i<10;i++){
                output.write(channelOutputBytes[i]);
            }            
        } catch (IOException ex) {
            Logger.getLogger(MusicEntry.class.getName()).log(Level.SEVERE, null, ex);
        }
        return output.toByteArray();
    }
    
}
