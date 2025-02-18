/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sega.md.snd.formats.furnace.clipboard;

import com.sega.md.snd.formats.furnace.pattern.Pattern;
import com.sega.md.snd.formats.furnace.pattern.Row;

/**
 *
 * @author Wiz
 */
public class FurnaceClipboardProducer {
    
    public static String produceClipboardHeaderOutput(){
        String clipboard="";
        clipboard+="org.tildearrow.furnace - Pattern Data (219)";
        clipboard+=System.lineSeparator();
        clipboard+="0";
        clipboard+=System.lineSeparator();
        return clipboard;
    }
    
    public static String produceClipboardOutput(Pattern[] patterns, int patternLength){
        StringBuilder clipboard= new StringBuilder();
        for(int i=0;i<patterns[0].getRows().length;i++){
            if(i%patternLength==0){
                clipboard.append(produceClipboardHeaderOutput());
            }
            for(int j=0;j<patterns.length;j++){
                clipboard.append(patterns[j].getRows()[i].produceClipboardOutput());
                clipboard.append("|");
            }
            clipboard.append(System.lineSeparator());
        }
        return clipboard.toString();
    }
    
    public static String produceClipboardOutput(Pattern channel){
        StringBuilder clipboard = new StringBuilder();
        clipboard.append(produceClipboardHeaderOutput());
        for(int i=0;i<channel.getRows().length;i++){
            clipboard.append(channel.getRows()[i].produceClipboardOutput());
            clipboard.append(System.lineSeparator());
        }
        return clipboard.toString();
    }
    
    public static String produceClipboardOutput(Row row){
        String clipboard=row.produceClipboardOutput();        
        return clipboard;
    }
    
}
