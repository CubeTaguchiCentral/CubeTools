/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.sound.convert.io.furnace;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Wiz
 */
public class FurnaceRow {
    
    private static final int MAX_EFFECT_SIZE=4;
    
    private FurnaceNote note;
    private FurnaceInstrument instrument;
    private FurnaceVolume volume;
    private List<FurnaceEffect> effectList = new ArrayList();

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

    public List<FurnaceEffect> getEffectList() {
        return effectList;
    }

    public void setEffectList(List<FurnaceEffect> effectList) {
        this.effectList = effectList;
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
        for(int i=0;i<MAX_EFFECT_SIZE;i++){
            if(effectList.size()>i && effectList.get(i)!=null){
                clipboard+=effectList.get(i).produceClipboardOutput();
            }else{
                clipboard+="....";
            }
        }
        return clipboard;
    }
    
}
