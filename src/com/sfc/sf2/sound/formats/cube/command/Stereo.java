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
public class Stereo extends CubeCommand {
    
    byte value = 0;

    public Stereo(byte value) {
        this.value = value;
    }

    @Override
    public byte[] produceBinaryOutput() {
        return new byte[]{(byte)0xFA, value};
    }

    @Override
    public String produceAsmOutput() {
        return "  stereo 0"+Integer.toHexString(value&0xFF)+"h";
    }

    @Override
    public boolean equals(CubeCommand cc) {
        if(cc instanceof Stereo && ((Stereo)cc).value == this.value){
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
