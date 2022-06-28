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
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Wiz
 */
public class BinaryMusicBankManager {
    
    public static final int BANK_SIZE = 0x8000;
       
    public static MusicEntry importMusicEntry(String filePath, int ptOffset, int index){
        MusicEntry me = new MusicEntry();
        try{
            File f = new File(filePath);
            byte[] data = Files.readAllBytes(Paths.get(f.getAbsolutePath()));
            int bankBaseOffset = ptOffset - (ptOffset % BANK_SIZE);
            index--;
            byte offsetLow = data[ptOffset + 2*index];
            byte offsetHigh = data[ptOffset + 2*index + 1];
            int offset = ((offsetHigh&0xFF)<<8) + (offsetLow&0xFF);
            int musicEntryOffset = bankBaseOffset + offset - BANK_SIZE;
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
        int offset = ((offsetHigh&0xFF)<<8) + (offsetLow&0xFF);
        int bankBaseOffset = musicEntryOffset - (musicEntryOffset % BANK_SIZE);
        int cursor = bankBaseOffset + offset - BANK_SIZE;
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
            
            System.out.println(ccs.get(ccs.size()-1).produceStringOutput());
            
            cursor+=cmdLength;
        }
        CubeCommand[] ccsArray = new CubeCommand[ccs.size()];
        return ccs.toArray(ccsArray);
    }
    
    public static CubeCommand[]  parseDacChannel(byte[] data, MusicEntry me, int musicEntryOffset){
        List<CubeCommand> ccs = new ArrayList();
        byte offsetLow = data[musicEntryOffset+4 + 2*5];
        byte offsetHigh = data[musicEntryOffset+4 + 2*5 + 1];
        int offset = ((offsetHigh&0xFF)<<8) + (offsetLow&0xFF);
        int bankBaseOffset = musicEntryOffset - (musicEntryOffset % BANK_SIZE);
        int cursor = bankBaseOffset + offset - BANK_SIZE;
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
            
            System.out.println(ccs.get(ccs.size()-1).produceStringOutput());
            
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
        int offset = ((offsetHigh&0xFF)<<8) + (offsetLow&0xFF);
        int bankBaseOffset = musicEntryOffset - (musicEntryOffset % BANK_SIZE);
        int cursor = bankBaseOffset + offset - BANK_SIZE;
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
            
            System.out.println(ccs.get(ccs.size()-1).produceStringOutput());
            
            cursor+=cmdLength;
        }
        CubeCommand[] ccsArray = new CubeCommand[ccs.size()];
        return ccs.toArray(ccsArray);
    }
    
    public static CubeCommand[]  parsePsgNoiseChannel(byte[] data, MusicEntry me, int musicEntryOffset){
        List<CubeCommand> ccs = new ArrayList();
        byte offsetLow = data[musicEntryOffset+4 + 6*2 + 2*3];
        byte offsetHigh = data[musicEntryOffset+4 + 6*2 + 2*3 + 1];
        int offset = ((offsetHigh&0xFF)<<8) + (offsetLow&0xFF);
        int bankBaseOffset = musicEntryOffset - (musicEntryOffset % BANK_SIZE);
        int cursor = bankBaseOffset + offset - BANK_SIZE;
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
            
            System.out.println(ccs.get(ccs.size()-1).produceStringOutput());
            
            cursor+=cmdLength;
        }
        CubeCommand[] ccsArray = new CubeCommand[ccs.size()];
        return ccs.toArray(ccsArray);
    }
/*
    for(i=1;i<=32;i++){
        addr = 0x8000+(i-1)*2;
        MakeWord(addr);
        target = Word(addr);
        MakeNameEx(target,form("Music_%s",ltoa(i,10)),0);
        OpOff(addr,0,0);
        Message(form("\n  Music %d at 0x%s...",i,ltoa(target,16)));    
        parseMusic(target,target+0x1000,i);
        Message(" DONE.\n");
  
    }    
    MakeArray(0xFE37,457);        
    SetManualInsn(0xFE37,"align 8000h");
}


static parseMusic(start,end,index){

    auto ea,channelsPointerTable,channel,channelStart,ymChannel,psgChannel,addr,target,cmd,cmdLength,cmdName,cmdComment,i,action;
    auto textIndex,flag;
    auto dac;
    
    cmdLength = 1;
    ea = start;
    
    action = 1;
    
    Message(form("\Music starting from 0x%s ...",ltoa(ea,16)));
    

    
    MakeByte(ea);
    SetManualInsn(ea,"");
    MakeByte(ea+1);
    SetManualInsn(ea+1,"");
    MakeByte(ea+2);
    SetManualInsn(ea+2,"");
    MakeByte(ea+3);
    SetManualInsn(ea+3,"");
    
    dac = Byte(ea+1);
    
    ea = ea+4;
    
    channelsPointerTable = ea;
    
    for(channel=0;channel<10;channel++){
        addr = ea+channel*2;
        target = Word(addr);
        MakeUnknown(addr,2,DOUNK_SIMPLE);
        MakeWord(addr);
        SetManualInsn(addr,"");
        MakeNameEx(target,form("Music_%d_Channel_%d",index,channel),0);
        OpOff(addr,0,0);
        Message(form("\n  Music %d channel %d at 0x%s",index,channel,ltoa(target,16)));
    }
    
    for(ymChannel=0;ymChannel<6;ymChannel++){
        channelStart = Word(channelsPointerTable+ymChannel*2);
        ea = channelStart;
        while(action==1){
            //Jump(ea);
            cmd = Byte(ea);
            if(strstr(GetTrueName(ea),form("Music_%d_Channel_%d",index,ymChannel))==-1 && strstr(GetTrueName(ea),"Music")!=-1){
                break;
            }
            if(cmd==0xFF){
                cmdLength = 3;
                if(Word(ea+1)==0){
                    MakeData(ea,FF_BYTE,cmdLength,1);
                    SetManualInsn(ea,"channel_end");
                    break;
                }else{
                    if(Byte(ea+2)==0){
                        MakeData(ea,FF_BYTE,cmdLength,1);
                        SetManualInsn(ea,form("newSoundCommand 0%sh",ltoa(Byte(ea+1),16)));
                        Jump(ea);
                        AskYN(1,"Found a newSoundCommand command !");
                        break;                        
                    }else{
                        MakeData(ea,FF_BYTE,cmdLength,1);            
                        if(GetTrueName(Word(ea+1))==""){
                            MakeNameEx(Word(ea+1),form("jump_%s",ltoa(Word(ea+1),16)),0);
                        }
                        SetManualInsn(ea,form("jump $%s",GetTrueName(Word(ea+1))));
                        Jump(ea);
                        AskYN(1,"Found a jump command !");
                        break;
                    }
                }
            }else if(cmd==0xFE){
                cmdLength = 2;
                MakeUnknown(ea,cmdLength,DOUNK_SIMPLE);
                MakeData(ea,FF_BYTE,cmdLength,1);
                SetManualInsn(ea,form("  inst %s",ltoa(Byte(ea+1),10)));
            }else if(cmd==0xFD){
                cmdLength = 2;
                MakeUnknown(ea,cmdLength,DOUNK_SIMPLE);
                MakeData(ea,FF_BYTE,cmdLength,1);
                SetManualInsn(ea,form("  vol 0%sh",ltoa(Byte(ea+1),16)));
            }else if(cmd==0xFC){
                cmdLength = 2;
                MakeUnknown(ea,cmdLength,DOUNK_SIMPLE);
                MakeData(ea,FF_BYTE,cmdLength,1);
                if(Byte(ea+1)<0x80){
                    SetManualInsn(ea,form("  setRelease 0%sh",ltoa(Byte(ea+1),16)));
                }else if(Byte(ea+1)==0x80){
                    SetManualInsn(ea,"  sustain");
                }else if(Byte(ea+1)<0xFF){
                    SetManualInsn(ea,form("  setSlide 0%sh",ltoa(Byte(ea+1)-0x80,16)));
                }else {
                    SetManualInsn(ea,"  noSlide");
                }
            }else if(cmd==0xFB){
                cmdLength = 2;
                MakeUnknown(ea,cmdLength,DOUNK_SIMPLE);
                MakeData(ea,FF_BYTE,cmdLength,1);
                SetManualInsn(ea,form("  vibrato 0%sh",ltoa(Byte(ea+1),16)));
            }else if(cmd==0xFA){
                cmdLength = 2;
                MakeUnknown(ea,cmdLength,DOUNK_SIMPLE);
                MakeData(ea,FF_BYTE,cmdLength,1);
                SetManualInsn(ea,form("  stereo 0%sh",ltoa(Byte(ea+1),16)));
            }else if(cmd==0xF9){
                cmdLength = 2;
                MakeUnknown(ea,cmdLength,DOUNK_SIMPLE);
                MakeData(ea,FF_BYTE,cmdLength,1);
                SetManualInsn(ea,form("  shifting 0%sh",ltoa(Byte(ea+1),16)));
            }else if(cmd==0xF8){
                cmdLength = 2;
                MakeUnknown(ea,cmdLength,DOUNK_SIMPLE);
                MakeData(ea,FF_BYTE,cmdLength,1);
                if(Byte(ea+1)==0x00){
                    SetManualInsn(ea,"mainLoopStart");
                }else if(Byte(ea+1)==0xA1){
                    SetManualInsn(ea,"mainLoopEnd");
                }else if(Byte(ea+1)==0x20){
                    SetManualInsn(ea,"repeatStart");
                }else if(Byte(ea+1)==0xA0){
                    SetManualInsn(ea,"repeatEnd");
                }else if(Byte(ea+1)==0x40){
                    SetManualInsn(ea,"repeatSection1Start");
                }else if(Byte(ea+1)==0x60){
                    SetManualInsn(ea,"repeatSection2Start");
                }else if(Byte(ea+1)==0x80){
                    SetManualInsn(ea,"repeatSection3Start");
                }else if(Byte(ea+1)==0xE0){
                    SetManualInsn(ea,"countedLoopEnd");
                }else{
                    SetManualInsn(ea,form("countedLoopStart %s",ltoa(Byte(ea+1)-0xC0,10)));
                }                
            }else if(cmd==0xF0){
                cmdLength = 2;
                MakeUnknown(ea,cmdLength,DOUNK_SIMPLE);
                MakeData(ea,FF_BYTE,cmdLength,1);
                SetManualInsn(ea,form("        waitL %s",ltoa(Byte(ea+1),10)));
            }else if(cmd==0x70){
                cmdLength = 1;
                MakeUnknown(ea,cmdLength,DOUNK_SIMPLE);
                MakeData(ea,FF_BYTE,cmdLength,1);
                SetManualInsn(ea,"        wait");
            }else if(cmd>=0x80){
                cmdLength = 2;
                MakeUnknown(ea,cmdLength,DOUNK_SIMPLE);
                MakeData(ea,FF_BYTE,cmdLength,1);
                if(ymChannel==5 && dac==0){
                    SetManualInsn(ea,form("        sampleL %s,%s",ltoa(Byte(ea)-0x80,10),ltoa(Byte(ea+1),10)));
                }else{
                    SetManualInsn(ea,form("        noteL %s,%s",getYmNote(Byte(ea)-0x80),ltoa(Byte(ea+1),10)));
                }
            }else {
                cmdLength = 1;
                MakeUnknown(ea,cmdLength,DOUNK_SIMPLE);
                MakeData(ea,FF_BYTE,cmdLength,1);
                if(ymChannel==5 && dac==0){
                    SetManualInsn(ea,form("        sample  %s",ltoa(Byte(ea),10)));
                }else{
                    SetManualInsn(ea,form("        note  %s",getYmNote(Byte(ea))));
                }
            }
        
        
        
            //action = AskYN(1,"Continue ?");
            ea = ea + cmdLength;
        }
    }    
    
    for(psgChannel=0;psgChannel<4;psgChannel++){
        channelStart = Word(channelsPointerTable+6*2+psgChannel*2);
        ea = channelStart;
        Message(form("\nChannel 0x%s",ltoa(channelStart,16)));
        while(action==1){
            //Jump(ea);
            cmd = Byte(ea);
            if(strstr(GetTrueName(ea),form("Music_%d_Channel_%d",index,psgChannel+6))==-1 && strstr(GetTrueName(ea),"Music")!=-1){
                break;
            }
            if(cmd==0xFF){
                cmdLength = 3;
                if(Word(ea+1)==0){
                    MakeData(ea,FF_BYTE,cmdLength,1);
                    SetManualInsn(ea,"channel_end");
                    break;
                }else{
                    if(Byte(ea+2)==0){
                        MakeData(ea,FF_BYTE,cmdLength,1);
                        SetManualInsn(ea,form("newSoundCommand 0%sh",ltoa(Byte(ea+1),16)));
                        Jump(ea);
                        AskYN(1,"Found a newSoundCommand command !");
                        break;                        
                    }else{
                        MakeData(ea,FF_BYTE,cmdLength,1);            
                        if(GetTrueName(Word(ea+1))==""){
                            MakeNameEx(Word(ea+1),form("jump_%s",ltoa(Word(ea+1),16)),0);
                        }
                        SetManualInsn(ea,form("jump $%s",GetTrueName(Word(ea+1))));
                        Jump(ea);
                        AskYN(1,"Found a jump command !");
                        break;
                    }
                }
            }else if(cmd==0xFD){
                cmdLength = 2;
                MakeUnknown(ea,cmdLength,DOUNK_SIMPLE);
                MakeData(ea,FF_BYTE,cmdLength,1);
                SetManualInsn(ea,form("  psgInst 0%sh",ltoa(Byte(ea+1),16)));
            }else if(cmd==0xFC){
                cmdLength = 2;
                MakeUnknown(ea,cmdLength,DOUNK_SIMPLE);
                MakeData(ea,FF_BYTE,cmdLength,1);
                SetManualInsn(ea,form("  setRelease 0%sh",ltoa(Byte(ea+1),16)));
            }else if(cmd==0xFB){
                cmdLength = 2;
                MakeUnknown(ea,cmdLength,DOUNK_SIMPLE);
                MakeData(ea,FF_BYTE,cmdLength,1);
                SetManualInsn(ea,form("  vibrato 0%sh",ltoa(Byte(ea+1),16)));
            }else if(cmd==0xFA){
                cmdLength = 2;
                MakeUnknown(ea,cmdLength,DOUNK_SIMPLE);
                MakeData(ea,FF_BYTE,cmdLength,1);
                SetManualInsn(ea,form("  ymTimer 0%sh",ltoa(Byte(ea+1),16)));
            }else if(cmd==0xF9){
                cmdLength = 2;
                MakeUnknown(ea,cmdLength,DOUNK_SIMPLE);
                MakeData(ea,FF_BYTE,cmdLength,1);
                SetManualInsn(ea,form("  shifting 0%sh",ltoa(Byte(ea+1),16)));
            }else if(cmd==0xF8){
                cmdLength = 2;
                MakeUnknown(ea,cmdLength,DOUNK_SIMPLE);
                MakeData(ea,FF_BYTE,cmdLength,1);
                if(Byte(ea+1)==0x00){
                    SetManualInsn(ea,"mainLoopStart");
                }else if(Byte(ea+1)==0xA1){
                    SetManualInsn(ea,"mainLoopEnd");
                }else if(Byte(ea+1)==0x20){
                    SetManualInsn(ea,"repeatStart");
                }else if(Byte(ea+1)==0xA0){
                    SetManualInsn(ea,"repeatEnd");
                }else if(Byte(ea+1)==0x40){
                    SetManualInsn(ea,"repeatSection1Start");
                }else if(Byte(ea+1)==0x60){
                    SetManualInsn(ea,"repeatSection2Start");
                }else if(Byte(ea+1)==0x80){
                    SetManualInsn(ea,"repeatSection3Start");
                }else if(Byte(ea+1)==0xE0){
                    SetManualInsn(ea,"countedLoopEnd");
                }else{
                    SetManualInsn(ea,form("countedLoopStart %s",ltoa(Byte(ea+1)-0xC0,10)));
                }    
            }else if(cmd==0xF0){
                cmdLength = 2;
                MakeUnknown(ea,cmdLength,DOUNK_SIMPLE);
                MakeData(ea,FF_BYTE,cmdLength,1);
                SetManualInsn(ea,form("        waitL    %s",ltoa(Byte(ea+1),10)));
            }else if(cmd==0x70){
                cmdLength = 1;
                MakeUnknown(ea,cmdLength,DOUNK_SIMPLE);
                MakeData(ea,FF_BYTE,cmdLength,1);
                SetManualInsn(ea,"        wait");
            }else if(cmd>=0x80){
                cmdLength = 2;
                MakeUnknown(ea,cmdLength,DOUNK_SIMPLE);
                MakeData(ea,FF_BYTE,cmdLength,1);
                SetManualInsn(ea,form("        psgNoteL %s,%s",getPsgNote(Byte(ea)-0x80),ltoa(Byte(ea+1),10)));
            }else {
                cmdLength = 1;
                MakeUnknown(ea,cmdLength,DOUNK_SIMPLE);
                MakeData(ea,FF_BYTE,cmdLength,1);
                SetManualInsn(ea,form("        psgNote  %s",getPsgNote(Byte(ea))));
            }
        
        
        
            //action = AskYN(1,"Continue ?");
            ea = ea + cmdLength;
        }
    }

*/    
    
    
    
    
    
}
