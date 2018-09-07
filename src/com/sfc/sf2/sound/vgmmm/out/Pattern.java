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
public class Pattern {
    
    private int index;
    private int length;
    private ChannelData[] channels;

    public ChannelData[] getChannels() {
        return channels;
    }

    public void setChannels(ChannelData[] channels) {
        this.channels = channels;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
    
}
