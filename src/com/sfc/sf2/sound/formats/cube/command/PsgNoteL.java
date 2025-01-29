/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.sound.formats.cube.command;

import com.sfc.sf2.sound.formats.cube.CubeCommand;
import com.sfc.sf2.sound.formats.cube.Pitch;

/**
 *
 * @author Wiz
 */
public class PsgNoteL extends CubeCommand {
    
    Pitch note;
    byte length = 0;

    public PsgNoteL(Pitch note, byte length) {
        this.note = note;
        this.length = length;
    }

    @Override
    public byte[] produceBinaryOutput() {
        return new byte[]{(byte)(note.getValue()+0x80), length};
    }

    @Override
    public String produceAsmOutput() {
        return "        psgNoteL "+note+", "+Integer.toString(length&0xFF);
    }

    @Override
    public boolean equals(CubeCommand cc) {
        if(cc instanceof PsgNoteL 
                && ((PsgNoteL)cc).note == this.note
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

    public byte getLength() {
        return length;
    }

    public void setLength(byte length) {
        this.length = length;
    }
    
    
    
}
