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
public class RepeatEnd extends CubeCommand {

    @Override
    public byte[] produceBinaryOutput() {
        return new byte[]{(byte)0xF8, (byte)0xA0};
    }

    @Override
    public String produceAsmOutput() {
        return "repeatEnd";
    }

    @Override
    public boolean equals(CubeCommand cc) {
        if(cc instanceof RepeatEnd){
            return true;
        }else{
            return false;
        }
    }
    
    
    
}
