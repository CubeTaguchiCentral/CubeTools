/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.sound.convert;

import com.sfc.sf2.sound.convert.io.AsmMusicEntryManager;
import com.sfc.sf2.sound.convert.io.BinaryMusicBankManager;
import com.sfc.sf2.sound.convert.io.cube.MusicEntry;

/**
 *
 * @author Wiz
 */
public class CubeConversionManager {
    
    MusicEntry me;
    
    public void importMusicEntryFromBinaryMusicBank(String filePath, int ptOffset, int index){
        me = BinaryMusicBankManager.importMusicEntry(filePath, ptOffset, index);
    }
    
    public void exportMusicEntryAsAsm(String filePath, String name){
        me.setName(name);
        AsmMusicEntryManager.exportMusicEntryAsAsm(me, filePath, name);
    }
    
    
    
    
    
    
}
