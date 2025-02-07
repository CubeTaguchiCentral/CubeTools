/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.sound.formats.furnace.file.section;

import com.sfc.sf2.sound.formats.furnace.file.FurnaceFile;
import com.sfc.sf2.sound.formats.furnace.pattern.Effect;
import com.sfc.sf2.sound.formats.furnace.pattern.Instrument;
import com.sfc.sf2.sound.formats.furnace.pattern.FNote;
import com.sfc.sf2.sound.formats.furnace.pattern.Pattern;
import com.sfc.sf2.sound.formats.furnace.pattern.Row;
import com.sfc.sf2.sound.formats.furnace.pattern.Volume;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 *
 * @author Wiz
 */
public class PatternBlock {
    
    private static final int MAX_PATTERN_LENGTH = 256;
    
    private String blockId = "PATN";
    private int blockSize = 0;
    private byte subsong = 0;
    private byte channel = 0;
    private short patternIndex = 0;
    private String name = "";
    private byte[] rawData = null;
    private Pattern pattern = null;

    public PatternBlock(Pattern pattern, int channel, int patternIndex) {
        this.pattern = pattern;
        this.channel = (byte)(0xFF&channel);
        this.patternIndex = (byte)(0xFF&patternIndex);
    }

    public PatternBlock(byte[] data, int startPointer) {
        ByteBuffer bb = ByteBuffer.wrap(data, startPointer, data.length-startPointer);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.position(startPointer);
        blockId = getString(bb, 4);
        blockSize = bb.getInt();
        subsong = bb.get();
        channel = bb.get();
        patternIndex = bb.getShort();
        name = getString(bb);
        rawData = getByteArray(bb, blockSize-1-1-2-name.length()-1);
        pattern = new Pattern(rawData);
    }

    public PatternBlock() {

    }

    private byte[] getByteArray(ByteBuffer bb, int length){
        return FurnaceFile.getByteArray(bb, length);
    }

    private int[] getIntArray(ByteBuffer bb, int length){
        return FurnaceFile.getIntArray(bb, length);
    }

    private float[] getFloatArray(ByteBuffer bb, int length){
        return FurnaceFile.getFloatArray(bb, length);
    }

    private String getString(ByteBuffer bb){
        return FurnaceFile.getString(bb);
    }

    private String getString(ByteBuffer bb, int length){
        return FurnaceFile.getString(bb, length);
    }
    
    private int findStringLength(ByteBuffer bb, int cursor){
        return FurnaceFile.findStringLength(bb, cursor);
    }

    private String[] getStringArray(ByteBuffer bb, int length){
        return FurnaceFile.getStringArray(bb, length);
    }

    public String getBlockId() {
        return blockId;
    }

    public void setBlockId(String blockId) {
        this.blockId = blockId;
    }

    public int getBlockSize() {
        return blockSize;
    }

    public void setBlockSize(int blockSize) {
        this.blockSize = blockSize;
    }

    public byte getSubsong() {
        return subsong;
    }

    public void setSubsong(byte subsong) {
        this.subsong = subsong;
    }

    public byte getChannel() {
        return channel;
    }

    public void setChannel(byte channel) {
        this.channel = channel;
    }

    public short getPatternIndex() {
        return patternIndex;
    }

    public void setPatternIndex(short patternIndex) {
        this.patternIndex = patternIndex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getRawData() {
        return rawData;
    }

    public void setRawData(byte[] rawData) {
        this.rawData = rawData;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }
    
    public byte[] toByteArray(){
        if(rawData==null){
            rawData = produceRawData();
            blockSize = findLength()-4;
        }
        ByteBuffer bb = ByteBuffer.allocate(findLength());
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.position(0);
        bb.put(blockId.getBytes(StandardCharsets.UTF_8));
        bb.putInt(findLength()-4-4);
        bb.put(subsong);
        bb.put(channel);
        bb.putShort(patternIndex);
        bb.put(name.getBytes(StandardCharsets.UTF_8));
        bb.put((byte)0);
        bb.put(rawData);
        return bb.array();
    }
    
    public byte[] produceRawData(){
        byte[] bytes = null;
        byte b = 0;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Row[] rows = pattern.getRows();
        int cursor = 0;
        while(cursor<rows.length){
            Row r = rows[cursor];
            if(r.isEmpty()){
                int skipLength = 1;
                cursor++;
                while(cursor<rows.length && rows[cursor].isEmpty()){
                    skipLength++;
                    cursor++;
                }
                while(skipLength>=128){
                    baos.write((byte)0xFE);
                    skipLength-=128;
                }
                if(skipLength==1){
                    baos.write((byte)0);
                }else if(skipLength>1){
                    baos.write((byte)(0x80+(skipLength-2)));
                }
            }else{
                FNote note = r.getNote();
                Instrument instrument = r.getInstrument();
                Volume volume = r.getVolume();
                List<Effect> effectList = r.getEffectList();
                boolean notePresent = note!=null;
                boolean instrumentPresent = instrument!=null;
                boolean volumePresent = volume!=null;
                boolean effect0TypePresent = effectList.size()>0;
                boolean effect0ValuePresent = effectList.size()>0 && effectList.get(0).getValue()!=null;
                boolean otherEffect0TypePresent = effectList.size()>0;
                boolean otherEffect0ValuePresent = effectList.size()>0 && effectList.get(0).getValue()!=null;
                boolean otherEffect1TypePresent = effectList.size()>1;
                boolean otherEffect1ValuePresent = effectList.size()>1 && effectList.get(1).getValue()!=null;
                boolean otherEffect2TypePresent = effectList.size()>2;
                boolean otherEffect2ValuePresent = effectList.size()>2 && effectList.get(2).getValue()!=null;
                boolean otherEffect3TypePresent = effectList.size()>3;
                boolean otherEffect3ValuePresent = effectList.size()>3 && effectList.get(3).getValue()!=null;
                boolean otherEffect4TypePresent = effectList.size()>4;
                boolean otherEffect4ValuePresent = effectList.size()>4 && effectList.get(4).getValue()!=null;
                boolean otherEffect5TypePresent = effectList.size()>5;
                boolean otherEffect5ValuePresent = effectList.size()>5 && effectList.get(5).getValue()!=null;
                boolean otherEffect6TypePresent = effectList.size()>6;
                boolean otherEffect6ValuePresent = effectList.size()>6 && effectList.get(6).getValue()!=null;
                boolean otherEffect7TypePresent = effectList.size()>7;
                boolean otherEffect7ValuePresent = effectList.size()>7 && effectList.get(7).getValue()!=null;
                boolean otherEffects03Present = otherEffect0TypePresent || otherEffect1TypePresent || otherEffect2TypePresent || otherEffect3TypePresent;
                boolean otherEffects47Present = otherEffect4TypePresent || otherEffect5TypePresent || otherEffect6TypePresent || otherEffect7TypePresent;
                byte nextByte = (byte)(
                        ((notePresent?1:0)<<0)
                        + ((instrumentPresent?1:0)<<1)
                        + ((volumePresent?1:0)<<2)
                        + ((effect0TypePresent?1:0)<<3)
                        + ((effect0ValuePresent?1:0)<<4)
                        + ((otherEffects03Present?1:0)<<5)
                        + ((otherEffects47Present?1:0)<<6)
                    );
                baos.write(nextByte);
                if(otherEffects03Present){
                    nextByte = (byte)(
                            ((otherEffect0TypePresent?1:0)<<0)
                            + ((otherEffect0ValuePresent?1:0)<<1)
                            + ((otherEffect1TypePresent?1:0)<<2)
                            + ((otherEffect1ValuePresent?1:0)<<3)
                            + ((otherEffect2TypePresent?1:0)<<4)
                            + ((otherEffect2ValuePresent?1:0)<<5)
                            + ((otherEffect3TypePresent?1:0)<<6)
                            + ((otherEffect3ValuePresent?1:0)<<7)
                        );
                    baos.write(nextByte);  
                    if(otherEffects47Present){
                        nextByte = (byte)(
                                ((otherEffect4TypePresent?1:0)<<0)
                                + ((otherEffect4ValuePresent?1:0)<<1)
                                + ((otherEffect5TypePresent?1:0)<<2)
                                + ((otherEffect5ValuePresent?1:0)<<3)
                                + ((otherEffect6TypePresent?1:0)<<4)
                                + ((otherEffect6ValuePresent?1:0)<<5)
                                + ((otherEffect7TypePresent?1:0)<<6)
                                + ((otherEffect7ValuePresent?1:0)<<7)
                            );
                        baos.write(nextByte);  
                    }
                }
                if(notePresent){
                    baos.write(note.getValue());
                }
                if(instrumentPresent){
                    baos.write(instrument.getValue());
                }
                if(volumePresent){
                    baos.write(volume.getValue());
                }
                /*if(effect0TypePresent){
                    baos.write(effectList.get(0).getType());
                    if(effect0ValuePresent){
                        baos.write((byte)effectList.get(0).getValue());
                    }
                }*/
                if(otherEffect0TypePresent){
                    baos.write(effectList.get(0).getType());
                    if(otherEffect0ValuePresent){
                        baos.write((byte)effectList.get(0).getValue());
                    }
                }
                if(otherEffect1TypePresent){
                    baos.write(effectList.get(1).getType());
                    if(otherEffect1ValuePresent){
                        baos.write((byte)effectList.get(1).getValue());
                    }
                }
                if(otherEffect2TypePresent){
                    baos.write(effectList.get(2).getType());
                    if(otherEffect2ValuePresent){
                        baos.write((byte)effectList.get(2).getValue());
                    }
                }
                if(otherEffect3TypePresent){
                    baos.write(effectList.get(3).getType());
                    if(otherEffect3ValuePresent){
                        baos.write((byte)effectList.get(3).getValue());
                    }
                }
                if(otherEffect4TypePresent){
                    baos.write(effectList.get(4).getType());
                    if(otherEffect4ValuePresent){
                        baos.write((byte)effectList.get(4).getValue());
                    }
                }
                if(otherEffect5TypePresent){
                    baos.write(effectList.get(5).getType());
                    if(otherEffect5ValuePresent){
                        baos.write((byte)effectList.get(5).getValue());
                    }
                }
                if(otherEffect6TypePresent){
                    baos.write(effectList.get(6).getType());
                    if(otherEffect6ValuePresent){
                        baos.write((byte)effectList.get(6).getValue());
                    }
                }
                if(otherEffect7TypePresent){
                    baos.write(effectList.get(7).getType());
                    if(otherEffect7ValuePresent){
                        baos.write((byte)effectList.get(7).getValue());
                    }
                }
                cursor++;
            }
        }
        baos.write((byte)0xFF);
        bytes = baos.toByteArray();
        return bytes;
    }
    
    public int findLength(){
        if(rawData==null){
            rawData = produceRawData();
            blockSize = findLength()-4;
        }
        return 4+4+1+1+2+name.length()+1+rawData.length;
    }
}
