/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sega.md.snd.convert.furnacetocube;

import com.sega.md.snd.formats.cube.CubeChannel;
import com.sega.md.snd.formats.cube.MusicEntry;
import com.sega.md.snd.formats.cube.channel.DacChannel;
import com.sega.md.snd.formats.cube.channel.PsgNoiseChannel;
import com.sega.md.snd.formats.cube.channel.PsgToneChannel;
import com.sega.md.snd.formats.cube.channel.YmChannel;
import com.sega.md.snd.formats.furnace.file.FurnaceFile;
import com.sega.md.snd.formats.furnace.file.section.AssetDirectoriesBlock;
import com.sega.md.snd.formats.furnace.file.section.AssetDirectory;
import com.sega.md.snd.formats.furnace.file.section.InstrumentBlock;
import com.sega.md.snd.formats.furnace.file.section.PatternBlock;
import com.sega.md.snd.formats.furnace.pattern.Instrument;
import com.sega.md.snd.formats.furnace.pattern.Pattern;
import com.sega.md.snd.formats.furnace.pattern.Row;

/**
 *
 * @author Wiz
 */
public class F2CMusicFileconverter {
    
    private static final int CHANNEL_COUNT = 10;
    
    public static MusicEntry convertFurnaceFile(FurnaceFile ff){
        MusicEntry me = new MusicEntry();
        
        me.setName(ff.getSongInfo().getSongName());
        me.setYm6InDacMode(isYm6InDacMode(ff));
        me.setYmTimerBValue((byte)(F2CPatternConverter.calculateYmTimerB(ff.getSongInfo().getTicksPerSecond(), ff.getSongInfo().getSpeed1()&0xFF)&0xFF));
        
        CubeChannel[] channels = new CubeChannel[CHANNEL_COUNT];
        
        channels[0] = new YmChannel();
        channels[1] = new YmChannel();
        channels[2] = new YmChannel();
        channels[3] = new YmChannel();
        channels[4] = new YmChannel();
        if(me.isYm6InDacMode()){
            channels[5] = new DacChannel();
        }else{
            channels[5] = new YmChannel();
        }
        channels[6] = new PsgToneChannel();
        channels[7] = new PsgToneChannel();
        channels[8] = new PsgToneChannel();
        channels[9] = new PsgNoiseChannel();
        
        me.setChannels(channels);
        
        return me;
    }
    
    private static boolean isYm6InDacMode(FurnaceFile ff){
        PatternBlock[] patternBlocks = ff.getPatterns();
        InstrumentBlock[] instrumentBlocks = ff.getInstruments();
        for(PatternBlock pb : patternBlocks){
            if(pb.getChannel()==5){
                for(Row r : pb.getPattern().getRows()){
                    if(r.getInstrument()!=null){
                        Instrument instrument = r.getInstrument();
                        byte instrumentValue = instrument.getValue();
                        if(instrumentBlocks[instrumentValue&0xFF].getInstrumentType()==4){
                            return true;
                        }else{
                            return false;
                        }
                    }
                }
            }
        }
        return false;
    }
    
}
