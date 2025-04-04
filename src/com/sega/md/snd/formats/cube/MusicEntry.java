/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sega.md.snd.formats.cube;

import static com.sega.md.snd.convert.io.CubeBinaryManager.BANK_SIZE;
import com.sega.md.snd.formats.cube.channel.DacChannel;
import com.sega.md.snd.formats.cube.channel.PsgNoiseChannel;
import com.sega.md.snd.formats.cube.channel.PsgToneChannel;
import com.sega.md.snd.formats.cube.channel.YmChannel;
import com.sega.md.snd.formats.cube.command.Inst;
import com.sega.md.snd.formats.cube.command.MainLoopStart;
import com.sega.md.snd.formats.cube.command.Note;
import com.sega.md.snd.formats.cube.command.NoteL;
import com.sega.md.snd.formats.cube.command.PsgInst;
import com.sega.md.snd.formats.cube.command.RepeatStart;
import com.sega.md.snd.formats.cube.command.Sample;
import com.sega.md.snd.formats.cube.command.SampleL;
import com.sega.md.snd.formats.cube.command.Vibrato;
import com.sega.md.snd.formats.cube.command.Wait;
import com.sega.md.snd.formats.cube.command.WaitL;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Wiz
 */
public class MusicEntry {
    
    public static final int BANK_SIZE = 0x8000;
    public static final int YM_LEVELS_LENGTH = 16;
    public static final int YM_INSTRUMENT_SIZE = 29;
    public static final int YM_INSTRUMENT_SIZE_NOSSGEG = 25;
    public static final int YM_INSTRUMENT_CHUNK_SIZE = 16;
    public static final int PSG_INSTRUMENT_CHUNK_SIZE = 8;
    public static final int PITCH_EFFECT_CHUNK_SIZE = 8;
    
    
    String name;
    boolean ym6InDacMode = false;
    byte ymTimerBValue = 0;
    int ymTimerBIncrement = 0;
    CubeChannel[] channels = new CubeChannel[10];
    byte[][] pitchEffects;
    byte[] ymLevels;
    byte[][] ymInstruments;
    byte[][] psgInstruments;
    boolean ssgEgAvailable = true;
    byte[][] sampleEntries;
    boolean multiSampleBank = true;
    byte[][] sampleBanks;

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
    
    public MusicEntry(byte [] data, int entryOffset, int baseOffset, int driverOffset, int pitchEffectsOffset, int ymLevelsOffset, int ymInstOffset, int psgInstOffset, boolean ssgEg, int ymTimerBIncrement){
        this(data, entryOffset, baseOffset);
        this.ymTimerBIncrement = ymTimerBIncrement;
        this.setSsgEgAvailable(ssgEg);
        if(pitchEffectsOffset!=0){
            int maxPitchEffectIndex = 0;
            int pitchEffectIndex = findMaxPitchEffectIndex();
            if(pitchEffectIndex>maxPitchEffectIndex){
                maxPitchEffectIndex = pitchEffectIndex;
            }
            int chunkCount = (maxPitchEffectIndex / PITCH_EFFECT_CHUNK_SIZE) + 1;
            maxPitchEffectIndex = chunkCount * PITCH_EFFECT_CHUNK_SIZE;
            pitchEffects = new byte[maxPitchEffectIndex][];
            for(int i=0;i<maxPitchEffectIndex;i++){
                try{         
                    int pointer = (((data[pitchEffectsOffset + 2*i + 1])&0x7F)<<8) + ((data[pitchEffectsOffset + 2*i])&0xFF);
                    int pitchEffectOffset = driverOffset + pointer;
                    int pitchEffectEndOffset = pitchEffectOffset;
                    while((data[pitchEffectEndOffset]&0xFF)!=0x80){
                        pitchEffectEndOffset++;
                    }
                    pitchEffects[i] = Arrays.copyOfRange(data, pitchEffectOffset, pitchEffectEndOffset);
                }catch(Exception e){
                    e.printStackTrace();
                    pitchEffects = Arrays.copyOfRange(pitchEffects, 0, i);
                    break;
                }
            } 
        }
        if(ymLevelsOffset!=0){
            ymLevels = Arrays.copyOfRange(data, ymLevelsOffset, ymLevelsOffset+YM_LEVELS_LENGTH);
        }
        if(ymInstOffset!=0){
            int instrumentSize = ssgEg?YM_INSTRUMENT_SIZE:YM_INSTRUMENT_SIZE_NOSSGEG;
            int maxYmInstrumentIndex = 0;
            int ymInstrumentIndex = findMaxYmInstrumentIndex();
            if(ymInstrumentIndex>maxYmInstrumentIndex){
                maxYmInstrumentIndex = ymInstrumentIndex;
            }
            System.out.println("maxYmInstrumentIndex="+maxYmInstrumentIndex);
            int chunkCount = (maxYmInstrumentIndex / YM_INSTRUMENT_CHUNK_SIZE) + 1;
            maxYmInstrumentIndex = chunkCount * YM_INSTRUMENT_CHUNK_SIZE;
            ymInstruments = new byte[maxYmInstrumentIndex][];
            for(int i=0;i<maxYmInstrumentIndex;i++){
                try{
                    ymInstruments[i] = Arrays.copyOfRange(data, ymInstOffset+i*instrumentSize, ymInstOffset+i*instrumentSize+instrumentSize);
                }catch(Exception e){
                    e.printStackTrace();
                    ymInstruments = Arrays.copyOfRange(ymInstruments, 0, i);
                    break;
                }
            } 
        } 
        if(psgInstOffset!=0){
            int maxPsgInstrumentIndex = 0;
            int psgInstrumentIndex = findMaxPsgInstrumentIndex();
            if(psgInstrumentIndex>maxPsgInstrumentIndex){
                maxPsgInstrumentIndex = psgInstrumentIndex;
            }
            int chunkCount = (maxPsgInstrumentIndex / PSG_INSTRUMENT_CHUNK_SIZE) + 1;
            maxPsgInstrumentIndex = chunkCount * PSG_INSTRUMENT_CHUNK_SIZE;
            psgInstruments = new byte[maxPsgInstrumentIndex][];
            for(int i=0;i<maxPsgInstrumentIndex;i++){
                try{         
                    int pointer = (((data[psgInstOffset + 2*i + 1])&0x7F)<<8) + ((data[psgInstOffset + 2*i])&0xFF);
                    int instrumentOffset = driverOffset + pointer;
                    int instrumentEndOffset = instrumentOffset;
                    boolean attackDone = false;
                    while(!((data[instrumentEndOffset]&0x80)!=0 && attackDone)){
                        if((data[instrumentEndOffset]&0x80)!=0){
                            attackDone = true;
                        }
                        instrumentEndOffset++;
                    }
                    psgInstruments[i] = Arrays.copyOfRange(data, instrumentOffset, instrumentEndOffset+1);
                }catch(Exception e){
                    e.printStackTrace();
                    psgInstruments = Arrays.copyOfRange(psgInstruments, 0, i);
                    break;
                }
            } 
        } 
    }
    
    public MusicEntry(byte [] data, int entryOffset, int baseOffset){
        if(data[entryOffset+1]==0){
            ym6InDacMode = true;
        }else{
            ym6InDacMode = false;
        }
        if(data[entryOffset+2]!=0){
            System.out.println("Music byte 2 : "+String.format("%02x", data[entryOffset+2]));
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
        outerloop:
        for(int i=0;i<10;i++){
            for(int j=0;j<i;j++){
                if(channels[i]==channels[j]){
                    sb.append("\n"+"    dw "+name+"_Channel_"+j);
                    continue outerloop;
                }
            }
            sb.append("\n"+"    dw "+name+"_Channel_"+i);
        }
        outerloop:
        for(int i=0;i<10;i++){
            for(int j=0;j<i;j++){
                if(channels[i]==channels[j]){
                    continue outerloop;
                }
            }
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
            int cursor = baseOffset+1+1+1+1+2*10;
            channelPointers[0] = cursor;
            outerloop:
            for(int i=1;i<10;i++){
                for(int j=0;j<i;j++){
                    if(channels[i]==channels[j]){
                        channelPointers[i] = channelPointers[j];
                        continue outerloop;
                    }
                }
                cursor += channelOutputBytes[i-1].length;
                channelPointers[i] = channelPointers[i-1]+channelOutputBytes[i-1].length;
            }        
            for(int i=0;i<10;i++){
                output.write((byte)(channelPointers[i]&0xFF));
                output.write((byte)((channelPointers[i]>>8)&0xFF));
            }
            outerloop:
            for(int i=0;i<10;i++){
                for(int j=0;j<i;j++){
                    if(channels[i]==channels[j]){
                        continue outerloop;
                    }
                }
                output.write(channelOutputBytes[i]);
            }   
        } catch (IOException ex) {
            Logger.getLogger(MusicEntry.class.getName()).log(Level.SEVERE, null, ex);
        }
        return output.toByteArray();
    }
    
    
    
    public void factorizeIdenticalChannels(){
        for(int i=1;i<10;i++){
            for(int j=0;j<i;j++){
                if(channels[i].equals(channels[j])){
                    channels[i] = channels[j];
                    //System.out.println("Channel "+i+" is identical to channel "+j+" : channel entry "+i+" now points to channel "+j+" content");
                }
            }
        }
    }
    
    public void unroll(){
        for(CubeChannel cc: channels){
            cc.unroll();
        }
    }
    
    public void optimize(){
        for(int i=0;i<channels.length;i++){
            System.out.println("Optimizing channel "+i+" ...");
            channels[i].optimize();
        }
    }
    
    public List<Integer> getYmInstrumentList(){
        Set<Integer> instSet = new HashSet();
        for(int i=0;i<6;i++){
            for(CubeCommand cc : channels[i].getCcs()){
                if(cc instanceof Inst){
                    instSet.add(((Inst) cc).getValue()&0xFF);
                }
            }
        }
        List<Integer> instList = new ArrayList(instSet);
        Collections.sort(instList);
        return instList;
    }
    
    public boolean hasMainLoop(){
        boolean hasMainLoop = false;
        CubeCommand[] ccs = channels[0].getCcs();
        for(int i=0;i<ccs.length;i++){
            CubeCommand cc = ccs[i];
            if(cc instanceof MainLoopStart){
                hasMainLoop = true;
            }
        }
        //System.out.println("hasMainLoop : "+hasMainLoop);
        return hasMainLoop;
    }
    
    public boolean hasRepeatLoop(){
        boolean hasMainLoop = false;
        CubeCommand[] ccs = channels[0].getCcs();
        for(int i=0;i<ccs.length;i++){
            CubeCommand cc = ccs[i];
            if(cc instanceof RepeatStart){
                hasMainLoop = true;
            }
        }
        return hasMainLoop;
    }
    
    public boolean hasIntro(){
        boolean hasIntro = false;
        for(int c=0;c<channels.length;c++){
            CubeCommand[] ccs = channels[c].getCcs();
            boolean noteOrWaitMet = false;
            for(int i=0;i<ccs.length;i++){
                CubeCommand cc = ccs[i];
                if(cc instanceof MainLoopStart || cc instanceof RepeatStart){
                    if(noteOrWaitMet){
                        hasIntro = true;
                        break;
                    }else{
                        break;
                    }
                }else if(cc instanceof Note
                        || cc instanceof NoteL
                        || cc instanceof Wait
                        || cc instanceof WaitL){
                    noteOrWaitMet = true;
                }
            }
        }
        //System.out.println("hasIntro : "+hasIntro);
        return hasIntro;
    }   
    
    public int findMaxYmInstrumentIndex(){
        int maxIndex = 0;
        for(int i=0;i<6;i++){
            CubeChannel cch = channels[i];
            for(CubeCommand cc : cch.getCcs()){
                if(cc instanceof Inst){
                    int index = ((Inst)cc).getValue();
                    if(index>maxIndex){
                        maxIndex = index;
                    }
                }
            }
        }
        return maxIndex;
    }
    
    public int findMaxSampleIndex(){
        int maxIndex = 0;
        CubeChannel cch = channels[5];
        for(CubeCommand cc : cch.getCcs()){
            if(cc instanceof Sample || cc instanceof SampleL){
                int index = 0;
                if(cc instanceof Sample){
                    index = ((Sample)cc).getSample();
                } else{
                    index = ((SampleL)cc).getSample();
                }
                if(index>maxIndex){
                    maxIndex = index;
                }
            }
        }
        return maxIndex;
    }
    
    public int findMaxPsgInstrumentIndex(){
        int maxIndex = 0;
        for(int i=6;i<channels.length-1;i++){
            CubeChannel cch = channels[i];
            for(CubeCommand cc : cch.getCcs()){
                if(cc instanceof PsgInst){
                    int index = (((PsgInst)cc).getValue()&0xF0)>>4;
                    if(index>maxIndex){
                        maxIndex = index;
                    }
                }
            }
        }
        return maxIndex;
    }
    
    public int findMaxPitchEffectIndex(){
        int maxIndex = 0;
        for(int i=0;i<channels.length-1;i++){
            CubeChannel cch = channels[i];
            for(CubeCommand cc : cch.getCcs()){
                if(cc instanceof Vibrato){
                    int index = (((Vibrato)cc).getValue()&0xF0)>>4;
                    if(index>maxIndex){
                        maxIndex = index;
                    }
                }
            }
        }
        return maxIndex;
    }

    public byte[][] getPitchEffects() {
        return pitchEffects;
    }

    public void setPitchEffects(byte[][] pitchEffects) {
        this.pitchEffects = pitchEffects;
    }

    public byte[] getYmLevels() {
        return ymLevels;
    }

    public void setYmLevels(byte[] ymLevels) {
        this.ymLevels = ymLevels;
    }

    public byte[][] getYmInstruments() {
        return ymInstruments;
    }

    public void setYmInstruments(byte[][] ymInstruments) {
        this.ymInstruments = ymInstruments;
    }

    public byte[][] getPsgInstruments() {
        return psgInstruments;
    }

    public void setPsgInstruments(byte[][] psgInstruments) {
        this.psgInstruments = psgInstruments;
    }

    public byte[][] getSampleEntries() {
        return sampleEntries;
    }

    public void setSampleEntries(byte[][] sampleEntries) {
        this.sampleEntries = sampleEntries;
    }

    public byte[][] getSampleBanks() {
        return sampleBanks;
    }

    public void setSampleBanks(byte[][] sampleBanks) {
        this.sampleBanks = sampleBanks;
    }

    public boolean isSsgEgAvailable() {
        return ssgEgAvailable;
    }

    public void setSsgEgAvailable(boolean ssgEg) {
        this.ssgEgAvailable = ssgEg;
    }

    public int getYmTimerBIncrement() {
        return ymTimerBIncrement;
    }

    public void setYmTimerBIncrement(int ymTimerBIncrement) {
        this.ymTimerBIncrement = ymTimerBIncrement;
    }

    public boolean isMultiSampleBank() {
        return multiSampleBank;
    }

    public void setMultiSampleBank(boolean multiSampleBank) {
        this.multiSampleBank = multiSampleBank;
    }
    
    public boolean hasContent(){
        for(int i=0;i<this.channels.length;i++){
            if(this.channels[i].getCcs().length>1){
                return true;
            }
        }            
        return false;
    }
    
    public Set<Integer> getUsedYmInstrumentIndexes(){
        Set<Integer> usedYmInstrumentIndexes = new HashSet();
        for(int i=0;i<6;i++){
            CubeChannel cch = channels[i];
            for(CubeCommand cc : cch.getCcs()){
                if(cc instanceof Inst){
                    usedYmInstrumentIndexes.add(((Inst)cc).getValue()&0xff);
                }
            }
        }
        return usedYmInstrumentIndexes;
    }
    
}
