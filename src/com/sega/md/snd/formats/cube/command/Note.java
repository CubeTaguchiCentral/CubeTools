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
public class Note extends CubeCommand {
    
    Pitch note;
    byte currentPlayLength = 0;

    public Note(Pitch note) {
        this.note = note;
    }

    @Override
    public byte[] produceBinaryOutput() {
        return new byte[]{(byte)(note.getValue()-24)};
    }

    @Override
    public String produceAsmOutput() {
        return "        note "+note;
    }

    @Override
    public boolean equals(CubeCommand cc) {
        if(cc instanceof Note){ 
            if(((Note)cc).note == this.note
                && (this.currentPlayLength==0
                    || ((Note)cc).currentPlayLength==0
                    || ((Note)cc).currentPlayLength==this.currentPlayLength
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

    public byte getCurrentPlayLength() {
        return currentPlayLength;
    }

    public void setCurrentPlayLength(byte currentLength) {
        this.currentPlayLength = currentLength;
    }

    @Override
    public int getPlayLength() {
        return 0;
    }
    
    @Override
    public boolean equals(CubeCommand cc, int currentPlayLength) {
        if(cc instanceof NoteL 
                && ((NoteL)cc).note == this.note
                && (((NoteL)cc).length&0xFF) == currentPlayLength){
            return true;
        }else{
            return equals(cc);
        }
    }
    
    
    
}
