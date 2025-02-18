/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sega.md.snd.formats.furnace.pattern;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Wiz
 */
public class Row {
    
    private static final int MAX_EFFECT_SIZE=4;
    
    private FNote note;
    private Instrument instrument;
    private Volume volume;
    private List<Effect> effectList = new ArrayList();

    public FNote getNote() {
        return note;
    }

    public void setNote(FNote note) {
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
        StringBuilder clipboard = new StringBuilder();
        if(note!=null){
            clipboard.append(note.produceClipboardOutput());
        }else{
            clipboard.append("...");
        }
        if(instrument!=null){
            clipboard.append(instrument.produceClipboardOutput());
        }else{
            clipboard.append("..");
        }
        if(volume!=null){
            clipboard.append(volume.produceClipboardOutput());
        }else{
            clipboard.append("..");
        }
        for(int i=0;i<MAX_EFFECT_SIZE;i++){
            if(effectList.size()>i && effectList.get(i)!=null){
                clipboard.append(effectList.get(i).produceClipboardOutput());
            }else{
                clipboard.append("....");
            }
        }
        return clipboard.toString();
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
    
    public Row clone(){
        Row newRow = new Row();
        newRow.setNote(this.note);
        newRow.setInstrument(this.instrument);
        newRow.setVolume(this.volume);
        List<Effect> newEffectList = new ArrayList(0);
        for(Effect e:this.effectList){
            newEffectList.add(e);
        }
        newRow.setEffectList(newEffectList);
        return newRow;
    }
    
}
