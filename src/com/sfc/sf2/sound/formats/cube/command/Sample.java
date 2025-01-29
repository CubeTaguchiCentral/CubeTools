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
public class Sample extends CubeCommand {
    
    byte sample = 0;

    public Sample(byte sample) {
        this.sample = sample;
    }

    @Override
    public byte[] produceBinaryOutput() {
        return new byte[]{sample};
    }

    @Override
    public String produceAsmOutput() {
        return "        sample "+sample;
    }

    @Override
    public boolean equals(CubeCommand cc) {
        if(cc instanceof Sample 
                && ((Sample)cc).sample == this.sample){
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
    
    
    
}
