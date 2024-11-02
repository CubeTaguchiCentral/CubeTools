/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.sound.convert.io.furnace;

/**
 *
 * @author Wiz
 */
public class FurnaceRow {
    
    private static final int MAX_EFFECT_SIZE=4;
    
    private FurnaceNote note;
    private FurnaceInstrument instrument;
    private FurnaceVolume volume;
    private FurnaceEffect[] effects = new FurnaceEffect[MAX_EFFECT_SIZE];

    public FurnaceNote getNote() {
        return note;
    }

    public void setNote(FurnaceNote note) {
        this.note = note;
    }

    public FurnaceInstrument getInstrument() {
        return instrument;
    }

    public void setInstrument(FurnaceInstrument instrument) {
        this.instrument = instrument;
    }

    public FurnaceVolume getVolume() {
        return volume;
    }

    public void setVolume(FurnaceVolume volume) {
        this.volume = volume;
    }

    public FurnaceEffect[] getEffects() {
        return effects;
    }

    public void setEffects(FurnaceEffect[] effects) {
        this.effects = effects;
    }
    
    public String produceClipboardOutput(){
        String clipboard="";
        if(note!=null){
            clipboard+=note.produceClipboardOutput();
        }else{
            clipboard+="...";
        }
        if(instrument!=null){
            clipboard+=instrument.produceClipboardOutput();
        }else{
            clipboard+="..";
        }
        if(volume!=null){
            clipboard+=volume.produceClipboardOutput();
        }else{
            clipboard+="..";
        }
        for(int i=0;i<effects.length;i++){
            if(effects[i]!=null){
                clipboard+=effects[i].produceClipboardOutput();
            }else{
                clipboard+="....";
            }
        }
        return clipboard;
    }
    
}
