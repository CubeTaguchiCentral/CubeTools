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
    
    MusicEntry[] mes = new MusicEntry[32];
    
    public void importMusicEntryFromBinaryMusicBank(String filePath, int ptOffset, int index, int ymInstOffset, boolean ssgEg, int sampleEntriesOffset, int[] sampleBanksOffsets){
        System.out.println("com.sfc.sf2.sound.convert.CubeConversionManager.importMusicEntryFromBinaryMusicBank() - Importing ...");
        try{        
            mes[0] = BinaryMusicBankManager.importMusicEntry(filePath, ptOffset, index, ymInstOffset, ssgEg);
            mes[0].factorizeIdenticalChannels();
            mes[0].hasMainLoop();
            mes[0].hasIntro();
            int maxSampleIndex = mes[0].findMaxSampleIndex();
            byte[][] sampleEntries = BinaryMusicBankManager.importSampleEntries(filePath, sampleEntriesOffset, maxSampleIndex);
            byte[][] sampleBanks = BinaryMusicBankManager.importSampleBanks(filePath, sampleBanksOffsets);
            mes[0].setSampleEntries(sampleEntries);
            mes[0].setSampleBanks(sampleBanks);
        }catch(Exception e){
            e.printStackTrace();
        }
        System.out.println("com.sfc.sf2.sound.convert.CubeConversionManager.importMusicEntryFromBinaryMusicBank() - ... Done.");
    }
    
    public void importMusicEntriesFromBinaryMusicBank(String filePath, int ptOffset, int ymInstOffset, boolean ssgEg, int sampleEntriesOffset, int[] sampleBanksOffsets){
        System.out.println("com.sfc.sf2.sound.convert.CubeConversionManager.importMusicEntryFromBinaryMusicBank() - Importing ...");
        int maxSampleIndex = 0;
        for(int i=0;i<mes.length;i++){
            try{
                mes[i] = BinaryMusicBankManager.importMusicEntry(filePath, ptOffset, i+1, ymInstOffset, ssgEg);
                mes[i].factorizeIdenticalChannels();
                mes[i].hasMainLoop();
                mes[i].hasIntro();
                System.out.println("Imported entry "+(i+1));
                int index = mes[i].findMaxSampleIndex();
                if(index>maxSampleIndex){
                    maxSampleIndex = index;
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        for(int i=0;i<mes.length;i++){
            byte[][] sampleEntries = BinaryMusicBankManager.importSampleEntries(filePath, sampleEntriesOffset, maxSampleIndex);
            byte[][] sampleBanks = BinaryMusicBankManager.importSampleBanks(filePath, sampleBanksOffsets);
            mes[i].setSampleEntries(sampleEntries);
            mes[i].setSampleBanks(sampleBanks);
        }
        System.out.println("com.sfc.sf2.sound.convert.CubeConversionManager.importMusicEntryFromBinaryMusicBank() - ... Done.");
    }
    
    public void exportMusicEntryAsAsm(String filePath, String name, boolean unroll, boolean optimize){
        System.out.println("com.sfc.sf2.sound.convert.CubeConversionManager.exportMusicEntryAsAsm() - Exporting ...");
        mes[0].setName(name);
        if(unroll){
            mes[0].unroll();
            if(optimize){
                mes[0].optimize();
            }
        }
        AsmMusicEntryManager.exportMusicEntryAsAsm(mes[0], filePath);
        System.out.println("com.sfc.sf2.sound.convert.CubeConversionManager.exportMusicEntryAsAsm() - ... Done.");
    }
    
    public void exportMusicEntriesAsAsm(String filePath, String name, boolean unroll, boolean optimize){
        System.out.println("com.sfc.sf2.sound.convert.CubeConversionManager.exportMusicEntryAsAsm() - Exporting ...");
        for(int i=0;i<32;i++){        
            mes[i].setName(name+String.format("%02d", i+1));
            String completePath = filePath + String.format("%02d", i+1) + ".asm";
            if(unroll){
                mes[i].unroll();
                if(optimize){
                    mes[i].optimize();
                }
            }
            AsmMusicEntryManager.exportMusicEntryAsAsm(mes[i], completePath);
        }
        System.out.println("com.sfc.sf2.sound.convert.CubeConversionManager.exportMusicEntryAsAsm() - ... Done.");
    }
    
    public void exportMusicEntryAsBinary(String filePath, boolean unroll, boolean optimize){
        System.out.println("com.sfc.sf2.sound.convert.CubeConversionManager.exportMusicEntryAsBinary() - Exporting ...");
        if(unroll){
            mes[0].unroll();
            if(optimize){
                mes[0].optimize();
            }
        }
        BinaryMusicEntryManager.exportMusicEntryAsBinary(mes[0], filePath);
        System.out.println("com.sfc.sf2.sound.convert.CubeConversionManager.exportMusicEntryAsBinary() - ... Done.");
    }
    
    public void exportMusicEntriesAsBinary(String filePath, boolean unroll, boolean optimize){
        System.out.println("com.sfc.sf2.sound.convert.CubeConversionManager.exportMusicEntryAsBinary() - Exporting ...");
        for(int i=0;i<32;i++){        
            String completePath = filePath + String.format("%02d", i+1) + ".bin";
            if(unroll){
                mes[i].unroll();
                if(optimize){
                    mes[i].optimize();
                }
            }
            BinaryMusicEntryManager.exportMusicEntryAsBinary(mes[i], completePath);
        }        
        System.out.println("com.sfc.sf2.sound.convert.CubeConversionManager.exportMusicEntryAsBinary() - ... Done.");
    }
    
    public void importMusicEntryFromBinaryFile(String filePath){
        System.out.println("com.sfc.sf2.sound.convert.CubeConversionManager.importMusicEntryFromBinaryFile() - Importing ...");
        mes[0] = BinaryMusicEntryManager.importMusicEntry(filePath);
        mes[0].factorizeIdenticalChannels();
        mes[0].hasMainLoop();
        mes[0].hasIntro();
        System.out.println("com.sfc.sf2.sound.convert.CubeConversionManager.importMusicEntryFromBinaryFile() - ... Done.");
    }
    
    public void exportMusicEntryToBinaryMusicBank(String filePath, int ptOffset, int index, boolean unroll, boolean optimize){
        System.out.println("com.sfc.sf2.sound.convert.CubeConversionManager.exportMusicEntryToBinaryMusicBank() - Exporting ...");
        if(unroll){
            mes[0].unroll();
            if(optimize){
                mes[0].optimize();
            }
        }
        BinaryMusicBankManager.exportMusicEntry(mes[0], filePath, ptOffset, index);
        System.out.println("com.sfc.sf2.sound.convert.CubeConversionManager.exportMusicEntryToBinaryMusicBank() - ... Done.");
    }
    
    public void exportMusicEntryAsFurnaceClipboard(String filePath){
        System.out.println("com.sfc.sf2.sound.convert.CubeConversionManager.exportMusicEntryAsFurnaceClipboard() - Exporting ...");
        mes[0].unroll();
        FurnaceClipboardManager.exportMusicEntryAsFurnaceClipboard(mes[0], filePath);
        System.out.println("com.sfc.sf2.sound.convert.CubeConversionManager.exportMusicEntryAsFurnaceClipboard() - ... Done.");
    }
    
    public void exportMusicEntryAsFurnaceFile(String templateFilePath, String outputFilePath){
        System.out.println("com.sfc.sf2.sound.convert.CubeConversionManager.exportMusicEntryAsFurnaceClipboard() - Exporting ...");
        mes[0].unroll();
        FurnaceFileManager.exportMusicEntryAsFurnaceFile(mes[0], templateFilePath, outputFilePath);
        System.out.println("com.sfc.sf2.sound.convert.CubeConversionManager.exportMusicEntryAsFurnaceClipboard() - ... Done.");
    }
    
    public void exportMusicEntriesAsFurnaceFiles(String templateFilePath, String outputFilePath){
        System.out.println("com.sfc.sf2.sound.convert.CubeConversionManager.exportMusicEntryAsFurnaceClipboard() - Exporting ...");
        for(int i=0;i<mes.length;i++){        
            try{
                String completePath = outputFilePath + String.format("%02d", i+1) + ".fur";
                mes[i].unroll();
                FurnaceFileManager.exportMusicEntryAsFurnaceFile(mes[i], templateFilePath, completePath);
                System.out.println("Exported entry "+(i+1));
            }catch(Exception e){
                System.out.println("Error while exporting entry "+(i+1)+" : "+e.getMessage());
            }
        }        
        System.out.println("com.sfc.sf2.sound.convert.CubeConversionManager.exportMusicEntryAsFurnaceClipboard() - ... Done.");
    }
    
    
}
