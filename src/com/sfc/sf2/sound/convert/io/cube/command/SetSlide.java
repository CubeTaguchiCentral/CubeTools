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
public class SetSlide extends CubeCommand {
    
    byte value = 0;

    public SetSlide(byte value) {
        this.value = value;
    }

    @Override
    public byte[] produceBinaryOutput() {
        return new byte[]{(byte)0xFC, (byte)(value+0x80)};
    }

    @Override
    public String produceStringOutput() {
        return "  setSlide "+Integer.toString(value&0xFF);
    }

    @Override
    public boolean equals(CubeCommand cc) {
        if(cc instanceof SetSlide && ((SetSlide)cc).value == this.value){
            return true;
        }else{
            return false;
        }
    }
    
    
    
}
