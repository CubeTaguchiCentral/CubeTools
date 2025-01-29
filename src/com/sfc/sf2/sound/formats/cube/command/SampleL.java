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
public class SampleL extends CubeCommand {
    
    byte sample = 0;
    byte length = 0;

    public SampleL(byte sample, byte length) {
        this.sample = sample;
        this.length = length;
    }

    @Override
    public byte[] produceBinaryOutput() {
        return new byte[]{(byte)(sample+0x80), length};
    }

    @Override
    public String produceAsmOutput() {
        return "        sampleL "+sample+", "+Integer.toString(length&0xFF);
    }

    @Override
    public boolean equals(CubeCommand cc) {
        if(cc instanceof SampleL 
                && ((SampleL)cc).sample == this.sample
                && ((SampleL)cc).length == this.length){
            return true;
        }else{
            return false;
        }
    }

    public byte getSample() {
        return sample;
    }

    public void setSample(byte sample) {
        this.sample = sample;
    }

    public byte getLength() {
        return length;
    }

    public void setLength(byte length) {
        this.length = length;
    }
    
    
    
}
