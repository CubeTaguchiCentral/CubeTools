/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.sound.formats.cube.command;

import com.sfc.sf2.sound.formats.cube.CubeCommand;

/**
 *
 * @author Wiz
 */
public class Vibrato extends CubeCommand {
    
    byte value = 0;

    public Vibrato(byte value) {
        this.value = value;
    }

    @Override
    public byte[] produceBinaryOutput() {
        return new byte[]{(byte)0xFB, value};
    }

    @Override
    public String produceAsmOutput() {
        return "  vibrato "+Integer.toString(value&0xFF);
    }

    @Override
    public boolean equals(CubeCommand cc) {
        if(cc instanceof Vibrato && ((Vibrato)cc).value == this.value){
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
