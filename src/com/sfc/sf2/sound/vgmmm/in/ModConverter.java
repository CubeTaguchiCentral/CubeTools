/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.sound.vgmmm.in;

import com.sfc.sf2.sound.vgmmm.out.ChannelContext;
import com.sfc.sf2.sound.vgmmm.out.ChannelData;
import com.sfc.sf2.sound.vgmmm.out.Pattern;
import com.sfc.sf2.sound.vgmmm.out.PcmConverter;
import com.sfc.sf2.sound.vgmmm.out.PsgConverter;
import com.sfc.sf2.sound.vgmmm.out.YmConverter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wiz
 */
public class ModConverter {
    
    
    public static void main(String[] args){
        
        if(args.length>0){
            exportMod(args[0]);
        }else{
            exportMod(null);
        }
        
        
    }
    
    private static void exportMod(String filePath){
        
        try{
            
            String inputFilePath = filePath;
            if(inputFilePath==null){
                //System.out.println("Input File Path argument is missing.");
                //return;
                inputFilePath = "D:\\SEGADEV\\GITHUB\\SF2DISASM\\disasm\\data\\sound\\musicbank1\\test1.mt";
            }
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            String[] args = new String[3];
            args[0] = inputFilePath;
            args[1] = "-out";
            
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            String date = dateFormat.format(new Date());          
            
            args[2] = inputFilePath.replaceFirst(".mt", date+".mod");
            
            com.sfc.sf2.sound.vgmmm.in.micromod.compiler.Compiler.main(args);
            
        }catch(IOException ex){
             Logger.getLogger(ModConverter.class.getName()).log(Level.SEVERE, null, ex);
        }         
        
        
        
        
    }
    
    
    private void copy(String inputFilePath){
        File inputFile = new File(inputFilePath);
            String orderLen = null;
            String orderLoop = null;
            String[] orderPositions = null;
            try{
                Scanner mainScan = new Scanner(inputFile);
                
                /* Parsing pattern layout */
                while(mainScan.hasNext()){
                    String mainLine = mainScan.nextLine();
                    if(mainLine.startsWith("OrderLen")){
                        orderLen = mainLine.substring(mainLine.indexOf("=")+1).trim();
                        System.out.println("OrderLen="+orderLen);
                    }else if(mainLine.startsWith("OrderLoop")){
                        orderLoop = mainLine.substring(mainLine.indexOf("=")+1).trim();
                        System.out.println("OrderLoop="+orderLoop);
                    }else if(mainLine.startsWith("OrderPositions")){
                        orderPositions = mainLine.substring(mainLine.indexOf("=")+1).trim().split(",");
                        System.out.println("OrderPositions="+Arrays.toString(orderPositions));
                        break;
                    }
                }
                int introLength = Integer.valueOf(orderLoop);
                String[] introPatterns = new String[introLength];
                for(int i=0;i<introLength;i++){
                    introPatterns[i] = orderPositions[i];
                }
                System.out.println("introPatterns="+Arrays.toString(introPatterns));
                String[] mainLoopPatterns = new String[orderPositions.length-introLength];
                for(int i=introLength;i<orderPositions.length;i++){
                    mainLoopPatterns[i-introLength] = orderPositions[i];
                }
                System.out.println("mainLoopPatterns="+Arrays.toString(mainLoopPatterns));
                
                /* Loading patterns */
                List<Pattern> inputPatterns = new ArrayList();
                while(mainScan.hasNext()){
                    String mainLine = mainScan.nextLine();
                    //System.out.println(mainLine);
                    if(mainLine.startsWith("[Pattern")&&!mainLine.startsWith("[Patterns]")){
                        Pattern p = new Pattern();
                        inputPatterns.add(p);
                        ChannelData[] channels = new ChannelData[10];
                        for(int i=0;i<10;i++){
                            ChannelData cd = new ChannelData();
                            cd.setInput(new StringBuilder());
                            cd.setOutput(new StringBuilder());
                            channels[i] = cd;
                        }
                        p.setChannels(channels);
                        int patternIndex = Integer.valueOf(mainLine.substring(mainLine.indexOf("n")+1,mainLine.indexOf("]")));
                        p.setIndex(patternIndex);
                        System.out.println("Found pattern "+patternIndex);
                        mainScan.nextLine();
                        String lengthLine = mainScan.nextLine();
                        int length = Integer.valueOf(lengthLine.substring(lengthLine.indexOf("=")+1).trim());
                        System.out.println("Length = "+length);
                        mainScan.nextLine();
                        for(int line=0;line<length;line++){
                            String patternLine = mainScan.nextLine();
                            for(int channel=0;channel<10;channel++){
                                p.getChannels()[channel].getInput().append(patternLine.substring(channel*20, channel*20+19)).append("\n");
                            }
                        }
                        /*for(int i=0;i<10;i++){
                        System.out.println("\n\n\nPattern "+patternIndex+" - Channel "+i);
                        System.out.println(p.getChannels()[i].getIntput().toString());
                        }*/
                    }
                }
                
                /* Arranging and merging patterns */
                Pattern introPattern = new Pattern();
                ChannelData[] introChannels = new ChannelData[10];
                for(int i=0;i<10;i++){
                    ChannelData cd = new ChannelData();
                    cd.setInput(new StringBuilder());
                    cd.setOutput(new StringBuilder());
                    introChannels[i] = cd;
                }
                introPattern.setChannels(introChannels);
                for(int i=0;i<introPatterns.length;i++){
                    int patternId = Integer.valueOf(introPatterns[i]);
                    Pattern p = null;
                    for(Pattern candidate : inputPatterns){
                        if(candidate.getIndex()==patternId){
                            p = candidate;
                            int length = p.getLength();
                            introPattern.setLength(introPattern.getLength()+length);
                            for(int channel=0;channel<10;channel++){
                                introPattern.getChannels()[channel].getInput().append(p.getChannels()[channel].getInput().toString());
                            }
                            break;
                        }
                    }
                }
                for(int i=0;i<10;i++){
                    System.out.println("\n\n\nIntro Pattern - Channel "+i);
                    System.out.println(introPattern.getChannels()[i].getInput().toString());
                }
                Pattern mainLoopPattern = new Pattern();
                ChannelData[] mainLoopChannels = new ChannelData[10];
                for(int i=0;i<10;i++){
                    ChannelData cd = new ChannelData();
                    cd.setInput(new StringBuilder());
                    cd.setOutput(new StringBuilder());
                    mainLoopChannels[i] = cd;
                }
                mainLoopPattern.setChannels(mainLoopChannels);
                for(int i=0;i<mainLoopPatterns.length;i++){
                    int patternId = Integer.valueOf(mainLoopPatterns[i]);
                    Pattern p = null;
                    for(Pattern candidate : inputPatterns){
                        if(candidate.getIndex()==patternId){
                            p = candidate;
                            int length = p.getLength();
                            mainLoopPattern.setLength(mainLoopPattern.getLength()+length);
                            for(int channel=0;channel<10;channel++){
                                mainLoopPattern.getChannels()[channel].getInput().append(p.getChannels()[channel].getInput().toString());
                            }
                            break;
                        }
                    }
                }
                for(int i=0;i<10;i++){
                    System.out.println("\n\n\nMainLoop Pattern - Channel "+i);
                    System.out.println(mainLoopPattern.getChannels()[i].getInput().toString());
                }
                
                /* Converting YM1-5 channel data */
                for(int i=0;i<5;i++){
                    ChannelData c = introPattern.getChannels()[i];
                    ChannelContext cc = new YmConverter().convertYmChannel(c, null);
                    System.out.println("Intro Channel "+i+" :\n"+c.getOutput().toString());
                    c = mainLoopPattern.getChannels()[i];
                    new YmConverter().convertYmChannel(mainLoopPattern.getChannels()[i], cc);
                    System.out.println("Main Loop Channel "+i+" :\n"+c.getOutput().toString());
                }
                /* Converting YM6 Channel Data */
                if(introPattern.getChannels()[5].getInput().toString().contains("S")
                        || mainLoopPattern.getChannels()[5].getInput().toString().contains("S")){
                    ChannelData c = introPattern.getChannels()[5];
                    ChannelContext cc = new PcmConverter().convertPcmChannel(c, null);
                    System.out.println("Intro Channel "+5+" :\n"+c.getOutput().toString());
                    c = mainLoopPattern.getChannels()[5];
                    new PcmConverter().convertPcmChannel(mainLoopPattern.getChannels()[5], cc);
                    System.out.println("Main Loop Channel "+5+" :\n"+c.getOutput().toString());
                }else{
                    ChannelData c = introPattern.getChannels()[5];
                    ChannelContext cc = new YmConverter().convertYmChannel(c, null);
                    System.out.println("Intro Channel "+5+" :\n"+c.getOutput().toString());
                    c = mainLoopPattern.getChannels()[5];
                    new YmConverter().convertYmChannel(mainLoopPattern.getChannels()[5], cc);
                    System.out.println("Main Loop Channel "+5+" :\n"+c.getOutput().toString());
                }
                
                /* Converting PSG Tone 1-3 Channel Data */
                for(int i=6;i<9;i++){
                    ChannelData c = introPattern.getChannels()[i];
                    ChannelContext cc = new PsgConverter().convertPsgChannel(c, null);
                    System.out.println("Intro Channel "+i+" :\n"+c.getOutput().toString());
                    c = mainLoopPattern.getChannels()[i];
                    new PsgConverter().convertPsgChannel(mainLoopPattern.getChannels()[i], cc);
                    System.out.println("Main Loop Channel "+i+" :\n"+c.getOutput().toString());
                }
                
                /* Producing output file */
                String outputFilePath = inputFilePath.replace(".txt","04.asm");
                File outputFile = new File(outputFilePath);
                BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
                String inputFileName = inputFile.getName();
                bw.write("\n; converted from file "+inputFileName+"\n\n");
                String musicName = inputFileName.replace(".txt", "");
                
                bw.write(musicName+":\n");
                bw.write("\t\tdb 0\n");
                bw.write("\t\tdb 0\n");
                bw.write("\t\tdb 0\n");
                bw.write("\t\tdb 0C0h\n");
                for(int i=0;i<10;i++){
                    bw.write("		dw "+musicName+"_channel_"+i+"\n");
                }
                for(int i=0;i<6;i++){
                    bw.write(musicName+"_channel_"+i+":\n");
                    String introPatternOutput = introPattern.getChannels()[i].getOutput().toString();
                    String mainLoopPatternOutput = mainLoopPattern.getChannels()[i].getOutput().toString();
                    if(!introPatternOutput.trim().isEmpty() || !mainLoopPatternOutput.trim().isEmpty()){
                        bw.write("\t\t    stereo 0C0h\n");
                        bw.write(introPattern.getChannels()[i].getOutput().toString());
                        bw.write("\t\tmainLoopStart\n");
                        bw.write(mainLoopPattern.getChannels()[i].getOutput().toString());
                        bw.write("\t\tmainLoopEnd\n");
                    }else{
                        bw.write("\t\tchannel_end\n");
                    }
                }
                for(int i=6;i<10;i++){
                    bw.write(musicName+"_channel_"+i+":\n");
                    String introPatternOutput = introPattern.getChannels()[i].getOutput().toString();
                    String mainLoopPatternOutput = mainLoopPattern.getChannels()[i].getOutput().toString();
                    if(!introPatternOutput.trim().isEmpty() || !mainLoopPatternOutput.trim().isEmpty()){
                        bw.write(introPattern.getChannels()[i].getOutput().toString());
                        bw.write("\t\tmainLoopStart\n");
                        bw.write(mainLoopPattern.getChannels()[i].getOutput().toString());
                        bw.write("\t\tmainLoopEnd\n");
                    }else{
                        bw.write("\t\tchannel_end\n");
                    }
                }
                
                
                
                bw.close();
                System.out.println("Created file "+outputFile.getCanonicalPath());
                
                
                
            }catch(Exception e){
                System.err.println("Error while converting VGM MM TXT to ASM Cube Music : "+e);
                e.printStackTrace();
            }
    }
    
    
    
    
}
