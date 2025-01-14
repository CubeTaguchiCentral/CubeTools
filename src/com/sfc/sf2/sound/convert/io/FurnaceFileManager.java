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
import com.sfc.sf2.sound.convert.io.furnace.file.section.InstrumentBlock;
import com.sfc.sf2.sound.convert.io.furnace.file.section.PatternBlock;
import com.sfc.sf2.sound.convert.io.furnace.pattern.Effect;
import com.sfc.sf2.sound.convert.io.furnace.pattern.Pattern;
import com.sfc.sf2.sound.convert.io.furnace.pattern.Row;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Wiz
 */
public class FurnaceFileManager {
    
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
            System.out.println("Timer B value "+Integer.toString(0xFF&me.getYmTimerBValue())+" -> "+ticksPerSecond+" ticks per second");
            ff.getSongInfo().setTicksPerSecond(ticksPerSecond);
            
            convertYmInstruments(me, ff);
            
            convertSamples(me, ff);
            
            File file = new File(outputFilePath);
            Path path = Paths.get(file.getAbsolutePath());
            byte[] outputData = ff.toByteArray();
            Files.write(path,outputData);
            
            System.out.println("com.sfc.sf2.sound.convert.io.FurnaceFileManager() - Furnace File exported.");
        } catch (IOException ex) {
            Logger.getLogger(FurnaceFileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void convertYmInstruments(MusicEntry me, FurnaceFile ff){
        byte[][] cubeInstruments = me.getYmInstruments();
        //ff.setInstruments(new InstrumentBlock[cubeInstruments.length]);
        for(int i=0;i<cubeInstruments.length;i++){
            Feature[] newFeatures = new Feature[2];
            newFeatures[0] = new Feature("yminst"+String.format("%02d", i));
            newFeatures[1] = new Feature(cubeInstruments[i]);
            /*if(ff.getInstruments()[i]==null){
                ff.getInstruments()[i] = new InstrumentBlock();
            }*/
            ff.getInstruments()[i].setRawData(null);
            ff.getInstruments()[i].setFeatures(newFeatures);
        }
    }
    
    public static void convertSamples(MusicEntry me, FurnaceFile ff){
        byte[][] sampleEntries = me.getSampleEntries();
        byte[][] sampleBanks = me.getSampleBanks();
        SortedSet<Byte>  bankIndexes = new TreeSet();
        for(int i=0;i<sampleEntries.length;i++){
            bankIndexes.add(sampleEntries[i][2]);
        }
        List<Byte> bankIndexList = new ArrayList(bankIndexes);
        
        for(int i=0;i<sampleEntries.length;i++){
            int period = sampleEntries[i][0];
            int bankIndex = bankIndexList.indexOf(sampleEntries[i][2]);
            byte b5 = sampleEntries[i][5];
            byte b4 = sampleEntries[i][4];
            int i5 = (0xFF&b5)<<8;
            int i4 = (0xFF&b4);
            int length = i5 + i4;
            byte b7 = sampleEntries[i][7];
            byte b6 = sampleEntries[i][6];
            int i7 = (0x7F&b7)<<8;
            int i6 = (0xFF&b6);
            int offset = i7 + i6;
            byte[] bank = sampleBanks[bankIndex];
            byte[] baseSample = Arrays.copyOfRange(bank, offset, offset+length);
            byte[] targetSample = new byte[baseSample.length*period]; 
            for(int j=0;j<targetSample.length;j++){
                targetSample[j] = (byte)(0xFF & (0x80 + baseSample[j/period]));
            }
            ff.getSamples()[i].setDepth((byte)8);
            ff.getSamples()[i].setRawData(targetSample);
            ff.getSamples()[i].setLength(length);
            ff.getSamples()[i].setC4Rate(13250);
            ff.getSamples()[i].setCompatibilityRate(13250);
        }
    }
    
}
