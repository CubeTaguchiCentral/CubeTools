/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.sound.vgmmm.out;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author wiz
 */
public class PcmConverter {
    
        
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
    String stereo = null;
    String sample = null;
    
    public ChannelContext convertPcmChannel(ChannelData pcmc, ChannelContext channelContext){
        cc = channelContext;
        if(cc==null){
            cc = new ChannelContext();
        }
        
        String channelString = pcmc.getInput().toString().replace(".", " ");
        
        if(!channelString.replace("\n", "").isBlank()){

            lines = channelString.split("\n");

            outsb = pcmc.getOutput();

            while(frame<lines.length){
                line = lines[frame];
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

                produceCommands();

                System.out.println("Parsed frame "+frame);
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
        
        }
        
        return cc;
    }
    
    
    private boolean eventFound(){
 
        if(frame+length>=lines.length){
            return true;
        }
        
        String sline = lines[frame+length];
        String seffect1 = sline.substring(7,10).trim();
        String seffect2 = sline.substring(10,13).trim();
        String seffect3 = sline.substring(13,16).trim();
        String seffect4 = sline.substring(16).trim();        
        
        if(!seffect1.isEmpty()){
            if(seffect1.startsWith("S")){
                return true;
            }
        }
        
        if(!seffect2.isEmpty()){
            if(seffect2.startsWith("S")){
                return true;
            }
        }
        
        if(!seffect3.isEmpty()){
            if(seffect3.startsWith("S")){
                return true;
            }
        }
        
        if(!seffect4.isEmpty()){
            if(seffect4.startsWith("S")){
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
        
        stereo = null;
        if(effect4.startsWith("E8")){ stereo = effect4;}
        if(effect3.startsWith("E8")){ stereo = effect3;}
        if(effect2.startsWith("E8")){ stereo = effect2;}
        if(effect1.startsWith("E8")){ stereo = effect1;}
        if(stereo!=null && !stereo.isEmpty()){
            int pan = Integer.valueOf(stereo);
            if(pan==1){
                pan = 0x80;
            }else if(pan==2){
                pan = 0x40;
            }else{
                pan = 0xC0;
            }
            if(pan!=cc.getStereo()){
                cc.setStereo(pan);
                String[] command = {"\t\t    stereo",String.valueOf(pan)};
                cmds.add(command);      
            }
        }   
        
        
        
    }
    
    private void produceNote(){
        
        sample = "";
        if(effect4.startsWith("S")){ sample = effect4;}
        if(effect3.startsWith("S")){ sample = effect3;}
        if(effect2.startsWith("S")){ sample = effect2;}
        if(effect1.startsWith("S")){ sample = effect1;}

        /* Managing first silence */
        if( (frame==0 && 
            (sample.isEmpty()) && cc.getKey().isEmpty())
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
        if(!sample.isEmpty()){
            int id = Integer.valueOf(sample.substring(1),16);
            if(outputLength==cc.getLength() && outputLength<256){
                String[] command = {"\t\t          sample",String.valueOf(id)};
                cmds.add(command);
            }else{
                cc.setLength(outputLength);
                int count = outputLength;
                if(outputLength>255){
                    String[] cmd0 = {"\t\t          sampleL",String.valueOf(id),"255"};
                    cmds.add(cmd0);
                    count-=255;
                    while(count>255){
                        String[] cmdr = {"\t\t          wait"};
                        cmds.add(cmdr);
                        count-=255;
                    }
                }
                String[] cmdf = {"\t\t          sampleL",String.valueOf(id),String.valueOf(count)};
                cmds.add(cmdf);
            }            
            return;
        }       
        
    }
    
    private void producePostCommands(){
        
        
    }
    
}
