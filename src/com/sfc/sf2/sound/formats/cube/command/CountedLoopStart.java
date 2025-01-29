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
public class CountedLoopStart extends CubeCommand {
    
    byte value = 0;

    public CountedLoopStart(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    public void setValue(byte value) {
        this.value = value;
    }

    @Override
    public byte[] produceBinaryOutput() {
        return new byte[]{(byte)0xF8, (byte)(value+0xC0)};
    }

    @Override
    public String produceAsmOutput() {
        return "countedLoopStart "+value;
    }

    @Override
    public boolean equals(CubeCommand cc) {
        if(cc instanceof CountedLoopStart && ((CountedLoopStart)cc).value == this.value){
            return true;
        }else{
            return false;
        }
    }
    
    
    
}
