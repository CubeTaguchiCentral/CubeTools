/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.sound.convert.io.cube.command;

import com.sfc.sf2.sound.convert.io.cube.CubeCommand;
import com.sfc.sf2.sound.convert.io.cube.Pitch;

/**
 *
 * @author Wiz
 */
public class NoteL extends CubeCommand {
    
    Pitch note;
    byte length = 0;

    public NoteL(Pitch note, byte length) {
        this.note = note;
        this.length = length;
    }

    @Override
    public byte[] produceBinaryOutput() {
        return new byte[]{(byte)(note.getValue()+24+0x80), length};
    }

    @Override
    public String produceAsmOutput() {
        return "        noteL "+note+", "+Integer.toString(length&0xFF);
    }

    @Override
    public boolean equals(CubeCommand cc) {
        if(cc instanceof NoteL 
                && ((NoteL)cc).note == this.note
                && ((NoteL)cc).length == this.length){
            return true;
        }else{
            return false;
        }
    }
    
    
    
}
