/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.sound.convert.cubetofurnace;

import com.sfc.sf2.sound.formats.cube.CubeChannel;
import com.sfc.sf2.sound.formats.cube.CubeCommand;
import com.sfc.sf2.sound.formats.cube.command.ChannelEnd;
import com.sfc.sf2.sound.formats.cube.command.Inst;
import com.sfc.sf2.sound.formats.cube.command.MainLoopEnd;
import com.sfc.sf2.sound.formats.cube.command.MainLoopStart;
import com.sfc.sf2.sound.formats.cube.command.NoSlide;
import com.sfc.sf2.sound.formats.cube.command.PsgInst;
import com.sfc.sf2.sound.formats.cube.command.PsgNote;
import com.sfc.sf2.sound.formats.cube.command.PsgNoteL;
import com.sfc.sf2.sound.formats.cube.command.RepeatEnd;
import com.sfc.sf2.sound.formats.cube.command.RepeatStart;
import com.sfc.sf2.sound.formats.cube.command.SetRelease;
import com.sfc.sf2.sound.formats.cube.command.SetSlide;
import com.sfc.sf2.sound.formats.cube.command.Shifting;
import com.sfc.sf2.sound.formats.cube.command.Stereo;
import com.sfc.sf2.sound.formats.cube.command.Sustain;
import com.sfc.sf2.sound.formats.cube.command.Vibrato;
import com.sfc.sf2.sound.formats.cube.command.Vol;
import com.sfc.sf2.sound.formats.cube.command.Wait;
import com.sfc.sf2.sound.formats.cube.command.WaitL;
import com.sfc.sf2.sound.formats.cube.command.YmTimer;
import com.sfc.sf2.sound.formats.furnace.pattern.Effect;
import com.sfc.sf2.sound.formats.furnace.pattern.Instrument;
import com.sfc.sf2.sound.formats.furnace.pattern.Note;
import com.sfc.sf2.sound.formats.furnace.pattern.Pattern;
import com.sfc.sf2.sound.formats.furnace.pattern.Row;
import com.sfc.sf2.sound.formats.furnace.pattern.Volume;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Wiz
 */
public class C2FPatternConverter {
    
    public static final int MD_CRYSTAL_FREQUENCY = 53693175;
    public static final float YM2612_INPUT_FREQUENCY = MD_CRYSTAL_FREQUENCY / 7;
    public static final int YM2612_CHANNEL_SAMPLE_CYCLES = 6*24;
    public static final float YM2612_OUTPUT_RATE = YM2612_INPUT_FREQUENCY / YM2612_CHANNEL_SAMPLE_CYCLES;
    
    public static final int[] YM_LEVELS = {0x70, 0x60, 0x50, 0x40, 0x38, 0x30, 0x2A, 0x26, 0x20, 0x1C, 0x18, 0x14, 0x10, 0xB, 0x8, 0x4};    
    
    public static final int PATTERN_LENGTH = 256;
    
    public static final int TYPE_FM = 0;
    public static final int TYPE_DAC = 1;
    public static final int TYPE_PSGTONE = 2;
    public static final int TYPE_PSGNOISE = 3;
    
    public static final byte NOTE_OFF = (byte)180;
    public static final byte NOTE_RELEASE = (byte)181;
    public static final byte MACRO_RELEASE = (byte)182;
    
    public static final int PSG_INSTRUMENT_OFFSET = 0xA0;
    public static final int SAMPLE_INSTRUMENT_OFFSET = 0xC0;
    
    private Row[] rows;
    
    private int newNoteValue = 0;
    private int playLength = 0;
    private int playCounter = 0;
    private int newVolume = 0;
    private int currentVolume = -1;
    private int newInstrument = 0;
    private int currentInstrument = 0;
    private int vibratoDelay = -1;
    private int vibratoIndex = 0;
    private int vibratoCounter = 0;
    private boolean vibratoTriggered = false;
    private int release = 0;
    private int releaseCounter = 0;
    private boolean released = false;
    private boolean legatoToActivate = false;
    private boolean sustainedNotePlayed = false;
    private boolean legatoActivated = false;
    private boolean legatoToDeactivate = true;
    private boolean releasePlayed = true;
    private int detune = -1;
    private int newPanning = -1;
    private int currentPanning = -1;
    private int slide = 0;
    private int cursor = 0;
    private int mainLoopStartPosition = 0;
    private boolean mainLoopStarted = false;
    boolean introOnly = false;
    boolean mainLoopOnly = false;
    List<Row> rowList = new ArrayList();
    Row currentRow = null;
    
    public static C2FPatternConverter[] instantiateConverterArray(int count){
        C2FPatternConverter[] converters = new C2FPatternConverter[count];
        for(int i=0;i<converters.length;i++){
            converters[i] = new C2FPatternConverter();
        }
        return converters;
    }
        
    public Pattern convertCubeChannelToFurnacePattern(CubeChannel cch, int channelType, boolean introOnly, boolean mainLoopOnly){
        Pattern p = new Pattern();
        rowList = new ArrayList();
        CubeCommand[] ccs = cch.getCcs();
        vibratoTriggered = mainLoopOnly?true:false;
        legatoToDeactivate = true;
        releasePlayed = true;
        mainLoopStarted = mainLoopOnly?true:false;
        currentRow = new Row();
        while(cursor<ccs.length){
            CubeCommand cc = ccs[cursor];
            if(cc instanceof MainLoopStart 
            /* Edge case */ || cc instanceof RepeatStart){
                if(introOnly){
                    mainLoopStartPosition = rowList.size();
                    break;
                }else{
                    mainLoopStarted = true;
                }
            } else if(cc instanceof Stereo){
                stereo(cc);
            } else if(cc instanceof Shifting){
                shifting(cc);
            } else if(cc instanceof Vibrato){
                vibrato(cc);
            } else if(cc instanceof SetSlide){
                setSlide(cc);
            } else if(cc instanceof NoSlide){
                noSlide(cc);
            } else if(cc instanceof SetRelease){
                setRelease(cc);
            } else if(cc instanceof Sustain){
                sustain(cc);
            } else if(cc instanceof Vol){
                vol(cc);
            } else if(cc instanceof Inst){
                inst(cc);
            }else if(cc instanceof PsgInst){
                psgInst(cc);
            }else if((cc instanceof com.sfc.sf2.sound.formats.cube.command.Note || cc instanceof com.sfc.sf2.sound.formats.cube.command.NoteL
                    ||cc instanceof com.sfc.sf2.sound.formats.cube.command.Sample || cc instanceof com.sfc.sf2.sound.formats.cube.command.SampleL)
                    ){
                if(cc instanceof com.sfc.sf2.sound.formats.cube.command.NoteL){
                    com.sfc.sf2.sound.formats.cube.command.NoteL n = (com.sfc.sf2.sound.formats.cube.command.NoteL) cc;
                    newNoteValue = C2FPitch.valueFromCubeValue(n.getNote().getValue()-12).getFurnaceValue();
                    playLength = 0xFF & n.getLength();
                    if((  (!introOnly && !mainLoopOnly)
                       || (introOnly && !mainLoopStarted)
                       || (mainLoopOnly && mainLoopStarted)
                       )){
                        currentRow.setNote(new Note(newNoteValue));
                    }
                    if(legatoToActivate && !sustainedNotePlayed){
                        sustainedNotePlayed = true;
                    }else if(legatoToActivate && !legatoActivated){
                        currentRow.getEffectList().add(new Effect(0xEA,0xFF));
                        legatoActivated = true;
                        legatoToActivate = false;
                    }
                    if(legatoToDeactivate && releasePlayed){
                        currentRow.getEffectList().add(new Effect(0xEA,0x00));
                        legatoToDeactivate = false;
                        releasePlayed = false;
                    }
                }else if(cc instanceof com.sfc.sf2.sound.formats.cube.command.SampleL){
                    com.sfc.sf2.sound.formats.cube.command.SampleL s = (com.sfc.sf2.sound.formats.cube.command.SampleL) cc;
                    newInstrument = s.getSample()+SAMPLE_INSTRUMENT_OFFSET;
                    currentInstrument = -1;
                    playLength = 0xFF & s.getLength();
                    if((  (!introOnly && !mainLoopOnly)
                       || (introOnly && !mainLoopStarted)
                       || (mainLoopOnly && mainLoopStarted)
                       )){
                        currentRow.setNote(new Note(C2FPitch.C4.getFurnaceValue()));
                    }
                }else if(cc instanceof com.sfc.sf2.sound.formats.cube.command.Note){
                    com.sfc.sf2.sound.formats.cube.command.Note n = (com.sfc.sf2.sound.formats.cube.command.Note) cc;
                    newNoteValue = C2FPitch.valueFromCubeValue(n.getNote().getValue()-12).getFurnaceValue();
                    if((  (!introOnly && !mainLoopOnly)
                       || (introOnly && !mainLoopStarted)
                       || (mainLoopOnly && mainLoopStarted)
                       )){
                        currentRow.setNote(new Note(newNoteValue));
                    }
                    if(legatoToActivate && !sustainedNotePlayed){
                        sustainedNotePlayed = true;
                    }else if(legatoToActivate && !legatoActivated){
                        currentRow.getEffectList().add(new Effect(0xEA,0xFF));
                        legatoActivated = true;
                        legatoToActivate = false;
                    }
                    if(legatoToDeactivate && releasePlayed){
                        currentRow.getEffectList().add(new Effect(0xEA,0x00));
                        legatoToDeactivate = false;
                    }
                }else{
                    com.sfc.sf2.sound.formats.cube.command.Sample s = (com.sfc.sf2.sound.formats.cube.command.Sample) cc;
                    newInstrument = s.getSample()+SAMPLE_INSTRUMENT_OFFSET;
                    currentInstrument = -1;
                    if((  (!introOnly && !mainLoopOnly)
                       || (introOnly && !mainLoopStarted)
                       || (mainLoopOnly && mainLoopStarted)
                       )){
                        currentRow.setNote(new Note(C2FPitch.C4.getFurnaceValue()));
                    }
                }
                if(newInstrument!=currentInstrument){
                    if((  (!introOnly && !mainLoopOnly)
                       || (introOnly && !mainLoopStarted)
                       || (mainLoopOnly && mainLoopStarted)
                       )){
                        currentRow.setInstrument(new Instrument(newInstrument));
                    }
                    currentInstrument = newInstrument;
                }
                if(channelType!=TYPE_DAC && newVolume!=currentVolume){
                    if((  (!introOnly && !mainLoopOnly)
                       || (introOnly && !mainLoopStarted)
                       || (mainLoopOnly && mainLoopStarted)
                       )){
                        currentRow.setVolume(new Volume(0x7F-YM_LEVELS[newVolume]));
                    }
                    currentVolume = newVolume;
                }
                if(vibratoTriggered && channelType!=TYPE_DAC){
                    if((  (!introOnly && !mainLoopOnly)
                       || (introOnly && !mainLoopStarted)
                       || (mainLoopOnly && mainLoopStarted)
                       )){
                        currentRow.getEffectList().add(new Effect(0x04,0x00));
                    }
                    vibratoTriggered = false;
                }
                if(detune>=0){
                    if((  (!introOnly && !mainLoopOnly)
                       || (introOnly && !mainLoopStarted)
                       || (mainLoopOnly && mainLoopStarted)
                       )){
                        currentRow.getEffectList().add(new Effect(0x53,0x00+detune));
                    }
                    detune=-1;
                }
                if(newPanning!=currentPanning){
                    if((  (!introOnly && !mainLoopOnly)
                       || (introOnly && !mainLoopStarted)
                       || (mainLoopOnly && mainLoopStarted)
                       )){
                        currentRow.getEffectList().add(new Effect(0x80,newPanning));
                    }
                    currentPanning=newPanning;
                }
                if(slide>0){
                    if((  (!introOnly && !mainLoopOnly)
                       || (introOnly && !mainLoopStarted)
                       || (mainLoopOnly && mainLoopStarted)
                       )){
                        currentRow.setNote(new Note(newNoteValue));
                        currentRow.getEffectList().add(new Effect(0x03,slide));
                    }
                }
                playCounter = 0;
                releaseCounter = 0;
                vibratoCounter = 0;
                if((  (!introOnly && !mainLoopOnly)
                   || (introOnly && !mainLoopStarted)
                   || (mainLoopOnly && mainLoopStarted)
                   )){
                    rowList.add(currentRow);
                    currentRow = new Row();
                }
                playCounter++;
                releaseCounter++;
                vibratoCounter++;
                if((  (!introOnly && !mainLoopOnly)
                       || (introOnly && !mainLoopStarted)
                       || (mainLoopOnly && mainLoopStarted)
                       )){
                    while(playCounter<playLength){
                        if(!vibratoTriggered && vibratoDelay>0){
                            if(vibratoCounter>=(vibratoDelay)){
                                
                                applyYmVibrato(currentRow);

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
                            releasePlayed = true;
                        }else{
                            rowList.add(currentRow);
                            currentRow = new Row();
                            playCounter++;
                            if(!released){
                                releaseCounter++;
                            }
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
                    currentRow.setNote(new Note(C2FPitch.valueFromCubeValue(n.getNote().getValue()-12).getFurnaceValue()));
                }else{
                    PsgNote n = (PsgNote) cc;
                    currentRow.setNote(new Note(C2FPitch.valueFromCubeValue(n.getNote().getValue()-12).getFurnaceValue()));
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
                    if(!vibratoTriggered && vibratoDelay!=-1){
                        if(vibratoCounter>=(vibratoDelay)){
                            applyPsgVibrato(currentRow);
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
                if(channelType==TYPE_PSGTONE){
                    currentRow.setNote(new Note(NOTE_OFF));
                }
                rowList.add(currentRow);
                currentRow = new Row();
                playCounter++;
                while(playCounter<playLength){
                    rowList.add(currentRow);
                    currentRow = new Row();
                    playCounter++;
                }
                playCounter=0;
            }else if(cc instanceof YmTimer){
                ymTimer(cc);
            }else if(cc instanceof ChannelEnd || cc instanceof MainLoopEnd ||cc instanceof RepeatEnd){
                
            }else {
                System.out.println("FurnacePattern.convertFmCubeChannel() - Ignoring command "+cursor+" : "+cc.produceAsmOutput());
            }
            cursor++;
        }
        if(introOnly){
            currentRow.getEffectList().add(new Effect(0x0D,0x00));
        }
        rows = new Row[rowList.size()];
        for(int j=0;j<rows.length;j++){
            rows[j]=rowList.get(j);
        }
        
        p.setRows(rows);
        
        return p;
    }
    
    public static int calculateTicksPersSecond(byte ymTimerB, int speed){  
        float timerPeriod = (8*144) * (PATTERN_LENGTH - (0xFF&ymTimerB)) / (YM2612_INPUT_FREQUENCY/2);
        float timerFrequency = 1/timerPeriod * speed;
        return Math.round(timerFrequency);
    }

    public int getMainLoopStartPosition() {
        return mainLoopStartPosition;
    }
    
    public void applyYmVibrato(Row row){
        
        row.getEffectList().add(new Effect(0x04,0x52));

        //TODO implement vibratos and slides depending on the game's pitch effect table 
        /*
          $FB xy     Load Vibrato x, triggered at Note Length 2*y
        */
        /*
        SF2 table :
        
        PITCH_EFFECT_0:  db  0, 80h
        PITCH_EFFECT_1:  db -16, 16, 16, -16, 80h
        PITCH_EFFECT_2:  db -3, -3, -1,  1,  3,  3,  3,  1, -1, -3, 80h
        PITCH_EFFECT_3:  db -2, -2, -1,  1,  2,  2,  2,  1, -1, -2, 80h
        PITCH_EFFECT_4:  db -1, -1,  0,  1,  1,  1,  1,  0, -1, -1, 80h
        PITCH_EFFECT_5:  db -1,  0,  0,  1,  0,  1,  0,  0, -1,  0, 80h
        PITCH_EFFECT_6:  db  2, 80h
        PITCH_EFFECT_7:  db -2, 80h
        PITCH_EFFECT_8:  db  4, 80h
        PITCH_EFFECT_9:  db -4, 80h
        PITCH_EFFECT_A:  db  8, 80h
        PITCH_EFFECT_B:  db -8, 80h
        PITCH_EFFECT_C:  db 16, 80h
        PITCH_EFFECT_D:  db -16, 80h
        PITCH_EFFECT_E:  db 32, 80h
        PITCH_EFFECT_F:  db -32, 80h
        */
        /*
        switch(vibratoIndex){
            case 0x1:
                currentRow.getEffectList().add(new Effect(0xE3,0x06));
                currentRow.getEffectList().add(new Effect(0x04,0x2F));
                break;
            case 0x2:
                currentRow.getEffectList().add(new Effect(0xE3,0x00));
                currentRow.getEffectList().add(new Effect(0x04,0x53));
                break;
            case 0x3:
                currentRow.getEffectList().add(new Effect(0xE3,0x00));
                currentRow.getEffectList().add(new Effect(0x04,0x52));
                break;
            case 0x4:
                currentRow.getEffectList().add(new Effect(0xE3,0x00));
                currentRow.getEffectList().add(new Effect(0x04,0x52));
                break;
            case 0x5:
                currentRow.getEffectList().add(new Effect(0xE3,0x00));
                currentRow.getEffectList().add(new Effect(0x04,0x32));
                break;
            case 0x6:
                currentRow.getEffectList().add(new Effect(0xE1,0x1F));
                break;
            case 0x7:
                currentRow.getEffectList().add(new Effect(0xE2,0x1F));
                break;
            case 0x8:
                currentRow.getEffectList().add(new Effect(0xE1,0x2F));
                break;
            case 0x9:
                currentRow.getEffectList().add(new Effect(0xE2,0x2F));
                break;
            case 0xA:
                currentRow.getEffectList().add(new Effect(0xE1,0x4F));
                break;
            case 0xB:
                currentRow.getEffectList().add(new Effect(0xE2,0x4F));
                break;
            case 0xC:
                currentRow.getEffectList().add(new Effect(0xE1,0x8F));
                break;
            case 0xD:
                currentRow.getEffectList().add(new Effect(0xE2,0x8F));
                break;
            case 0xE:
                currentRow.getEffectList().add(new Effect(0xE1,0xFF));
                break;
            case 0xF:
                currentRow.getEffectList().add(new Effect(0xE2,0xFF));
                break;
            default:
                currentRow.getEffectList().add(new Effect(0xE3,0x00));
                currentRow.getEffectList().add(new Effect(0x04,0x52));
                break;
        }
        */
    }
    
    public void applyPsgVibrato(Row row){
        
        row.getEffectList().add(new Effect(0x04,0x52));
        
        //TODO same as above for YM
        
    }    

    private void stereo(CubeCommand cc) {
        Stereo s = (Stereo) cc;
        switch(0xFF&s.getValue()){
            case 0x80:
                newPanning = 0x00;
                break;
            case 0x40:
                newPanning = 0xFF;
                break;
            default:
                newPanning = 0x80;
                break;
        }
    }

    private void shifting(CubeCommand cc) {
        Shifting s = (Shifting) cc;
        detune = ((s.getValue()&0x30)>>4)+3;
    }

    private void vibrato(CubeCommand cc) {
        Vibrato v = (Vibrato) cc;
        vibratoIndex = (v.getValue()&0xF0)>>4;
        vibratoDelay = (v.getValue()&0xF)*2;
    }

    private void setSlide(CubeCommand cc) {
        SetSlide ss = (SetSlide) cc;
        byte value = ss.getValue();
        slide = (value&0x7F) / 2 + 1;
    }

    private void noSlide(CubeCommand cc) {
        slide = 0;
    }

    private void setRelease(CubeCommand cc) {
        SetRelease sr = (SetRelease) cc;
        release = sr.getValue();
        legatoToDeactivate = true;
    }

    private void sustain(CubeCommand cc) {
        release = 0;
        sustainedNotePlayed = false;
        legatoActivated = false;
        legatoToActivate = true;
    }

    private void vol(CubeCommand cc) {
        Vol v = (Vol) cc;
        newVolume = v.getValue()&0x0F;
    }

    private void inst(CubeCommand cc) {
        Inst inst = (Inst) cc;
        newInstrument = inst.getValue();
    }

    private void psgInst(CubeCommand cc) {
        PsgInst inst = (PsgInst) cc;
        newInstrument = ((0xF0&inst.getValue())>>4)+PSG_INSTRUMENT_OFFSET;
        newVolume = ((0x0F&inst.getValue()));
    }

    private void ymTimer(CubeCommand cc) {
        YmTimer yt = (YmTimer) cc;
        currentRow.getEffectList().add(new Effect(0xC0,calculateTicksPersSecond(yt.getValue(),1)));
    }
    
}
