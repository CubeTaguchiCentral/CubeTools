/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.sound.convert.io;

import static com.sfc.sf2.sound.convert.io.FurnaceClipboardManager.printChannelSizes;
import com.sfc.sf2.sound.convert.io.cube.MusicEntry;
import com.sfc.sf2.sound.convert.io.furnace.PatternRange;
import com.sfc.sf2.sound.convert.io.furnace.clipboard.*;
import com.sfc.sf2.sound.convert.io.furnace.file.FurnaceFile;
import com.sfc.sf2.sound.convert.io.furnace.file.section.Feature;
import com.sfc.sf2.sound.convert.io.furnace.file.section.PatternBlock;
import com.sfc.sf2.sound.convert.io.furnace.pattern.Effect;
import com.sfc.sf2.sound.convert.io.furnace.pattern.Row;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Wiz
 */
public class FurnaceFileManager {
    
    public static final int MD_CRYSTAL_FREQUENCY = 53693175;
    public static final float YM2612_INPUT_FREQUENCY = MD_CRYSTAL_FREQUENCY / 7;
    public static final int YM2612_CHANNEL_SAMPLE_CYCLES = 6*24;
    public static final float YM2612_OUTPUT_RATE = YM2612_INPUT_FREQUENCY / YM2612_CHANNEL_SAMPLE_CYCLES;
    
    private static FurnaceFile currentFile = null;   
       
    public static MusicEntry importFurnaceFile(String filePath){
        MusicEntry me = null;
        try{
            File f = new File(filePath);
            byte[] data = Files.readAllBytes(Paths.get(f.getAbsolutePath()));
            currentFile = new FurnaceFile(data);
        } catch (IOException ex) {
            Logger.getLogger(BinaryMusicBankManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return me;
    }
    
    
    
    public static void exportMusicEntryAsFurnaceFile(MusicEntry me, String templateFilePath, String outputFilePath){
        try {
            System.out.println("com.sfc.sf2.sound.convert.io.FurnaceFileManager() - Exporting Furnace File ...");
            
            File f = new File(templateFilePath);
            byte[] inputData = Files.readAllBytes(Paths.get(f.getAbsolutePath()));
            FurnaceFile ff = new FurnaceFile(inputData);
            
            PatternRange[] prs = null;
            List<PatternRange> prList = new ArrayList();
            /*
              - MusicEntry -> PatternRange[]
              - PatternRange[] -> PatternBlock[]
                               -> orders
            */
            
            /* 
              - no loop
              - loop
              - intro + loop
            */

            if(!me.hasMainLoop()){
                PatternRange pr = new PatternRange(me, false, false);
                pr.fillChannelsToMaxLength();
                prs = PatternRange.split(pr, 256);
                
                Row[] rows = prs[prs.length-1].getPatterns()[0].getRows();
                List<Row> rowList = new ArrayList();
                rowList.addAll(Arrays.asList(rows));
                Row row = new Row();
                row.getEffectList().add(new Effect(0xFF,0x00));
                rowList.add(row);
                prs[prs.length-1].getPatterns()[0].setRows(rowList.toArray(new Row[0]));
            }else{
                
                /*
                Row[] rows = null;
                List<Row> rowList = null;
                Row row = null;
                int introPatternCount = 0;
                */
                
                PatternRange intro = new PatternRange(me, true, false);
                
                /*
                rows = intro.getPatterns()[0].getRows();
                introPatternCount = (rows.length / 256) + 1;
                rowList = new ArrayList();
                rowList.addAll(Arrays.asList(rows));
                row = new Row();
                row.getEffectList().add(new Effect(0x0B,introPatternCount));
                rowList.add(row);
                intro.getPatterns()[0].setRows(rowList.toArray(new Row[0]));
                */

                // intro.fillChannelsToMaxLength();

                // prList.addAll(Arrays.asList(PatternRange.split(intro, 256)));
                
                PatternRange mainLoop = new PatternRange(me, false, true);
                mainLoop.repeatMainLoopToMaxLength();
                
                PatternRange pr = new PatternRange(intro, mainLoop);
                
                pr.fillChannelsToMaxLength();
                
                /*
                rows = mainLoop.getPatterns()[0].getRows();
                rowList = new ArrayList();
                rowList.addAll(Arrays.asList(rows));
                row = new Row();
                row.getEffectList().add(new Effect(0x0B,introPatternCount));
                rowList.add(row);
                mainLoop.getPatterns()[0].setRows(rowList.toArray(new Row[0]));
                */
                
                // prList.addAll(Arrays.asList(PatternRange.split(pr, 256)));
                
                // prs = prList.toArray(new PatternRange[0]);
                prs = PatternRange.split(pr, 256);
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
             
            int ticksPerSecond = calculateTicksPersSecond(me.getYmTimerBValue(), ff.getSongInfo().getSpeed1());
            System.out.println("Timer B value "+Integer.toString(0xFF&me.getYmTimerBValue())+" -> "+ticksPerSecond+" ticks per second");
            ff.getSongInfo().setTicksPerSecond(ticksPerSecond);
            
            convertYmInstruments(me, ff);
            
            File file = new File(outputFilePath);
            Path path = Paths.get(file.getAbsolutePath());
            byte[] outputData = ff.toByteArray();
            Files.write(path,outputData);
            
            System.out.println("com.sfc.sf2.sound.convert.io.FurnaceFileManager() - Furnace File exported.");
        } catch (IOException ex) {
            Logger.getLogger(FurnaceFileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static int calculateTicksPersSecond(byte ymTimerB, int speed){  
        float timerPeriod = (8*144) * (256 - (0xFF&ymTimerB)) / (YM2612_INPUT_FREQUENCY/2);
        float timerFrequency = 1/timerPeriod * speed;
        return Math.round(timerFrequency);
    }
    
    public static void convertYmInstruments(MusicEntry me, FurnaceFile ff){
        byte[][] cubeInstruments = me.getYmInstruments();
        for(int i=0;i<cubeInstruments.length;i++){
            Feature[] newFeatures = new Feature[2];
            newFeatures[0] = new Feature("yminst"+String.format("%02d", i));
            newFeatures[1] = new Feature(cubeInstruments[i]);
            ff.getInstruments()[i].setRawData(null);
            ff.getInstruments()[i].setFeatures(newFeatures);
        }
    }
    
}
