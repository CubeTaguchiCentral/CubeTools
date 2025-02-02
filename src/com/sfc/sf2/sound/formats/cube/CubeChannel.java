/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.sound.formats.cube;

import com.sfc.sf2.sound.formats.cube.command.ChannelEnd;
import com.sfc.sf2.sound.formats.cube.command.CountedLoopEnd;
import com.sfc.sf2.sound.formats.cube.command.CountedLoopStart;
import com.sfc.sf2.sound.formats.cube.command.MainLoopEnd;
import com.sfc.sf2.sound.formats.cube.command.RepeatEnd;
import com.sfc.sf2.sound.formats.cube.command.RepeatSection1Start;
import com.sfc.sf2.sound.formats.cube.command.RepeatSection2Start;
import com.sfc.sf2.sound.formats.cube.command.RepeatSection3Start;
import com.sfc.sf2.sound.formats.cube.command.RepeatStart;
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
        int startPosition = -1;
        int loopCount = -1;
        for(int i=0;i<ccl.size();i++){
            CubeCommand cc = ccl.get(i);
            if(cc instanceof CountedLoopStart){
                startPosition = i;
                loopCount = ((CountedLoopStart) cc).getValue();
            }else if(cc instanceof CountedLoopEnd){
                if(loopCount>0){
                    loopCount--;
                    i = startPosition;
                }
            }else{     
                newCcl.add(cc);
            }
        }
        CubeCommand[] newCcs = new CubeCommand[newCcl.size()];
        ccs = newCcl.toArray(newCcs);
    }
    
    public void unrollVoltaBrackets(){
        List<CubeCommand> ccl = new ArrayList(Arrays.asList(ccs));
        List<CubeCommand> newCcl = new ArrayList();       
        
        int startPosition = -1;
        boolean section1Started = false;
        boolean section2Started = false;
        
        for(int i=0;i<ccl.size();i++){
            CubeCommand cc = ccl.get(i);
            
            if(cc instanceof RepeatStart){
                startPosition = i;
                section1Started = false;
                section2Started = false;
            }else if(cc instanceof RepeatSection1Start){
                if(startPosition>=0){
                    if(section1Started){
                        for(int j=i+1;j<ccl.size();j++){
                            CubeCommand target = ccl.get(j);
                            if(target instanceof RepeatSection2Start || target instanceof MainLoopEnd || target instanceof ChannelEnd){
                                i = j-1;
                                break;
                            }
                        }
                    }else{
                        section1Started = true;
                    }
                }
            }else if(cc instanceof RepeatSection2Start){
                if(startPosition>=0){
                    if(section2Started){
                        for(int j=i+1;j<ccl.size();j++){
                            CubeCommand target = ccl.get(j);
                            if(target instanceof RepeatSection3Start || target instanceof MainLoopEnd || target instanceof ChannelEnd){
                                i = j-1;
                                break;
                            }
                        }
                    }else{
                        section2Started = true;
                    }
                }
            }else if(cc instanceof RepeatSection3Start){
                
            }else if(cc instanceof RepeatEnd){
                if(startPosition>=0){
                    if(section1Started){
                        i = startPosition;
                    }else{
                        /* Infinite loop without using the dedicated command */
                        newCcl.add(startPosition, ccl.get(startPosition));
                        newCcl.add(cc);
                        break;
                    }
                }
            }else{     
                newCcl.add(cc);
            }
            
        }
        
        CubeCommand[] newCcs = new CubeCommand[newCcl.size()];
        ccs = newCcl.toArray(newCcs);
    }
    
    public void optimize(){
        int pass=0;
        do{
            System.out.println("  Pass "+pass+" ...");
            pass++;
        }while(applyNextBestOptimization()>0);
        //TODO Second pass to optimize counted loops, when splitting into several counted loops gives better gain
    }
    
    public int applyNextBestOptimization(){
        int finalGain=0;
        int candidateCountedLoopGain = 0;
        int candidateCountedLoopStartIndex = 0;
        int candidateCountedLoopLength = 0;
        int candidateCountedLoopCount = 0;
        int candidateRepeatGain = 0;
        int candidateRepeatStart = 0;
        int candidateRepeatSection2 = 0;
        boolean currentlyInCountedLoop = false;
        boolean currentlyInVoltaBrackets = false;
        List<CubeCommand> ccl = new ArrayList(Arrays.asList(ccs));
        for(int i=0;i<ccl.size();i++){
            if(ccl.get(i) instanceof CountedLoopStart){
                currentlyInCountedLoop = true;
            }
            if(ccl.get(i) instanceof RepeatStart){
                currentlyInVoltaBrackets = true;
            }
            if(!currentlyInCountedLoop){
                for(int j=i+1;j-i<((ccl.size()-i+1)/2);j++){
                    int count = countLoops(i,j);
                    if(count>0){
                        if(count>31){
                            count = 31;
                        }                        
                        int gain = evaluateCountedLoopGain(i,j-i,count);
                        if(gain>candidateCountedLoopGain){                      
                            candidateCountedLoopGain = gain;
                            candidateCountedLoopStartIndex = i;
                            candidateCountedLoopLength = j-i;
                            candidateCountedLoopCount = count;
                            System.out.println("    Detected new Counted Loop candidate with gain of "+candidateCountedLoopGain+" : start="+candidateCountedLoopStartIndex
                                +", candidateCommandLength="+candidateCountedLoopLength+", candidateLoopCount="+candidateCountedLoopCount);
                            /*for(int p=i;p<j;p++){System.out.println(ccl.get(p).produceAsmOutput());}  */
                        }
                    }
                }
            }
            if(!currentlyInCountedLoop&&!currentlyInVoltaBrackets){
                for(int j=i+1;j-i<((ccl.size()-i+1)/2);j++){
                    if(ccs[j] instanceof RepeatStart
                        ||ccs[j] instanceof CountedLoopStart
                        || ccs[j] instanceof CountedLoopEnd){
                        break;
                    }else{
                        int gain = evaluateRepeatGain(i,j);
                        if(gain>candidateRepeatGain){                      
                            candidateRepeatGain = gain;
                            candidateRepeatStart = i;
                            candidateRepeatSection2 = j;
                            System.out.println("    Detected new Volta Brackets candidate with gain of "+candidateRepeatGain+" : candidateRepeatStart="+candidateRepeatStart
                                +", candidateRepeatSection2="+candidateRepeatSection2);
                            /*for(int p=i;p<j;p++){System.out.println(ccl.get(p).produceAsmOutput());}*/
                        }
                    }
                }
            }
            if(ccl.get(i) instanceof CountedLoopEnd){
                currentlyInCountedLoop = false;
            }  
            if(ccl.get(i) instanceof RepeatEnd){
                currentlyInVoltaBrackets = false;
                for(int s=i+1;s<ccl.size();s++){
                    if(ccl.get(s) instanceof RepeatEnd){
                        currentlyInVoltaBrackets = true;
                        break;
                    } else if(ccl.get(s) instanceof RepeatStart){
                        currentlyInVoltaBrackets = false;
                        break;
                    }
                }
            }
        }
        if(candidateCountedLoopGain>0 || candidateRepeatGain>0){
            if(candidateCountedLoopGain>=candidateRepeatGain){
                finalGain = candidateCountedLoopGain;
                for(int c=0;c<candidateCountedLoopCount;c++){
                    ccl.subList(candidateCountedLoopStartIndex, candidateCountedLoopStartIndex+candidateCountedLoopLength).clear();
                }
                ccl.add(candidateCountedLoopStartIndex+candidateCountedLoopLength, new CountedLoopEnd());
                ccl.add(candidateCountedLoopStartIndex, new CountedLoopStart((byte)(0xFF&candidateCountedLoopCount)));
                CubeCommand[] newCcs = new CubeCommand[ccl.size()];
                ccs = ccl.toArray(newCcs);
                System.out.println("    Applied Counted Loop with gain "+candidateCountedLoopGain+" : start="+candidateCountedLoopStartIndex
                        +", candidateCommandLength="+candidateCountedLoopLength+", candidateLoopCount="+candidateCountedLoopCount);  
                /*for(int p=candidateStartIndex;p<candidateStartIndex+candidateCommandLength;p++){System.out.println(ccl.get(p).produceAsmOutput());}        */
            }else if(candidateRepeatGain>candidateCountedLoopGain){
                finalGain = candidateRepeatGain;
                applyRepeat(ccl, candidateRepeatStart, candidateRepeatSection2);
                CubeCommand[] newCcs = new CubeCommand[ccl.size()];
                ccs = ccl.toArray(newCcs);
                /*System.out.println("    Applied Volta Brackets with gain of "+candidateRepeatGain+" : candidateRepeatStart="+candidateRepeatStart
                                    +", candidateRepeatSection2="+candidateRepeatSection2);  */
                /*for(int p=candidateStartIndex;p<candidateStartIndex+candidateCommandLength;p++){System.out.println(ccl.get(p).produceAsmOutput());}        */
            }
        }
        
        return finalGain;
    }
    
    private int countLoops(int start, int end){
        int count = 0;
        for(int i=0;start+i<end;i++){
            if(!ccs[start+i].equals(ccs[end+i])){
                break;
            }
            if(start+i==end-1){
                count++;
                if(end+i+1<ccs.length){
                    count+=countLoops(end,end+i+1);
                }
                break;
            }
        }
        return count;
    }
    
    private int evaluateCountedLoopGain(int start, int length, int count){
        int gain = 0;
        for(int i=0;i<length;i++){
            gain+=ccs[start+i].produceBinaryOutput().length;
        }
        gain = gain*(count) - 2 - 2;
        return gain;
    }
    

    private int evaluateRepeatGain(int start, int end){
        int gain = 0;
        int repeatCommandsSize = 2 + 2 + 2 + 2;
        for(int i=0;start+i<end;i++){
            if(ccs[end+i] instanceof RepeatStart
                    ||ccs[end+i] instanceof CountedLoopStart
                    || ccs[end+i] instanceof CountedLoopEnd){
                /* Met an incompatible pattern ahead, stop here */
                gain = 0;
                break;
            }
            if(ccs[start+i].equals(ccs[end+i])){
                /* Equal intro pattern keeps matching, add potential gain */
                gain+=ccs[start+i].produceBinaryOutput().length;
            }
            if(!ccs[start+i].equals(ccs[end+i])){
                if(gain>6){
                    boolean thirdEnding = false;
                    outerloop:
                    for(int sb=0;end+sb<ccs.length-i;sb++){
                        for(int sc=0;sc<=i;sc++){
                            if(ccs[end+i] instanceof RepeatStart
                                ||ccs[end+i] instanceof CountedLoopStart
                                || ccs[end+i] instanceof CountedLoopEnd){
                                /* Met an incompatible pattern ahead, stop here */
                                break;
                            }
                            if(!ccs[start+sc].equals(ccs[end+sb+sc])){
                                /* Intro pattern stopped matching, try again from one command ahead */
                                break;
                            }
                            if(sc==i){
                                /* Third ending does exist */
                                thirdEnding = true;
                                break outerloop;
                            }
                        }
                    }
                    if(thirdEnding){
                        gain = gain*2;
                        repeatCommandsSize += 2 + 2;
                    }
                    if(gain-repeatCommandsSize>0){
                        /* We have a candidate gain with Repeat commands */
                        gain = gain - repeatCommandsSize;
                        /*System.out.println("Detected candidate Volta Brackets with "+(thirdEnding?"3":"2")+" endings for gain "+gain+" : start="+start+", end="+end);*/
                        /*for(int p=start;p<start+i;p++){System.out.println(ccs[p].produceAsmOutput());}*/
                        break;
                    }else{
                        /* Gain is not enough to compensate for repeat commands */
                        gain = 0;
                        break;
                    }
                }else{
                    /* Gain is not enough to compensate for repeat commands, even in case of third ending */
                    gain = 0;
                    break;
                }
            }
            if(start+i==end-1){
                /* Counted loop case, ignore */
                gain = 0;
                break;
            }
        }
        return gain;
    }    
    
    private int applyRepeat(List<CubeCommand> ccl, int start, int end){
        int gain = 0;
        int repeatCommandsSize = 2 + 2 + 2 + 2;
        for(int i=0;start+i<end;i++){
            if(ccs[end+i] instanceof RepeatStart
                ||ccs[end+i] instanceof CountedLoopStart
                || ccs[end+i] instanceof CountedLoopEnd){
                /* Met an incompatible pattern ahead, stop here */
                gain = 0;
                break;
            }
            if(ccs[start+i].equals(ccs[end+i])){
                /* Equal intro pattern keeps matching, add potential gain */
                gain+=ccs[start+i].produceBinaryOutput().length;
            }
            if(!ccs[start+i].equals(ccs[end+i])){
                if(gain>6){
                    boolean thirdEnding = false;
                    int thirdEndingStart = 0;
                    outerloop:
                    for(int sb=0;end+sb<ccs.length-i;sb++){
                        for(int sc=0;sc<=i;sc++){
                            if(ccs[end+i] instanceof RepeatStart
                                ||ccs[end+i] instanceof CountedLoopStart
                                || ccs[end+i] instanceof CountedLoopEnd){
                                /* Met an incompatible pattern ahead, stop here */
                                break;
                            }
                            if(!ccs[start+sc].equals(ccs[end+sb+sc])){
                                /* Intro pattern stopped matching, try again from one command ahead */
                                break;
                            }
                            if(sc==i){
                                /* Third ending does exist */
                                thirdEnding = true;
                                thirdEndingStart = end+sb+sc;
                                break outerloop;
                            }
                        }
                    }
                    if(thirdEnding){
                        gain = gain*2;
                        repeatCommandsSize += 2 + 2;
                    }
                    if(gain-repeatCommandsSize>0){
                        /* We have a candidate gain with Repeat commands */
                        gain = gain - repeatCommandsSize;
                
                        if(thirdEnding){
                            ccl.subList(thirdEndingStart,thirdEndingStart+i).clear();
                            ccl.add(thirdEndingStart, new RepeatSection3Start());
                            ccl.add(thirdEndingStart, new RepeatEnd());
                        }
                        ccl.subList(end,end+i).clear();
                        ccl.add(end, new RepeatSection2Start());
                        ccl.add(end, new RepeatEnd());
                        ccl.add(start+i, new RepeatSection1Start());
                        ccl.add(start, new RepeatStart());
                        
                        System.out.println("    Applied Volta Brackets with "+(thirdEnding?"3":"2")+" endings for gain "+gain
                                +" : start="+start+", secondEndingStart="+end+(thirdEnding?(", thirdEndingStart="+thirdEndingStart):""));
                        /*for(int p=start;p<start+i;p++){System.out.println(ccs[p].produceAsmOutput());}*/
                        break;
                    }else{
                        /* Gain is not enough to compensate for repeat commands */
                        gain = 0;
                        break;
                    }
                }else{
                    /* Gain is not enough to compensate for repeat commands, even in case of third ending */
                    gain = 0;
                    break;
                }
            }
            if(start+i==end-1){
                /* Counted loop case, ignore */
                gain = 0;
                break;
            }
        }
        return gain;
    }        
    
}
