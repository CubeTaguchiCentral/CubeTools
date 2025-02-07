/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.sound.formats.furnace.pattern;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Wiz
 */
public class Pattern {
    
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
    
    public Pattern(){
        rows = new Row[0];
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
                    r.setNote(new FNote(bb.get()));
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
    
    public Row[] getRows() {
        return rows;
    }

    public void setRows(Row[] rows) {
        this.rows = rows;
    }
    
    public static int calculateTicksPersSecond(byte ymTimerB, int speed){  
        float timerPeriod = (8*144) * (PATTERN_LENGTH - (0xFF&ymTimerB)) / (YM2612_INPUT_FREQUENCY/2);
        float timerFrequency = 1/timerPeriod * speed;
        return Math.round(timerFrequency);
    }
}
