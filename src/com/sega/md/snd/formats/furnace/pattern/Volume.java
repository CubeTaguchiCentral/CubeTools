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
public class Volume {
    
    private byte value;
    
    public Volume(int value){
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
        clipboard+=String.format("%02x", value).toUpperCase();
        return clipboard;
    }
    
}
