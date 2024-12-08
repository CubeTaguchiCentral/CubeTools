/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.sound.convert.io.furnace.pattern;

import com.sfc.sf2.sound.convert.io.cube.CubeChannel;
import com.sfc.sf2.sound.convert.io.cube.CubeCommand;
import com.sfc.sf2.sound.convert.io.cube.command.Inst;
import com.sfc.sf2.sound.convert.io.cube.command.MainLoopStart;
import com.sfc.sf2.sound.convert.io.cube.command.PsgInst;
import com.sfc.sf2.sound.convert.io.cube.command.PsgNote;
import com.sfc.sf2.sound.convert.io.cube.command.PsgNoteL;
import com.sfc.sf2.sound.convert.io.cube.command.SetRelease;
import com.sfc.sf2.sound.convert.io.cube.command.Shifting;
import com.sfc.sf2.sound.convert.io.cube.command.Stereo;
import com.sfc.sf2.sound.convert.io.cube.command.Sustain;
import com.sfc.sf2.sound.convert.io.cube.command.Vibrato;
import com.sfc.sf2.sound.convert.io.cube.command.Vol;
import com.sfc.sf2.sound.convert.io.cube.command.Wait;
import com.sfc.sf2.sound.convert.io.cube.command.WaitL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Wiz
 */
public class Pattern {
    
    private Row[] rows;
    
    public Pattern(){
        
    }
    
    public Pattern(byte[] data){
        ByteBuffer bb = ByteBuffer.wrap(data);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.position(0);
        byte b = 0;
        boolean notePresent = false;
        boolean instrumentPresent = false;
        boolean volumePresent = false;
        boolean effect00TypePresent = false;
        boolean effect00ValuePresent = false;
        boolean otherEffects03Present = false;
        boolean otherEffects47Present = false;
        boolean effect0TypePresent = false;
        boolean effect0ValuePresent = false;
        boolean effect1TypePresent = false;
        boolean effect1ValuePresent = false;
        boolean effect2TypePresent = false;
        boolean effect2ValuePresent = false;
        boolean effect3TypePresent = false;
        boolean effect3ValuePresent = false;
        boolean effect4TypePresent = false;
        boolean effect4ValuePresent = false;
        boolean effect5TypePresent = false;
        boolean effect5ValuePresent = false;
        boolean effect6TypePresent = false;
        boolean effect6ValuePresent = false;
        boolean effect7TypePresent = false;
        boolean effect7ValuePresent = false;
        List<Row> rowList = new ArrayList();
        while(bb.position()<bb.capacity()){
            b = bb.get();
            if((b&0xFF)==0xFF){
                break;
            }
            if(b==0){
                rowList.add(new Row());
            }else if((b&0x80)!=0){
                int skipLength = (b&0x7F)+2;
                for(int i=0;i<skipLength;i++){
                    rowList.add(new Row());
                }
            }else{
                Row r = new Row();
                notePresent = (b&0x01)!=0;
                instrumentPresent = (b&0x02)!=0;
                volumePresent = (b&0x04)!=0;
                effect00TypePresent = (b&0x08)!=0;
                effect00ValuePresent = (b&0x10)!=0;
                otherEffects03Present = (b&0x20)!=0;
                otherEffects47Present = (b&0x40)!=0;
                if(otherEffects03Present){
                    b = bb.get();
                    effect0TypePresent = (b&0x01)!=0;
                    effect0ValuePresent = (b&0x02)!=0;
                    effect1TypePresent = (b&0x04)!=0;
                    effect1ValuePresent = (b&0x08)!=0;
                    effect2TypePresent = (b&0x10)!=0;
                    effect2ValuePresent = (b&0x20)!=0;
                    effect3TypePresent = (b&0x40)!=0;
                    effect3ValuePresent = (b&0x80)!=0;
                }else{
                    effect0TypePresent = false;
                    effect0ValuePresent = false;
                    effect1TypePresent = false;
                    effect1ValuePresent = false;
                    effect2TypePresent = false;
                    effect2ValuePresent = false;
                    effect3TypePresent = false;
                    effect3ValuePresent = false;
                }
                if(otherEffects47Present){
                    b = bb.get();
                    effect4TypePresent = (b&0x01)!=0;
                    effect4ValuePresent = (b&0x02)!=0;
                    effect5TypePresent = (b&0x04)!=0;
                    effect5ValuePresent = (b&0x08)!=0;
                    effect6TypePresent = (b&0x10)!=0;
                    effect6ValuePresent = (b&0x20)!=0;
                    effect7TypePresent = (b&0x40)!=0;
                    effect7ValuePresent = (b&0x80)!=0;
                }else{
                    effect4TypePresent = false;
                    effect4ValuePresent = false;
                    effect5TypePresent = false;
                    effect5ValuePresent = false;
                    effect6TypePresent = false;
                    effect6ValuePresent = false;
                    effect7TypePresent = false;
                    effect7ValuePresent = false;
                }
                if(notePresent){
                    r.setNote(new Note(bb.get()));
                }
                if(instrumentPresent){
                    r.setInstrument(new Instrument(bb.get()));
                }
                if(volumePresent){
                    r.setVolume(new Volume(bb.get()));
                }
                if(effect00TypePresent){
                    Effect e = new Effect(bb.get());
                    if(effect00ValuePresent){
                        e.setValue(bb.get());
                    }
                    r.getEffectList().add(e);
                }
                if(effect0TypePresent){
                    Effect e = new Effect(bb.get());
                    if(effect0ValuePresent){
                        e.setValue(bb.get());
                    }
                    r.getEffectList().add(e);
                }
                if(effect1TypePresent){
                    Effect e = new Effect(bb.get());
                    if(effect1ValuePresent){
                        e.setValue(bb.get());
                    }
                    r.getEffectList().add(e);
                }
                if(effect2TypePresent){
                    Effect e = new Effect(bb.get());
                    if(effect2ValuePresent){
                        e.setValue(bb.get());
                    }
                    r.getEffectList().add(e);
                }
                if(effect3TypePresent){
                    Effect e = new Effect(bb.get());
                    if(effect3ValuePresent){
                        e.setValue(bb.get());
                    }
                    r.getEffectList().add(e);
                }
                if(effect4TypePresent){
                    Effect e = new Effect(bb.get());
                    if(effect4ValuePresent){
                        e.setValue(bb.get());
                    }
                    r.getEffectList().add(e);
                }
                if(effect5TypePresent){
                    Effect e = new Effect(bb.get());
                    if(effect5ValuePresent){
                        e.setValue(bb.get());
                    }
                    r.getEffectList().add(e);
                }
                if(effect6TypePresent){
                    Effect e = new Effect(bb.get());
                    if(effect6ValuePresent){
                        e.setValue(bb.get());
                    }
                    r.getEffectList().add(e);
                }
                if(effect7TypePresent){
                    Effect e = new Effect(bb.get());
                    if(effect7ValuePresent){
                        e.setValue(bb.get());
                    }
                    r.getEffectList().add(e);
                }
                rowList.add(r);
            }
        }
        rows = new Row[rowList.size()];
        for(int j=0;j<rows.length;j++){
            rows[j]=rowList.get(j);
        }
    }
    
    public Pattern(CubeChannel cch, int channelType, boolean introOnly, boolean mainLoopOnly){
        List<Row> rowList = new ArrayList();
        CubeCommand[] ccs = cch.getCcs();
        int playLength = 0;
        int playCounter = 0;
        int release = 0;
        int releaseCounter = 0;
        int vibrato = -1;
        int vibratoCounter = 0;
        int currentInstrument = 0;
        int currentVolume = 0;
        int detune = -1;
        int panning = -1;
        boolean released = false;
        boolean vibratoTriggered = false;
        boolean mainLoopStarted = false;
        Row currentRow = new Row();
        for(int i=0;i<ccs.length;i++){
            CubeCommand cc = ccs[i];
            if(cc instanceof MainLoopStart){
                if(introOnly){
                    break;
                }else{
                    mainLoopStarted = true;
                }
            } else if(cc instanceof Stereo){
                Stereo s = (Stereo) cc;
                switch(0xFF&s.getValue()){
                    case 0x80:
                        panning = 0x00;
                        break;
                    case 0x40:
                        panning = 0xFF;
                        break;
                    default:
                        panning = 0x80;
                        break;
                }
            } else if(cc instanceof Shifting){
                Shifting s = (Shifting) cc;
                detune = ((s.getValue()&0x30)>>4)+3;
            } else if(cc instanceof Vibrato){
                Vibrato v = (Vibrato) cc;
                vibrato = (v.getValue()&0xF)*2;
            } else if(cc instanceof SetRelease){
                SetRelease sr = (SetRelease) cc;
                release = sr.getValue();
            } else if(cc instanceof Sustain){
                release = 0;
            } else if(cc instanceof Vol){
                Vol v = (Vol) cc;
                currentVolume = v.getValue();
            } else if(cc instanceof Inst){
                Inst inst = (Inst) cc;
                currentInstrument = inst.getValue();
            }else if(cc instanceof PsgInst){
                PsgInst inst = (PsgInst) cc;
                currentInstrument = (0xF0&inst.getValue())>>4;
                currentVolume = (0x0F&inst.getValue())/2;
            }else if((cc instanceof com.sfc.sf2.sound.convert.io.cube.command.Note || cc instanceof com.sfc.sf2.sound.convert.io.cube.command.NoteL)
                    && (  (!introOnly && !mainLoopOnly)
                       || (introOnly && !mainLoopStarted)
                       || (mainLoopOnly && mainLoopStarted)
                       )
                    ){
                if(cc instanceof com.sfc.sf2.sound.convert.io.cube.command.NoteL){
                    com.sfc.sf2.sound.convert.io.cube.command.NoteL n = (com.sfc.sf2.sound.convert.io.cube.command.NoteL) cc;
                    playLength = 0xFF & n.getLength();
                    currentRow.setNote(new Note(Pitch.valueFromCubeValue(n.getNote().getValue()-12).getValue()));
                }else{
                    com.sfc.sf2.sound.convert.io.cube.command.Note n = (com.sfc.sf2.sound.convert.io.cube.command.Note) cc;
                    currentRow.setNote(new Note(Pitch.valueFromCubeValue(n.getNote().getValue()-12).getValue()));
                }
                currentRow.setInstrument(new Instrument(currentInstrument));
                currentRow.setVolume(new Volume(currentVolume*8));
                currentRow.getEffectList().add(new Effect(0x04,0x00));
                if(detune>=0){
                    currentRow.getEffectList().add(new Effect(0x53,0x00+detune));
                    detune=-1;
                }
                if(panning>=0){
                    currentRow.getEffectList().add(new Effect(0x80,panning));
                    panning=-1;
                }
                playCounter = 0;
                releaseCounter = 0;
                vibratoCounter = 0;
                rowList.add(currentRow);
                currentRow = new Row();
                playCounter++;
                releaseCounter++;
                vibratoCounter++;
                while(playCounter<playLength){
                    if(!vibratoTriggered && vibrato!=-1){
                        if(vibratoCounter>=(vibrato)){
                            currentRow.getEffectList().add(new Effect(0x04,0x22));
                            vibratoTriggered = true;
                            vibratoCounter = 0;
                        } else{
                            vibratoCounter++;
                        }
                    }
                    if(releaseCounter>=(playLength-release)){
                        currentRow.setNote(new Note(0xFF));
                        rowList.add(currentRow);
                        currentRow = new Row();
                        releaseCounter=0;
                        playCounter++;
                        released = true;
                    }else{
                        rowList.add(currentRow);
                        currentRow = new Row();
                        playCounter++;
                        if(!released){
                            releaseCounter++;
                        }
                    }
                }
                playCounter=0;
                released = false;
                vibratoTriggered = false;
            }else if((cc instanceof PsgNote || cc instanceof PsgNoteL)
                    && (  (!introOnly && !mainLoopOnly)
                       || (introOnly && !mainLoopStarted)
                       || (mainLoopOnly && mainLoopStarted)
                       )
                    ){
                if(cc instanceof PsgNoteL){
                    PsgNoteL n = (PsgNoteL) cc;
                    playLength = 0xFF & n.getLength();
                    currentRow.setNote(new Note(Pitch.valueFromCubeValue(n.getNote().getValue()-12).getValue()));
                }else{
                    PsgNote n = (PsgNote) cc;
                    currentRow.setNote(new Note(Pitch.valueFromCubeValue(n.getNote().getValue()-12).getValue()));
                }
                currentRow.setInstrument(new Instrument(currentInstrument));
                currentRow.setVolume(new Volume(currentVolume*8));
                currentRow.getEffectList().add(new Effect(0x04,0x00));
                if(detune>=0){
                    currentRow.getEffectList().add(new Effect(0x53,0x00+detune));
                    detune=-1;
                }
                playCounter = 0;
                releaseCounter = 0;
                vibratoCounter = 0;
                rowList.add(currentRow);
                currentRow = new Row();
                playCounter++;
                releaseCounter++;
                vibratoCounter++;
                while(playCounter<playLength){
                    if(!vibratoTriggered && vibrato!=-1){
                        if(vibratoCounter>=(vibrato)){
                            currentRow.getEffectList().add(new Effect(0x04,0x22));
                            vibratoTriggered = true;
                            vibratoCounter = 0;
                        } else{
                            vibratoCounter++;
                        }
                    }
                    if(releaseCounter>=(playLength-release)){
                        currentRow.setNote(new Note(0xFE));
                        rowList.add(currentRow);
                        currentRow = new Row();
                        releaseCounter=0;
                        playCounter++;
                        released = true;
                    }else{
                        rowList.add(currentRow);
                        currentRow = new Row();
                        playCounter++;
                        if(!released){
                            releaseCounter++;
                        }
                    }
                }
                playCounter=0;
                released = false;
                vibratoTriggered = false;
            }else if((cc instanceof Wait || cc instanceof WaitL)
                    && (  (!introOnly && !mainLoopOnly)
                       || (introOnly && !mainLoopStarted)
                       || (mainLoopOnly && mainLoopStarted)
                       )
                    ){
                if(cc instanceof WaitL){
                    WaitL n = (WaitL) cc;
                    playLength = 0xFF & n.getValue();
                }
                playCounter = 0;
                rowList.add(currentRow);
                currentRow = new Row();
                playCounter++;
                while(playCounter<playLength){
                    rowList.add(currentRow);
                    currentRow = new Row();
                    playCounter++;
                }
                playCounter=0;
            }else {
                System.out.println("com.sfc.sf2.sound.convert.io.furnace.FurnacePattern.convertFmCubeChannel() - Ignoring command "+i+" : "+cc.produceAsmOutput());
            }
        }
        if(introOnly){
            currentRow.getEffectList().add(new Effect(0x0D,0x00));
        }
        rows = new Row[rowList.size()];
        for(int j=0;j<rows.length;j++){
            rows[j]=rowList.get(j);
        }
    }
    
    public Row[] getRows() {
        return rows;
    }

    public void setRows(Row[] rows) {
        this.rows = rows;
    }
}
