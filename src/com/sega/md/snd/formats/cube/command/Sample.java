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
public class Sample extends CubeCommand {
    
    byte sample = 0;
    byte currentPlayLength = 0;

    public Sample(byte sample) {
        this.sample = sample;
    }

    public Sample(byte sample, byte currentLength) {
        this.sample = sample;
        this.currentPlayLength = currentLength;
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
        if(cc instanceof Sample){ 
            if(((Sample)cc).sample == this.sample
                && (this.currentPlayLength==0
                    || ((Sample)cc).currentPlayLength==0
                    || ((Sample)cc).currentPlayLength==this.currentPlayLength
                    )
               ){
                return true;
            }
        }
        return false;
    }

    public byte getSample() {
        return sample;
    }

    public void setSample(byte sample) {
        this.sample = sample;
    }

    public byte getCurrentPlayLength() {
        return currentPlayLength;
    }

    public void setCurrentPlayLength(byte currentLength) {
        this.currentPlayLength = currentLength;
    }

    @Override
    public int getPlayLength() {
        return 0;
    }
    
    @Override
    public boolean equals(CubeCommand cc, int currentPlayLength) {
        if(cc instanceof SampleL){ 
                if((((SampleL)cc).sample&0xFF) == this.sample
                    && (((SampleL)cc).length&0xFF) == currentPlayLength
                    ){
                    return true;
                }
        }
        return equals(cc);
    }
    
    
    
}
