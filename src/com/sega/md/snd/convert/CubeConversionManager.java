/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sega.md.snd.convert;

import com.sega.md.snd.convert.io.CubeAsmManager;
import com.sega.md.snd.convert.io.CubeBankManager;
import com.sega.md.snd.convert.io.CubeEntryManager;
import com.sega.md.snd.convert.io.FurnaceClipboardManager;
import com.sega.md.snd.convert.io.FurnaceFileManager;
import com.sega.md.snd.formats.cube.MusicEntry;
import java.io.File;

/**
 *
 * @author Wiz
 */
public class CubeConversionManager {
    
    public static final String DEFAULT_ASM_MUSIC_ENTRY_NAME = "Music_";
    
    public static final String MASS_EXPORT_FOLDER_ASM = "ASM";
    public static final String MASS_EXPORT_FOLDER_FURNACE = "Furnace";    
    public static final String MASS_EXPORT_FOLDER_FURNACE_CLIPBOARD = "FurnaceClipboard";
    
    MusicEntry[] mes = new MusicEntry[32];
    
    public void importMusicEntryFromBinaryMusicBank(String filePath, int ptOffset, int ramPreloadOffset, int index, int driverOffset, int pitchEffectsOffset, int ymLevelsOffset, int ymInstOffset, int psgInstOffset, boolean ssgEg, int sampleEntriesOffset, boolean multipleBanksFormat, int[] sampleBanksOffsets){
        System.out.println("CubeConversionManager.importMusicEntryFromBinaryMusicBank() - Importing ...");
        try{        
            mes[0] = CubeBankManager.importMusicEntry(filePath, ptOffset, ramPreloadOffset, index, driverOffset, pitchEffectsOffset, ymLevelsOffset, ymInstOffset, psgInstOffset, ssgEg);
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
    
    public void importMusicEntriesFromBinaryMusicBank(String filePath, int ptOffset, int ramPreloadOffset, int driverOffset, int pitchEffectsOffset, int ymLevelsOffset, int ymInstOffset, int psgInstOffset, boolean ssgEg, int sampleEntriesOffset, boolean multipleBanksFormat, int[] sampleBanksOffsets){
        System.out.println("CubeConversionManager.importMusicEntryFromBinaryMusicBank() - Importing ...");
        int maxSampleIndex = 0;      
        for(int i=0;i<mes.length;i++){
            try{
                mes[i] = CubeBankManager.importMusicEntry(filePath, ptOffset, ramPreloadOffset, i+1, driverOffset, pitchEffectsOffset, ymLevelsOffset, ymInstOffset, psgInstOffset, ssgEg);
                mes[i].factorizeIdenticalChannels();
                mes[i].hasMainLoop();
                mes[i].hasIntro();
                //System.out.println("Imported entry "+(i+1));
                int sampleIndex = mes[i].findMaxSampleIndex();
                if(sampleIndex>maxSampleIndex){
                    maxSampleIndex = sampleIndex;
                }
            }catch(Exception e){
                e.printStackTrace();
                break;
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
            try{
                mes[i].setName(name+String.format("%02d", i+1));
                String completePath = filePath + String.format("%02d", i+1) + ".asm";
                if(unroll){
                    mes[i].unroll();
                    if(optimize){
                        mes[i].optimize();
                    }
                }
                CubeAsmManager.exportMusicEntryAsAsm(mes[i], completePath);
                System.out.println("Exported ASM entry "+(i+1));
            }catch(Exception e){
                System.out.println("Error while exporting ASM entry "+(i+1)+" : "+e.getMessage());
                e.printStackTrace();
            }
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
    
    public void exportMusicEntriesAsFurnaceClipboards(String outputFilePath){
        System.out.println("CubeConversionManager.exportMusicEntriesAsFurnaceClipboards() - Exporting ...");
        for(int i=0;i<mes.length;i++){  
            if(mes[i]!=null && mes[i].hasContent()){
                try{
                    String completePath = outputFilePath + String.format("%02d", i+1) + ".txt";
                    mes[i].unroll();
                    FurnaceClipboardManager.exportMusicEntryAsFurnaceClipboard(mes[i], completePath);
                    System.out.println("Exported Furnace clipboard entry "+(i+1));
                }catch(Exception e){
                    System.out.println("Error while exporting Furnace clipboard entry "+(i+1)+" : "+e.getMessage());
                    e.printStackTrace();
                }
            }            
        }        
        System.out.println("CubeConversionManager.exportMusicEntriesAsFurnaceClipboards() - ... Done.");
    }
    
    public void exportMusicEntryAsFurnaceFile(String templateFilePath, String outputFilePath){
        System.out.println("CubeConversionManager.exportMusicEntryAsFurnaceClipboard() - Exporting ...");
        mes[0].unroll();
        FurnaceFileManager.exportMusicEntryAsFurnaceFile(mes[0], templateFilePath, outputFilePath);
        System.out.println("CubeConversionManager.exportMusicEntryAsFurnaceClipboard() - ... Done.");
    }
    
    public void exportMusicEntriesAsFurnaceFiles(String templateFilePath, String outputFilePath){
        System.out.println("CubeConversionManager.exportMusicEntriesAsFurnaceFiles() - Exporting ...");
        for(int i=0;i<mes.length;i++){  
            if(mes[i]!=null && mes[i].hasContent()){
                try{
                    String completePath = outputFilePath + String.format("%02d", i+1) + ".fur";
                    mes[i].unroll();
                    FurnaceFileManager.exportMusicEntryAsFurnaceFile(mes[i], templateFilePath, completePath);
                    System.out.println("Exported Furnace entry "+(i+1));
                }catch(Exception e){
                    System.out.println("Error while exporting Furnace entry "+(i+1)+" : "+e.getMessage());
                    e.printStackTrace();
                }
            }            
        }        
        System.out.println("CubeConversionManager.exportMusicEntriesAsFurnaceFiles() - ... Done.");
    }
    
    public void massExportFromBinaryMusicBankToFurnaceFiles(String inputFilePath, String templateFilePath){
        System.out.println("CubeConversionManager.massExportFromBinaryMusicBankToFurnaceFiles() - Exporting ...");
        ConversionInputs[] cis = ConversionInputs.importConversionInputs(inputFilePath);
        String inputFileFolder = inputFilePath.substring(0, inputFilePath.lastIndexOf(File.separator)+1);
        for(int i=0;i<cis.length;i++){
            String gameName = cis[i].getGameName();
            String completRomFilepath = inputFileFolder + cis[i].getRomFilePath();
            int[] musicBankOffsets = cis[i].getMusicBankOffsets();
            int driverOffset = cis[i].getDriverOffset();
            int pitchEffectsOffset = driverOffset + cis[i].getPitchEffectsOffset();
            int ymLevelsOffset = driverOffset + cis[i].getYmLevelsOffset();
            int psgInstruments = driverOffset + cis[i].getPsgInstruments();
            int inRamPreloadOffset = cis[i].getInRamPreloadOffset();
            int[] ymInstruments = cis[i].getYmInstruments();
            boolean ssgEg = cis[i].isSsgEg();
            int sampleTableOffset = cis[i].getSampleTableOffset();
            boolean multiBankSampleTableFormat = cis[i].isMultiBankSampleTableFormat();
            int[] sampleBankOffsets = cis[i].getSampleBankOffsets();
            String[] targetFolders = new String[musicBankOffsets.length];
            for(int b=0;b<targetFolders.length;b++){
                targetFolders[b] = ".\\bank"+b+"\\";
            }
            for(int j=0;j<musicBankOffsets.length;j++){
                System.out.println("Importing "+gameName+" music bank "+(j)+" ...");
                mes = new MusicEntry[32];
                int ymInstrumentsOffset = ymInstruments[0];
                if(ymInstruments.length>j){
                    ymInstrumentsOffset = ymInstruments[j];
                }
                importMusicEntriesFromBinaryMusicBank(completRomFilepath, musicBankOffsets[j], inRamPreloadOffset, driverOffset, pitchEffectsOffset, ymLevelsOffset, ymInstrumentsOffset, psgInstruments, ssgEg, sampleTableOffset, multiBankSampleTableFormat, sampleBankOffsets);
                System.out.println("... "+gameName+" music bank "+(j)+" imported.");
                String completeAsmOutputFilePath = completRomFilepath.substring(0, completRomFilepath.lastIndexOf(File.separator)+1) + MASS_EXPORT_FOLDER_ASM + File.separator + targetFolders[j];
                exportMusicEntriesAsAsm(completeAsmOutputFilePath, DEFAULT_ASM_MUSIC_ENTRY_NAME, false, false);
                String completeFurnaceClipboardOutputFilePath = completRomFilepath.substring(0, completRomFilepath.lastIndexOf(File.separator)+1) + MASS_EXPORT_FOLDER_FURNACE_CLIPBOARD + File.separator + targetFolders[j];
                exportMusicEntriesAsFurnaceClipboards(completeFurnaceClipboardOutputFilePath);
                String completeFurnaceOutputFilePath = completRomFilepath.substring(0, completRomFilepath.lastIndexOf(File.separator)+1) + MASS_EXPORT_FOLDER_FURNACE + File.separator + targetFolders[j];
                System.out.println("Exporting "+gameName+" music bank "+(j)+" ...");
                exportMusicEntriesAsFurnaceFiles(templateFilePath, completeFurnaceOutputFilePath);
                System.out.println("... "+gameName+" music bank "+(j)+" exported.");
            }
        }    
        System.out.println("CubeConversionManager.massExportFromBinaryMusicBankToFurnaceFiles() - ... Done.");
    }
    
    
}
