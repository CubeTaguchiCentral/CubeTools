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
public class PsgInst extends CubeCommand {
    
    byte value = 0;

    public PsgInst(byte value) {
        this.value = value;
    }

    @Override
    public byte[] produceBinaryOutput() {
        return new byte[]{(byte)0xFD, value};
    }

    @Override
    public String produceStringOutput() {
        return "  psgInst "+value;
    }

    @Override
    public boolean equals(CubeCommand cc) {
        if(cc instanceof PsgInst && ((PsgInst)cc).value == this.value){
            return true;
        }else{
            return false;
        }
    }
    
    
    
}
