/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.sound.convert.io.furnace;

import com.sfc.sf2.sound.convert.io.cube.CubeChannel;
import com.sfc.sf2.sound.convert.io.cube.CubeCommand;
import com.sfc.sf2.sound.convert.io.cube.MusicEntry;
import com.sfc.sf2.sound.convert.io.cube.command.Inst;
import com.sfc.sf2.sound.convert.io.cube.command.Note;
import com.sfc.sf2.sound.convert.io.cube.command.NoteL;
import com.sfc.sf2.sound.convert.io.cube.command.PsgInst;
import com.sfc.sf2.sound.convert.io.cube.command.PsgNote;
import com.sfc.sf2.sound.convert.io.cube.command.PsgNoteL;
import com.sfc.sf2.sound.convert.io.cube.command.Wait;
import com.sfc.sf2.sound.convert.io.cube.command.WaitL;
import com.sfc.sf2.sound.convert.io.furnace.FurnaceChannel;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Wiz
 */
public class FurnacePattern {
    
    private static final int MAX_CHANNELS_SIZE=10;
    
    private FurnaceChannel[] channels = new FurnaceChannel[MAX_CHANNELS_SIZE];

    public FurnaceChannel[] getChannels() {
        return channels;
    }

    public void setChannels(FurnaceChannel[] channels) {
        this.channels = channels;
    }
    
    public FurnacePattern(MusicEntry me){
        for(int i=0;i<channels.length;i++){
            channels[i] = new FurnaceChannel();
            List<FurnaceRow> rowList = null;
            if(i<5){
                rowList = convertFmCubeChannel(me.getChannels()[i]);
            }else if(i==5){
                if(!me.isYm6InDacMode()){
                    rowList = convertFmCubeChannel(me.getChannels()[i]);
                }else{
                    rowList = convertDacCubeChannel(me.getChannels()[i]);
                }
            }else if(i<9){
                rowList = convertPsgToneCubeChannel(me.getChannels()[i]);
            }else{
                rowList = convertPsgNoiseCubeChannel(me.getChannels()[i]);
            }
            FurnaceRow[] rows = new FurnaceRow[rowList.size()];
            for(int j=0;j<rows.length;j++){
                rows[j]=rowList.get(j);
            }
            channels[i].setRows(rows);
        }
        fillChannelsToMaxLength();
    }
    
    public List<FurnaceRow> convertFmCubeChannel(CubeChannel cch){
        List<FurnaceRow> rowList = new ArrayList();
        CubeCommand[] ccs = cch.getCcs();
        int playLength = 0;
        int playCounter = 0;
        int release = 0;
        int releaseCounter = 0;
        int currentInstrument = 0;
        FurnaceRow currentRow = new FurnaceRow();
        for(int i=0;i<ccs.length;i++){
            CubeCommand cc = ccs[i];
            if(cc instanceof Inst){
                Inst inst = (Inst) cc;
                currentInstrument = inst.getValue();
            }else if(cc instanceof Note){
                Note n = (Note) cc;
                currentRow.setNote(new FurnaceNote(FurnacePitch.valueFromCubeValue(n.getNote().getValue()-12).getValue()));
                currentRow.setInstrument(new FurnaceInstrument(currentInstrument));
                playCounter = 0;
                releaseCounter = 0;
                rowList.add(currentRow);
                currentRow = new FurnaceRow();
                playCounter++;
                while(playCounter<playLength){
                    rowList.add(currentRow);
                    currentRow = new FurnaceRow();
                    playCounter++;
                }
                playCounter=0;
            }else if(cc instanceof NoteL){
                NoteL n = (NoteL) cc;
                currentRow.setNote(new FurnaceNote(FurnacePitch.valueFromCubeValue(n.getNote().getValue()-12).getValue()));
                currentRow.setInstrument(new FurnaceInstrument(currentInstrument));
                playLength = n.getLength();
                playCounter = 0;
                releaseCounter = 0;
                rowList.add(currentRow);
                currentRow = new FurnaceRow();
                playCounter++;
                while(playCounter<playLength){
                    rowList.add(currentRow);
                    currentRow = new FurnaceRow();
                    playCounter++;
                }
                playCounter=0;
            }else if(cc instanceof Wait){
                Note n = (Note) cc;
                playCounter = 0;
                rowList.add(currentRow);
                currentRow = new FurnaceRow();
                playCounter++;
                while(playCounter<playLength){
                    rowList.add(currentRow);
                    currentRow = new FurnaceRow();
                    playCounter++;
                }
                playCounter=0;
            }else if(cc instanceof WaitL){
                WaitL n = (WaitL) cc;
                playLength = n.getValue();
                playCounter = 0;
                rowList.add(currentRow);
                currentRow = new FurnaceRow();
                playCounter++;
                while(playCounter<playLength){
                    rowList.add(currentRow);
                    currentRow = new FurnaceRow();
                    playCounter++;
                }
                playCounter=0;
            }else {
                System.out.println("com.sfc.sf2.sound.convert.io.furnace.FurnacePattern.convertFmCubeChannel() - Currently ignoring command "+i+" : "+cc.produceAsmOutput());
            }
        }
        return rowList;
    }
    
    public List<FurnaceRow> convertDacCubeChannel(CubeChannel cch){
        List<FurnaceRow> rowList = new ArrayList();
        
        return rowList;
        
    }
    
    public List<FurnaceRow> convertPsgToneCubeChannel(CubeChannel cch){
        List<FurnaceRow> rowList = new ArrayList();
        CubeCommand[] ccs = cch.getCcs();
        int playLength = 0;
        int playCounter = 0;
        int release = 0;
        int releaseCounter = 0;
        int currentInstrument = 0;
        FurnaceRow currentRow = new FurnaceRow();
        for(int i=0;i<ccs.length;i++){
            CubeCommand cc = ccs[i];
            if(cc instanceof PsgInst){
                PsgInst inst = (PsgInst) cc;
                currentInstrument = inst.getValue();
            }else if(cc instanceof PsgNote){
                PsgNote n = (PsgNote) cc;
                currentRow.setNote(new FurnaceNote(FurnacePitch.valueFromCubeValue(n.getNote().getValue()).getValue()));
                currentRow.setInstrument(new FurnaceInstrument(currentInstrument));
                playCounter = 0;
                releaseCounter = 0;
                rowList.add(currentRow);
                currentRow = new FurnaceRow();
                playCounter++;
                while(playCounter<playLength){
                    rowList.add(currentRow);
                    currentRow = new FurnaceRow();
                    playCounter++;
                }
                playCounter=0;
            }else if(cc instanceof PsgNoteL){
                PsgNoteL n = (PsgNoteL) cc;
                currentRow.setNote(new FurnaceNote(FurnacePitch.valueFromCubeValue(n.getNote().getValue()).getValue()));
                currentRow.setInstrument(new FurnaceInstrument(currentInstrument));
                playLength = n.getLength();
                playCounter = 0;
                releaseCounter = 0;
                rowList.add(currentRow);
                currentRow = new FurnaceRow();
                playCounter++;
                while(playCounter<playLength){
                    rowList.add(currentRow);
                    currentRow = new FurnaceRow();
                    playCounter++;
                }
                playCounter=0;
            }else if(cc instanceof Wait){
                Note n = (Note) cc;
                playCounter = 0;
                rowList.add(currentRow);
                currentRow = new FurnaceRow();
                playCounter++;
                while(playCounter<playLength){
                    rowList.add(currentRow);
                    currentRow = new FurnaceRow();
                    playCounter++;
                }
                playCounter=0;
            }else if(cc instanceof WaitL){
                WaitL n = (WaitL) cc;
                playLength = n.getValue();
                playCounter = 0;
                rowList.add(currentRow);
                currentRow = new FurnaceRow();
                playCounter++;
                while(playCounter<playLength){
                    rowList.add(currentRow);
                    currentRow = new FurnaceRow();
                    playCounter++;
                }
                playCounter=0;
            }else {
                System.out.println("com.sfc.sf2.sound.convert.io.furnace.FurnacePattern.convertFmCubeChannel() - Currently ignoring command "+i+" : "+cc.produceAsmOutput());
            }
        }
        return rowList;
        
    }
    
    public List<FurnaceRow> convertPsgNoiseCubeChannel(CubeChannel cch){
        List<FurnaceRow> rowList = new ArrayList();
        CubeCommand[] ccs = cch.getCcs();
        int playLength = 0;
        int playCounter = 0;
        int release = 0;
        int releaseCounter = 0;
        int currentInstrument = 0;
        FurnaceRow currentRow = new FurnaceRow();
        for(int i=0;i<ccs.length;i++){
            CubeCommand cc = ccs[i];
            if(cc instanceof PsgInst){
                PsgInst inst = (PsgInst) cc;
                currentInstrument = inst.getValue();
            }else if(cc instanceof PsgNote){
                PsgNote n = (PsgNote) cc;
                currentRow.setNote(new FurnaceNote(FurnacePitch.valueFromCubeValue(n.getNote().getValue()).getValue()));
                currentRow.setInstrument(new FurnaceInstrument(currentInstrument));
                playCounter = 0;
                releaseCounter = 0;
                rowList.add(currentRow);
                currentRow = new FurnaceRow();
                playCounter++;
                while(playCounter<playLength){
                    rowList.add(currentRow);
                    currentRow = new FurnaceRow();
                    playCounter++;
                }
                playCounter=0;
            }else if(cc instanceof PsgNoteL){
                PsgNoteL n = (PsgNoteL) cc;
                currentRow.setNote(new FurnaceNote(FurnacePitch.valueFromCubeValue(n.getNote().getValue()).getValue()));
                currentRow.setInstrument(new FurnaceInstrument(currentInstrument));
                playLength = n.getLength();
                playCounter = 0;
                releaseCounter = 0;
                rowList.add(currentRow);
                currentRow = new FurnaceRow();
                playCounter++;
                while(playCounter<playLength){
                    rowList.add(currentRow);
                    currentRow = new FurnaceRow();
                    playCounter++;
                }
                playCounter=0;
            }else if(cc instanceof Wait){
                Note n = (Note) cc;
                playCounter = 0;
                rowList.add(currentRow);
                currentRow = new FurnaceRow();
                playCounter++;
                while(playCounter<playLength){
                    rowList.add(currentRow);
                    currentRow = new FurnaceRow();
                    playCounter++;
                }
                playCounter=0;
            }else if(cc instanceof WaitL){
                WaitL n = (WaitL) cc;
                playLength = n.getValue();
                playCounter = 0;
                rowList.add(currentRow);
                currentRow = new FurnaceRow();
                playCounter++;
                while(playCounter<playLength){
                    rowList.add(currentRow);
                    currentRow = new FurnaceRow();
                    playCounter++;
                }
                playCounter=0;
            } else {
                System.out.println("com.sfc.sf2.sound.convert.io.furnace.FurnacePattern.convertFmCubeChannel() - Currently ignoring command "+i+" : "+cc.produceAsmOutput());
            }
        }
        return rowList;
        
    }
    
    public void fillChannelUntilNextNote(){
        
    }
    
    public void fillChannelsToMaxLength(){
        int maxLength=0;
        for (int i=0;i<channels.length;i++){
            if(maxLength<channels[i].getRows().length){
                maxLength = channels[i].getRows().length;
            }
        }
        for (int i=0;i<channels.length;i++){
            FurnaceRow[] rows = channels[i].getRows();
            if(rows.length<maxLength){
                FurnaceRow[] newRows = new FurnaceRow[maxLength];
                System.arraycopy(rows, 0, newRows, 0, rows.length);
                for(int j=0;(rows.length+j)<maxLength;j++){
                    FurnaceRow voidRow = new FurnaceRow();
                    newRows[rows.length+j] = voidRow;
                }
                channels[i].setRows(newRows);
            }
        }
    }
    
}
