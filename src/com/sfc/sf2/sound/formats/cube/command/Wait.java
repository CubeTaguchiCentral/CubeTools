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
public class Wait extends CubeCommand {

    @Override
    public byte[] produceBinaryOutput() {
        return new byte[]{(byte)0x70};
    }

    @Override
    public String produceAsmOutput() {
        return "        wait";
    }

    @Override
    public boolean equals(CubeCommand cc) {
        if(cc instanceof Wait){
            return true;
        }else{
            return false;
        }
    }
    
    
    
}
