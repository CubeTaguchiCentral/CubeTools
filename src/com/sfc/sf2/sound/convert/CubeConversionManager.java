/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.sound.convert;

import com.sfc.sf2.sound.convert.io.CubeAsmManager;
import com.sfc.sf2.sound.convert.io.CubeBankManager;
import com.sfc.sf2.sound.convert.io.CubeEntryManager;
import com.sfc.sf2.sound.convert.io.FurnaceClipboardManager;
import com.sfc.sf2.sound.convert.io.FurnaceFileManager;
import com.sfc.sf2.sound.formats.cube.MusicEntry;
import java.io.File;

/**
 *
 * @author Wiz
 */
public class CubeConversionManager {
    
    MusicEntry[] mes = new MusicEntry[32];
    
    public void importMusicEntryFromBinaryMusicBank(String filePath, int ptOffset, int ramPreloadOffset, int index, int ymInstOffset, boolean ssgEg, int sampleEntriesOffset, boolean multipleBanksFormat, int[] sampleBanksOffsets){
        System.out.println("CubeConversionManager.importMusicEntryFromBinaryMusicBank() - Importing ...");
        try{        
            mes[0] = CubeBankManager.importMusicEntry(filePath, ptOffset, ramPreloadOffset, index, ymInstOffset, ssgEg);
            mes[0].factorizeIdenticalChannels();
            mes[0].hasMainLoop();
            mes[0].hasIntro();
            int maxSampleIndex = mes[0].findMaxSampleIndex();
            byte[][] sampleEntries = CubeBankManager.importSampleEntries(filePath, sampleEntriesOffset, multipleBanksFormat, maxSampleIndex);
            byte[][] sampleBanks = CubeBankManager.importSampleBanks(filePath, sampleBanksOffsets);
            mes[0].setSampleEntries(sampleEntries);
            mes[0].setMultiSampleBank(multipleBanksFormat);
            mes[0].setSampleBanks(sampleBanks);
        }catch(Exception e){
            e.printStackTrace();
        }
        System.out.println("CubeConversionManager.importMusicEntryFromBinaryMusicBank() - ... Done.");
    }
    
    public void importMusicEntriesFromBinaryMusicBank(String filePath, int ptOffset, int ramPreloadOffset, int ymInstOffset, boolean ssgEg, int sampleEntriesOffset, boolean multipleBanksFormat, int[] sampleBanksOffsets){
        System.out.println("CubeConversionManager.importMusicEntryFromBinaryMusicBank() - Importing ...");
        int maxSampleIndex = 0;      
        for(int i=0;i<mes.length;i++){
            try{
                mes[i] = CubeBankManager.importMusicEntry(filePath, ptOffset, ramPreloadOffset, i+1, ymInstOffset, ssgEg);
                mes[i].factorizeIdenticalChannels();
                mes[i].hasMainLoop();
                mes[i].hasIntro();
                //System.out.println("Imported entry "+(i+1));
                int sampleIndex = mes[i].findMaxSampleIndex();
                if(sampleIndex>maxSampleIndex){
                    maxSampleIndex = sampleIndex;
                }
            }catch(Exception e){
                //e.printStackTrace();
                //break;
            }
        }
        
        for(int i=0;i<mes.length;i++){
            if(mes[i]!=null){
                byte[][] sampleEntries = CubeBankManager.importSampleEntries(filePath, sampleEntriesOffset, multipleBanksFormat, maxSampleIndex);
                byte[][] sampleBanks = CubeBankManager.importSampleBanks(filePath, sampleBanksOffsets);
                mes[i].setSampleEntries(sampleEntries);
                mes[i].setMultiSampleBank(multipleBanksFormat);
                mes[i].setSampleBanks(sampleBanks);   
            }else{
                break;
            }
        }

        System.out.println("CubeConversionManager.importMusicEntryFromBinaryMusicBank() - ... Done.");
    }
    
    public void exportMusicEntryAsAsm(String filePath, String name, boolean unroll, boolean optimize){
        System.out.println("CubeConversionManager.exportMusicEntryAsAsm() - Exporting ...");
        mes[0].setName(name);
        if(unroll){
            mes[0].unroll();
            if(optimize){
                mes[0].optimize();
            }
        }
        CubeAsmManager.exportMusicEntryAsAsm(mes[0], filePath);
        System.out.println("CubeConversionManager.exportMusicEntryAsAsm() - ... Done.");
    }
    
    public void exportMusicEntriesAsAsm(String filePath, String name, boolean unroll, boolean optimize){
        System.out.println("CubeConversionManager.exportMusicEntryAsAsm() - Exporting ...");
        for(int i=0;i<32;i++){        
            mes[i].setName(name+String.format("%02d", i+1));
            String completePath = filePath + String.format("%02d", i+1) + ".asm";
            if(unroll){
                mes[i].unroll();
                if(optimize){
                    mes[i].optimize();
                }
            }
            CubeAsmManager.exportMusicEntryAsAsm(mes[i], completePath);
        }
        System.out.println("CubeConversionManager.exportMusicEntryAsAsm() - ... Done.");
    }
    
    public void exportMusicEntryAsBinary(String filePath, boolean unroll, boolean optimize){
        System.out.println("CubeConversionManager.exportMusicEntryAsBinary() - Exporting ...");
        if(unroll){
            mes[0].unroll();
            if(optimize){
                mes[0].optimize();
            }
        }
        CubeEntryManager.exportMusicEntryAsBinary(mes[0], filePath);
        System.out.println("CubeConversionManager.exportMusicEntryAsBinary() - ... Done.");
    }
    
    public void exportMusicEntriesAsBinary(String filePath, boolean unroll, boolean optimize){
        System.out.println("CubeConversionManager.exportMusicEntryAsBinary() - Exporting ...");
        for(int i=0;i<32;i++){        
            String completePath = filePath + String.format("%02d", i+1) + ".bin";
            if(unroll){
                mes[i].unroll();
                if(optimize){
                    mes[i].optimize();
                }
            }
            CubeEntryManager.exportMusicEntryAsBinary(mes[i], completePath);
        }        
        System.out.println("CubeConversionManager.exportMusicEntryAsBinary() - ... Done.");
    }
    
    public void importMusicEntryFromBinaryFile(String filePath){
        System.out.println("CubeConversionManager.importMusicEntryFromBinaryFile() - Importing ...");
        mes[0] = CubeEntryManager.importMusicEntry(filePath);
        mes[0].factorizeIdenticalChannels();
        mes[0].hasMainLoop();
        mes[0].hasIntro();
        System.out.println("CubeConversionManager.importMusicEntryFromBinaryFile() - ... Done.");
    }
    
    public void exportMusicEntryToBinaryMusicBank(String filePath, int ptOffset, int index, boolean unroll, boolean optimize){
        System.out.println("CubeConversionManager.exportMusicEntryToBinaryMusicBank() - Exporting ...");
        if(unroll){
            mes[0].unroll();
            if(optimize){
                mes[0].optimize();
            }
        }
        CubeBankManager.exportMusicEntry(mes[0], filePath, ptOffset, index);
        System.out.println("CubeConversionManager.exportMusicEntryToBinaryMusicBank() - ... Done.");
    }
    
    public void exportMusicEntryAsFurnaceClipboard(String filePath){
        System.out.println("CubeConversionManager.exportMusicEntryAsFurnaceClipboard() - Exporting ...");
        mes[0].unroll();
        FurnaceClipboardManager.exportMusicEntryAsFurnaceClipboard(mes[0], filePath);
        System.out.println("CubeConversionManager.exportMusicEntryAsFurnaceClipboard() - ... Done.");
    }
    
    public void exportMusicEntryAsFurnaceFile(String templateFilePath, String outputFilePath){
        System.out.println("CubeConversionManager.exportMusicEntryAsFurnaceClipboard() - Exporting ...");
        mes[0].unroll();
        FurnaceFileManager.exportMusicEntryAsFurnaceFile(mes[0], templateFilePath, outputFilePath);
        System.out.println("CubeConversionManager.exportMusicEntryAsFurnaceClipboard() - ... Done.");
    }
    
    public void exportMusicEntriesAsFurnaceFiles(String templateFilePath, String outputFilePath){
        System.out.println("CubeConversionManager.exportMusicEntryAsFurnaceClipboard() - Exporting ...");
        for(int i=0;i<mes.length;i++){  
            if(mes[i]!=null && mes[i].hasContent()){
                try{
                    String completePath = outputFilePath + String.format("%02d", i+1) + ".fur";
                    mes[i].unroll();
                    FurnaceFileManager.exportMusicEntryAsFurnaceFile(mes[i], templateFilePath, completePath);
                    System.out.println("Exported entry "+(i+1));
                }catch(Exception e){
                    System.out.println("Error while exporting entry "+(i+1)+" : "+e.getMessage());
                    e.printStackTrace();
                }
            }            
        }        
        System.out.println("CubeConversionManager.exportMusicEntryAsFurnaceClipboard() - ... Done.");
    }
    
    public void massExportFromBinaryMusicBankToFurnaceFiles(String inputFilePath, String templateFilePath){
        System.out.println("CubeConversionManager.massExportFromBinaryMusicBankToFurnaceFiles() - Exporting ...");
        ConversionInputs[] cis = ConversionInputs.importConversionInputs(inputFilePath);
        String inputFileFolder = inputFilePath.substring(0, inputFilePath.lastIndexOf(File.separator)+1);
        for(int i=0;i<cis.length;i++){
            String gameName = cis[i].getGameName();
            String completRomFilepath = inputFileFolder + cis[i].getRomFilePath();
            int[] musicBankOffsets = cis[i].getMusicBankOffsets();
            int inRamPreloadOffset = cis[i].getInRamPreloadOffset();
            int[] ymInstruments = cis[i].getYmInstruments();
            boolean ssgEg = cis[i].isSsgEg();
            int sampleTableOffset = cis[i].getSampleTableOffset();
            boolean multiBankSampleTableFormat = cis[i].isMultiBankSampleTableFormat();
            int[] sampleBankOffsets = cis[i].getSampleBankOffsets();
            String[] targetFolders = cis[i].getTargetFolders();
            for(int j=0;j<musicBankOffsets.length;j++){
                System.out.println("Importing "+gameName+" music bank "+(j)+" ...");
                mes = new MusicEntry[32];
                int ymInstrumentsOffset = ymInstruments[0];
                if(ymInstruments.length>j){
                    ymInstrumentsOffset = ymInstruments[j];
                }
                importMusicEntriesFromBinaryMusicBank(completRomFilepath, musicBankOffsets[j], inRamPreloadOffset, ymInstrumentsOffset, ssgEg, sampleTableOffset, multiBankSampleTableFormat, sampleBankOffsets);
                String completeOutputFilePath = completRomFilepath.substring(0, completRomFilepath.lastIndexOf(File.separator)+1) + targetFolders[j];
                System.out.println("... "+gameName+" music bank "+(j)+" imported.");
                System.out.println("Exporting "+gameName+" music bank "+(j)+" ...");
                exportMusicEntriesAsFurnaceFiles(templateFilePath, completeOutputFilePath);
                System.out.println("... "+gameName+" music bank "+(j)+" exported.");
            }
        }    
        System.out.println("CubeConversionManager.massExportFromBinaryMusicBankToFurnaceFiles() - ... Done.");
    }
    
    
}
