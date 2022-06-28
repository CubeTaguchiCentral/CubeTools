/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.sound.convert.io.cube.command;

import com.sfc.sf2.sound.convert.io.cube.CubeCommand;

/**
 *
 * @author Wiz
 */
public class Shifting extends CubeCommand {
    
    byte value = 0;

    public Shifting(byte value) {
        this.value = value;
    }

    @Override
    public byte[] produceBinaryOutput() {
        return new byte[]{(byte)0xF9, value};
    }

    @Override
    public String produceStringOutput() {
        return "  shifting "+Integer.toString(value&0xFF);
    }

    @Override
    public boolean equals(CubeCommand cc) {
        if(cc instanceof Shifting && ((Shifting)cc).value == this.value){
            return true;
        }else{
            return false;
        }
    }
    
    
    
}
