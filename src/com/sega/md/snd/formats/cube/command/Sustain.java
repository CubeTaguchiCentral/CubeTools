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
public class Sustain extends CubeCommand {

    @Override
    public byte[] produceBinaryOutput() {
        return new byte[]{(byte)0xFC, (byte)0x80};
    }

    @Override
    public String produceAsmOutput() {
        return "  sustain";
    }

    @Override
    public boolean equals(CubeCommand cc) {
        if(cc instanceof Sustain){
            return true;
        }else{
            return false;
        }
    }
    
    
    
}
