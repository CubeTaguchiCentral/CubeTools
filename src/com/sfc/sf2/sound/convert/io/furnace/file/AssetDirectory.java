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
public class AssetDirectory {
    
    private String blockId = "ADIR";
    private int size = 0;
    private int numberOfDirs = 0;
    private Asset[] assets = null;

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

    public int getNumberOfDirs() {
        return numberOfDirs;
    }

    public void setNumberOfDirs(int numberOfDirs) {
        this.numberOfDirs = numberOfDirs;
    }

    public Asset[] getAssets() {
        return assets;
    }

    public void setAssets(Asset[] assets) {
        this.assets = assets;
    }
    
}
