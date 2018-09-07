/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.sound.vgmmm.out;

/**
 *
 * @author wiz
 */
public class ChannelContext {
    
    
    private int level=0xF;
    private int release;
    private int length;
    private int legato;
    private int vibrato;
    private String key = "";
    private int instrument=-1;
    private int slideTarget;
    private int noteShift;
    private int freqShift;
    private int stereo = 0xC0;
    private int slideSpeed;

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getRelease() {
        return release;
    }

    public void setRelease(int release) {
        this.release = release;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getLegato() {
        return legato;
    }

    public void setLegato(int legato) {
        this.legato = legato;
    }

    public int getVibrato() {
        return vibrato;
    }

    public void setVibrato(int vibrato) {
        this.vibrato = vibrato;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getInstrument() {
        return instrument;
    }

    public void setInstrument(int instrument) {
        this.instrument = instrument;
    }

    public int getSlideTarget() {
        return slideTarget;
    }

    public void setSlideTarget(int slideTarget) {
        this.slideTarget = slideTarget;
    }

    public int getNoteShift() {
        return noteShift;
    }

    public void setNoteShift(int noteShift) {
        this.noteShift = noteShift;
    }

    public int getFreqShift() {
        return freqShift;
    }

    public void setFreqShift(int freqShift) {
        this.freqShift = freqShift;
    }

    public int getStereo() {
        return stereo;
    }

    public void setStereo(int stereo) {
        this.stereo = stereo;
    }

    public int getSlideSpeed() {
        return slideSpeed;
    }

    public void setSlideSpeed(int slideSpeed) {
        this.slideSpeed = slideSpeed;
    }
    
    
    
}
