/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.sound.vgmmm.export;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author wiz
 */
public class TxtConverter {
    
    
    
    public static void main(String[] args){
        
        
        exportTxt();
        
        
    }
    
    private static void exportTxt(){
        
        
        File txt = new File("D:\\SEGADEV\\MUSIC\\vgmmusicmaker111\\propellerplanesf2.txt");
        String orderLen = null;
        String orderLoop = null;
        String[] orderPositions = null;
        try{
        Scanner mainScan = new Scanner(txt);
        
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
        
        /* Converting channel data */
        for(int i=0;i<5;i++){
            ChannelData c = introPattern.getChannels()[i];
            ChannelContext cc = YmConverter.convertYmChannel(c, null);
            System.out.println("Intro Channel "+i+" :\n"+c.getOutput().toString());
            c = mainLoopPattern.getChannels()[i];
            cc = YmConverter.convertYmChannel(mainLoopPattern.getChannels()[i], cc);
            System.out.println("Main Loop Channel "+i+" :\n"+c.getOutput().toString());
        }
        
        
        
        
        
        
        
        
        
        }catch(Exception e){
             System.err.println("Error while converting VGM MM TXT to ASM Cube Music : "+e);
        }         
        
        
        
        
    }
    
    
    
}
