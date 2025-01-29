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
public class NoSlide extends CubeCommand {

    @Override
    public byte[] produceBinaryOutput() {
        return new byte[]{(byte)0xFC, (byte)0xFF};
    }

    @Override
    public String produceAsmOutput() {
        return "  noSlide";
    }

    @Override
    public boolean equals(CubeCommand cc) {
        if(cc instanceof NoSlide){
            return true;
        }else{
            return false;
        }
    }
    
    
    
}
