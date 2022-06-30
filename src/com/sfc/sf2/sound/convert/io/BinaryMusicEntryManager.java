/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.sound.convert.io;

import com.sfc.sf2.sound.convert.io.cube.CubeCommand;
import com.sfc.sf2.sound.convert.io.cube.MusicEntry;
import com.sfc.sf2.sound.convert.io.cube.Pitch;
import com.sfc.sf2.sound.convert.io.cube.command.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Wiz
 */
public class BinaryMusicEntryManager {
       
    public static void exportMusicEntryAsBinary(MusicEntry me, String filePath){
    
        try {
            System.out.println("com.sfc.sf2.sound.convert.io.BinaryMusicEntryManager.exportMusicEntryAsBinary() - Writing file ...");
            File file = new File(filePath);
            Path path = Paths.get(file.getAbsolutePath());
            byte[] data = me.produceBinaryOutput();
            Files.write(path,data);
            System.out.println(data.length + " bytes into " + filePath);  
            System.out.println("com.sfc.sf2.sound.convert.io.BinaryMusicEntryManager.exportMusicEntryAsBinary() - File written.");
        } catch (IOException ex) {
            Logger.getLogger(BinaryMusicEntryManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }
       
    public static MusicEntry importMusicEntry(String filePath){
        MusicEntry me = new MusicEntry();
        try{
            File f = new File(filePath);
            byte[] data = Files.readAllBytes(Paths.get(f.getAbsolutePath()));
            int musicEntryOffset = 0;
            if(data[musicEntryOffset+1]==0){
                me.setYm6InDacMode(true);
            }else{
                me.setYm6InDacMode(false);
            }
            me.setYmTimerBValue(data[musicEntryOffset+3]);
            System.out.println("YM1:");
            me.setYm1ChannelData(parseYmChannel(data, me, musicEntryOffset, 1));
            System.out.println("YM2:");
            me.setYm2ChannelData(parseYmChannel(data, me, musicEntryOffset, 2));
            System.out.println("YM3:");
            me.setYm3ChannelData(parseYmChannel(data, me, musicEntryOffset, 3));
            System.out.println("YM4:");
            me.setYm4ChannelData(parseYmChannel(data, me, musicEntryOffset, 4));
            System.out.println("YM5:");
            me.setYm5ChannelData(parseYmChannel(data, me, musicEntryOffset, 5));
            System.out.println("YM6:");
            if(me.isYm6InDacMode()){
                me.setYm6ChannelData(parseDacChannel(data, me, musicEntryOffset));
            }else{
                me.setYm6ChannelData(parseYmChannel(data, me, musicEntryOffset, 6));
            }
            System.out.println("PSG1:");
            me.setPsgTone1ChannelData(parsePsgToneChannel(data, me, musicEntryOffset, 1));
            System.out.println("PSG2:");
            me.setPsgTone2ChannelData(parsePsgToneChannel(data, me, musicEntryOffset, 2));
            System.out.println("PSG3:");
            me.setPsgTone3ChannelData(parsePsgToneChannel(data, me, musicEntryOffset, 3));
            System.out.println("PSGN:");
            me.setPsgNoiseChannelData(parsePsgNoiseChannel(data, me, musicEntryOffset));
        } catch (IOException ex) {
            Logger.getLogger(BinaryMusicBankManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return me;
    }
    
    public static CubeCommand[] parseYmChannel(byte[] data, MusicEntry me, int musicEntryOffset, int channelIndex){
        List<CubeCommand> ccs = new ArrayList();
        channelIndex--;
        byte offsetLow = data[musicEntryOffset+4 + 2*channelIndex];
        byte offsetHigh = data[musicEntryOffset+4 + 2*channelIndex + 1];
        int cursor = ((offsetHigh&0xFF)<<8) + (offsetLow&0xFF);
        int cmdLength = 1;
        while(ccs.isEmpty() || (!(ccs.get(ccs.size()-1) instanceof ChannelEnd) && !(ccs.get(ccs.size()-1) instanceof MainLoopEnd))){
            byte cmd = data[cursor];
            
            if((cmd&0xFF)==0xFF){
                cmdLength = 3;
                byte b1 = data[cursor+1];
                byte b2 = data[cursor+2];
                if((b1&0xFF)==0&&(b2&0xFF)==0){
                    ccs.add(new ChannelEnd());
                }else{
                    System.out.println("Unsupported command at offset 0x"+Integer.toHexString(cursor)+" : $"+String.format("%02x", cmd)+String.format("%02x", b1)+String.format("%02x", b2));
                }
            }else if((cmd&0xFF)==0xFE){
                cmdLength = 2;
                byte b1 = data[cursor+1];
                ccs.add(new Inst(b1));
            }else if((cmd&0xFF)==0xFD){
                cmdLength = 2;
                byte b1 = data[cursor+1];
                ccs.add(new Vol(b1));
            }else if((cmd&0xFF)==0xFC){
                cmdLength = 2;
                byte b1 = data[cursor+1];
                if((b1&0xFF)<0x80){
                    ccs.add(new SetRelease(b1));
                }else if((b1&0xFF)==0x80){
                    ccs.add(new Sustain());
                }else if((b1&0xFF)<0xFF){
                    ccs.add(new SetSlide(b1));
                }else {
                    ccs.add(new NoSlide());
                }
            }else if((cmd&0xFF)==0xFB){
                cmdLength = 2;
                byte b1 = data[cursor+1];
                ccs.add(new Vibrato(b1));
            }else if((cmd&0xFF)==0xFA){
                cmdLength = 2;
                byte b1 = data[cursor+1];
                ccs.add(new Stereo(b1));
            }else if((cmd&0xFF)==0xF9){
                cmdLength = 2;
                byte b1 = data[cursor+1];
                ccs.add(new Shifting(b1));
            }else if((cmd&0xFF)==0xF8){
                cmdLength = 2;
                byte b1 = data[cursor+1];
                if((b1&0xFF)==0x00){
                    ccs.add(new MainLoopStart());
                }else if((b1&0xFF)==0xA1){
                    ccs.add(new MainLoopEnd());
                }else if((b1&0xFF)==0x20){
                    ccs.add(new RepeatStart());
                }else if((b1&0xFF)==0xA0){
                    ccs.add(new RepeatEnd());
                }else if((b1&0xFF)==0x40){
                    ccs.add(new RepeatSection1Start());
                }else if((b1&0xFF)==0x60){
                    ccs.add(new RepeatSection2Start());
                }else if((b1&0xFF)==0x80){
                    ccs.add(new RepeatSection3Start());
                }else if((b1&0xFF)==0xE0){
                    ccs.add(new CountedLoopEnd());
                }else{
                    ccs.add(new CountedLoopStart((byte)((b1&0xFF)-0xC0)));
                }                
            }else if((cmd&0xFF)==0xF0){
                cmdLength = 2;
                byte b1 = data[cursor+1];
                ccs.add(new WaitL(b1));
            }else if((cmd&0xFF)==0x70){
                cmdLength = 1;
                ccs.add(new Wait());
            }else if((cmd&0xFF)>=0x80){
                cmdLength = 2;
                byte b1 = data[cursor+1];
                ccs.add(new NoteL(Pitch.valueOf((cmd&0x7F)+24),b1));
            }else {
                cmdLength = 1;
                ccs.add(new Note(Pitch.valueOf((cmd&0xFF)+24)));
            }            
            
            System.out.println(ccs.get(ccs.size()-1).produceAsmOutput());
            
            cursor+=cmdLength;
        }
        CubeCommand[] ccsArray = new CubeCommand[ccs.size()];
        return ccs.toArray(ccsArray);
    }
    
    public static CubeCommand[]  parseDacChannel(byte[] data, MusicEntry me, int musicEntryOffset){
        List<CubeCommand> ccs = new ArrayList();
        byte offsetLow = data[musicEntryOffset+4 + 2*5];
        byte offsetHigh = data[musicEntryOffset+4 + 2*5 + 1];
        int cursor = ((offsetHigh&0xFF)<<8) + (offsetLow&0xFF);
        int cmdLength = 1;
        while(ccs.isEmpty() || (!(ccs.get(ccs.size()-1) instanceof ChannelEnd) && !(ccs.get(ccs.size()-1) instanceof MainLoopEnd))){
            byte cmd = data[cursor];
            
            if((cmd&0xFF)==0xFF){
                cmdLength = 3;
                byte b1 = data[cursor+1];
                byte b2 = data[cursor+2];
                if((b1&0xFF)==0&&(b2&0xFF)==0){
                    ccs.add(new ChannelEnd());
                }else{
                    System.out.println("Unsupported command at offset 0x"+Integer.toHexString(cursor)+" : $"+String.format("%02x", cmd)+String.format("%02x", b1)+String.format("%02x", b2));
                }
            }else if((cmd&0xFF)==0xFE){
                cmdLength = 2;
                byte b1 = data[cursor+1];
                ccs.add(new Inst(b1));
            }else if((cmd&0xFF)==0xFD){
                cmdLength = 2;
                byte b1 = data[cursor+1];
                ccs.add(new Vol(b1));
            }else if((cmd&0xFF)==0xFC){
                cmdLength = 2;
                byte b1 = data[cursor+1];
                if((b1&0xFF)<0x80){
                    ccs.add(new SetRelease(b1));
                }else if((b1&0xFF)==0x80){
                    ccs.add(new Sustain());
                }else if((b1&0xFF)<0xFF){
                    ccs.add(new SetSlide(b1));
                }else {
                    ccs.add(new NoSlide());
                }
            }else if((cmd&0xFF)==0xFB){
                cmdLength = 2;
                byte b1 = data[cursor+1];
                ccs.add(new Vibrato(b1));
            }else if((cmd&0xFF)==0xFA){
                cmdLength = 2;
                byte b1 = data[cursor+1];
                ccs.add(new Stereo(b1));
            }else if((cmd&0xFF)==0xF9){
                cmdLength = 2;
                byte b1 = data[cursor+1];
                ccs.add(new Shifting(b1));
            }else if((cmd&0xFF)==0xF8){
                cmdLength = 2;
                byte b1 = data[cursor+1];
                if((b1&0xFF)==0x00){
                    ccs.add(new MainLoopStart());
                }else if((b1&0xFF)==0xA1){
                    ccs.add(new MainLoopEnd());
                }else if((b1&0xFF)==0x20){
                    ccs.add(new RepeatStart());
                }else if((b1&0xFF)==0xA0){
                    ccs.add(new RepeatEnd());
                }else if((b1&0xFF)==0x40){
                    ccs.add(new RepeatSection1Start());
                }else if((b1&0xFF)==0x60){
                    ccs.add(new RepeatSection2Start());
                }else if((b1&0xFF)==0x80){
                    ccs.add(new RepeatSection3Start());
                }else if((b1&0xFF)==0xE0){
                    ccs.add(new CountedLoopEnd());
                }else{
                    ccs.add(new CountedLoopStart((byte)((b1&0xFF)-0xC0)));
                }                
            }else if((cmd&0xFF)==0xF0){
                cmdLength = 2;
                byte b1 = data[cursor+1];
                ccs.add(new WaitL(b1));
            }else if((cmd&0xFF)==0x70){
                cmdLength = 1;
                ccs.add(new Wait());
            }else if((cmd&0xFF)>=0x80){
                cmdLength = 2;
                byte b1 = data[cursor+1];
                ccs.add(new SampleL((byte)(cmd&0x7F),b1));
            }else {
                cmdLength = 1;
                ccs.add(new Sample(cmd));
            }            
            
            System.out.println(ccs.get(ccs.size()-1).produceAsmOutput());
            
            cursor+=cmdLength;
        }
        CubeCommand[] ccsArray = new CubeCommand[ccs.size()];
        return ccs.toArray(ccsArray);
    }
    
    public static CubeCommand[]  parsePsgToneChannel(byte[] data, MusicEntry me, int musicEntryOffset, int channelIndex){
        List<CubeCommand> ccs = new ArrayList();
        channelIndex--;
        byte offsetLow = data[musicEntryOffset+4 + 6*2 + 2*channelIndex];
        byte offsetHigh = data[musicEntryOffset+4 + 6*2 + 2*channelIndex + 1];
        int cursor = ((offsetHigh&0xFF)<<8) + (offsetLow&0xFF);
        int cmdLength = 1;
        while(ccs.isEmpty() || (!(ccs.get(ccs.size()-1) instanceof ChannelEnd) && !(ccs.get(ccs.size()-1) instanceof MainLoopEnd))){
            byte cmd = data[cursor];
            
            if((cmd&0xFF)==0xFF){
                cmdLength = 3;
                byte b1 = data[cursor+1];
                byte b2 = data[cursor+2];
                if((b1&0xFF)==0&&(b2&0xFF)==0){
                    ccs.add(new ChannelEnd());
                }else{
                    System.out.println("Unsupported command at offset 0x"+Integer.toHexString(cursor)+" : $"+String.format("%02x", cmd)+String.format("%02x", b1)+String.format("%02x", b2));
                }
            }else if((cmd&0xFF)==0xFD){
                cmdLength = 2;
                byte b1 = data[cursor+1];
                ccs.add(new PsgInst(b1));
            }else if((cmd&0xFF)==0xFC){
                cmdLength = 2;
                byte b1 = data[cursor+1];
                if((b1&0xFF)<0x80){
                    ccs.add(new SetRelease(b1));
                }else if((b1&0xFF)==0x80){
                    ccs.add(new Sustain());
                }else if((b1&0xFF)<0xFF){
                    ccs.add(new SetSlide(b1));
                }else {
                    ccs.add(new NoSlide());
                }
            }else if((cmd&0xFF)==0xFB){
                cmdLength = 2;
                byte b1 = data[cursor+1];
                ccs.add(new Vibrato(b1));
            }else if((cmd&0xFF)==0xFA){
                cmdLength = 2;
                byte b1 = data[cursor+1];
                ccs.add(new Stereo(b1));
            }else if((cmd&0xFF)==0xF9){
                cmdLength = 2;
                byte b1 = data[cursor+1];
                ccs.add(new Shifting(b1));
            }else if((cmd&0xFF)==0xF8){
                cmdLength = 2;
                byte b1 = data[cursor+1];
                if((b1&0xFF)==0x00){
                    ccs.add(new MainLoopStart());
                }else if((b1&0xFF)==0xA1){
                    ccs.add(new MainLoopEnd());
                }else if((b1&0xFF)==0x20){
                    ccs.add(new RepeatStart());
                }else if((b1&0xFF)==0xA0){
                    ccs.add(new RepeatEnd());
                }else if((b1&0xFF)==0x40){
                    ccs.add(new RepeatSection1Start());
                }else if((b1&0xFF)==0x60){
                    ccs.add(new RepeatSection2Start());
                }else if((b1&0xFF)==0x80){
                    ccs.add(new RepeatSection3Start());
                }else if((b1&0xFF)==0xE0){
                    ccs.add(new CountedLoopEnd());
                }else{
                    ccs.add(new CountedLoopStart((byte)((b1&0xFF)-0xC0)));
                }                
            }else if((cmd&0xFF)==0xF0){
                cmdLength = 2;
                byte b1 = data[cursor+1];
                ccs.add(new WaitL(b1));
            }else if((cmd&0xFF)==0x70){
                cmdLength = 1;
                ccs.add(new Wait());
            }else if((cmd&0xFF)>=0x80){
                cmdLength = 2;
                byte b1 = data[cursor+1];
                ccs.add(new PsgNoteL(Pitch.valueOf(cmd&0x7F),b1));
            }else {
                cmdLength = 1;
                ccs.add(new PsgNote(Pitch.valueOf(cmd&0xFF)));
            }            
            
            System.out.println(ccs.get(ccs.size()-1).produceAsmOutput());
            
            cursor+=cmdLength;
        }
        CubeCommand[] ccsArray = new CubeCommand[ccs.size()];
        return ccs.toArray(ccsArray);
    }
    
    public static CubeCommand[]  parsePsgNoiseChannel(byte[] data, MusicEntry me, int musicEntryOffset){
        List<CubeCommand> ccs = new ArrayList();
        byte offsetLow = data[musicEntryOffset+4 + 6*2 + 2*3];
        byte offsetHigh = data[musicEntryOffset+4 + 6*2 + 2*3 + 1];
        int cursor = ((offsetHigh&0xFF)<<8) + (offsetLow&0xFF);
        int cmdLength = 1;
        while(ccs.isEmpty() || (!(ccs.get(ccs.size()-1) instanceof ChannelEnd) && !(ccs.get(ccs.size()-1) instanceof MainLoopEnd))){
            byte cmd = data[cursor];
            
            if((cmd&0xFF)==0xFF){
                cmdLength = 3;
                byte b1 = data[cursor+1];
                byte b2 = data[cursor+2];
                if((b1&0xFF)==0&&(b2&0xFF)==0){
                    ccs.add(new ChannelEnd());
                }else{
                    System.out.println("Unsupported command at offset 0x"+Integer.toHexString(cursor)+" : $"+String.format("%02x", cmd)+String.format("%02x", b1)+String.format("%02x", b2));
                }
            }else if((cmd&0xFF)==0xFD){
                cmdLength = 2;
                byte b1 = data[cursor+1];
                ccs.add(new PsgInst(b1));
            }else if((cmd&0xFF)==0xFC){
                cmdLength = 2;
                byte b1 = data[cursor+1];
                if((b1&0xFF)<0x80){
                    ccs.add(new SetRelease(b1));
                }else if((b1&0xFF)==0x80){
                    ccs.add(new Sustain());
                }else if((b1&0xFF)<0xFF){
                    ccs.add(new SetSlide(b1));
                }else {
                    ccs.add(new NoSlide());
                }
            }else if((cmd&0xFF)==0xF8){
                cmdLength = 2;
                byte b1 = data[cursor+1];
                if((b1&0xFF)==0x00){
                    ccs.add(new MainLoopStart());
                }else if((b1&0xFF)==0xA1){
                    ccs.add(new MainLoopEnd());
                }else if((b1&0xFF)==0x20){
                    ccs.add(new RepeatStart());
                }else if((b1&0xFF)==0xA0){
                    ccs.add(new RepeatEnd());
                }else if((b1&0xFF)==0x40){
                    ccs.add(new RepeatSection1Start());
                }else if((b1&0xFF)==0x60){
                    ccs.add(new RepeatSection2Start());
                }else if((b1&0xFF)==0x80){
                    ccs.add(new RepeatSection3Start());
                }else if((b1&0xFF)==0xE0){
                    ccs.add(new CountedLoopEnd());
                }else{
                    ccs.add(new CountedLoopStart((byte)((b1&0xFF)-0xC0)));
                }                
            }else if((cmd&0xFF)==0xF0){
                cmdLength = 2;
                byte b1 = data[cursor+1];
                ccs.add(new WaitL(b1));
            }else if((cmd&0xFF)==0x70){
                cmdLength = 1;
                ccs.add(new Wait());
            }else if((cmd&0xFF)>=0x80){
                cmdLength = 2;
                byte b1 = data[cursor+1];
                ccs.add(new PsgNoteL(Pitch.valueOf(cmd&0xFF),b1));
            }else {
                cmdLength = 1;
                ccs.add(new PsgNote(Pitch.valueOf(cmd&0xFF)));
            }            
            
            System.out.println(ccs.get(ccs.size()-1).produceAsmOutput());
            
            cursor+=cmdLength;
        }
        CubeCommand[] ccsArray = new CubeCommand[ccs.size()];
        return ccs.toArray(ccsArray);
    }
    
    
}
