/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sega.md.snd.convert.furnacetocube;

import com.sega.md.snd.formats.cube.CubeChannel;
import com.sega.md.snd.formats.cube.CubeCommand;
import com.sega.md.snd.formats.cube.Pitch;
import com.sega.md.snd.formats.cube.channel.DacChannel;
import com.sega.md.snd.formats.cube.channel.PsgNoiseChannel;
import com.sega.md.snd.formats.cube.channel.PsgToneChannel;
import com.sega.md.snd.formats.cube.channel.YmChannel;
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
import com.sega.md.snd.formats.furnace.file.FurnaceFile;
import com.sega.md.snd.formats.furnace.pattern.Effect;
import com.sega.md.snd.formats.furnace.pattern.FNote;
import com.sega.md.snd.formats.furnace.pattern.Instrument;
import com.sega.md.snd.formats.furnace.pattern.Pattern;
import com.sega.md.snd.formats.furnace.pattern.Row;
import com.sega.md.snd.formats.furnace.pattern.Volume;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Wiz
 */
public class F2CPatternConverter {
    
    private static final int MD_CRYSTAL_FREQUENCY = 53693175;
    private static final float YM2612_INPUT_FREQUENCY = MD_CRYSTAL_FREQUENCY / 7;
    private static final int YM2612_CHANNEL_SAMPLE_CYCLES = 6*24;
    private static final float YM2612_OUTPUT_RATE = YM2612_INPUT_FREQUENCY / YM2612_CHANNEL_SAMPLE_CYCLES;
    
    private static final byte[] DEFAULT_YM_LEVELS = {0x7F-0x70, 0x7F-0x60, 0x7F-0x50, 0x7F-0x40, 0x7F-0x38, 0x7F-0x30, 0x7F-0x2A, 0x7F-0x26, 0x7F-0x20, 0x7F-0x1C, 0x7F-0x18, 0x7F-0x14, 0x7F-0x10, 0x7F-0xB, 0x7F-0x8, 0x7F-0x4};
    
    private static final byte EFFECT_PANNING = (byte)0x80;
    private static final byte STEREO_LEFT = (byte)0x80;
    private static final byte STEREO_CENTER = (byte)0xC0;
    private static final byte STEREO_RIGHT = (byte)0x40;
    
    private static final byte EFFECT_PORTAMENTO = (byte)0x03;
    private static final byte EFFECT_VIBRATO = (byte)0x04;
    private static final byte EFFECT_DETUNE = (byte)0x53;
    private static final byte EFFECT_TICKRATE = (byte)0xC0;
    private static final byte EFFECT_LEGATO = (byte)0xEA;
    
    private static final int YMINSTR_INDEX_OFFSET = 0x0;
    private static final int PSGINSTR_INDEX_OFFSET = 0xA0;
    private static final int SAMPLE_INDEX_OFFSET = 0xC0;
    
    private static final int NOTE_OFFSET = 12;
    
    public static final byte NOTE_OFF = (byte)0xB4;
    public static final byte NOTE_RELEASE = (byte)0xB5;
    public static final byte MACRO_RELEASE = (byte)0xB6;
    
    public static int calculateYmTimerB(float ticksPerSecond, int speed){  
        int ymTimerB;
        float timerPeriod = speed / ticksPerSecond;
        ymTimerB = Math.round(256 - (timerPeriod * (YM2612_INPUT_FREQUENCY/2) / (8*144)));
        System.out.println(ticksPerSecond+" ticks per second"+" -> "+"Timer B value "+Integer.toString(0xFF&ymTimerB));
        return ymTimerB;
    }
    
    public static CubeChannel convertFurnacePatternToCubeChannel(Pattern[] patterns, int channelIndex, CubeChannel cubeChannel, int mainLoopStartIndex, int mainLoopEndIndex, FurnaceFile ff){
        
        if(cubeChannel instanceof YmChannel){
            convertFurnacePatternToYmChannel(patterns, channelIndex, (YmChannel)cubeChannel, mainLoopStartIndex, mainLoopEndIndex);
        }else if(cubeChannel instanceof DacChannel){
            convertFurnacePatternToDacChannel(patterns, channelIndex, (DacChannel)cubeChannel, mainLoopStartIndex, mainLoopEndIndex);
        }else if (cubeChannel instanceof PsgToneChannel){
            convertFurnacePatternToPsgToneChannel(patterns, channelIndex, (PsgToneChannel)cubeChannel, mainLoopStartIndex, mainLoopEndIndex, ff);
        }else if (cubeChannel instanceof PsgNoiseChannel){
            convertFurnacePatternToPsgNoiseChannel(patterns, channelIndex, (PsgNoiseChannel)cubeChannel, mainLoopStartIndex, mainLoopEndIndex);
        }
        
        return cubeChannel;
    }
    
    private static void convertFurnacePatternToYmChannel(Pattern[] patterns, int channelIndex, YmChannel ymChannel, int mainLoopStartIndex, int mainLoopEndIndex){
        Row[] rows = patterns[channelIndex].getRows();
        List<CubeCommand> cubeCommands = new ArrayList();
        int cursor = 0;
        boolean mainLoopEndReached = false;
        int currentPlayLength = 0;
        int currentInstrument = 0;
        int currentVolume = 0;
        int currentRelease = 0;
        int currentVibratoDelay = 0;
        byte currentPanning = (byte)0x80;
        byte currentDetune = 3;
        boolean mainLoopStartRequiresSustain = false;
        boolean legato = false;
        boolean portamentoEffectFound = false;
        int currentSlide = 0;
        int endIndex = -1;
        FNote previousNote = null;
        if(mainLoopEndIndex < 0){
            endIndex = findChannelEndIndex(rows);
        }else{
            endIndex = mainLoopEndIndex;
        }
        if(!channelHasNotes(rows)){
            cubeCommands.add(new ChannelEnd());
        } else{
            while(cursor<rows.length){
                portamentoEffectFound = false;
                if(mainLoopEndIndex >0 && cursor==mainLoopStartIndex){
                    cubeCommands.add(new MainLoopStart());
                    currentPlayLength = 0;
                    currentRelease = 0;
                    applyYmInstrument(cubeCommands, currentInstrument, -1);
                    applyYmVolume(cubeCommands, currentVolume, -1);
                    applyDetune(cubeCommands, currentDetune);
                    applyStereo(cubeCommands, currentPanning);
                }
                if(mainLoopEndIndex >0 && !mainLoopEndReached && cursor>=mainLoopEndIndex){
                    mainLoopEndReached = true;
                    cubeCommands.add(new MainLoopEnd());
                    break;
                }
                if(mainLoopEndIndex<0 && cursor>=endIndex){
                    cubeCommands.add(new ChannelEnd());
                    break;
                }
                Row row = rows[cursor];
                FNote note = row.getNote();
                Instrument instrument = row.getInstrument();
                Volume volume = row.getVolume();
                List<Effect> effects = row.getEffectList();
                int playLength = findPlayLength(rows, cursor, mainLoopStartIndex, endIndex);
                if(playLength==1 && (cursor+1)<rows.length){
                    /* Managing special case of Cube slide effect converted to a 2-note solution for Furnace 
                       Workaround description at https://github.com/CubeTaguchiCentral/CubeAssets/issues/2 
                    */
                    byte nextRowPortamentoValue = getNextRowPortamentoValue(rows, cursor);
                    if(nextRowPortamentoValue!=0){
                        int newSlide = applySlide(cubeCommands, nextRowPortamentoValue, currentSlide);
                        if(newSlide != currentSlide){
                            currentSlide = newSlide;
                        }
                        portamentoEffectFound = true;
                        note = rows[cursor+1].getNote();
                        playLength = 1 + findPlayLength(rows, cursor+1, mainLoopStartIndex, endIndex);
                    }
                }
                boolean noteInterruptedByMainLoopStart = isNoteInterruptedByMainLoopStart(rows, cursor, mainLoopStartIndex, playLength);
                boolean noteInterruptedByMainLoopEnd = isNoteInterruptedByMainLoopEnd(rows, cursor, mainLoopEndIndex, playLength);
                int release = findRelease(rows, cursor, playLength);
                if(!legato && release==playLength){
                    boolean legatoActivated = isLegatoActivated(rows, cursor, playLength);
                    if(legatoActivated){
                        cubeCommands.add(new Sustain());
                        legato = true;
                        currentRelease = 0;
                    }
                }else if(legato){
                    boolean legatoDeactivated = isLegatoDeactivated(rows, cursor, playLength);
                    if(legatoDeactivated){
                        legato = false;
                    }
                }
                if((noteInterruptedByMainLoopStart || noteInterruptedByMainLoopEnd) && release==playLength){
                    release = currentRelease;
                    mainLoopStartRequiresSustain = true;
                }
                for(Effect effect : effects){
                    byte type = effect.getType();
                    byte value = effect.getValue();
                    if(type==EFFECT_PANNING){
                        applyStereo(cubeCommands, value);
                        currentPanning = value;
                    }
                    if(type==EFFECT_DETUNE){
                        applyDetune(cubeCommands, value);
                        currentDetune = value;
                    }
                    if(type==EFFECT_LEGATO){
                        if(value==0){
                            legato = false;
                        }
                    }
                    if(type==EFFECT_PORTAMENTO){
                        int newSlide = applySlide(cubeCommands, value, currentSlide);
                        if(newSlide != currentSlide){
                            currentSlide = newSlide;
                        }
                        portamentoEffectFound = true;
                    }
                }
                if(currentSlide!=0 && !portamentoEffectFound){
                    cubeCommands.add(new NoSlide());
                    currentSlide = 0;
                }
                if(instrument!=null){
                    currentInstrument = applyYmInstrument(cubeCommands, instrument.getValue()&0xFF, currentInstrument);
                    int cubeVolume = currentVolume;
                    if(volume!=null){
                        cubeVolume = findCubeVolume(volume.getValue()&0xFF);
                    }
                    currentVolume = applyYmVolume(cubeCommands, cubeVolume, -1);
                }
                if(volume!=null){
                    int cubeVolume = findCubeVolume(volume.getValue()&0xFF);
                    currentVolume = applyYmVolume(cubeCommands, cubeVolume, currentVolume);
                }
                if(note==null || note.getValue()==NOTE_RELEASE){
                    if(mainLoopStartRequiresSustain){
                        mainLoopStartRequiresSustain = false;
                    }else if(release!=currentRelease && release==1){
                        cubeCommands.add(new SetRelease((byte)release));
                        currentRelease = release;
                    }
                    if(legato && previousNote!=null){
                        note = previousNote;
                    }else if(playLength==currentPlayLength){
                        cubeCommands.add(new Wait());
                    }else{
                        cubeCommands.add(new WaitL((byte)playLength));
                        currentPlayLength = playLength;
                    }
                }
                if(note!=null && note.getValue()!=NOTE_RELEASE){
                    if(mainLoopStartRequiresSustain){
                        cubeCommands.add(new Sustain());
                        mainLoopStartRequiresSustain = false;
                    }else if(!legato && release!=currentRelease && release<playLength){
                        cubeCommands.add(new SetRelease((byte)release));
                        currentRelease = release;
                    }
                    int vibratoDelay = findVibratoDelay(rows, cursor, playLength);
                    if(playLength>currentVibratoDelay && vibratoDelay!=currentVibratoDelay){
                        int index = 2;
                        int delay = (vibratoDelay/2)&0x0F;
                        byte value = (byte)(index<<4|delay);
                        cubeCommands.add(new Vibrato(value));
                        currentVibratoDelay = vibratoDelay;
                    }
                    int noteValue = note.getValue()&0xFF;
                    if(playLength==currentPlayLength){
                        cubeCommands.add(new Note(Pitch.valueOf(F2CPitch.valueOf(noteValue+NOTE_OFFSET).getCubeValue())));
                    }else{
                        cubeCommands.add(new NoteL(Pitch.valueOf(F2CPitch.valueOf(noteValue+NOTE_OFFSET).getCubeValue()), (byte)playLength));
                        currentPlayLength = playLength;
                    }
                    previousNote = note;
                }
                cursor+=playLength;
            }
        }
        int firstNoteIndex = findFirstNoteIndex(cubeCommands);
        if(firstNoteIndex>=0){
            secureStartStereo(cubeCommands);
            secureStartVibrato(cubeCommands, firstNoteIndex);
        }
        ymChannel.setCcs(cubeCommands.toArray(new CubeCommand[cubeCommands.size()]));
    }
    
    private static void convertFurnacePatternToDacChannel(Pattern[] patterns, int channelIndex, DacChannel dacChannel, int mainLoopStartIndex, int mainLoopEndIndex){
        Row[] rows = patterns[channelIndex].getRows();
        List<CubeCommand> cubeCommands = new ArrayList();
        int cursor = 0;
        boolean mainLoopEndReached = false;
        int currentPlayLength = 0;
        byte currentInstrument = 0;
        byte currentVolume = 0;
        int endIndex = -1;
        if(mainLoopEndIndex < 0){
            endIndex = findChannelEndIndex(rows);
        }else{
            endIndex = mainLoopEndIndex;
        }
        if(!channelHasNotes(rows)){
            cubeCommands.add(new ChannelEnd());
        } else{
            while(cursor<rows.length){            
                if(mainLoopEndIndex >0 && cursor==mainLoopStartIndex){
                    cubeCommands.add(new MainLoopStart());
                }
                if(mainLoopEndIndex >0 && !mainLoopEndReached && cursor>=mainLoopEndIndex){
                    mainLoopEndReached = true;
                    cubeCommands.add(new MainLoopEnd());
                    break;
                }
                if(mainLoopEndIndex<0 && cursor>=endIndex){
                    cubeCommands.add(new ChannelEnd());
                    break;
                }
                Row row = rows[cursor];
                FNote note = row.getNote();
                Instrument instrument = row.getInstrument();
                Volume volume = row.getVolume();
                List<Effect> effects = row.getEffectList();
                int playLength = findPlayLength(rows, cursor, mainLoopStartIndex, endIndex);
                for(Effect effect : effects){
                    byte type = effect.getType();
                    byte value = effect.getValue();
                    if(type==EFFECT_PANNING){
                        applyStereo(cubeCommands, value);
                    }
                }
                if(note==null || note.getValue()==NOTE_RELEASE || note.getValue()==NOTE_OFF){
                    if(playLength==currentPlayLength){
                        cubeCommands.add(new Wait());
                    }else{
                        cubeCommands.add(new WaitL((byte)playLength));
                        currentPlayLength = playLength;
                    }
                    //applyWait(cubeCommands, currentPlayLength, playLength);
                }else{
                    int sampleValue = (instrument.getValue()&0xFF) - SAMPLE_INDEX_OFFSET;
                    if(playLength==currentPlayLength){
                        cubeCommands.add(new Sample((byte)sampleValue, (byte)playLength));
                    }else{
                        cubeCommands.add(new SampleL((byte)sampleValue, (byte)playLength));
                        currentPlayLength = playLength;
                    }
                }
                cursor+=playLength;
            }
        }
        dacChannel.setCcs(cubeCommands.toArray(new CubeCommand[cubeCommands.size()]));
    }
    
    private static void convertFurnacePatternToPsgToneChannel(Pattern[] patterns, int channelIndex, PsgToneChannel psgToneChannel, int mainLoopStartIndex, int mainLoopEndIndex, FurnaceFile ff){
        Row[] rows = patterns[channelIndex].getRows();
        List<CubeCommand> cubeCommands = new ArrayList();
        int cursor = 0;
        boolean mainLoopEndReached = false;
        int currentPlayLength = 0;
        int currentInstrument = -1;
        boolean newInstrument = false;
        int currentVolume = 0;
        boolean newVolume = false;
        int currentRelease = 0;
        int currentVibratoDelay = 0;
        boolean mainLoopStartRequiresSustain = false;
        boolean legato = false;
        int endIndex = -1;
        if(mainLoopEndIndex < 0){
            endIndex = findChannelEndIndex(rows);
        }else{
            endIndex = mainLoopEndIndex;
        }
        if(!channelHasNotes(rows)){
            cubeCommands.add(new ChannelEnd());
        } else{
            while(cursor<rows.length){
                newInstrument = false;
                newVolume = false;
                if(mainLoopEndIndex >0 && cursor==mainLoopStartIndex){
                    cubeCommands.add(new MainLoopStart());
                    currentPlayLength = 0;
                    currentRelease = 0;
                }
                if(mainLoopEndIndex >0 && !mainLoopEndReached && cursor>=mainLoopEndIndex){
                    mainLoopEndReached = true;
                    cubeCommands.add(new MainLoopEnd());
                    break;
                }
                if(mainLoopEndIndex<0 && cursor>=endIndex){
                    cubeCommands.add(new ChannelEnd());
                    break;
                }
                Row row = rows[cursor];
                FNote note = row.getNote();
                Instrument instrument = row.getInstrument();
                Volume volume = row.getVolume();
                List<Effect> effects = row.getEffectList();
                int playLength = findPlayLength(rows, cursor, mainLoopStartIndex, endIndex);
                boolean noteInterruptedByMainLoopStart = isNoteInterruptedByMainLoopStart(rows, cursor, mainLoopStartIndex, playLength);
                boolean noteInterruptedByMainLoopEnd = isNoteInterruptedByMainLoopEnd(rows, cursor, mainLoopEndIndex, playLength);
                int release = findRelease(rows, cursor, playLength);
                if(!legato && release==playLength){
                    boolean legatoActivated = isLegatoActivated(rows, cursor, playLength);
                    if(legatoActivated){
                        cubeCommands.add(new Sustain());
                        legato = true;
                        currentRelease = 0;
                    }
                }else if(legato){
                    boolean legatoDeactivated = isLegatoDeactivated(rows, cursor, playLength);
                    if(legatoDeactivated){
                        legato = false;
                    }
                }
                if((noteInterruptedByMainLoopStart || noteInterruptedByMainLoopEnd) && release==playLength){
                    release = currentRelease;
                    mainLoopStartRequiresSustain = true;
                }
                for(Effect effect : effects){
                    byte type = effect.getType();
                    byte value = effect.getValue();
                    if(type==EFFECT_DETUNE){
                        applyDetune(cubeCommands, value);
                    }
                    if((type&0xF0)==(EFFECT_TICKRATE&0xF0)){
                        applyYmTimer(cubeCommands, type, value, ff);
                    }
                    if(type==EFFECT_LEGATO){
                        if(value==0){
                            legato = false;
                        }
                    }
                }
                if(instrument!=null){
                    int instrumentValue = (instrument.getValue()&0xFF) - PSGINSTR_INDEX_OFFSET;
                    if(instrumentValue!=currentInstrument){
                        currentInstrument = instrumentValue;
                        newInstrument = true;
                    }
                }
                if(volume!=null){
                    int volumeValue = volume.getValue()&0xFF;
                    if(volumeValue!=currentVolume){
                        currentVolume = volumeValue;
                        newVolume = true;
                    }
                }
                if(newInstrument || newVolume){
                    byte psgInstValue = (byte)((currentInstrument<<4) + currentVolume);
                    cubeCommands.add(new PsgInst((byte)psgInstValue));
                }
                if(note==null || note.getValue()==NOTE_RELEASE){
                    if(playLength==currentPlayLength){
                        cubeCommands.add(new Wait());
                    }else{
                        cubeCommands.add(new WaitL((byte)playLength));
                        currentPlayLength = playLength;
                    }
                }else{
                    if(mainLoopStartRequiresSustain){
                        cubeCommands.add(new Sustain());
                        mainLoopStartRequiresSustain = false;
                    }else if(!legato && release!=currentRelease){
                        cubeCommands.add(new SetRelease((byte)release));
                        currentRelease = release;
                    }
                    int vibratoDelay = findVibratoDelay(rows, cursor, playLength);
                    if(playLength>currentVibratoDelay && vibratoDelay!=currentVibratoDelay){
                        //byte value = getVibratoValue(rows, cursor, vibratoDelay);
                        int index = 4;
                        int delay = (vibratoDelay/2)&0x0F;
                        byte value = (byte)(index<<4|delay);
                        cubeCommands.add(new Vibrato(value));
                        currentVibratoDelay = vibratoDelay;
                    }                    
                    int noteValue = note.getValue()&0xFF;
                    if(playLength==currentPlayLength){
                        cubeCommands.add(new PsgNote(Pitch.valueOf(F2CPitch.valueOf(noteValue+NOTE_OFFSET).getCubeValue())));
                    }else{
                        cubeCommands.add(new PsgNoteL(Pitch.valueOf(F2CPitch.valueOf(noteValue+NOTE_OFFSET).getCubeValue()), (byte)playLength));
                        currentPlayLength = playLength;
                    }
                }
                cursor+=playLength;
            }
        }
        int firstNoteIndex = findFirstNoteIndex(cubeCommands);
        if(firstNoteIndex>=0){
            secureStartVibrato(cubeCommands, firstNoteIndex);
        }
        psgToneChannel.setCcs(cubeCommands.toArray(new CubeCommand[cubeCommands.size()]));
    }
    
    private static void convertFurnacePatternToPsgNoiseChannel(Pattern[] patterns, int channelIndex, PsgNoiseChannel psgNoiseChannel, int mainLoopStartIndex, int mainLoopEndIndex){
        Row[] rows = patterns[channelIndex].getRows();
        List<CubeCommand> cubeCommands = new ArrayList();
        int cursor = 0;
        boolean mainLoopEndReached = false;
        int currentPlayLength = 0;
        int currentInstrument = -1;
        boolean newInstrument = false;
        int currentVolume = 0;
        boolean newVolume = false;
        int currentRelease = 0;
        int currentVibratoDelay = 0;
        boolean mainLoopStartRequiresSustain = false;
        boolean legato = false;
        int endIndex = -1;
        if(mainLoopEndIndex < 0){
            endIndex = findChannelEndIndex(rows);
        }else{
            endIndex = mainLoopEndIndex;
        }
        if(!channelHasNotes(rows)){
            cubeCommands.add(new ChannelEnd());
        } else{
            while(cursor<rows.length){
                newInstrument = false;
                newVolume = false;
                if(mainLoopEndIndex >0 && cursor==mainLoopStartIndex){
                    cubeCommands.add(new MainLoopStart());
                    currentPlayLength = 0;
                    currentRelease = 0;
                }
                if(mainLoopEndIndex >0 && !mainLoopEndReached && cursor>=mainLoopEndIndex){
                    mainLoopEndReached = true;
                    cubeCommands.add(new MainLoopEnd());
                    break;
                }
                if(mainLoopEndIndex<0 && cursor>=endIndex){
                    cubeCommands.add(new ChannelEnd());
                    break;
                }
                Row row = rows[cursor];
                FNote note = row.getNote();
                Instrument instrument = row.getInstrument();
                Volume volume = row.getVolume();
                List<Effect> effects = row.getEffectList();
                int playLength = findPlayLength(rows, cursor, mainLoopStartIndex, endIndex);
                boolean noteInterruptedByMainLoopStart = isNoteInterruptedByMainLoopStart(rows, cursor, mainLoopStartIndex, playLength);
                boolean noteInterruptedByMainLoopEnd = isNoteInterruptedByMainLoopEnd(rows, cursor, mainLoopEndIndex, playLength);
                int release = findRelease(rows, cursor, playLength);
                if(!legato && release==playLength){
                    boolean legatoActivated = isLegatoActivated(rows, cursor, playLength);
                    if(legatoActivated){
                        cubeCommands.add(new Sustain());
                        legato = true;
                        currentRelease = 0;
                    }
                }else if(legato){
                    boolean legatoDeactivated = isLegatoDeactivated(rows, cursor, playLength);
                    if(legatoDeactivated){
                        legato = false;
                    }
                }
                if((noteInterruptedByMainLoopStart || noteInterruptedByMainLoopEnd) && release==playLength){
                    release = currentRelease;
                    mainLoopStartRequiresSustain = true;
                }
                for(Effect effect : effects){
                    byte type = effect.getType();
                    byte value = effect.getValue();
                    if(type==EFFECT_LEGATO){
                        if(value==0){
                            legato = false;
                        }
                    }
                }
                if(instrument!=null){
                    int instrumentValue = (instrument.getValue()&0xFF) - PSGINSTR_INDEX_OFFSET;
                    if(instrumentValue!=currentInstrument){
                        currentInstrument = instrumentValue;
                        newInstrument = true;
                    }
                }
                if(volume!=null){
                    int volumeValue = volume.getValue()&0xFF;
                    if(volumeValue!=currentVolume){
                        currentVolume = volumeValue;
                        newVolume = true;
                    }
                }
                if(newInstrument || newVolume){
                    byte psgInstValue = (byte)((currentInstrument<<4) + currentVolume);
                    cubeCommands.add(new PsgInst((byte)psgInstValue));
                }
                if(note==null || note.getValue()==NOTE_RELEASE){
                    if(playLength==currentPlayLength){
                        cubeCommands.add(new Wait());
                    }else{
                        cubeCommands.add(new WaitL((byte)playLength));
                        currentPlayLength = playLength;
                    }
                }else{
                    if(mainLoopStartRequiresSustain){
                        cubeCommands.add(new Sustain());
                        mainLoopStartRequiresSustain = false;
                    }else if(!legato && release!=currentRelease){
                        cubeCommands.add(new SetRelease((byte)release));
                        currentRelease = release;
                    }
                    int vibratoDelay = findVibratoDelay(rows, cursor, playLength);
                    if(playLength>currentVibratoDelay && vibratoDelay!=currentVibratoDelay){
                        //byte value = getVibratoValue(rows, cursor, vibratoDelay);
                        int index = 4;
                        int delay = (vibratoDelay/2)&0x0F;
                        byte value = (byte)(index<<4|delay);
                        cubeCommands.add(new Vibrato(value));
                        currentVibratoDelay = vibratoDelay;
                    }                    
                    int noteValue = note.getValue()&0xFF;
                    if(playLength==currentPlayLength){
                        cubeCommands.add(new PsgNote(Pitch.valueOf(F2CPitch.valueOf(noteValue+NOTE_OFFSET).getCubeValue())));
                    }else{
                        cubeCommands.add(new PsgNoteL(Pitch.valueOf(F2CPitch.valueOf(noteValue+NOTE_OFFSET).getCubeValue()), (byte)playLength));
                        currentPlayLength = playLength;
                    }
                }
                cursor+=playLength;
            }
        }
        int firstNoteIndex = findFirstNoteIndex(cubeCommands);
        if(firstNoteIndex>=0){
            secureStartVibrato(cubeCommands, firstNoteIndex);
        }
        psgNoiseChannel.setCcs(cubeCommands.toArray(new CubeCommand[cubeCommands.size()]));
    }
    
    private static int findCubeVolume(int furnaceValue){
        int index = 0;
        int diff = 0xFF;
        for(int i=0;i<DEFAULT_YM_LEVELS.length;i++){
            if(furnaceValue==DEFAULT_YM_LEVELS[i]){
                index = i;
                break;
            }else{
                int candidateDiff = Math.abs(furnaceValue-DEFAULT_YM_LEVELS[i]);
                if(candidateDiff<diff){
                    index = i;
                    diff = candidateDiff;
                }
            }
        }
        return index;
    }
    
    private static void applyStereo(List<CubeCommand> cubeCommands, byte value){
        byte stereoValue = 0;
        switch(value){
            case (byte)0x00 : stereoValue = STEREO_LEFT; break;
            case (byte)0x80 : stereoValue = STEREO_CENTER; break;
            case (byte)0xFF : stereoValue = STEREO_RIGHT; break;
            default : stereoValue = STEREO_CENTER; break;
        }
        Stereo stereo = new Stereo(stereoValue);
        cubeCommands.add(stereo);
    }
    
    private static void secureStartStereo(List<CubeCommand> cubeCommands){
        int stereoCommandIndex = -1;
        for(int i=0;i<cubeCommands.size();i++){
            CubeCommand cc = cubeCommands.get(i);
            if(cc instanceof Stereo){
                stereoCommandIndex = i;
                break;
            }
        }
        if(stereoCommandIndex>=0){
            CubeCommand stereoCommand = cubeCommands.get(stereoCommandIndex);
            cubeCommands.remove(stereoCommandIndex);
            cubeCommands.add(0, stereoCommand);
        }else{
            cubeCommands.add(0, new Stereo(STEREO_CENTER));
        }
    }
    
    private static void secureStartVibrato(List<CubeCommand> cubeCommands, int firstNoteIndex){
        int vibratoCommandIndex = -1;
        for(int i=0;i<cubeCommands.size();i++){
            CubeCommand cc = cubeCommands.get(i);
            if(cc instanceof Vibrato){
                vibratoCommandIndex = i;
                break;
            }
        }
        if(vibratoCommandIndex>=0){
            CubeCommand vibratoCommand = cubeCommands.get(vibratoCommandIndex);
            cubeCommands.remove(vibratoCommandIndex);
            cubeCommands.add(firstNoteIndex, vibratoCommand);
        }else{
            cubeCommands.add(firstNoteIndex, new Vibrato((byte)0));
        }
    }
    
    private static int findFirstNoteIndex(List<CubeCommand> cubeCommands){
        for(int i=0;i<cubeCommands.size();i++){
            CubeCommand cc = cubeCommands.get(i);
            if(cc instanceof Note || cc instanceof NoteL || cc instanceof PsgNote || cc instanceof PsgNoteL){
                return i;
            }
        }
        return -1;
    }
    
    private static void applyDetune(List<CubeCommand> cubeCommands, byte value){
        /* Furnace detune command description : 
        53 xy Set detune (x: operator from 1 to 4 (0 for all ops); y: detune where 3 is center)
        */        
        int detuneValue = value - 3;
        int shiftingValue = Math.abs(detuneValue)<<4;
        if(detuneValue<0){
            shiftingValue+=0x80;
        }
        Shifting shifting = new Shifting((byte)(shiftingValue&0xFF));
        cubeCommands.add(shifting);
    }
    
    private static void applyYmTimer(List<CubeCommand> cubeCommands, byte type, byte value, FurnaceFile ff){
        int timerValue = ((type&0x0F)<<8) + (value&0xFF);
        YmTimer ymTimer = new YmTimer((byte)(F2CPatternConverter.calculateYmTimerB(timerValue, ff.getSongInfo().getSpeed1()&0xFF)&0xFF));
        cubeCommands.add(ymTimer);
    }
    
    private static int applySlide(List<CubeCommand> cubeCommands, byte value, int currentSlide){
            int slideValue = 0x80|(value*2-1);
            if(slideValue!=currentSlide){
                SetSlide setSlide = new SetSlide((byte)(slideValue&0xFF));
                cubeCommands.add(setSlide);
            }
            return slideValue;
    }
    
    private static int applyYmInstrument(List<CubeCommand> cubeCommands, int instrument, int currentInstrument){
        int instrumentValue = instrument - YMINSTR_INDEX_OFFSET;
        if(instrumentValue!=currentInstrument){
            cubeCommands.add(new Inst((byte)instrumentValue));
            currentInstrument = instrumentValue;
        }
        return currentInstrument;
    }
    
    private static int applyYmVolume(List<CubeCommand> cubeCommands, int volume, int currentVolume){
        if(volume!=currentVolume){
                cubeCommands.add(new Vol((byte)volume));
                currentVolume = volume;
        }
        return currentVolume;
    }
    
    private static int findChannelEndIndex(Row[] rows){
        int cursor = 0;
        while(cursor<rows.length){
            Row row = rows[cursor];
            FNote note = row.getNote();
            if(note!=null && note.getValue()==NOTE_OFF){
                return cursor;
            }
            cursor++;
        }
        System.out.println("WARNING : reached end of channel data without finding an OFF note for channel end. Cursor="+cursor);
        return cursor;
    }
    
    private static int findPlayLength(Row[] rows, int startIndex, int mainLoopStartIndex, int endIndex){
        int cursor = startIndex+1;
        boolean released = false;
        while(cursor<rows.length){
            Row row = rows[cursor];
            if((cursor-startIndex)==255
                    || cursor==mainLoopStartIndex
                    || cursor==endIndex
                    || row.getNote()!=null
                    || row.getInstrument()!=null
                    || row.getVolume()!=null){
                FNote note = row.getNote();
                Instrument instrument = row.getInstrument();
                Volume vol = row.getVolume();
                List<Effect> effects = row.getEffectList();
                if(cursor==mainLoopStartIndex || instrument!=null || vol!=null){
                    break;
                }
                if(!released && note!=null && note.getValue()==NOTE_RELEASE && cursor!=endIndex){
                    released = true;
                }else{
                    break;
                }
            }
            cursor++;
        }
        return cursor-startIndex;
    }
    
    private static byte getNextRowPortamentoValue(Row[] rows, int cursor){
        Row row = rows[cursor+1];
        List<Effect> effects = row.getEffectList();
        for(Effect effect : effects){
            if(row.getNote()!=null && effect.getType()==EFFECT_PORTAMENTO){
                return effect.getValue();
            }
        }
        return 0;
    }
    
    private static boolean isNoteInterruptedByMainLoopStart(Row[] rows, int startIndex, int mainLoopStartIndex, int playLength){
        if((startIndex+playLength)==mainLoopStartIndex){
            return true;
        }else{
            return false;
        }
    }
    
    private static boolean isNoteInterruptedByMainLoopEnd(Row[] rows, int startIndex, int mainLoopEndIndex, int playLength){
        if((startIndex+playLength)==mainLoopEndIndex){
            return true;
        }else{
            return false;
        }
    }
    
    private static boolean isLegatoActivated(Row[] rows, int startIndex, int playLength){
        int cursor = startIndex+playLength;
        if(cursor>=rows.length){
            System.out.println("WARNING - isLegatoActivated out of bounds : rows.length="+rows.length+", cursor="+cursor+". Cursor decreased to last row.");
            cursor = rows.length-1;
        }
        while(cursor>startIndex){
            Row row = rows[cursor];
            List<Effect> effects = row.getEffectList();
            for(Effect effect : effects){
                if(effect.getType()==EFFECT_LEGATO && effect.getValue()!=0){
                    return true;
                }
            }
            cursor--;
        }
        return false;
    }
    
    private static boolean isLegatoDeactivated(Row[] rows, int startIndex, int playLength){
        int cursor = startIndex+playLength;
        if(cursor>=rows.length){
            System.out.println("WARNING - isLegatoDeactivated out of bounds : rows.length="+rows.length+", cursor="+cursor+". Cursor decreased to last row.");
            cursor = rows.length-1;
        }        
        while(cursor>startIndex){
            Row row = rows[cursor];
            List<Effect> effects = row.getEffectList();
            for(Effect effect : effects){
                if(effect.getType()==EFFECT_LEGATO && effect.getValue()==0){
                    return true;
                }
            }
            cursor--;
        }
        return false;
    }
    
    private static int findRelease(Row[] rows, int startIndex, int playLength){
        int release = 1;
        int cursor = startIndex+playLength-1;
        while(cursor>startIndex){
            Row row = rows[cursor];
            if(row.getNote()!=null && row.getNote().getValue()==NOTE_RELEASE){
                break;
            }
            release++;
            cursor--;
        }
        return release;
    }
    
    private static int findVibratoDelay(Row[] rows, int startIndex, int playLength){
        int delay = 0;
        while(delay<playLength){
            Row row = rows[startIndex+delay];
            List<Effect> effects = row.getEffectList();
            for(Effect effect : effects){
                if(effect.getType()==EFFECT_VIBRATO && effect.getValue()!=0){
                    //System.out.println("Effect:0x"+String.format("%02X", effect.getType())+",Value:0x"+String.format("%02X", effect.getValue()));
                    return delay;
                }
            }
            delay++;
        }
        return 0;
    }
    
    private static byte getVibratoValue(Row[] rows, int startIndex, int delay){
        byte value = 0;
        Row row = rows[delay];
        List<Effect> effects = row.getEffectList();
        for(Effect effect : effects){
            if(effect.getType()==EFFECT_VIBRATO){
                value = effect.getValue();
            }
        }
        return value;
    }
    
    private static void applyWait(List<CubeCommand> cubeCommands, int previousPlayLength, int playLength){
        if(playLength==previousPlayLength){
            cubeCommands.add(new Wait());
        }else{
            cubeCommands.add(new WaitL((byte)playLength));
            previousPlayLength = playLength;
        }
    }
    
    private static boolean channelHasNotes(Row[] rows){
        for(int i=0;i<rows.length;i++){
            if(rows[i].getNote()!=null && rows[i].getNote().getValue()!=NOTE_RELEASE){
                return true;
            }
        }
        return false;
    }
    
}
