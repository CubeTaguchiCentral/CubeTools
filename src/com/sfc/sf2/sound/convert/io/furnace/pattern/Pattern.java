/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.sound.convert.io.furnace.pattern;

import com.sfc.sf2.sound.convert.io.cube.CubeChannel;
import com.sfc.sf2.sound.convert.io.cube.CubeCommand;
import com.sfc.sf2.sound.convert.io.cube.command.ChannelEnd;
import com.sfc.sf2.sound.convert.io.cube.command.Inst;
import com.sfc.sf2.sound.convert.io.cube.command.MainLoopStart;
import com.sfc.sf2.sound.convert.io.cube.command.PsgInst;
import com.sfc.sf2.sound.convert.io.cube.command.PsgNote;
import com.sfc.sf2.sound.convert.io.cube.command.PsgNoteL;
import com.sfc.sf2.sound.convert.io.cube.command.SetRelease;
import com.sfc.sf2.sound.convert.io.cube.command.SetSlide;
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
    
    public static final int TYPE_FM = 0;
    public static final int TYPE_DAC = 1;
    public static final int TYPE_PSGTONE = 2;
    public static final int TYPE_PSGNOISE = 3;
    
    public static final byte NOTE_OFF = (byte)180;
    public static final byte NOTE_RELEASE = (byte)181;
    public static final byte MACRO_RELEASE = (byte)182;
    
    public static final int SAMPLE_INSTRUMENT_OFFSET = 0x50;
    
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
        boolean effect0TypePresent = false;
        boolean effect0ValuePresent = false;
        boolean otherEffects03Present = false;
        boolean otherEffects47Present = false;
        boolean otherEffect0TypePresent = false;
        boolean otherEffect0ValuePresent = false;
        boolean otherEffect1TypePresent = false;
        boolean otherEffect1ValuePresent = false;
        boolean otherEffect2TypePresent = false;
        boolean otherEffect2ValuePresent = false;
        boolean otherEffect3TypePresent = false;
        boolean otherEffect3ValuePresent = false;
        boolean otherEffect4TypePresent = false;
        boolean otherEffect4ValuePresent = false;
        boolean otherEffect5TypePresent = false;
        boolean otherEffect5ValuePresent = false;
        boolean otherEffect6TypePresent = false;
        boolean otherEffect6ValuePresent = false;
        boolean otherEffect7TypePresent = false;
        boolean otherEffect7ValuePresent = false;
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
                effect0TypePresent = (b&0x08)!=0;
                effect0ValuePresent = (b&0x10)!=0;
                otherEffects03Present = (b&0x20)!=0;
                otherEffects47Present = (b&0x40)!=0;
                if(otherEffects03Present){
                    b = bb.get();
                    otherEffect0TypePresent = (b&0x01)!=0;
                    otherEffect0ValuePresent = (b&0x02)!=0;
                    otherEffect1TypePresent = (b&0x04)!=0;
                    otherEffect1ValuePresent = (b&0x08)!=0;
                    otherEffect2TypePresent = (b&0x10)!=0;
                    otherEffect2ValuePresent = (b&0x20)!=0;
                    otherEffect3TypePresent = (b&0x40)!=0;
                    otherEffect3ValuePresent = (b&0x80)!=0;
                }else{
                    otherEffect0TypePresent = false;
                    otherEffect0ValuePresent = false;
                    otherEffect1TypePresent = false;
                    otherEffect1ValuePresent = false;
                    otherEffect2TypePresent = false;
                    otherEffect2ValuePresent = false;
                    otherEffect3TypePresent = false;
                    otherEffect3ValuePresent = false;
                }
                if(otherEffects47Present){
                    b = bb.get();
                    otherEffect4TypePresent = (b&0x01)!=0;
                    otherEffect4ValuePresent = (b&0x02)!=0;
                    otherEffect5TypePresent = (b&0x04)!=0;
                    otherEffect5ValuePresent = (b&0x08)!=0;
                    otherEffect6TypePresent = (b&0x10)!=0;
                    otherEffect6ValuePresent = (b&0x20)!=0;
                    otherEffect7TypePresent = (b&0x40)!=0;
                    otherEffect7ValuePresent = (b&0x80)!=0;
                }else{
                    otherEffect4TypePresent = false;
                    otherEffect4ValuePresent = false;
                    otherEffect5TypePresent = false;
                    otherEffect5ValuePresent = false;
                    otherEffect6TypePresent = false;
                    otherEffect6ValuePresent = false;
                    otherEffect7TypePresent = false;
                    otherEffect7ValuePresent = false;
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
                /*if(effect0TypePresent){
                    Effect e = new Effect(bb.get());
                    if(effect0ValuePresent){
                        e.setValue(bb.get());
                    }
                    r.getEffectList().add(e);
                }*/
                if(otherEffect0TypePresent){
                    Effect e = new Effect(bb.get());
                    if(otherEffect0ValuePresent){
                        e.setValue(bb.get());
                    }
                    r.getEffectList().add(e);
                }
                if(otherEffect1TypePresent){
                    Effect e = new Effect(bb.get());
                    if(otherEffect1ValuePresent){
                        e.setValue(bb.get());
                    }
                    r.getEffectList().add(e);
                }
                if(otherEffect2TypePresent){
                    Effect e = new Effect(bb.get());
                    if(otherEffect2ValuePresent){
                        e.setValue(bb.get());
                    }
                    r.getEffectList().add(e);
                }
                if(otherEffect3TypePresent){
                    Effect e = new Effect(bb.get());
                    if(otherEffect3ValuePresent){
                        e.setValue(bb.get());
                    }
                    r.getEffectList().add(e);
                }
                if(otherEffect4TypePresent){
                    Effect e = new Effect(bb.get());
                    if(otherEffect4ValuePresent){
                        e.setValue(bb.get());
                    }
                    r.getEffectList().add(e);
                }
                if(otherEffect5TypePresent){
                    Effect e = new Effect(bb.get());
                    if(otherEffect5ValuePresent){
                        e.setValue(bb.get());
                    }
                    r.getEffectList().add(e);
                }
                if(otherEffect6TypePresent){
                    Effect e = new Effect(bb.get());
                    if(otherEffect6ValuePresent){
                        e.setValue(bb.get());
                    }
                    r.getEffectList().add(e);
                }
                if(otherEffect7TypePresent){
                    Effect e = new Effect(bb.get());
                    if(otherEffect7ValuePresent){
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
        int newNoteValue = 0;
        int playLength = 0;
        int playCounter = 0;
        int newVolume = 0;
        int currentVolume = 0;
        int newInstrument = 0;
        int currentInstrument = 0;
        int vibrato = -1;
        int vibratoCounter = 0;
        boolean vibratoTriggered = mainLoopOnly?true:false;
        int release = 0;
        int releaseCounter = 0;
        boolean released = false;
        boolean sustain = false;
        boolean sustainedNotePlayed = false;
        int detune = -1;
        int panning = -1;
        int slide = 0;
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
            } else if(cc instanceof SetSlide){
                SetSlide ss = (SetSlide) cc;
                byte value = ss.getValue();
                if((value&0xFF)==0xFF){
                    slide = 0;
                }else{
                    slide = value&0x7F;
                }
            } else if(cc instanceof SetRelease){
                SetRelease sr = (SetRelease) cc;
                release = sr.getValue();
                sustain = false;
                sustainedNotePlayed = false;
            } else if(cc instanceof Sustain){
                sustain = true;
                sustainedNotePlayed = false;
                release = 0;
            } else if(cc instanceof Vol){
                Vol v = (Vol) cc;
                newVolume = v.getValue();
            } else if(cc instanceof Inst){
                Inst inst = (Inst) cc;
                newInstrument = inst.getValue();
            }else if(cc instanceof PsgInst){
                PsgInst inst = (PsgInst) cc;
                newInstrument = (0xF0&inst.getValue())>>4;
                newVolume = ((0x0F&inst.getValue())/2);
            }else if((cc instanceof com.sfc.sf2.sound.convert.io.cube.command.Note || cc instanceof com.sfc.sf2.sound.convert.io.cube.command.NoteL
                    ||cc instanceof com.sfc.sf2.sound.convert.io.cube.command.Sample || cc instanceof com.sfc.sf2.sound.convert.io.cube.command.SampleL)
                    && (  (!introOnly && !mainLoopOnly)
                       || (introOnly && !mainLoopStarted)
                       || (mainLoopOnly && mainLoopStarted)
                       )
                    ){
                if(cc instanceof com.sfc.sf2.sound.convert.io.cube.command.NoteL){
                    com.sfc.sf2.sound.convert.io.cube.command.NoteL n = (com.sfc.sf2.sound.convert.io.cube.command.NoteL) cc;
                    newNoteValue = Pitch.valueFromCubeValue(n.getNote().getValue()-12).getValue();
                    playLength = 0xFF & n.getLength();
                    if(!sustain || (sustain && !sustainedNotePlayed)){
                        currentRow.setNote(new Note(newNoteValue));
                        if(sustain && !sustainedNotePlayed){
                            sustainedNotePlayed = true;
                        }
                    }
                }else if(cc instanceof com.sfc.sf2.sound.convert.io.cube.command.SampleL){
                    com.sfc.sf2.sound.convert.io.cube.command.SampleL s = (com.sfc.sf2.sound.convert.io.cube.command.SampleL) cc;
                    newInstrument = s.getSample()+SAMPLE_INSTRUMENT_OFFSET;
                    currentInstrument = -1;
                    playLength = 0xFF & s.getLength();
                    currentRow.setNote(new Note(Pitch.C4.getValue()));
                }else if(cc instanceof com.sfc.sf2.sound.convert.io.cube.command.Note){
                    com.sfc.sf2.sound.convert.io.cube.command.Note n = (com.sfc.sf2.sound.convert.io.cube.command.Note) cc;
                    newNoteValue = Pitch.valueFromCubeValue(n.getNote().getValue()-12).getValue();
                    if(!sustain || (sustain && !sustainedNotePlayed)){
                        currentRow.setNote(new Note(newNoteValue));
                        if(sustain && !sustainedNotePlayed){
                            sustainedNotePlayed = true;
                        }
                    }
                }else{
                    com.sfc.sf2.sound.convert.io.cube.command.Sample s = (com.sfc.sf2.sound.convert.io.cube.command.Sample) cc;
                    currentInstrument = s.getSample()+SAMPLE_INSTRUMENT_OFFSET;
                    currentInstrument = -1;
                    currentRow.setNote(new Note(Pitch.C4.getValue()));
                }
                if(newInstrument!=currentInstrument){
                    currentRow.setInstrument(new Instrument(newInstrument));
                    currentInstrument = newInstrument;
                }
                if(newVolume!=currentVolume){
                    currentRow.setVolume(new Volume(Math.min((newVolume+1)*8,0x7F)));
                    currentVolume = newVolume;
                }
                if(vibratoTriggered){
                    currentRow.getEffectList().add(new Effect(0x04,0x00));
                    vibratoTriggered = false;
                }
                if(detune>=0){
                    currentRow.getEffectList().add(new Effect(0x53,0x00+detune));
                    detune=-1;
                }
                if(panning>=0){
                    currentRow.getEffectList().add(new Effect(0x80,panning));
                    panning=-1;
                }
                if(slide>0){
                    currentRow.setNote(new Note(newNoteValue));
                    currentRow.getEffectList().add(new Effect(0x03,slide));
                    slide=0;
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
                    if(!vibratoTriggered && vibrato>0){
                        if(vibratoCounter>=(vibrato)){
                            currentRow.getEffectList().add(new Effect(0x04,0x52));
                            vibratoTriggered = true;
                            vibratoCounter = 0;
                        } else{
                            vibratoCounter++;
                        }
                    }
                    if(releaseCounter>=(playLength-release)){
                        currentRow.setNote(new Note(NOTE_RELEASE));
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
                if(newInstrument!=currentInstrument){
                    currentRow.setInstrument(new Instrument(newInstrument));
                    currentInstrument = newInstrument;
                }
                if(newVolume!=currentVolume){
                    currentRow.setVolume(new Volume(newVolume));
                    currentVolume = newVolume;
                }
                if(vibratoTriggered){
                    currentRow.getEffectList().add(new Effect(0x04,0x00));
                    vibratoTriggered = false;
                }
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
                            currentRow.getEffectList().add(new Effect(0x04,0x52));
                            vibratoTriggered = true;
                            vibratoCounter = 0;
                        } else{
                            vibratoCounter++;
                        }
                    }
                    if(releaseCounter>=(playLength-release)){
                        currentRow.setNote(new Note(NOTE_OFF));
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
            }else if(cc instanceof ChannelEnd){
                
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
