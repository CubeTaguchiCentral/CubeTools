/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.sound.convert.io.furnace.file;

/**
 *
 * @author Wiz
 */
public class Instrument {
    
    private String blockId = "INS2";
    private int size = 0;
    private short formatVersion = 219;
    private short instrumentType = 0;
    private Feature[] features = null;

    public String getBlockId() {
        return blockId;
    }

    public void setBlockId(String blockId) {
        this.blockId = blockId;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public short getFormatVersion() {
        return formatVersion;
    }

    public void setFormatVersion(short formatVersion) {
        this.formatVersion = formatVersion;
    }

    public short getInstrumentType() {
        return instrumentType;
    }

    public void setInstrumentType(short instrumentType) {
        this.instrumentType = instrumentType;
    }

    public Feature[] getFeatures() {
        return features;
    }

    public void setFeatures(Feature[] features) {
        this.features = features;
    }
    
}
