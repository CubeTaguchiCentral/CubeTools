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
import com.sfc.sf2.sound.convert.io.furnace.file.section.PatternBlock;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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
            
            if(!me.hasIntro()){
            //if(true){
                PatternRange pr = new PatternRange(me, false, false);
                prs = PatternRange.split(pr, 256);
            }else{
                PatternRange intro = new PatternRange(me, true, false);
                PatternRange mainLoop = new PatternRange(me, false, true);
                PatternRange pr = new PatternRange(intro, mainLoop);
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
    
}
