/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sega.md.snd.formats.cube.command;

import com.sega.md.snd.formats.cube.CubeCommand;
import com.sega.md.snd.formats.cube.Pitch;

/**
 *
 * @author Wiz
 */
public class PsgNote extends CubeCommand {
    
    Pitch note;
    byte noiseValue = 0;
    byte currentPlayLength = 0;

    public PsgNote(Pitch note) {
        this.note = note;
    }

    public PsgNote(byte noiseValue) {
        this.noiseValue = noiseValue;
    }

    @Override
    public byte[] produceBinaryOutput() {
        return new byte[]{(byte)(note!=null?note.getValue():noiseValue)};
    }

    @Override
    public String produceAsmOutput() {
        return "        psgNote "+(note!=null?note:("0"+Integer.toHexString(noiseValue&0xFF)+"h"));
    }

    @Override
    public boolean equals(CubeCommand cc) {
        if(cc instanceof PsgNote){ 
            if( ( (note!=null && ((PsgNote)cc).note == this.note) || (note==null && ((PsgNote)cc).noiseValue == this.noiseValue) )
                && (this.currentPlayLength==0
                    || ((PsgNote)cc).currentPlayLength==0
                    || ((PsgNote)cc).currentPlayLength==this.currentPlayLength
                    )
               ){
                return true;
            }
        }
        return false;         
    }

    public Pitch getNote() {
        return note;
    }

    public void setNote(Pitch note) {
        this.note = note;
    }

    public byte getNoiseValue() {
        return noiseValue;
    }

    public void setNoiseValue(byte noiseValue) {
        this.noiseValue = noiseValue;
    }

    @Override
    public int getPlayLength() {
        return 0;
    }

    public byte getCurrentPlayLength() {
        return currentPlayLength;
    }

    public void setCurrentPlayLength(byte currentLength) {
        this.currentPlayLength = currentLength;
    }
    
    @Override
    public boolean equals(CubeCommand cc, int currentPlayLength) {
        if(cc instanceof PsgNoteL 
                && ((note!=null && ((PsgNoteL)cc).note == this.note) || (note==null && ((PsgNoteL)cc).noiseValue == noiseValue))
                && (((PsgNoteL)cc).length&0xFF) == currentPlayLength){
            return true;
        }else{
            return equals(cc);
        }
    }
    
    
    
}
