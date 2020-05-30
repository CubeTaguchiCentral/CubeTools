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
public class PsgConverter {
    
        
    
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
    String vibrato = null;
    String stereo = null;
    
    public ChannelContext convertPsgChannel(ChannelData psgc, ChannelContext channelContext){
        cc = channelContext;
        if(cc==null){
            cc = new ChannelContext();
        }
        
        String channelString = psgc.getInput().toString().replace(".", " ");
        
        if(!channelString.replace("\n", "").isBlank()){

            lines = channelString.split("\n");

            outsb = psgc.getOutput();

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
                int vol = 0;
                if(!volume.isEmpty()){
                    vol = Integer.valueOf(volume,16)/2;
                    cc.setLevel(vol);
                    volumeSent = true;
                }else{
                    vol = cc.getLevel();
                }                
                String param = "0"+Integer.toHexString(inst*16+vol)+"h";
                String[] command0 = {"\t\t    psgInst",param};
                cmds.add(command0);
            }
        }
                        
        if(!volume.isEmpty() && !volumeSent){
            int vol = Integer.valueOf(volume,16)/2;
            if(vol!=cc.getLevel()){
                cc.setLevel(vol);
                int inst = cc.getInstrument();
                String param = "0"+Integer.toHexString(inst*16+vol)+"h";
                String[] command0 = {"\t\t    psgInst",param};
                cmds.add(command0);
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
        
        vibrato = null;
        if(effect4.startsWith("4")){ vibrato = effect4;}
        if(effect3.startsWith("4")){ vibrato = effect3;}
        if(effect2.startsWith("4")){ vibrato = effect2;}
        if(effect1.startsWith("4")){ vibrato = effect1;}
        if(cc.getVibrato()!=0 && vibrato==null){
            cc.setVibrato(0);
            String[] command = {"\t\t    vibrato","0"};
            cmds.add(command);
        }else if(vibrato!=null && cc.getVibrato()==0){
            int speed = Integer.valueOf(vibrato.substring(1,2),16);
            int depth = Integer.valueOf(vibrato.substring(2,3),16);
            int vibratoParams = speed*16+depth;
            cc.setVibrato(0x20);
            String[] command = {"\t\t    vibrato","020h"};
            cmds.add(command);
        }    
        
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
        
        if(!key.isEmpty() && cc.getRelease()==0x80){
            cc.setRelease(0);
            String[] cmdr = {"\t\t    setRelease","0"};
            cmds.add(cmdr);
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
                if(octave<2){
                    octave=2;
                }
                while(octave>7){
                    octave--;
                }
                if(("C".compareTo(note.substring(0,1))>0 && octave==7)){
                    octave=6;
                }
                key = note+octave;
                key = key.replace("-", "").replace("#","s");
            }else{
                sustain();
                key = cc.getKey();
            }
                
            if(outputLength==cc.getLength() && outputLength<256){
                cc.setKey(key);
                String[] command = {"\t\t          psgNote ",String.valueOf(key)};
                cmds.add(command);
            }else{
                cc.setKey(key);
                cc.setLength(outputLength);
                int count = outputLength;
                if(outputLength>255){
                    if(cc.getRelease()!=0x80){
                        String[] cmdnr = {"\t\t    sustain"};
                        cmds.add(cmdnr);
                    }
                    String[] cmd0 = {"\t\t          psgNoteL",String.valueOf(key),"255"};
                    cmds.add(cmd0);
                    count-=255;
                    while(count>255){
                        String[] cmdr = {"\t\t          psgNote ",String.valueOf(key)};
                        cmds.add(cmdr);
                        count-=255;
                    }
                }
                String[] cmdf = {"\t\t          psgNoteL",String.valueOf(key),String.valueOf(count)};
                cmds.add(cmdf);
                if(outputLength>255){
                    String[] cmdr = {"\t\t    setRelease","0"};
                    cmds.add(cmdr);
                }
            }            
            return;
        }       
        
    }
    
    private void sustain(){
        if(cc.getRelease()!=0){
            // keep as is
        }else{
            cc.setRelease(0x80);
            int cursor = 1;
            if(cursor>cmds.size()){
                return;
            }
            String cmd = cmds.get(cmds.size()-cursor)[0];
            while(!cmd.contains("note")&&!cmd.contains("wait")){
                cursor++;
                if(cursor>cmds.size()){
                    return;
                }
                cmd = cmds.get(cmds.size()-cursor)[0];
            }
            String[] command = {"\t\t    sustain"};
            cmds.add(cmds.size()-cursor,command);
            
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
