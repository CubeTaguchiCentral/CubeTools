/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.sound.convert.io.furnace.pattern;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Wiz
 */
public class Row {
    
    private static final int MAX_EFFECT_SIZE=4;
    
    private Note note;
    private Instrument instrument;
    private Volume volume;
    private List<Effect> effectList = new ArrayList();

    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
    }

    public Instrument getInstrument() {
        return instrument;
    }

    public void setInstrument(Instrument instrument) {
        this.instrument = instrument;
    }

    public Volume getVolume() {
        return volume;
    }

    public void setVolume(Volume volume) {
        this.volume = volume;
    }

    public List<Effect> getEffectList() {
        return effectList;
    }

    public void setEffectList(List<Effect> effectList) {
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
    
    public boolean isEmpty(){
        if(note==null
                && instrument==null
                && volume==null
                && effectList.size()==0){
            return true;
        }else{
            return false;
        }
    }
    
}
