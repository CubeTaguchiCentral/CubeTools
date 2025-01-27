/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.sound.convert.io.cubetofurnace;

import com.sfc.sf2.sound.convert.io.cube.MusicEntry;
import com.sfc.sf2.sound.convert.io.furnace.PatternRange;
import com.sfc.sf2.sound.convert.io.furnace.file.FurnaceFile;
import com.sfc.sf2.sound.convert.io.furnace.file.section.PatternBlock;
import com.sfc.sf2.sound.convert.io.furnace.pattern.Effect;
import com.sfc.sf2.sound.convert.io.furnace.pattern.Pattern;
import com.sfc.sf2.sound.convert.io.furnace.pattern.Row;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Wiz
 */
public class C2FFileConverter {
    
    private static final int CHANNEL_COUNT = 10;
    private static final int PATTERN_LENGTH = 256;
    
    public static FurnaceFile convertMusicEntry(MusicEntry me, FurnaceFile ff){
        
        ff.getSongInfo().setPatternLength((short)PATTERN_LENGTH);

        /* Stateful converters */
        C2FPatternConverter[] converters = initializeConverters();
        Pattern[][] splitPatterns = null;

        if(!me.hasMainLoop()){
            /* No loop :
            - Extract
            - Fill
            - Split
            - Add "stop song" effect
            */
            Pattern[] patterns = convertPatterns(me, converters, false, false); /* Extract whole content */
            fillChannelsToMaxLength(patterns);
            splitPatterns = splitPatterns(patterns, PATTERN_LENGTH);
            Row[] rows = splitPatterns[splitPatterns.length-1][0].getRows();
            List<Row> rowList = new ArrayList();
            rowList.addAll(Arrays.asList(rows));
            Row row = new Row();
            row.getEffectList().add(new Effect(0xFF,0x00));
            rowList.add(row);
            splitPatterns[splitPatterns.length-1][0].setRows(rowList.toArray(new Row[0]));
        }else{              
            
            /* Loop :
            - Extract introPatterns
            - Extract loop from introPatterns's end context
            - Shorter channels repeat loop to max length
            - Longest introPatterns channel jumps back to loop start
            - Concatenate introPatterns and loop
            - Fill to max Length
            */            
            
            /* Target :
            - Extract introPatterns
            - Extract loop from introPatterns's end context
            - Longest introPatterns channel jumps back to loop start
            - Concatenate introPatterns and loop
            - Shorter channels repeat loop to max length
            - Fill to max Length (empty channels)
            */
            
            Pattern[] introPatterns = convertPatterns(me, converters, true, false); /* Extract introPatterns */
            int longestIntroChannel = 0;
            int longestIntroChannelLength = 0;
            for (int i=0;i<introPatterns.length;i++){
                if(longestIntroChannelLength<introPatterns[i].getRows().length){
                    longestIntroChannelLength = introPatterns[i].getRows().length;
                    longestIntroChannel = i;
                }
            }
            int longestIntroChannelMainLoopStartPattern = (longestIntroChannelLength) / PATTERN_LENGTH;
            int longestIntroChannelMainLoopStartPosition = (longestIntroChannelLength) % PATTERN_LENGTH;

            Pattern[] mainLoopPatterns = convertPatterns(me, converters, false, true); /* Extract main loop */
            
            Pattern[] concatenatedPatterns = concatenatePatterns(introPatterns, mainLoopPatterns);

            repeatMainLoopToMaxLength(concatenatedPatterns, converters);

            int longestChannel = 0;
            int longestChannelLength = 0;
            for (int i=0;i<concatenatedPatterns.length;i++){
                if(longestChannelLength<concatenatedPatterns[i].getRows().length){
                    longestChannelLength = concatenatedPatterns[i].getRows().length;
                    longestChannel = i;
                }
            }
            Row[] rows = concatenatedPatterns[longestChannel].getRows();
            Row longestChannelLastRow = rows[concatenatedPatterns[longestChannel].getRows().length-1];
            longestChannelLastRow.getEffectList().add(new Effect(0x0B,longestIntroChannelMainLoopStartPattern));
            longestChannelLastRow.getEffectList().add(new Effect(0x0D,longestIntroChannelMainLoopStartPosition));
            
            fillChannelsToMaxLength(concatenatedPatterns); /* empty channels */

            splitPatterns = splitPatterns(concatenatedPatterns, PATTERN_LENGTH);
        }            

        List<PatternBlock> pbList = new ArrayList();
        List<Byte> orderList = new ArrayList();
        int orderLength = splitPatterns.length;
        for(int i=0;i<splitPatterns[0].length;i++){
            for(int j=0;j<orderLength;j++){
                pbList.add(new PatternBlock(splitPatterns[j][i],i,j));
                orderList.add((byte)(0xFF&j));
            }
        }

        PatternBlock[] pbs = new PatternBlock[pbList.size()];
        ff.setPatterns(pbList.toArray(pbs));
        byte[] orders = new byte[orderList.size()];
        for(int i=0;i<orderList.size();i++){
            orders[i] = (byte)orderList.get(i);
        }
        ff.getSongInfo().setOrders(orders);
        ff.getSongInfo().setOrdersLength((short)(0xFFFF&orderLength));

        int ticksPerSecond = C2FPatternConverter.calculateTicksPersSecond(me.getYmTimerBValue(), ff.getSongInfo().getSpeed1());
        //System.out.println("Timer B value "+Integer.toString(0xFF&me.getYmTimerBValue())+" -> "+ticksPerSecond+" ticks per second");
        ff.getSongInfo().setTicksPerSecond(ticksPerSecond);

        C2FYmInstrumentConverter.convertYmInstruments(me, ff);

        C2FSampleConverter.convertSamples(me, ff);
        
        return ff;
    }
    
    public static Pattern[] convertPatterns(MusicEntry me, C2FPatternConverter[] converters, boolean introOnly, boolean mainLoopOnly){
        Pattern[] patterns = new Pattern[CHANNEL_COUNT];
        patterns[0] = converters[0].convertCubeChannelToFurnacePattern(me.getChannels()[0], Pattern.TYPE_FM, introOnly, mainLoopOnly);
        patterns[1] = converters[1].convertCubeChannelToFurnacePattern(me.getChannels()[1], Pattern.TYPE_FM, introOnly, mainLoopOnly);
        patterns[2] = converters[2].convertCubeChannelToFurnacePattern(me.getChannels()[2], Pattern.TYPE_FM, introOnly, mainLoopOnly);
        patterns[3] = converters[3].convertCubeChannelToFurnacePattern(me.getChannels()[3], Pattern.TYPE_FM, introOnly, mainLoopOnly);
        patterns[4] = converters[4].convertCubeChannelToFurnacePattern(me.getChannels()[4], Pattern.TYPE_FM, introOnly, mainLoopOnly);
        if(!me.isYm6InDacMode()){
            patterns[5] = converters[5].convertCubeChannelToFurnacePattern(me.getChannels()[5], Pattern.TYPE_FM, introOnly, mainLoopOnly);
        }else{
            patterns[5] = converters[5].convertCubeChannelToFurnacePattern(me.getChannels()[5], Pattern.TYPE_DAC, introOnly, mainLoopOnly);
        }
        patterns[6] = converters[6].convertCubeChannelToFurnacePattern(me.getChannels()[6], Pattern.TYPE_PSGTONE, introOnly, mainLoopOnly);
        patterns[7] = converters[7].convertCubeChannelToFurnacePattern(me.getChannels()[7], Pattern.TYPE_PSGTONE, introOnly, mainLoopOnly);
        patterns[8] = converters[8].convertCubeChannelToFurnacePattern(me.getChannels()[8], Pattern.TYPE_PSGTONE, introOnly, mainLoopOnly);
        patterns[9] = converters[9].convertCubeChannelToFurnacePattern(me.getChannels()[9], Pattern.TYPE_PSGNOISE, introOnly, mainLoopOnly);
        return patterns;
    }
    
    public static int getMaxLength(Pattern[] patterns){
        int maxLength = 0;
        for (int i=0;i<patterns.length;i++){
            if(maxLength<patterns[i].getRows().length){
                maxLength = patterns[i].getRows().length;
            }
        }
        return maxLength;
    }
    
    public static int getLongestChannel(Pattern[] patterns){
        int maxLength = 0;
        int channel = 0;
        for (int i=0;i<patterns.length;i++){
            if(maxLength<patterns[i].getRows().length){
                maxLength = patterns[i].getRows().length;
                channel = i;
            }
        }
        return channel;
    }
    
    public static void fillChannelsToMaxLength(Pattern[] patterns){
        int maxLength = getMaxLength(patterns);
        for (int i=0;i<patterns.length;i++){
            Row[] rows = patterns[i].getRows();
            patterns[i].setRows(fillToMaxLength(rows, maxLength));
        }
    }
    
    public static Row[] fillToMaxLength(Row[] rows, int maxLength){
        if(rows.length<maxLength){
            Row[] newRows = new Row[maxLength];
            System.arraycopy(rows, 0, newRows, 0, rows.length);
            for(int j=0;(rows.length+j)<maxLength;j++){
                Row voidRow = new Row();
                newRows[rows.length+j] = voidRow;
            }
            return newRows;
        }else{
            return rows;
        }
    }
    
    public static void repeatMainLoopToMaxLength(Pattern[] patterns, C2FPatternConverter[] converters){
        int maxLength = getMaxLength(patterns);
        for (int i=0;i<patterns.length;i++){
            if(patterns[i].getRows()!=null && patterns[i].getRows().length>0){
                int mainLoopStartPosition = converters[i].getMainLoopStartPosition();
                patterns[i].setRows(repeat(patterns[i].getRows(), mainLoopStartPosition, maxLength));
            }
        }
    }
    
    public static Row[] repeat(Row[] rows, int start, int newLength) {
        Row[] newRows = Arrays.copyOf(rows, newLength);
        int sourceCursor = start;
        int targetCursor = rows.length;
        while(targetCursor<newRows.length){
            newRows[targetCursor] = newRows[sourceCursor].clone();
            sourceCursor++;
            targetCursor++;
        }
        return newRows;
    }
    
    public static Pattern[] concatenatePatterns(Pattern[] intro, Pattern[] mainLoop){
        Pattern[] patterns = new Pattern[CHANNEL_COUNT];
        for (int i=0;i<CHANNEL_COUNT;i++){
            Row[] introRows = intro[i].getRows();
            Row[] mainLoopRows = mainLoop[i].getRows();
            Row[] rows = concatenate(introRows, mainLoopRows);
            Pattern fc = new Pattern();
            fc.setRows(rows);
            patterns[i] = fc;
        }
        return patterns;
    }
    
    public static <T> T[] concatenate(T[] a, T[] b) {
        int aLen = a.length;
        int bLen = b.length;

        @SuppressWarnings("unchecked")
        T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);

        return c;
    }

    public static Pattern[][] splitPatterns(Pattern[] patterns, int patternLength) {
        int maxLength=0;
        for (int i=0;i<patterns.length;i++){
            if(maxLength<(patterns[i].getRows().length)){
                maxLength = patterns[i].getRows().length;
            }
        }
        int splitPatternNumber = (maxLength/patternLength);
        if((maxLength % patternLength) > 0){
            splitPatternNumber++;
        }
        Pattern[][] splitPatterns = new Pattern[splitPatternNumber][];
        for(int i=0;i<splitPatterns.length;i++){
            splitPatterns[i] = new Pattern[patterns.length];
        }
        for(int i=0;i<patterns.length;i++){
            Row[] rows = patterns[i].getRows();
            for(int j=0;j*patternLength<rows.length;j++){
                splitPatterns[j][i] = new Pattern();
                splitPatterns[j][i].setRows(Arrays.copyOfRange(rows, j*patternLength, Math.min(rows.length,j*patternLength+patternLength)));
            }     
        }
        return splitPatterns;
    }
    
    private static C2FPatternConverter[] initializeConverters(){
        C2FPatternConverter[] converters = new C2FPatternConverter[CHANNEL_COUNT];
        for(int i=0;i<converters.length;i++){
            converters[i] = new C2FPatternConverter();
        }
        return converters;
    }
    
}
