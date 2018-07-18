/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.sound.vgmmm.export;

/**
 *
 * @author wiz
 */
public class YmConverter {
    
    
    
    
    
    public static ChannelContext convertYmChannel(ChannelData ymc, ChannelContext cc){
        
        if(cc==null){
            cc = new ChannelContext();
        }
        
        String[] lines = ymc.getInput().toString().replace(".", " ").split("\n");
        
        StringBuilder outsb = ymc.getOutput();
        
        String note = null;
        String volume = null;
        String instrument = null;
        String effect1 = null;
        String effect2 = null;
        String effect3 = null;
        String effect4 = null;
        
        for(int i=0;i<lines.length;i++){
            String line = lines[i];
            note = line.substring(0,3);
            volume = line.substring(3,5);
            instrument = line.substring(5,7);
            effect1 = line.substring(7,10);
            effect2 = line.substring(10,13);
            effect3 = line.substring(13,16);
            effect4 = line.substring(16);
                        
            if(!volume.trim().isEmpty()){
                int vol = Integer.valueOf(volume,16);
                if(vol/2!=cc.getLevel()){
                    cc.setLevel(vol/2);
                    outsb.append("\t\t    ymVol "+vol/2+"\n");
                }
            }
            
            if(!instrument.trim().isEmpty()){
                int inst = Integer.valueOf(instrument,16);
                if(inst!=cc.getInstrument()){
                    cc.setInstrument(inst);
                    outsb.append("\t\t    ymInst "+inst+"\n");
                }
            }

            //TODO manage first silence
            
            if(!note.trim().isEmpty()){
                String key = note.replace("-", "").replace("#","s");
                int length = 0;
                for(int lc=1;lc<lines.length-i;lc++){
                    length++;
                    if(!lines[i+lc].substring(0,3).trim().isEmpty()){
                        break;
                    }
                }
                //TODO manage lengths higher than 255.
                if(length!=cc.getLength()){
                    cc.setLength(length);
                    outsb.append("\t\t          noteL "+key+","+length+"\n");
                }else{
                    outsb.append("\t\t          note "+key+"\n");
                }
                
                
                
            }            
            
            
        }
        
        
        return cc;
    }
    
    
    
}
