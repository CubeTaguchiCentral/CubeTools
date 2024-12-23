/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.sound.convert;

import com.sfc.sf2.sound.convert.io.AsmMusicEntryManager;
import com.sfc.sf2.sound.convert.io.BinaryMusicBankManager;
import com.sfc.sf2.sound.convert.io.BinaryMusicEntryManager;
import com.sfc.sf2.sound.convert.io.FurnaceClipboardManager;
import com.sfc.sf2.sound.convert.io.FurnaceFileManager;
import com.sfc.sf2.sound.convert.io.cube.MusicEntry;

/**
 *
 * @author Wiz
 */
public class CubeConversionManager {
    
    MusicEntry me;
    
    public void importMusicEntryFromBinaryMusicBank(String filePath, int ptOffset, int index, int ymInstOffset){
        System.out.println("com.sfc.sf2.sound.convert.CubeConversionManager.importMusicEntryFromBinaryMusicBank() - Importing ...");
        me = BinaryMusicBankManager.importMusicEntry(filePath, ptOffset, index, ymInstOffset);
        me.factorizeIdenticalChannels();
        me.hasMainLoop();
        me.hasIntro();
        System.out.println("com.sfc.sf2.sound.convert.CubeConversionManager.importMusicEntryFromBinaryMusicBank() - ... Done.");
    }
    
    public void exportMusicEntryAsAsm(String filePath, String name, boolean unroll, boolean optimize){
        System.out.println("com.sfc.sf2.sound.convert.CubeConversionManager.exportMusicEntryAsAsm() - Exporting ...");
        me.setName(name);
        if(unroll){
            me.unroll();
            if(optimize){
                me.optimize();
            }
        }
        AsmMusicEntryManager.exportMusicEntryAsAsm(me, filePath, name);
        System.out.println("com.sfc.sf2.sound.convert.CubeConversionManager.exportMusicEntryAsAsm() - ... Done.");
    }
    
    public void exportMusicEntryAsBinary(String filePath, boolean unroll, boolean optimize){
        System.out.println("com.sfc.sf2.sound.convert.CubeConversionManager.exportMusicEntryAsBinary() - Exporting ...");
        if(unroll){
            me.unroll();
            if(optimize){
                me.optimize();
            }
        }
        BinaryMusicEntryManager.exportMusicEntryAsBinary(me, filePath);
        System.out.println("com.sfc.sf2.sound.convert.CubeConversionManager.exportMusicEntryAsBinary() - ... Done.");
    }
    
    public void importMusicEntryFromBinaryFile(String filePath){
        System.out.println("com.sfc.sf2.sound.convert.CubeConversionManager.importMusicEntryFromBinaryFile() - Importing ...");
        me = BinaryMusicEntryManager.importMusicEntry(filePath);
        me.factorizeIdenticalChannels();
        me.hasMainLoop();
        me.hasIntro();
        System.out.println("com.sfc.sf2.sound.convert.CubeConversionManager.importMusicEntryFromBinaryFile() - ... Done.");
    }
    
    public void exportMusicEntryToBinaryMusicBank(String filePath, int ptOffset, int index, boolean unroll, boolean optimize){
        System.out.println("com.sfc.sf2.sound.convert.CubeConversionManager.exportMusicEntryToBinaryMusicBank() - Exporting ...");
        if(unroll){
            me.unroll();
            if(optimize){
                me.optimize();
            }
        }
        BinaryMusicBankManager.exportMusicEntry(me, filePath, ptOffset, index);
        System.out.println("com.sfc.sf2.sound.convert.CubeConversionManager.exportMusicEntryToBinaryMusicBank() - ... Done.");
    }
    
    public void exportMusicEntryAsFurnaceClipboard(String filePath){
        System.out.println("com.sfc.sf2.sound.convert.CubeConversionManager.exportMusicEntryAsFurnaceClipboard() - Exporting ...");
        me.unroll();
        FurnaceClipboardManager.exportMusicEntryAsFurnaceClipboard(me, filePath);
        System.out.println("com.sfc.sf2.sound.convert.CubeConversionManager.exportMusicEntryAsFurnaceClipboard() - ... Done.");
    }
    
    public void exportMusicEntryAsFurnaceFile(String templateFilePath, String outputFilePath, boolean singleLoop){
        System.out.println("com.sfc.sf2.sound.convert.CubeConversionManager.exportMusicEntryAsFurnaceClipboard() - Exporting ...");
        me.unroll();
        FurnaceFileManager.exportMusicEntryAsFurnaceFile(me, templateFilePath, outputFilePath, singleLoop);
        System.out.println("com.sfc.sf2.sound.convert.CubeConversionManager.exportMusicEntryAsFurnaceClipboard() - ... Done.");
    }
    
    
}
