/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sega.md.snd.convert.furnacetocube;

import com.sega.md.snd.convert.cubetofurnace.C2FPatternConverter;
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
import com.sega.md.snd.formats.furnace.pattern.Effect;
import com.sega.md.snd.formats.furnace.pattern.Instrument;
import com.sega.md.snd.formats.furnace.pattern.Pattern;
import com.sega.md.snd.formats.furnace.pattern.Row;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Wiz
 */
public class F2CMusicFileconverter {
    
    private static final int CHANNEL_COUNT = 10;
    
    private static byte EFFECT_JUMP_TO_ORDER = (byte)0x0B;
    private static byte EFFECT_JUMP_TO_NEXT_PATTERN = (byte)0x0D;
    
    public static MusicEntry convertFurnaceFile(FurnaceFile ff){
        MusicEntry me = new MusicEntry();
        
        me.setName(ff.getSongInfo().getSongName());
        me.setYm6InDacMode(isYm6InDacMode(ff));
        me.setYmTimerBValue((byte)(F2CPatternConverter.calculateYmTimerB(ff.getSongInfo().getTicksPerSecond(), ff.getSongInfo().getSpeed1()&0xFF)&0xFF));
        
        Pattern[] aggregatedFurnacePatterns = extractPatterns(ff);
        
        int mainLoopStartIndex = getMainLoopStartIndex(aggregatedFurnacePatterns, ff.getSongInfo().getPatternLength());   
        int mainLoopEndIndex = getMainLoopEndIndex(aggregatedFurnacePatterns, ff.getSongInfo().getPatternLength());        
        
        CubeChannel[] cubeChannels = convertPatterns(aggregatedFurnacePatterns, me.isYm6InDacMode(), mainLoopStartIndex, mainLoopEndIndex);
        
        me.setChannels(cubeChannels);
        
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
    
    private static Pattern[] extractPatterns(FurnaceFile ff){
        
        byte[] orders = ff.getSongInfo().getOrders();
        System.out.println("Orders "+Arrays.toString(orders));
        short orderLength = ff.getSongInfo().getOrdersLength();
        int channelCount = orders.length / orderLength;
        byte[][] channelPatternIndexes = new byte[channelCount][];
        for(int i=0;i<channelCount;i++){
            byte[] patternIndexes = new byte[orderLength];
            for(int j=0;j<orderLength;j++){
                patternIndexes[j] = orders[i*orderLength+j];
            }
            System.out.println("Channel "+i+" : "+Arrays.toString(patternIndexes));
            channelPatternIndexes[i] = patternIndexes;
        }
        
        List<Pattern>[] patternLists = new ArrayList[channelCount];
        for(int i=0;i<patternLists.length;i++){
            patternLists[i] = new ArrayList(orderLength);
        }
        for(PatternBlock patternBlock : ff.getPatterns()){
            Pattern pattern = patternBlock.getPattern();
            int patternIndex = patternBlock.getPatternIndex();
            byte channelIndex = patternBlock.getChannel();
            patternLists[channelIndex].add(patternIndex, pattern);
        }
        
        Pattern[][] channelPatterns = new Pattern[channelCount][];
        
        Pattern[] patterns = new Pattern[channelCount];
        
        for(int i=0;i<channelCount;i++){
            channelPatterns[i] = patternLists[i].toArray(new Pattern[patternLists[i].size()]);
            Pattern aggregatedPattern = new Pattern();
            List<Row> rowList = new ArrayList();
            for(Pattern p : channelPatterns[i]){
                rowList.addAll(Arrays.asList(p.getRows()));
            }
            aggregatedPattern.setRows(rowList.toArray(new Row[rowList.size()]));
            patterns[i] = aggregatedPattern;
        }
        
        return patterns;
    }
    
    private int getMainLoopStart(Pattern[] patterns){
        int rowIndex = -1;
        for(Pattern pattern : patterns){
            Row[] rows = pattern.getRows();
            int cursor = 0;
            for(Row row : rows){
                for(Effect effect : row.getEffectList()){
                    if(effect.getType()==0x0
                            && effect.getType()==0x0){
                        
                    }
                }
                cursor++;
            }
        }
        return rowIndex;
    }
    
    public static CubeChannel[] convertPatterns(Pattern[] patterns, boolean ym6InDacMode, int mainLoopStartIndex, int mainLoopEndIndex){
        CubeChannel[] channels = new CubeChannel[CHANNEL_COUNT];
        
        channels[0] = F2CPatternConverter.convertFurnacePatternToCubeChannel(patterns, 0, new YmChannel(), mainLoopStartIndex, mainLoopEndIndex);
        channels[1] = F2CPatternConverter.convertFurnacePatternToCubeChannel(patterns, 1, new YmChannel(), mainLoopStartIndex, mainLoopEndIndex);
        channels[2] = F2CPatternConverter.convertFurnacePatternToCubeChannel(patterns, 2, new YmChannel(), mainLoopStartIndex, mainLoopEndIndex);
        channels[3] = F2CPatternConverter.convertFurnacePatternToCubeChannel(patterns, 3, new YmChannel(), mainLoopStartIndex, mainLoopEndIndex);
        channels[4] = F2CPatternConverter.convertFurnacePatternToCubeChannel(patterns, 4, new YmChannel(), mainLoopStartIndex, mainLoopEndIndex);
        if(ym6InDacMode){
            channels[5] = F2CPatternConverter.convertFurnacePatternToCubeChannel(patterns, 5, new DacChannel(), mainLoopStartIndex, mainLoopEndIndex);
        }else{
            channels[5] = F2CPatternConverter.convertFurnacePatternToCubeChannel(patterns, 5, new YmChannel(), mainLoopStartIndex, mainLoopEndIndex);
        }
        channels[6] = F2CPatternConverter.convertFurnacePatternToCubeChannel(patterns, 6, new PsgToneChannel(), mainLoopStartIndex, mainLoopEndIndex);
        channels[7] = F2CPatternConverter.convertFurnacePatternToCubeChannel(patterns, 7, new PsgToneChannel(), mainLoopStartIndex, mainLoopEndIndex);
        channels[8] = F2CPatternConverter.convertFurnacePatternToCubeChannel(patterns, 8, new PsgToneChannel(), mainLoopStartIndex, mainLoopEndIndex);
        channels[9] = F2CPatternConverter.convertFurnacePatternToCubeChannel(patterns, 9, new PsgNoiseChannel(), mainLoopStartIndex, mainLoopEndIndex);
        
        return channels;
    }
    
    private static int getMainLoopStartIndex(Pattern[] patterns, int patternLength){
        for(Pattern pattern : patterns){
            for(Row row : pattern.getRows()){
                for(Effect effect1 : row.getEffectList()){
                    if(effect1.getType()==EFFECT_JUMP_TO_ORDER){
                        int orderIndex = effect1.getValue()&0xFF;
                        for(Effect effect2 : row.getEffectList()){
                            if(effect2.getType()==EFFECT_JUMP_TO_NEXT_PATTERN){
                                int rowIndex = effect2.getValue()&0xFF;
                                return patternLength*orderIndex+rowIndex;
                            }
                        }
                        return patternLength*orderIndex;
                    }
                }
            }
        }
        return -1;
    }
    
    private static int getMainLoopEndIndex(Pattern[] patterns, int patternLength){
        int cursor;
        for(Pattern pattern : patterns){
            cursor = 0;
            for(Row row : pattern.getRows()){
                for(Effect effect1 : row.getEffectList()){
                    if(effect1.getType()==EFFECT_JUMP_TO_ORDER){
                        return cursor;
                    }
                }
                cursor++;
            }
        }
        return -1;
    }
}
