/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sega.md.snd.formats.cube.channel;

import com.sega.md.snd.formats.cube.CubeChannel;
import com.sega.md.snd.formats.cube.CubeCommand;
import com.sega.md.snd.formats.cube.command.ChannelEnd;
import com.sega.md.snd.formats.cube.command.CountedLoopEnd;
import com.sega.md.snd.formats.cube.command.CountedLoopStart;
import com.sega.md.snd.formats.cube.command.Inst;
import com.sega.md.snd.formats.cube.command.MainLoopEnd;
import com.sega.md.snd.formats.cube.command.MainLoopStart;
import com.sega.md.snd.formats.cube.command.NoSlide;
import com.sega.md.snd.formats.cube.command.RepeatEnd;
import com.sega.md.snd.formats.cube.command.RepeatSection1Start;
import com.sega.md.snd.formats.cube.command.RepeatSection2Start;
import com.sega.md.snd.formats.cube.command.RepeatSection3Start;
import com.sega.md.snd.formats.cube.command.RepeatStart;
import com.sega.md.snd.formats.cube.command.Sample;
import com.sega.md.snd.formats.cube.command.SampleL;
import com.sega.md.snd.formats.cube.command.SetRelease;
import com.sega.md.snd.formats.cube.command.SetSlide;
import com.sega.md.snd.formats.cube.command.Shifting;
import com.sega.md.snd.formats.cube.command.Stereo;
import com.sega.md.snd.formats.cube.command.Sustain;
import com.sega.md.snd.formats.cube.command.Vibrato;
import com.sega.md.snd.formats.cube.command.Vol;
import com.sega.md.snd.formats.cube.command.Wait;
import com.sega.md.snd.formats.cube.command.WaitL;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Wiz
 */
public class DacChannel extends CubeChannel {

    public DacChannel(){
        
    }    
    
    public DacChannel(byte[] data, int cursor){
        List<CubeCommand> ccs = new ArrayList();
        int cmdLength = 1;
        while(ccs.isEmpty() || (!(ccs.get(ccs.size()-1) instanceof ChannelEnd) && !(ccs.get(ccs.size()-1) instanceof MainLoopEnd))){
            byte cmd = data[cursor];
            
            if((cmd&0xFF)==0xFF){
                cmdLength = 3;
                byte b1 = data[cursor+1];
                byte b2 = data[cursor+2];
                if((b1&0xFF)==0&&(b2&0xFF)==0){
                    ccs.add(new ChannelEnd());
                }else{
                    System.out.println("Unsupported command at offset 0x"+Integer.toHexString(cursor)+" : $"+String.format("%02x", cmd)+String.format("%02x", b1)+String.format("%02x", b2));
                    break;
                }
            }else if((cmd&0xFF)==0xFE){
                cmdLength = 2;
                byte b1 = data[cursor+1];
                ccs.add(new Inst(b1));
            }else if((cmd&0xFF)==0xFD){
                cmdLength = 2;
                byte b1 = data[cursor+1];
                ccs.add(new Vol(b1));
            }else if((cmd&0xFF)==0xFC){
                cmdLength = 2;
                byte b1 = data[cursor+1];
                if((b1&0xFF)<0x80){
                    ccs.add(new SetRelease(b1));
                }else if((b1&0xFF)==0x80){
                    ccs.add(new Sustain());
                }else if((b1&0xFF)<0xFF){
                    ccs.add(new SetSlide(b1));
                }else {
                    ccs.add(new NoSlide());
                }
            }else if((cmd&0xFF)==0xFB){
                cmdLength = 2;
                byte b1 = data[cursor+1];
                ccs.add(new Vibrato(b1));
            }else if((cmd&0xFF)==0xFA){
                cmdLength = 2;
                byte b1 = data[cursor+1];
                ccs.add(new Stereo(b1));
            }else if((cmd&0xFF)==0xF9){
                cmdLength = 2;
                byte b1 = data[cursor+1];
                ccs.add(new Shifting(b1));
            }else if((cmd&0xFF)==0xF8){
                cmdLength = 2;
                byte b1 = data[cursor+1];
                if((b1&0xFF)==0x00){
                    ccs.add(new MainLoopStart());
                }else if((b1&0xFF)==0xA1){
                    ccs.add(new MainLoopEnd());
                }else if((b1&0xFF)==0x20){
                    ccs.add(new RepeatStart());
                }else if((b1&0xFF)==0xA0){
                    ccs.add(new RepeatEnd());
                }else if((b1&0xFF)==0x40){
                    ccs.add(new RepeatSection1Start());
                }else if((b1&0xFF)==0x60){
                    ccs.add(new RepeatSection2Start());
                }else if((b1&0xFF)==0x80){
                    ccs.add(new RepeatSection3Start());
                }else if((b1&0xFF)==0xE0){
                    ccs.add(new CountedLoopEnd());
                }else{
                    ccs.add(new CountedLoopStart((byte)((b1&0xFF)-0xC0)));
                }                
            }else if((cmd&0xFF)==0xF0){
                cmdLength = 2;
                byte b1 = data[cursor+1];
                ccs.add(new WaitL(b1));
            }else if((cmd&0xFF)==0x70){
                cmdLength = 1;
                ccs.add(new Wait());
            }else if((cmd&0xFF)>=0x80){
                cmdLength = 2;
                byte b1 = data[cursor+1];
                ccs.add(new SampleL((byte)(cmd&0x7F),b1));
            }else {
                cmdLength = 1;
                ccs.add(new Sample(cmd));
            }            
            
            //System.out.println(ccs.get(ccs.size()-1).produceAsmOutput());
            
            cursor+=cmdLength;
        }
        CubeCommand[] ccsArray = new CubeCommand[ccs.size()];
        this.setCcs(ccs.toArray(ccsArray));
    }
    
    
    
    
}
