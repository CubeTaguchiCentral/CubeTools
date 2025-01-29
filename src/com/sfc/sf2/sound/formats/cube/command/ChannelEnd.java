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
public class ChannelEnd extends CubeCommand {

    @Override
    public byte[] produceBinaryOutput() {
        return new byte[]{(byte)0xFF, (byte)0x0, (byte)0x0};
    }

    @Override
    public String produceAsmOutput() {
        return "channel_end";
    }

    @Override
    public boolean equals(CubeCommand cc) {
        if(cc instanceof ChannelEnd){
            return true;
        }else{
            return false;
        }
    }
    
    
    
}
