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
public class PsgNoteL extends CubeCommand {
    
    Pitch note;
    byte noiseValue = 0;
    byte length = 0;

    public PsgNoteL(Pitch note, byte length) {
        this.note = note;
        this.length = length;
    }

    public PsgNoteL(byte noiseValue, byte length) {
        this.noiseValue = noiseValue;
        this.length = length;
    }

    @Override
    public byte[] produceBinaryOutput() {
        return new byte[]{(byte)((note!=null?note.getValue():noiseValue)+0x80), length};
    }

    @Override
    public String produceAsmOutput() {
        return "        psgNoteL "+(note!=null?note:("0"+Integer.toHexString(noiseValue&0xFF)+"h"))+", "+Integer.toString(length&0xFF);
    }

    @Override
    public boolean equals(CubeCommand cc) {
        if(cc instanceof PsgNoteL 
                && ((note!=null && ((PsgNoteL)cc).note == this.note) || (note==null && ((PsgNoteL)cc).noiseValue == noiseValue))
                && ((PsgNoteL)cc).length == this.length){
            return true;
        }else{
            return false;
        }
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

    public byte getLength() {
        return length;
    }

    public void setLength(byte length) {
        this.length = length;
    }

    @Override
    public int getPlayLength() {
        return this.length;
    }
    
    @Override
    public boolean equals(CubeCommand cc, int currentPlayLength) {
        if(cc instanceof PsgNote 
                && ( (note!=null && ((PsgNote)cc).note == this.note) || (note==null && ((PsgNote)cc).noiseValue == this.noiseValue) )
                && (this.length&0xFF) == currentPlayLength){
            return true;
        }else{
            return equals(cc);
        }
    }
    
    
    
}
