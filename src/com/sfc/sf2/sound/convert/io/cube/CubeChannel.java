/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.sound.convert.io.cube;

import com.sfc.sf2.sound.convert.io.cube.command.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    
    public boolean equals(CubeChannel cch){
        if(this.ccs.length!=cch.getCcs().length){
            return false;
        }
        for(int i=0;i<this.ccs.length;i++){
            if(!(this.ccs[i].equals(cch.getCcs()[i]))){
                return false;
            }
        }
        return true;
    }
    
    public void unroll(){
        unrollCountedLoops();
        unrollVoltaBrackets();
    }
    
    public void unrollCountedLoops(){
        List<CubeCommand> ccl = new ArrayList(Arrays.asList(ccs));
        List<CubeCommand> newCcl = new ArrayList();
        for(int i=0;i<ccl.size();i++){
            if(ccl.get(i) instanceof CountedLoopStart){
                CountedLoopStart cls = (CountedLoopStart)ccl.get(i);
                int loopCount = (cls.getValue()&0xFF)+1;
                for(int j=i;j<ccl.size();j++){
                    if(ccl.get(j) instanceof CountedLoopEnd){
                        List<CubeCommand> loopContent = ccl.subList(i+1, j);
                        //ccl.subList(i, j+1).clear();
                        for(int c=0;c<loopCount;c++){
                            newCcl.addAll(loopContent);
                        }
                        i=j+1;
                        break;
                    }
                }
            }else{
                newCcl.add(ccl.get(i));
            }
        }
        CubeCommand[] newCcs = new CubeCommand[newCcl.size()];
        ccs = newCcl.toArray(newCcs);
    }
    
    
    public void unrollVoltaBrackets(){
        List<CubeCommand> ccl = new ArrayList(Arrays.asList(ccs));
        List<CubeCommand> newCcl = new ArrayList();       
        
        for(int i=0;i<ccl.size();i++){
            if(ccl.get(i) instanceof RepeatStart){
                
                /* Build and add Start Section */
                List<CubeCommand> voltaStartCcl = new ArrayList();
                for(int j=i;j<ccl.size();j++){
                    if(ccl.get(j) instanceof RepeatSection1Start){ 
                        voltaStartCcl = ccl.subList(i+1, j);
                        newCcl.addAll(voltaStartCcl);
                        i=j+1;
                        break;
                    }
                }
                
                /* Build and add Ending 1 */
                for(int j=i;j<ccl.size();j++){
                    if(ccl.get(j) instanceof RepeatEnd){
                        List<CubeCommand> voltaEnd1Ccl = ccl.subList(i, j);
                        newCcl.addAll(voltaEnd1Ccl);
                        i=j+1;
                        break;
                    }
                }

                /* Build and add Ending 2, only if it has an end */
                for(int j=i;j<ccl.size();j++){
                    if(ccl.get(j) instanceof RepeatStart){
                        break;
                    }              
                    if(ccl.get(j) instanceof RepeatEnd){
                        List<CubeCommand> voltaEnd2Ccl = ccl.subList(i+1, j);
                        newCcl.addAll(voltaStartCcl);
                        newCcl.addAll(voltaEnd2Ccl);
                        i=j+1;
                        break;
                    }
                }
                
                newCcl.addAll(voltaStartCcl);
                
            }else{
                newCcl.add(ccl.get(i));
            }
        }
        
        CubeCommand[] newCcs = new CubeCommand[newCcl.size()];
        ccs = newCcl.toArray(newCcs);
    }
    
}
