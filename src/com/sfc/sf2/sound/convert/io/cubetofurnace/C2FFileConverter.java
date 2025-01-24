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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Wiz
 */
public class C2FFileConverter {
    
    public static FurnaceFile convertMusicEntry(MusicEntry me, FurnaceFile ff){
        
        ff.getSongInfo().setPatternLength((short)Pattern.PATTERN_LENGTH);

        PatternRange[] prs = null;

        if(!me.hasMainLoop()){
            PatternRange pr = new PatternRange(me, false, false);
            pr.fillChannelsToMaxLength();
            prs = PatternRange.split(pr, Pattern.PATTERN_LENGTH);

            Row[] rows = prs[prs.length-1].getPatterns()[0].getRows();
            List<Row> rowList = new ArrayList();
            rowList.addAll(Arrays.asList(rows));
            Row row = new Row();
            row.getEffectList().add(new Effect(0xFF,0x00));
            rowList.add(row);
            prs[prs.length-1].getPatterns()[0].setRows(rowList.toArray(new Row[0]));
        }else{                
            PatternRange intro = new PatternRange(me, true, false);
            int longestIntroChannel = 0;
            int longestIntroChannelLength = 0;
            for (int i=0;i<intro.getPatterns().length;i++){
                if(longestIntroChannelLength<intro.getPatterns()[i].getRows().length){
                    longestIntroChannelLength = intro.getPatterns()[i].getRows().length;
                    longestIntroChannel = i;
                }
            }
            int longestIntroChannelMainLoopStartPattern = (longestIntroChannelLength+1) / Pattern.PATTERN_LENGTH;
            int longestIntroChannelMainLoopStartPosition = (longestIntroChannelLength+1) % Pattern.PATTERN_LENGTH;

            PatternRange mainLoop = new PatternRange(me, false, true);
            mainLoop.repeatMainLoopToMaxLength();

            Row[] rows = mainLoop.getPatterns()[longestIntroChannel].getRows();
            List<Row> rowList = new ArrayList();
            rowList.addAll(Arrays.asList(rows));
            Row row = new Row();
            row.getEffectList().add(new Effect(0x0B,longestIntroChannelMainLoopStartPattern));
            row.getEffectList().add(new Effect(0x0D,longestIntroChannelMainLoopStartPosition));
            rowList.add(row);
            mainLoop.getPatterns()[longestIntroChannel].setRows(rowList.toArray(new Row[0]));

            PatternRange pr = new PatternRange(intro, mainLoop);

            pr.fillChannelsToMaxLength();

            prs = PatternRange.split(pr, Pattern.PATTERN_LENGTH);
        }            

        List<PatternBlock> pbList = new ArrayList();
        List<Byte> orderList = new ArrayList();
        int orderLength = prs.length;
        for(int i=0;i<prs[0].getPatterns().length;i++){
            for(int j=0;j<orderLength;j++){
                pbList.add(new PatternBlock(prs[j].getPatterns()[i],i,j));
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

        int ticksPerSecond = Pattern.calculateTicksPersSecond(me.getYmTimerBValue(), ff.getSongInfo().getSpeed1());
        //System.out.println("Timer B value "+Integer.toString(0xFF&me.getYmTimerBValue())+" -> "+ticksPerSecond+" ticks per second");
        ff.getSongInfo().setTicksPerSecond(ticksPerSecond);

        C2FYmInstrumentConverter.convertYmInstruments(me, ff);

        C2FSampleConverter.convertSamples(me, ff);
        
        return ff;
    }
    
}
