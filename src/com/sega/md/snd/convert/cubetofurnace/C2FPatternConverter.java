/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sega.md.snd.convert.cubetofurnace;

import com.sega.md.snd.formats.cube.CubeChannel;
import com.sega.md.snd.formats.cube.CubeCommand;
import com.sega.md.snd.formats.cube.command.ChannelEnd;
import com.sega.md.snd.formats.cube.command.Inst;
import com.sega.md.snd.formats.cube.command.MainLoopEnd;
import com.sega.md.snd.formats.cube.command.MainLoopStart;
import com.sega.md.snd.formats.cube.command.NoSlide;
import com.sega.md.snd.formats.cube.command.Note;
import com.sega.md.snd.formats.cube.command.NoteL;
import com.sega.md.snd.formats.cube.command.PsgInst;
import com.sega.md.snd.formats.cube.command.PsgNote;
import com.sega.md.snd.formats.cube.command.PsgNoteL;
import com.sega.md.snd.formats.cube.command.RepeatEnd;
import com.sega.md.snd.formats.cube.command.RepeatStart;
import com.sega.md.snd.formats.cube.command.Sample;
import com.sega.md.snd.formats.cube.command.SampleL;
import com.sega.md.snd.formats.cube.command.SetRelease;
import com.sega.md.snd.formats.cube.command.SetSlide;
import com.sega.md.snd.formats.cube.command.Shifting;
import com.sega.md.snd.formats.cube.command.Stereo;
import com.sega.md.snd.formats.cube.command.Sustain;
import com.sega.md.snd.formats.cube.command.Vibrato;
import com.sega.md.snd.formats.cube.command.Vol;
import com.sega.md.snd.formats.cube.command.Wait;
import com.sega.md.snd.formats.cube.command.WaitL;
import com.sega.md.snd.formats.cube.command.YmTimer;
import com.sega.md.snd.formats.furnace.pattern.Effect;
import com.sega.md.snd.formats.furnace.pattern.Instrument;
import com.sega.md.snd.formats.furnace.pattern.FNote;
import com.sega.md.snd.formats.furnace.pattern.Pattern;
import com.sega.md.snd.formats.furnace.pattern.Row;
import com.sega.md.snd.formats.furnace.pattern.Volume;
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
    
    private int channelType = 0;
    private int newNoteValue = 0;
    private int previousNoteValue = 0;
    private int playLength = 0;
    private int currentNotePlayLength = 0;
    private int nextNoteLengthDecrement = 0;
    private int playCounter = 0;
    private int newVolume = 0;
    private int currentVolume = -1;
    private int newInstrument = 0;
    private int currentInstrument = 0;
    private int vibratoDelay = -1;
    private int currentNoteVibratoDelay = -1;
    private int vibratoIndex = 0;
    private int vibratoCounter = 0;
    private boolean vibratoOngoing = false;
    private boolean sustainVibrato = false;
    private boolean noRelease = false;
    private boolean legatoToActivateAfterCurrentNote = false;
    private boolean legatoToActivate = false;
    private boolean legatoToDeactivateAfterCurrentNote = false;
    private boolean legatoToDeactivate = false;
    private int release = 0;
    private int releaseCounter = 0;
    private boolean released = false;
    private boolean previousNoteReleased = false;
    private boolean sustainOngoing = false;
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
        this.channelType = channelType;
        vibratoOngoing = mainLoopOnly;
        mainLoopStarted = mainLoopOnly;
        currentRow = new Row();
        if(mainLoopOnly){
            stopVibrato();
            stopLegato();
        }
        while(cursor<ccs.length){
            CubeCommand cc = ccs[cursor];
            if(cc instanceof MainLoopStart || cc instanceof RepeatStart /* Edge case */ ){
                if(introOnly){
                    mainLoopStartPosition = rowList.size();
                    break;
                }else{
                    mainLoopStarted = true;
                }
            }else if(cc instanceof Inst){
                inst(cc);
            }else if(cc instanceof PsgInst){
                psgInst(cc);
            }else if(cc instanceof Vol){
                vol(cc);
            }else if(cc instanceof Stereo){
                stereo(cc);
            }else if(cc instanceof SetSlide){
                setSlide(cc);
            }else if(cc instanceof NoSlide){
                noSlide(cc);
            }else if(cc instanceof Shifting){
                shifting(cc);
            }else if(cc instanceof Vibrato){
                vibrato(cc);
            }else if(cc instanceof SetRelease){
                setRelease(cc);
            }else if(cc instanceof Sustain){
                sustain();
            }else if((cc instanceof Note || cc instanceof NoteL || cc instanceof Sample || cc instanceof SampleL || cc instanceof PsgNote || cc instanceof PsgNoteL)){
                if(cc instanceof Sample || cc instanceof SampleL){
                    applySample(cc);
                }else{
                    applyNote(cc);
                }
                applyDetune();
                applyInstrument();
                applyVolume();
                applyPanning();
                applyLegato();
                applyPortamento();
                applyVibratoEnd();
                sustainVibrato = false;
                rowList.add(currentRow);
                currentNoteVibratoDelay = vibratoDelay;
                currentNotePlayLength = playLength;
                if(nextNoteLengthDecrement>0){
                    currentNotePlayLength-=nextNoteLengthDecrement;
                    if(currentNoteVibratoDelay>0){
                        currentNoteVibratoDelay-=nextNoteLengthDecrement;
                    }
                    nextNoteLengthDecrement = 0;
                }
                if(currentNotePlayLength==0){
                    /* Managing tracker limitation here :
                    Cube uses 0-length note as a portamento starting point, while trackers can't have 0-length notes.
                    Apply note followed immediately by new note with decreased playLength. */ 
                    nextNoteLengthDecrement++;
                }
                currentRow = new Row();
                playCounter = 1;
                vibratoCounter = playCounter;
                releaseCounter = playCounter;
                released = false;
                while(playCounter<currentNotePlayLength){
                    applyVibrato();
                    applyRelease();
                    playCounter++;
                }
                previousNoteReleased = released;
                playCounter=0;
            }else if(cc instanceof Wait || cc instanceof WaitL){
                applyWait(cc);
            }else if(cc instanceof YmTimer){
                ymTimer(cc);
            }else if(cc instanceof ChannelEnd || cc instanceof MainLoopEnd ||cc instanceof RepeatEnd){
                
            }else {
                System.out.println("FurnacePattern.convertFmCubeChannel() - Ignoring command "+cursor+" : "+cc.produceAsmOutput());
            }
            cursor++;
        }
        rows = rowList.toArray(new Row[0]);
        p.setRows(rows);
        return p;
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
        release = sr.getValue()&0x7F;
        if((sr.getValue()&0x80)!=0){
            sustain();
        }else{
            if(noRelease){
                noRelease = false;
                legatoToDeactivateAfterCurrentNote = true;
            }
        }
    }

    private void sustain() {
        noRelease = true;
        legatoToActivateAfterCurrentNote = true;
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
    
    private void applyNote(CubeCommand cc){
        if(channelType==TYPE_FM){
            applyYmNote(cc);
        }else{
            applyPsgNote(cc);
        }
    }
    
    private void applyYmNote(CubeCommand cc){
        previousNoteValue = newNoteValue;
        newNoteValue = cc instanceof Note ? 
                C2FPitch.valueFromCubeValue(((Note)cc).getNote().getValue()-12).getFurnaceValue()
                : C2FPitch.valueFromCubeValue(((NoteL)cc).getNote().getValue()-12).getFurnaceValue();
        if(cc instanceof NoteL){
            playLength = 0xFF & ((NoteL)cc).getLength();
        }
        if(!sustainOngoing || (newNoteValue!=previousNoteValue)){
            currentRow.setNote(new FNote(newNoteValue));
        }
    }
    
    private void applySample(CubeCommand cc){
        newInstrument = cc instanceof Sample ?
                ((Sample)cc).getSample()+SAMPLE_INSTRUMENT_OFFSET
                : ((SampleL)cc).getSample()+SAMPLE_INSTRUMENT_OFFSET;
        currentInstrument = -1;
        if(cc instanceof SampleL){
            playLength = 0xFF & ((SampleL)cc).getLength();
        }
        currentRow.setNote(new FNote(C2FPitch.C4.getFurnaceValue()));       
    }
    
    private void applyPsgNote(CubeCommand cc){
        previousNoteValue = newNoteValue;
        newNoteValue = cc instanceof PsgNote ? 
                C2FPitch.valueFromCubeValue(((PsgNote)cc).getNote().getValue()-12).getFurnaceValue()
                : C2FPitch.valueFromCubeValue(((PsgNoteL)cc).getNote().getValue()-12).getFurnaceValue();
        if(cc instanceof PsgNoteL){
            playLength = 0xFF & ((PsgNoteL)cc).getLength();
        }
        if(!noRelease || (newNoteValue!=previousNoteValue)){
            currentRow.setNote(new FNote(newNoteValue));
        }        
    }

    private void applyWait(CubeCommand cc){
        if(cc instanceof WaitL){
            playLength = 0xFF & ((WaitL)cc).getValue();
        }
        playCounter = 0;
        if(channelType==TYPE_PSGTONE){
            currentRow.setNote(new FNote(NOTE_OFF));
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
    }
    
    private void applyInstrument(){
        if(newInstrument!=currentInstrument){
            currentRow.setInstrument(new Instrument(newInstrument));
            currentInstrument = newInstrument;
        }        
    }
    
    private void applyVolume(){
        if(channelType!=TYPE_DAC && newVolume!=currentVolume){
            if(channelType==TYPE_FM){
                currentRow.setVolume(new Volume(0x7F-YM_LEVELS[newVolume]));
            }else{
                currentRow.setVolume(new Volume(newVolume));
            }
            currentVolume = newVolume;
        }        
    }
    
    private void applyDetune(){
        if(detune>=0){
            currentRow.getEffectList().add(new Effect(0x53,0x00+detune));
            detune=-1;
        }        
    }
    
    private void applyPanning(){
        if(newPanning!=currentPanning){
            currentRow.getEffectList().add(new Effect(0x80,newPanning));
            currentPanning=newPanning;
        }        
    }
    
    private void applyPortamento(){
        if(slide>0 && (newNoteValue!=previousNoteValue) && previousNoteValue!=0){
            if((previousNoteReleased || release==0)&&nextNoteLengthDecrement==0){      
                /* Managing Furnace limitation : portamento expects previous note to be active, not released.
                Apply last note again, followed immediately by new note with decreased playLength */
                currentRow.setNote(new FNote(previousNoteValue));
                rowList.add(currentRow);
                currentNotePlayLength = playLength;
                nextNoteLengthDecrement++;
                currentRow = new Row();
                currentRow.setNote(new FNote(newNoteValue));
                currentRow.getEffectList().add(new Effect(0x03,slide));
            }else{
                currentRow.setNote(new FNote(newNoteValue));
                currentRow.getEffectList().add(new Effect(0x03,slide));
            }
        }        
    }
    
    private void applyVibratoEnd(){
        if(vibratoOngoing && channelType!=TYPE_DAC && (previousNoteReleased || release==0) && !sustainVibrato){
            stopVibrato(); 
        }      
    }
    
    private void stopVibrato(){
        currentRow.getEffectList().add(new Effect(0x04,0x00));
        vibratoOngoing = false; 
    } 
    
    private void applyLegato(){
        if(legatoToActivateAfterCurrentNote){
            legatoToActivate = true;
            legatoToActivateAfterCurrentNote = false;
        }else if(legatoToActivate){
            currentRow.getEffectList().add(new Effect(0xEA,0xFF));
            legatoToActivate = false;
        }
        if(legatoToDeactivate){
            stopLegato();
            legatoToDeactivate = false;
        }else if(legatoToDeactivateAfterCurrentNote){
            legatoToDeactivate = true;
            legatoToDeactivateAfterCurrentNote = false;
        }
    }
    
    private void stopLegato(){
        currentRow.getEffectList().add(new Effect(0xEA,0x00));
    }
    
    public void applyRelease(){
        if(releaseCounter>=(currentNotePlayLength-release)){
            if(!noRelease){
                currentRow.setNote(new FNote(NOTE_RELEASE));
                sustainOngoing = false;
                released = true;
            }else{
                sustainOngoing = true;
                released = false;
            }
            rowList.add(currentRow);
            currentRow = new Row();
            releaseCounter=0;
        }else{
            rowList.add(currentRow);
            currentRow = new Row();
            if(!released){
                releaseCounter++;
            }
        }        
    }
    
    public static int calculateTicksPersSecond(byte ymTimerB, int speed){  
        float timerPeriod = (8*144) * (PATTERN_LENGTH - (0xFF&ymTimerB)) / (YM2612_INPUT_FREQUENCY/2);
        float timerFrequency = 1/timerPeriod * speed;
        int ticksPerSecond = Math.round(timerFrequency);
        //System.out.println("Timer B value "+Integer.toString(0xFF&ymTimerB)+" -> "+ticksPerSecond+" ticks per second");
        return ticksPerSecond;
    }

    public int getMainLoopStartPosition() {
        return mainLoopStartPosition;
    }
    
    private void applyVibrato(){
        if((currentNoteVibratoDelay>0 || vibratoIndex!=0) && !vibratoOngoing){
            if(vibratoCounter>=(currentNoteVibratoDelay)){
                if(channelType==TYPE_FM){
                    applyYmVibrato();
                }else{
                    applyPsgVibrato();
                }
                vibratoOngoing = true;
                vibratoCounter = 0;
            } else{
                vibratoCounter++;
            }
        }        
    } 
    
    public void applyPsgVibrato(){
        
        currentRow.getEffectList().add(new Effect(0x04,0x52));
        
    } 
    
    public void applyYmVibrato(){
        
        currentRow.getEffectList().add(new Effect(0x04,0x52));

        //TODO implement vibratos and slides depending on the game's pitch effect table 
        /*
          $FB xy     Load Vibrato x, triggered at FNote Length 2*y
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
    
}
