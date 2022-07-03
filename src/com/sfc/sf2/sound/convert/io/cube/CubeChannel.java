/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.sound.convert.io.cube;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 *
 * @author Wiz
 */
public abstract class CubeChannel {
    
    private CubeCommand[] ccs;

    public CubeCommand[] getCcs() {
        return ccs;
    }

    public void setCcs(CubeCommand[] ccs) {
        this.ccs = ccs;
    }
    

    
    public String produceAsmOutput(){
        StringBuilder sb = new StringBuilder();
        for(CubeCommand cc : ccs){
            sb.append("\n    "+cc.produceAsmOutput());
        }
        return sb.toString();
    }    
    
    public byte[] produceBinaryOutput() throws IOException{
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        for(CubeCommand cc : ccs){
           output.write(cc.produceBinaryOutput());
        }
        return output.toByteArray();
    }
    
    
    
}
