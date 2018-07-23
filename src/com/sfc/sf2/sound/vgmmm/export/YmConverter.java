/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.sound.vgmmm.export;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author wiz
 */
public class YmConverter {
    
        
    
    String key = null;
    String volume = null;
    String instrument = null;
    String effect1 = null;
    String effect2 = null;
    String effect3 = null;
    String effect4 = null;
    int frame = 0;
    
    StringBuilder outsb = null;

    List<String[]> cmds = new ArrayList();
    List<String[]> commandList = new ArrayList();
    
    String[] lines = null;
    String line = null;
    
    ChannelContext cc = null;
    int length = 0;
    int outputLength = 0;
    boolean volumeSent = false;
    String noteCut = null;
    
    public ChannelContext convertYmChannel(ChannelData ymc, ChannelContext channelContext){
        cc = channelContext;
        if(cc==null){
            cc = new ChannelContext();
        }
        
        lines = ymc.getInput().toString().replace(".", " ").split("\n");
        
        outsb = ymc.getOutput();
        
        while(frame<lines.length){
            line = lines[frame];
            key = line.substring(0,3).trim();
            volume = line.substring(3,5).trim();
            instrument = line.substring(5,7).trim();
            effect1 = line.substring(7,10).trim();
            effect2 = line.substring(10,13).trim();
            effect3 = line.substring(13,16).trim();
            effect4 = line.substring(16).trim();
            
            /* Get length before next event */
            length = 1;
            while(!eventFound()){
                length++;
            }
            outputLength=length*3;
            
            volumeSent = false;
            noteCut = null;
            produceCommands();
            
            
            frame+=length;
        }
        
        
        for(String[] command : cmds){
            
            outsb.append(command[0]);
            if(command.length>=2){
                outsb.append(" "+command[1]);
            }
            if(command.length>=3){
                outsb.append(","+command[2]);
            }
            outsb.append("\n");
        }
        
        
        return cc;
    }
    
    
    
    private void ymVol(List<String[]> commandList, ChannelContext cc, int volume){
        if(volume!=cc.getLevel()){
            cc.setLevel(volume);
            String[] command = {"\t\t    ymVol",String.valueOf(volume)};
            commandList.add(command);
        }
    }
    
    
    private boolean eventFound(){
 
        if(frame+length>=lines.length){
            return true;
        }
        
        String sline = lines[frame+length];
        String skey = sline.substring(0,3).trim();
        String svolume = sline.substring(3,5).trim();
        String sinstrument = sline.substring(5,7).trim();
        String seffect1 = sline.substring(7,10).trim();
        String seffect2 = sline.substring(10,13).trim();
        String seffect3 = sline.substring(13,16).trim();
        String seffect4 = sline.substring(16).trim();        
        
        if(!skey.isEmpty()){
            return true;
        }
        
        if(!svolume.isEmpty() && Integer.valueOf(svolume,16)/2!=cc.getLevel()){
            return true;
        }
        
        if(!sinstrument.isEmpty() && Integer.valueOf(sinstrument,16)-1!=cc.getInstrument()){
            return true;
        }
        
        if(!seffect1.isEmpty()){
            if(seffect1.startsWith("3")
                    || seffect1.startsWith("4")
                    || seffect1.startsWith("E8")
                    || seffect1.startsWith("EC")){
                return true;
            }
        }
        
        if(!seffect2.isEmpty()){
            if(seffect2.startsWith("3")
                    || seffect2.startsWith("4")
                    || seffect2.startsWith("E8")
                    || seffect2.startsWith("EC")){
                return true;
            }
        }
        
        if(!seffect3.isEmpty()){
            if(seffect3.startsWith("3")
                    || seffect3.startsWith("4")
                    || seffect3.startsWith("E8")
                    || seffect3.startsWith("EC")){
                return true;
            }
        }
        
        if(!seffect4.isEmpty()){
            if(seffect4.startsWith("3")
                    || seffect4.startsWith("4")
                    || seffect4.startsWith("E8")
                    || seffect4.startsWith("EC")){
                return true;
            }
        }
        
        return false;
    }
    
    private void produceCommands(){
        
        producePreCommands();
        produceNote();
        producePostCommands();
        
        
        
    }
    
    private void producePreCommands(){

        if(!instrument.isEmpty()){
            int inst = Integer.valueOf(instrument,16)-1;
            if(inst!=cc.getInstrument()){
                cc.setInstrument(inst);
                String[] command0 = {"\t\t    inst",String.valueOf(inst)};
                cmds.add(command0);
                String vol = null;
                if(!volume.isEmpty()){
                    vol = String.valueOf(Integer.valueOf(volume,16)/2);
                    volumeSent = true;
                }else{
                    vol = String.valueOf(cc.getLevel());
                }                
                String[] command1 = {"\t\t    vol",vol};
                cmds.add(command1);
            }
        }
                        
        if(!volume.isEmpty() && !volumeSent){
            int vol = Integer.valueOf(volume,16)/2;
            if(vol!=cc.getLevel()){
                cc.setLevel(vol);
                String[] command = {"\t\t    vol",String.valueOf(vol)};
                cmds.add(command);      
            }
        }
        
        noteCut = null;
        if(effect4.startsWith("EC")){ noteCut = effect4;}
        if(effect3.startsWith("EC")){ noteCut = effect3;}
        if(effect2.startsWith("EC")){ noteCut = effect2;}
        if(effect1.startsWith("EC")){ noteCut = effect1;}
        if(noteCut!=null){
            int val = Integer.valueOf(noteCut.substring(2),16);
            if(!key.isEmpty() && val>1){
                int release = length - val;
                if(release>0){release=0;}
                if(release<256){
                    String[] command = {"\t\t    setRelease",String.valueOf(release)};
                    cmds.add(command);
                }
            }
            else if(val==1){
                key = "R";
            }
        }
        
        
        
    }
    
    private void produceNote(){

        /* Managing first silence */
        if( (frame==0 && 
            (key.isEmpty() || "R".equals(key))
            && cc.getKey().isEmpty())
            ||
            "R".equals(key)
            ){
            if(outputLength==cc.getLength() && outputLength<256){
                String[] command = {"\t\t          wait"};
                cmds.add(command);
            }else{
                cc.setLength(outputLength);
                if(outputLength>255){
                    String[] command0 = {"\t\t          waitL","255"};
                    cmds.add(command0);
                        outputLength-=255;
                    while(outputLength>255){
                        String[] command1 = {"\t\t          wait"};
                        cmds.add(command1);
                        outputLength-=255;
                    }
                }
                String[] command = {"\t\t          waitL",String.valueOf(outputLength)};
                cmds.add(command);
            }
            return;
        }
            
        /* Managing note */
        if(!key.isEmpty() || !cc.getKey().isEmpty()){
            if(!key.isEmpty()){
                String note = key.substring(0,2);
                int octave = Integer.valueOf(key.substring(2));
                if(("B".compareTo(note)<0 && octave==7) || octave>7){
                    octave=6;
                }
                if(("C".compareTo(note)>0 && octave==1) || octave<1){
                    octave = 2;
                }
                key = note+octave;
                key = key.replace("-", "").replace("#","s");
            }else{
                key = cc.getKey();
            }
                
            if(outputLength==cc.getLength() && outputLength<256){
                cc.setKey(key);
                String[] command = {"\t\t          note",String.valueOf(key)};
                cmds.add(command);
            }else{
                cc.setKey(key);
                cc.setLength(outputLength);
                int count = outputLength;
                if(outputLength>255){
                    String[] cmdnr = {"\t\t          sustain"};
                    cmds.add(cmdnr);
                    String[] cmd0 = {"\t\t          noteL",String.valueOf(key),"255"};
                    cmds.add(cmd0);
                    count-=255;
                    while(count>255){
                        String[] cmdr = {"\t\t          note",String.valueOf(key)};
                        cmds.add(cmdr);
                        count-=255;
                    }
                }
                String[] cmdf = {"\t\t          noteL",String.valueOf(key),String.valueOf(count)};
                cmds.add(cmdf);
                if(outputLength>255){
                    String[] cmdr = {"\t\t          setRelease","0"};
                    cmds.add(cmdr);
                }
            }            
            return;
        }       
        
    }
    
    private void producePostCommands(){
        
        if(noteCut!=null){
            int val = Integer.valueOf(noteCut.substring(2),16);
            if(!key.isEmpty() && val>1){
                int release = length - val;
                if(release>0){release=0;}
                if(release<256){
                    String[] command = {"\t\t    setRelease",String.valueOf(0)};
                    cmds.add(command);
                }
            }
        }
        
    }
    
}
