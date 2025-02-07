/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.sound.formats.furnace.pattern;

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
            case (byte)0xFF:
                clipboard+="===";
                break;
            case (byte)0xFE: 
                clipboard+="OFF";
                break;   
            default:
                clipboard+=Pitch.valueOf(value).getStringValue();
                break;
        }
        return clipboard;
    }
    
}
