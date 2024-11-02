/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.sound.convert.io.furnace.clipboard;

import com.sfc.sf2.sound.convert.io.furnace.*;

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
    
    public static String produceClipboardOutput(FurnacePattern pattern){
        String clipboard=produceClipboardHeaderOutput();
        for(int i=0;i<pattern.getChannels()[0].getRows().length;i++){
            for(int j=0;j<pattern.getChannels().length;j++){
                clipboard+=pattern.getChannels()[j].getRows()[i].produceClipboardOutput();
                clipboard+="|";
            }
            clipboard+=System.lineSeparator();
        }
        return clipboard;
    }
    
    public static String produceClipboardOutput(FurnaceChannel channel){
        String clipboard=produceClipboardHeaderOutput();
        for(int i=0;i<channel.getRows().length;i++){
            clipboard+=channel.getRows()[i].produceClipboardOutput();
            clipboard+=System.lineSeparator();
        }
        return clipboard;
    }
    
    public static String produceClipboardOutput(FurnaceRow row){
        String clipboard=row.produceClipboardOutput();        
        return clipboard;
    }
    
}
