/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sega.md.snd.convert.cubetofurnace;

import com.sega.md.snd.formats.cube.MusicEntry;
import com.sega.md.snd.formats.cube.SfxEntry;
import com.sega.md.snd.formats.furnace.file.FurnaceFile;
import com.sega.md.snd.formats.furnace.file.section.PatternBlock;
import static com.sega.md.snd.formats.furnace.file.section.SongInfoBlock.LOOP_MODALITY_HARD_RESET;
import com.sega.md.snd.formats.furnace.pattern.Effect;
import com.sega.md.snd.formats.furnace.pattern.FNote;
import com.sega.md.snd.formats.furnace.pattern.Pattern;
import static com.sega.md.snd.formats.furnace.pattern.Pattern.NOTE_OFF;
import com.sega.md.snd.formats.furnace.pattern.Row;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Wiz
 */
public class C2FSfxFileConverter {
    
    private static final int TEMPLATE_FILE_CHANNEL_COUNT = 10;
    private static final int SFX_TYPE_1_CHANNEL_COUNT = 10;
    private static final int SFX_TYPE_2_CHANNEL_COUNT = 3;
    private static final int PATTERN_LENGTH = 256;
    
    public static FurnaceFile convertSfxEntry(SfxEntry se, FurnaceFile ff){

        int channelCount = se.getType()==1?SFX_TYPE_1_CHANNEL_COUNT:SFX_TYPE_2_CHANNEL_COUNT;
        
        /* Stateful converters for intro+loop case */
        C2FPatternConverter[] converters = C2FPatternConverter.instantiateConverterArray(channelCount);

        Pattern[] patterns = null;
        if(!se.hasMainLoop() && !se.hasRepeatLoop()){
            ff.getSongInfo().setLoopModality(LOOP_MODALITY_HARD_RESET);
            patterns = convertPatterns(se, converters, false, false);
            applyChannelEnds(patterns);
        }else{         
            Pattern[] introPatterns = convertPatterns(se, converters, true, false);
            Pattern[] mainLoopPatterns = convertPatterns(se, converters, false, true);
            patterns = concatenatePatterns(introPatterns, mainLoopPatterns);
            fillChannelsAndApplyLoop(introPatterns, mainLoopPatterns, patterns, converters);
        } 
        fillChannelsToMaxLength(patterns);
        Pattern[][] splitPatterns = splitPatterns(patterns, PATTERN_LENGTH);
        
        if(!se.hasMainLoop() && !se.hasRepeatLoop()){
            splitPatterns[0][splitPatterns[0].length-1].getRows()[PATTERN_LENGTH-1].getEffectList().add(new Effect(0xFF,0x00));
        }
        
        List<PatternBlock> pbList = new ArrayList();
        List<Byte> orderList = new ArrayList();
        int orderLength = splitPatterns[0].length;
        for(int i=0;i<splitPatterns.length;i++){
            for(int j=0;j<orderLength;j++){
                pbList.add(new PatternBlock(splitPatterns[i][j],i,j));
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

        ff.getSongInfo().setPatternLength((short)PATTERN_LENGTH);
        int ticksPerSecond = C2FPatternConverter.calculateTicksPersSecond(se.getAverageYmTimerBValue(), se.getYmTimerBIncrement(), ff.getSongInfo().getSpeed1());
        ff.getSongInfo().setTicksPerSecond(ticksPerSecond);

        C2FYmInstrumentConverter.convertYmInstruments(se.getYmInstruments(), se.isSsgEgAvailable(), ff);
        C2FSampleConverter.convertSamples(se.getSampleEntries(), se.getSampleBanks(), se.isMultiSampleBank(), ff);
        C2FPsgInstrumentConverter.convertPsgInstruments(se.getPsgInstruments(), ff);
        
        return ff;
    }
    
    public static Pattern[] convertPatterns(SfxEntry se, C2FPatternConverter[] converters, boolean introOnly, boolean mainLoopOnly){
        Pattern[] patterns = new Pattern[TEMPLATE_FILE_CHANNEL_COUNT];
        if(se.getType()==1){
            patterns[0] = converters[0].convertCubeChannelToFurnacePattern(se.getYmLevels(), se.getPitchEffects(), se.getChannels()[0], Pattern.TYPE_FM, introOnly, mainLoopOnly);
            patterns[1] = converters[1].convertCubeChannelToFurnacePattern(se.getYmLevels(), se.getPitchEffects(), se.getChannels()[1], Pattern.TYPE_FM, introOnly, mainLoopOnly);
            patterns[2] = converters[2].convertCubeChannelToFurnacePattern(se.getYmLevels(), se.getPitchEffects(), se.getChannels()[2], Pattern.TYPE_FM, introOnly, mainLoopOnly);
            patterns[3] = converters[3].convertCubeChannelToFurnacePattern(se.getYmLevels(), se.getPitchEffects(), se.getChannels()[3], Pattern.TYPE_FM, introOnly, mainLoopOnly);
            patterns[4] = converters[4].convertCubeChannelToFurnacePattern(se.getYmLevels(), se.getPitchEffects(), se.getChannels()[4], Pattern.TYPE_FM, introOnly, mainLoopOnly);
            patterns[5] = converters[5].convertCubeChannelToFurnacePattern(se.getYmLevels(), se.getPitchEffects(), se.getChannels()[5], Pattern.TYPE_DAC, introOnly, mainLoopOnly);
            patterns[6] = converters[6].convertCubeChannelToFurnacePattern(se.getYmLevels(), se.getPitchEffects(), se.getChannels()[6], Pattern.TYPE_PSGTONE, introOnly, mainLoopOnly);
            patterns[7] = converters[7].convertCubeChannelToFurnacePattern(se.getYmLevels(), se.getPitchEffects(), se.getChannels()[7], Pattern.TYPE_PSGTONE, introOnly, mainLoopOnly);
            patterns[8] = converters[8].convertCubeChannelToFurnacePattern(se.getYmLevels(), se.getPitchEffects(), se.getChannels()[8], Pattern.TYPE_PSGTONE, introOnly, mainLoopOnly);
            patterns[9] = converters[9].convertCubeChannelToFurnacePattern(se.getYmLevels(), se.getPitchEffects(), se.getChannels()[9], Pattern.TYPE_PSGNOISE, introOnly, mainLoopOnly);
        }else{
            patterns[0] = new Pattern();
            patterns[1] = new Pattern();
            patterns[2] = new Pattern();
            patterns[3] = converters[0].convertCubeChannelToFurnacePattern(se.getYmLevels(), se.getPitchEffects(), se.getChannels()[0], Pattern.TYPE_FM, introOnly, mainLoopOnly);
            patterns[4] = converters[1].convertCubeChannelToFurnacePattern(se.getYmLevels(), se.getPitchEffects(), se.getChannels()[1], Pattern.TYPE_FM, introOnly, mainLoopOnly);
            patterns[5] = converters[2].convertCubeChannelToFurnacePattern(se.getYmLevels(), se.getPitchEffects(), se.getChannels()[2], Pattern.TYPE_DAC, introOnly, mainLoopOnly);
            patterns[6] = new Pattern();
            patterns[7] = new Pattern();
            patterns[8] = new Pattern();
            patterns[9] = new Pattern();
        }
        return patterns;
    }
    
    public static Pattern[] concatenatePatterns(Pattern[] intro, Pattern[] mainLoop){
        Pattern[] patterns = new Pattern[intro.length];
        for (int i=0;i<patterns.length;i++){
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
    
    public static void applyChannelEnds(Pattern[] patterns){
        for(int i=0;i<patterns.length;i++){
            Row[] rows = patterns[i].getRows();
            List<Row> rowList = new ArrayList();
            rowList.addAll(Arrays.asList(rows));
            Row row = new Row();
            row.setNote(new FNote(NOTE_OFF));
            rowList.add(row);
            patterns[i].setRows(rowList.toArray(new Row[0]));
        }
    }
    
    public static void fillChannelsAndApplyLoop(Pattern[] introPatterns, Pattern[] mainLoopPatterns, Pattern[] concatenatedPatterns, C2FPatternConverter[] converters){
        int loopStartOffset = maximizeLongestIntroChannelLength(introPatterns, mainLoopPatterns, concatenatedPatterns, converters);
        repeatMainLoopToMaxLength(concatenatedPatterns, converters);
        applyLoopEnd(concatenatedPatterns, introPatterns, converters, loopStartOffset);
    }
    
    public static void applyLoopEnd(Pattern[] patterns, Pattern[] introPatterns, C2FPatternConverter[] converters, int loopStartOffset){
        int longestIntroChannel = getLongestChannel(introPatterns);
        int loopStartPosition = converters[longestIntroChannel].getMainLoopStartPosition() + loopStartOffset;
        int longestIntroChannelMainLoopStartPattern = loopStartPosition / PATTERN_LENGTH;
        int longestIntroChannelMainLoopStartPosition = loopStartPosition % PATTERN_LENGTH;
        Row[] rows = patterns[longestIntroChannel].getRows();
        Row longestChannelLastRow = rows[patterns[longestIntroChannel].getRows().length-1];
        longestChannelLastRow.getEffectList().add(new Effect(0x0B,longestIntroChannelMainLoopStartPattern));
        longestChannelLastRow.getEffectList().add(new Effect(0x0D,longestIntroChannelMainLoopStartPosition));
    }
    
    public static int maximizeLongestIntroChannelLength(Pattern[] introPatterns, Pattern[] mainLoopPatterns, Pattern[] concatenatedPatterns, C2FPatternConverter[] converters){
        int loopStartOffset = 0;
        int maxIntroLength = getMaxLength(introPatterns);
        int longestIntroChannel = getLongestChannel(introPatterns);
        int longestChannel = getLongestChannel(concatenatedPatterns);
        int longestIntroChannelMainLoopLength = mainLoopPatterns[longestIntroChannel].getRows().length;
        if(longestIntroChannel!=longestChannel
                && longestIntroChannelMainLoopLength>0){
            int maxLength = concatenatedPatterns[longestChannel].getRows().length;
            int mainLoopStartPosition = converters[longestIntroChannel].getMainLoopStartPosition();
            int newLength = mainLoopStartPosition + longestIntroChannelMainLoopLength;
            while(concatenatedPatterns[longestIntroChannel].getRows().length < maxLength){
                if(newLength>maxLength){
                    loopStartOffset = maxLength - concatenatedPatterns[longestIntroChannel].getRows().length;
                    newLength = maxLength;
                }
                concatenatedPatterns[longestIntroChannel].setRows(repeat(concatenatedPatterns[longestIntroChannel].getRows(), mainLoopStartPosition, newLength));
                newLength += longestIntroChannelMainLoopLength;
            }
        }
        return loopStartOffset;
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
        if((maxLength%PATTERN_LENGTH)>0){
            maxLength = ((maxLength/PATTERN_LENGTH)+1)*PATTERN_LENGTH;
        }
        fillToMaxLength(patterns, maxLength);
    }
    
    public static void fillToMaxLength(Pattern[] patterns, int maxLength){
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
        Pattern[][] splitPatterns = new Pattern[patterns.length][];
        for(int i=0;i<splitPatterns.length;i++){
            splitPatterns[i] = new Pattern[splitPatternNumber];
        }
        for(int i=0;i<patterns.length;i++){
            Row[] rows = patterns[i].getRows();
            for(int j=0;j*patternLength<rows.length;j++){
                splitPatterns[i][j] = new Pattern();
                splitPatterns[i][j].setRows(Arrays.copyOfRange(rows, j*patternLength, Math.min(rows.length,j*patternLength+patternLength)));
            }     
        }
        return splitPatterns;
    }
    
}
