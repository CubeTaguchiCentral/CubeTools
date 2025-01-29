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
public class Effect {
    
    private byte type;
    private Byte value;
    
    public Effect(int type){
        this.type=(byte)(0xFF&type);
    }
    
    public Effect(int type, int value){
        this.type=(byte)(0xFF&type);
        this.value=(byte)(0xFF&value);
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public Byte getValue() {
        return value;
    }

    public void setValue(byte value) {
        this.value = value;
    }
    
    public String produceClipboardOutput(){
        String clipboard="";
        clipboard+=String.format("%02x", type).toUpperCase();
        if(value!=null){
            clipboard+=String.format("%02x", value).toUpperCase();
        }else{
            clipboard+="..";
        }
        return clipboard;
    }
    
}
