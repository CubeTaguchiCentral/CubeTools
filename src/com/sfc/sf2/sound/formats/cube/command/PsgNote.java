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
public class PsgNote extends CubeCommand {
    
    Pitch note;

    public PsgNote(Pitch note) {
        this.note = note;
    }

    @Override
    public byte[] produceBinaryOutput() {
        return new byte[]{(byte)note.getValue()};
    }

    @Override
    public String produceAsmOutput() {
        return "        psgNote "+note;
    }

    @Override
    public boolean equals(CubeCommand cc) {
        if(cc instanceof PsgNote 
                && ((PsgNote)cc).note == this.note){
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
    
    
    
}
