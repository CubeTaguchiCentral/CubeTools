/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sega.md.snd.formats.cube;

import com.sega.md.snd.formats.cube.command.ChannelEnd;
import com.sega.md.snd.formats.cube.command.CountedLoopEnd;
import com.sega.md.snd.formats.cube.command.CountedLoopStart;
import com.sega.md.snd.formats.cube.command.MainLoopEnd;
import com.sega.md.snd.formats.cube.command.Note;
import com.sega.md.snd.formats.cube.command.NoteL;
import com.sega.md.snd.formats.cube.command.PsgNote;
import com.sega.md.snd.formats.cube.command.PsgNoteL;
import com.sega.md.snd.formats.cube.command.RepeatEnd;
import com.sega.md.snd.formats.cube.command.RepeatSection1Start;
import com.sega.md.snd.formats.cube.command.RepeatSection2Start;
import com.sega.md.snd.formats.cube.command.RepeatSection3Start;
import com.sega.md.snd.formats.cube.command.RepeatStart;
import com.sega.md.snd.formats.cube.command.Sample;
import com.sega.md.snd.formats.cube.command.SampleL;
import com.sega.md.snd.formats.cube.command.Wait;
import com.sega.md.snd.formats.cube.command.WaitL;
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
    
    private CubeCommand[] ccs = new CubeCommand[0];

    public CubeCommand[] getCcs() {
        return ccs;
    }

    public void setCcs(CubeCommand[] ccs) {
        this.ccs = ccs;
    }
    

    
    public String produceAsmOutput(){
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<ccs.length;i++){
            sb.append("\n    "+ccs[i].produceAsmOutput());
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
        int candidateCountedLoopStartPlayLength = 0;
        int candidateRepeatGain = 0;
        int candidateRepeatStart = 0;
        int candidateRepeatSection2 = 0;
        boolean currentlyInCountedLoop = false;
        boolean currentlyInVoltaBrackets = false;
        List<CubeCommand> ccl = new ArrayList(Arrays.asList(ccs));
        int currentPlayLength = 0;
        for(int i=0;i<ccl.size();i++){
            CubeCommand cc = ccl.get(i);
            int newLength = cc.getPlayLength();
            if(newLength>0 && newLength!=currentPlayLength){
                currentPlayLength = newLength;
            }
            if(cc instanceof CountedLoopStart){
                currentlyInCountedLoop = true;
            }
            if(cc instanceof RepeatStart){
                currentlyInVoltaBrackets = true;
            }
            if(!currentlyInCountedLoop){
                for(int j=i+1;j-i<((ccl.size()-i+1)/2);j++){
                    int count = countLoops(i,j,currentPlayLength);
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
                            candidateCountedLoopStartPlayLength = currentPlayLength;
                            System.out.println("    Detected new Counted Loop candidate with gain of "+candidateCountedLoopGain+" : start="+candidateCountedLoopStartIndex
                                +", candidateCommandLength="+candidateCountedLoopLength+", candidateLoopCount="+candidateCountedLoopCount);
                            /*for(int p=repeatLength;p<j;p++){System.out.println(ccl.get(p).produceAsmOutput());}  */
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
                            /*for(int p=repeatLength;p<j;p++){System.out.println(ccl.get(p).produceAsmOutput());}*/
                        }
                    }
                }
            }
            if(cc instanceof CountedLoopEnd){
                currentlyInCountedLoop = false;
            }  
            if(cc instanceof RepeatEnd){
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
                    ccl.subList(candidateCountedLoopStartIndex+candidateCountedLoopLength, candidateCountedLoopStartIndex+2*candidateCountedLoopLength).clear();
                }
                int foundCurrentPlayLength = findCurrentPlayLength(ccl, candidateCountedLoopStartIndex);
                int countedLoopStartPlayLength = 0;
                if(foundCurrentPlayLength>=0){
                    countedLoopStartPlayLength = foundCurrentPlayLength;
                }else{
                    countedLoopStartPlayLength = candidateCountedLoopStartPlayLength;
                }
                applyStartPlayLength(ccl, candidateCountedLoopStartIndex, candidateCountedLoopStartIndex+candidateCountedLoopLength,countedLoopStartPlayLength);
                ccl.add(candidateCountedLoopStartIndex+candidateCountedLoopLength, new CountedLoopEnd());
                ccl.add(candidateCountedLoopStartIndex, new CountedLoopStart((byte)(0xFF&candidateCountedLoopCount)));
                CubeCommand[] newCcs = new CubeCommand[ccl.size()];
                ccs = ccl.toArray(newCcs);
                System.out.println("    Applied Counted Loop with gain "+candidateCountedLoopGain+" : start="+candidateCountedLoopStartIndex
                        +", candidateCommandLength="+candidateCountedLoopLength+", candidateLoopCount="+candidateCountedLoopCount+", countedLoopStartPlayLength="+countedLoopStartPlayLength);  
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
    
    private int countLoops(int start, int end, int startPlayLength){
        int count = 0;
        int currentPlayLength = startPlayLength;
        for(int i=0;start+i<end;i++){
            int newPlayLength = ccs[start+i].getPlayLength()&0xFF;
            if(!ccs[start+i].equals(ccs[end+i], currentPlayLength)){
                break;
            }
            if(start+i==end-1){
                count++;
                if(end+i+1<ccs.length){
                    count+=countLoops(end,end+i+1, startPlayLength);
                }
                break;
            }
            if(newPlayLength>0 && newPlayLength!=currentPlayLength){
                currentPlayLength = newPlayLength;
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
    
    private int findCurrentPlayLength(List<CubeCommand> ccl, int cursor){
        int playLength = -1;
        while(cursor>=0){
            CubeCommand cc = ccl.get(cursor);
            if(cc instanceof WaitL || cc instanceof NoteL || cc instanceof SampleL || cc instanceof PsgNoteL){
                playLength = cc.getPlayLength()&0xFF;
                break;
            }else{
                if(cc instanceof RepeatSection3Start || cc instanceof RepeatSection2Start){
                    int repeatCursor = cursor-1;
                    int repeatPlayLength = -1;
                    while(repeatCursor>=0){
                        CubeCommand rcc = ccl.get(repeatCursor);
                        if(rcc instanceof RepeatSection1Start){
                            int startSectionCursor = repeatCursor-1;
                            while(startSectionCursor>=0){
                                CubeCommand sscc = ccl.get(startSectionCursor);
                                if(sscc instanceof WaitL || sscc instanceof NoteL || sscc instanceof SampleL || sscc instanceof PsgNoteL){
                                    repeatPlayLength = sscc.getPlayLength()&0xFF;
                                    break;
                                }else{
                                     if(sscc instanceof RepeatStart){
                                         break;
                                     }
                                }
                                startSectionCursor--;
                            }
                        }
                        repeatCursor--;
                    }
                    if(repeatPlayLength>=0){
                        playLength = repeatPlayLength;
                        break;
                    }
                }
            }
            cursor--;
        }        
        return playLength;
    }
    
    private void applyStartPlayLength(List<CubeCommand> ccl, int startIndex, int endIndex, int playLength){
        for(int i=0;startIndex+i<endIndex;i++){
            CubeCommand cc = ccl.get(startIndex+i);
            if(cc instanceof WaitL || cc instanceof NoteL || cc instanceof SampleL || cc instanceof PsgNoteL){
                break;
            }else if(cc instanceof Wait || cc instanceof Note || cc instanceof Sample || cc instanceof PsgNote){
                if(cc instanceof Wait){
                    ccl.set(startIndex+i, new WaitL((byte)playLength));
                    break;
                }else if(cc instanceof Note){
                    ccl.set(startIndex+i, new NoteL(((Note) cc).getNote(),(byte)playLength));
                    break;
                }else if(cc instanceof Sample){
                    ccl.set(startIndex+i, new SampleL(((Sample) cc).getSample(),(byte)playLength));
                    break;
                }else if(cc instanceof PsgNote){
                    ccl.set(startIndex+i, new PsgNoteL(((PsgNote) cc).getNote(),(byte)playLength));
                    break;
                }
            }
        }
    }

    private int evaluateRepeatGain(int start, int section2Start){
        int gain = 0;
        int repeatCommandsSize = 2 + 2 + 2 + 2;
        for(int repeatLength=0;start+repeatLength<section2Start;repeatLength++){
            if(ccs[section2Start+repeatLength] instanceof RepeatStart
                    ||ccs[section2Start+repeatLength] instanceof CountedLoopStart
                    || ccs[section2Start+repeatLength] instanceof CountedLoopEnd){
                /* Met an incompatible pattern ahead, stop here */
                gain = 0;
                break;
            }
            if(ccs[start+repeatLength].equals(ccs[section2Start+repeatLength])){
                /* Equal intro pattern keeps matching, add potential gain */
                gain+=ccs[start+repeatLength].produceBinaryOutput().length;
            }
            if(!ccs[start+repeatLength].equals(ccs[section2Start+repeatLength])){
                if(gain>6){
                    boolean thirdEnding = false;
                    outerloop:
                    for(int sectionBase=0;section2Start+repeatLength+sectionBase<ccs.length-repeatLength;sectionBase++){
                        for(int sectionCursor=0;sectionCursor<=repeatLength;sectionCursor++){
                            CubeCommand cc = ccs[section2Start+repeatLength+sectionBase+sectionCursor];
                            if(cc instanceof RepeatStart || cc instanceof CountedLoopStart || cc instanceof CountedLoopEnd){
                                /* Met an incompatible pattern ahead, stop here */
                                break outerloop;
                            }
                            if(!ccs[start+sectionCursor].equals(ccs[section2Start+repeatLength+sectionBase+sectionCursor])){
                                /* Intro pattern stopped matching, try again from one command ahead */
                                break;
                            }
                            if(sectionCursor==repeatLength){
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
                        /*System.out.println("Detected candidate Volta Brackets with "+(thirdEnding?"3":"2")+" endings for gain "+gain+" : start="+start+", section2Start="+section2Start);*/
                        /*for(int p=start;p<start+repeatLength;p++){System.out.println(ccs[p].produceAsmOutput());}*/
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
            if(start+repeatLength==section2Start-1){
                /* Counted loop case, ignore */
                gain = 0;
                break;
            }
        }
        return gain;
    }    
    
    private int applyRepeat(List<CubeCommand> ccl, int start, int section2Start){
        int gain = 0;
        int repeatCommandsSize = 2 + 2 + 2 + 2;
        for(int repeatLength=0;start+repeatLength<section2Start;repeatLength++){
            if(ccs[section2Start+repeatLength] instanceof RepeatStart
                ||ccs[section2Start+repeatLength] instanceof CountedLoopStart
                || ccs[section2Start+repeatLength] instanceof CountedLoopEnd){
                /* Met an incompatible pattern ahead, stop here */
                gain = 0;
                break;
            }
            if(ccs[start+repeatLength].equals(ccs[section2Start+repeatLength])){
                /* Equal intro pattern keeps matching, add potential gain */
                gain+=ccs[start+repeatLength].produceBinaryOutput().length;
            }
            if(!ccs[start+repeatLength].equals(ccs[section2Start+repeatLength])){
                if(gain>6){
                    boolean thirdEnding = false;
                    int thirdEndingStart = 0;
                    outerloop:
                    for(int sectionBase=0;section2Start+repeatLength+sectionBase<ccs.length-repeatLength;sectionBase++){
                        for(int sectionCursor=0;sectionCursor<=repeatLength;sectionCursor++){
                            CubeCommand cc = ccs[section2Start+repeatLength+sectionBase+sectionCursor];
                            if(cc instanceof RepeatStart || cc instanceof CountedLoopStart || cc instanceof CountedLoopEnd){
                                /* Met an incompatible pattern ahead, stop here */
                                break outerloop;
                            }
                            if(!ccs[start+sectionCursor].equals(ccs[section2Start+repeatLength+sectionBase+sectionCursor])){
                                /* Intro pattern stopped matching, try again from one command ahead */
                                break;
                            }
                            if(sectionCursor==repeatLength){
                                /* Third ending does exist */
                                thirdEnding = true;
                                thirdEndingStart = section2Start+repeatLength+sectionBase;
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
                            ccl.subList(thirdEndingStart,thirdEndingStart+repeatLength).clear();
                            ccl.add(thirdEndingStart, new RepeatSection3Start());
                            ccl.add(thirdEndingStart, new RepeatEnd());
                        }
                        ccl.subList(section2Start,section2Start+repeatLength).clear();
                        ccl.add(section2Start, new RepeatSection2Start());
                        ccl.add(section2Start, new RepeatEnd());
                        ccl.add(start+repeatLength, new RepeatSection1Start());
                        ccl.add(start, new RepeatStart());
                        
                        System.out.println("    Applied Volta Brackets with "+(thirdEnding?"3":"2")+" endings for gain "+gain
                                +" : start="+start+", secondEndingStart="+section2Start+(thirdEnding?(", thirdEndingStart="+thirdEndingStart):""));
                        /*for(int p=start;p<start+repeatLength;p++){System.out.println(ccs[p].produceAsmOutput());}*/
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
            if(start+repeatLength==section2Start-1){
                /* Counted loop case, ignore */
                gain = 0;
                break;
            }
        }
        return gain;
    }        
    
}
