/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sega.md.snd.formats.furnace.pattern;

/**
 *
 * @author Wiz
 */
public class FNote {
    
    private byte value;
    
    public FNote(int value){
        this.value=(byte)(0xFF&value);
    }

    public byte getValue() {
        return value;
    }

    public void setValue(byte value) {
        this.value = value;
    }
    
    public String produceClipboardOutput(){
        String clipboard="";
        switch(value){
            case (byte)0xB4: 
                clipboard+="OFF";
                break;   
            case (byte)0xB5:
                clipboard+="===";
                break;
            case (byte)0xB6:
                clipboard+="REL";
                break;
            default:
                clipboard+=Pitch.valueOf(value).getStringValue();
                break;
        }
        return clipboard;
    }
    
}
