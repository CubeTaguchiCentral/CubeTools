/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sega.md.snd.formats.cube.command;

import com.sega.md.snd.formats.cube.CubeCommand;

/**
 *
 * @author Wiz
 */
public class SetRelease extends CubeCommand {
    
    byte value = 0;

    public SetRelease(byte value) {
        this.value = value;
    }

    @Override
    public byte[] produceBinaryOutput() {
        return new byte[]{(byte)0xFC, value};
    }

    @Override
    public String produceAsmOutput() {
        return "  setRelease "+Integer.toString(value&0xFF);
    }

    @Override
    public boolean equals(CubeCommand cc) {
        if(cc instanceof SetRelease && ((SetRelease)cc).value == this.value){
            return true;
        }else{
            return false;
        }
    }

    public byte getValue() {
        return value;
    }

    public void setValue(byte value) {
        this.value = value;
    }
    
    
    
}
