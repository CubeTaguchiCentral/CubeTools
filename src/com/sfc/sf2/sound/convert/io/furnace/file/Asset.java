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
public class Asset {
    
    private String name = "";
    private short numberOfAssets = 0;
    private byte[] assets = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public short getNumberOfAssets() {
        return numberOfAssets;
    }

    public void setNumberOfAssets(short numberOfAssets) {
        this.numberOfAssets = numberOfAssets;
    }

    public byte[] getAssets() {
        return assets;
    }

    public void setAssets(byte[] assets) {
        this.assets = assets;
    }
    
}
