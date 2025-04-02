/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sega.md.snd.convert;

import com.sega.md.snd.convert.cubetofurnace.C2FPatternConverter;
import com.sega.md.snd.convert.io.CubeAsmManager;
import com.sega.md.snd.convert.io.CubeBinaryManager;
import com.sega.md.snd.convert.io.CubeEntryManager;
import com.sega.md.snd.convert.io.FurnaceClipboardManager;
import com.sega.md.snd.convert.io.FurnaceFileManager;
import com.sega.md.snd.formats.cube.MusicEntry;
import com.sega.md.snd.formats.cube.SampleEntry;
import com.sega.md.snd.formats.cube.SfxEntry;
import java.io.File;

/**
 *
 * @author Wiz
 */
public class CubeConversionManager {
    
    public static final String DEFAULT_ASM_MUSIC_ENTRY_NAME = "Music_";
    public static final String DEFAULT_ASM_SFX_ENTRY_NAME = "Sfx_";
    
    public static final String MASS_EXPORT_FOLDER_ASM = "ASM";
    public static final String MASS_EXPORT_FOLDER_FURNACE = "Furnace";    
    public static final String MASS_EXPORT_FOLDER_FURNACE_CLIPBOARD = "FurnaceClipboard";
    
    MusicEntry[] mes = new MusicEntry[32];
    SfxEntry[] ses;
    int maxSampleIndex = 0;
    
    public void importMusicEntryFromBinaryMusicBank(String filePath, int ptOffset, int ramPreloadOffset, int index, int driverOffset, int pitchEffectsOffset, int ymLevelsOffset, int ymInstOffset, int psgInstOffset, boolean ssgEg, int ymTimerBIncrement, int sampleEntriesOffset, boolean multipleBanksFormat, int[] sampleBanksOffsets){
        System.out.println("CubeConversionManager.importMusicEntryFromBinaryMusicBank() - Importing ...");
        try{        
            mes[0] = CubeBinaryManager.importMusicEntry(filePath, ptOffset, ramPreloadOffset, index, driverOffset, pitchEffectsOffset, ymLevelsOffset, ymInstOffset, psgInstOffset, ssgEg, ymTimerBIncrement);
            mes[0].factorizeIdenticalChannels();
            mes[0].hasMainLoop();
            mes[0].hasIntro();
            int maxSampleIndex = mes[0].findMaxSampleIndex();
            byte[][] sampleEntries = CubeBinaryManager.importSampleEntries(filePath, sampleEntriesOffset, multipleBanksFormat, maxSampleIndex);
            byte[][] sampleBanks = CubeBinaryManager.importSampleBanks(filePath, sampleBanksOffsets);
            mes[0].setSampleEntries(sampleEntries);
            mes[0].setMultiSampleBank(multipleBanksFormat);
            mes[0].setSampleBanks(sampleBanks);
        }catch(Exception e){
            e.printStackTrace();
        }
        System.out.println("CubeConversionManager.importMusicEntryFromBinaryMusicBank() - ... Done.");
    }
    
    public void importMusicEntriesFromBinaryMusicBank(String filePath, int ptOffset, int ramPreloadOffset, int driverOffset, int pitchEffectsOffset, int ymLevelsOffset, int ymInstOffset, int psgInstOffset, boolean ssgEg, int ymTimerBIncrement, int sampleEntriesOffset, int sampleCount, boolean multipleBanksFormat, int[] sampleBanksOffsets){
        System.out.println("CubeConversionManager.importMusicEntriesFromBinaryMusicBank() - Importing ...");     
        for(int i=0;i<mes.length;i++){
            try{
                mes[i] = CubeBinaryManager.importMusicEntry(filePath, ptOffset, ramPreloadOffset, i+1, driverOffset, pitchEffectsOffset, ymLevelsOffset, ymInstOffset, psgInstOffset, ssgEg, ymTimerBIncrement);
                if(mes[i]!=null){
                    mes[i].factorizeIdenticalChannels();
                    mes[i].hasMainLoop();
                    mes[i].hasIntro();
                    //System.out.println("Imported entry "+(i+1));
                    int sampleIndex = mes[i].findMaxSampleIndex();
                    if(sampleIndex>maxSampleIndex){
                        maxSampleIndex = sampleIndex;
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
                break;
            }
        }
        
        maxSampleIndex = sampleCount!=0?sampleCount-1:maxSampleIndex;
        byte[][] sampleEntries = CubeBinaryManager.importSampleEntries(filePath, sampleEntriesOffset, multipleBanksFormat, maxSampleIndex);
        byte[][] sampleBanks = CubeBinaryManager.importSampleBanks(filePath, sampleBanksOffsets);
        for(int i=0;i<mes.length;i++){
            if(mes[i]!=null){
                mes[i].setSampleEntries(sampleEntries);
                mes[i].setMultiSampleBank(multipleBanksFormat);
                mes[i].setSampleBanks(sampleBanks);   
            }else{
                break;
            }
        }

        System.out.println("CubeConversionManager.importMusicEntriesFromBinaryMusicBank() - ... Done.");
    }
    
    public void importSfxEntriesFromBinary(String filePath, int sfxOffset, int ramPreloadOffset, int driverOffset, int pitchEffectsOffset, int ymLevelsOffset, int ymInstOffset, int psgInstOffset, boolean ssgEg, int ymTimerBIncrement, int sampleEntriesOffset, int sampleCount, boolean multipleBanksFormat, int[] sampleBanksOffsets, int sfxCount, int sfxParamSize, byte averageYmTimerBValue){
        System.out.println("CubeConversionManager.importSfxEntriesFromBinary() - Importing ...");
        ses = new SfxEntry[sfxCount];
        for(int i=0;i<ses.length;i++){
            try{
                ses[i] = CubeBinaryManager.importSfxEntry(filePath, sfxOffset, sfxParamSize, ramPreloadOffset, i+1, driverOffset, pitchEffectsOffset, ymLevelsOffset, ymInstOffset, psgInstOffset, ssgEg, ymTimerBIncrement, averageYmTimerBValue);
                if(ses[i]!=null){
                    ses[i].factorizeIdenticalChannels();
                    //System.out.println("Imported SFX entry "+(i+1));
                    int sampleIndex = ses[i].findMaxSampleIndex();
                    if(sampleIndex>maxSampleIndex){
                        maxSampleIndex = sampleIndex;
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
                break;
            }
        }
        
        maxSampleIndex = sampleCount!=0?sampleCount-1:maxSampleIndex;
        byte[][] sampleEntries = CubeBinaryManager.importSampleEntries(filePath, sampleEntriesOffset, multipleBanksFormat, maxSampleIndex);
        byte[][] sampleBanks = CubeBinaryManager.importSampleBanks(filePath, sampleBanksOffsets);
        for(int i=0;i<ses.length;i++){
            if(ses[i]!=null){
                ses[i].setSampleEntries(sampleEntries);
                ses[i].setMultiSampleBank(multipleBanksFormat);
                ses[i].setSampleBanks(sampleBanks);   
            }else{
                break;
            }
        }

        System.out.println("CubeConversionManager.importSfxEntriesFromBinary() - ... Done.");
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
            if(mes[i]!=null){
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
        }
        System.out.println("CubeConversionManager.exportMusicEntryAsAsm() - ... Done.");
    }
    
    public void exportSfxEntriesAsAsm(String filePath, String name, boolean unroll, boolean optimize){
        System.out.println("CubeConversionManager.exportSfxEntriesAsAsm() - Exporting ...");
        for(int i=0;i<ses.length;i++){    
            try{
                ses[i].setName(name+String.format("%03d", i+1));
                String completePath = filePath + String.format("%03d", i+1) + ".asm";
                if(unroll){
                    ses[i].unroll();
                    if(optimize){
                        ses[i].optimize();
                    }
                }
                CubeAsmManager.exportSfxEntryAsAsm(ses[i], completePath);
                System.out.println("Exported ASM entry "+(i+1));
            }catch(Exception e){
                System.out.println("Error while exporting ASM entry "+(i+1)+" : "+e.getMessage());
                e.printStackTrace();
            }
        }
        System.out.println("CubeConversionManager.exportSfxEntriesAsAsm() - ... Done.");
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
    
    public void exportSampleBanks(SampleEntry[] sampleEntries, String filePath){
        System.out.println("CubeConversionManager.exportSampleBanks() - Exporting ...");
        for(int i=0;i<sampleEntries.length;i++){        
            String completePath = filePath + String.format("%02d", i+1) + ".bin";
            CubeEntryManager.exportMusicEntryAsBinary(mes[i], completePath);
        }        
        System.out.println("CubeConversionManager.exportSampleBanks() - ... Done.");
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
        CubeBinaryManager.exportMusicEntry(mes[0], filePath, ptOffset, index);
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
    
    public void exportSfxEntriesAsFurnaceClipboards(String outputFilePath){
        System.out.println("CubeConversionManager.exportSfxEntriesAsFurnaceClipboards() - Exporting ...");
        for(int i=0;i<ses.length;i++){  
            if(ses[i]!=null && ses[i].hasContent()){
                try{
                    String completePath = outputFilePath + String.format("%03d", i+1) + ".txt";
                    ses[i].unroll();
                    FurnaceClipboardManager.exportSfxEntryAsFurnaceClipboard(ses[i], completePath);
                    System.out.println("Exported Furnace clipboard entry "+(i+1));
                }catch(Exception e){
                    System.out.println("Error while exporting Furnace clipboard entry "+(i+1)+" : "+e.getMessage());
                    e.printStackTrace();
                }
            }            
        }        
        System.out.println("CubeConversionManager.exportSfxEntriesAsFurnaceClipboards() - ... Done.");
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
    
    public void exportSfxEntriesAsFurnaceFiles(String templateFilePath, String outputFilePath){
        System.out.println("CubeConversionManager.exportSfxEntriesAsFurnaceFiles() - Exporting ...");
        for(int i=0;i<ses.length;i++){  
            if(ses[i]!=null && ses[i].hasContent()){
                try{
                    String completePath = outputFilePath + String.format("%03d", i+1) + ".fur";
                    ses[i].unroll();
                    FurnaceFileManager.exportSfxEntryAsFurnaceFile(ses[i], templateFilePath, completePath);
                    System.out.println("Exported Furnace entry "+(i+1));
                }catch(Exception e){
                    System.out.println("Error while exporting Furnace entry "+(i+1)+" : "+e.getMessage());
                    e.printStackTrace();
                }
            }            
        }        
    }
    
    public void massExportFromBinaryMusicBankToFurnaceFiles(String inputFilePath, String templateFilePath){
        System.out.println("CubeConversionManager.massExportFromBinaryMusicBankToFurnaceFiles() - Exporting ...");
        ConversionInputs[] cis = ConversionInputs.importConversionInputs(inputFilePath);
        String inputFileFolder = inputFilePath.substring(0, inputFilePath.lastIndexOf(File.separator)+1);
        for(int i=0;i<cis.length;i++){
            String gameName = cis[i].getGameName();
            String completeRomFilepath = inputFileFolder + cis[i].getRomFilePath();
            int[] musicBankOffsets = cis[i].getMusicBankOffsets();
            int driverOffset = cis[i].getDriverOffset();
            int pitchEffectsOffset = driverOffset + cis[i].getPitchEffectsOffset();
            int ymLevelsOffset = driverOffset + cis[i].getYmLevelsOffset();
            int psgInstruments = driverOffset + cis[i].getPsgInstruments();
            int inRamPreloadOffset = cis[i].getInRamPreloadOffset();
            int ymTimerBIncrement = cis[i].getYmTimerBIncrement();
            int[] ymInstruments = cis[i].getYmInstruments();
            boolean ssgEg = cis[i].isSsgEg();
            int sampleTableOffset = cis[i].getSampleTableOffset();
            int sampleCount = cis[i].getSampleCount();
            boolean multiBankSampleTableFormat = cis[i].isMultiBankSampleTableFormat();
            int[] sampleBankOffsets = cis[i].getSampleBankOffsets();
            String[] targetFolders = new String[musicBankOffsets.length];
            for(int b=0;b<targetFolders.length;b++){
                targetFolders[b] = ".\\bank"+b+"\\";
            }
            int sfxOffset = cis[i].getSfxOffset();
            int sfxParamSize = cis[i].getSfxParamSize();
            int sfxCount = cis[i].getSfxCount();
            int ymTimerBValues = 0;
            int ymTimerBValueCount = 0;
            byte[][] yminstruments = new byte[0][];
            for(int j=0;j<musicBankOffsets.length;j++){
                System.out.println("Importing "+gameName+" music bank "+(j)+" ...");
                mes = new MusicEntry[32];
                int ymInstrumentsOffset = ymInstruments[0];
                if(ymInstruments.length>j){
                    ymInstrumentsOffset = ymInstruments[j];
                }
                importMusicEntriesFromBinaryMusicBank(completeRomFilepath, musicBankOffsets[j], inRamPreloadOffset, driverOffset, pitchEffectsOffset, ymLevelsOffset, ymInstrumentsOffset, psgInstruments, ssgEg, ymTimerBIncrement, sampleTableOffset, sampleCount, multiBankSampleTableFormat, sampleBankOffsets);
                System.out.println("... "+gameName+" music bank "+(j)+" imported.");
                for(int k=0;k<mes.length;k++){
                    if(mes[k]!=null){
                        ymTimerBValues+=0xFF&mes[k].getYmTimerBValue();
                        ymTimerBValueCount++;
                        if(mes[k].getYmInstruments().length>yminstruments.length){
                            yminstruments = mes[k].getYmInstruments();
                        }
                    }
                }
                String completeAsmOutputFilePath = completeRomFilepath.substring(0, completeRomFilepath.lastIndexOf(File.separator)+1) + MASS_EXPORT_FOLDER_ASM + File.separator + targetFolders[j];
                exportMusicEntriesAsAsm(completeAsmOutputFilePath, DEFAULT_ASM_MUSIC_ENTRY_NAME, false, false);
                String completeFurnaceClipboardOutputFilePath = completeRomFilepath.substring(0, completeRomFilepath.lastIndexOf(File.separator)+1) + MASS_EXPORT_FOLDER_FURNACE_CLIPBOARD + File.separator + targetFolders[j];
                exportMusicEntriesAsFurnaceClipboards(completeFurnaceClipboardOutputFilePath);
                String completeFurnaceOutputFilePath = completeRomFilepath.substring(0, completeRomFilepath.lastIndexOf(File.separator)+1) + MASS_EXPORT_FOLDER_FURNACE + File.separator + targetFolders[j];
                System.out.println("Exporting "+gameName+" music bank "+(j)+" ...");
                exportMusicEntriesAsFurnaceFiles(templateFilePath, completeFurnaceOutputFilePath);
                System.out.println("... "+gameName+" music bank "+(j)+" exported.");
            }
            byte averageYmTimerBValue = (byte)(0xFF & (ymTimerBValues / ymTimerBValueCount) );
            System.out.println("Exporting "+gameName+" SFX ...");
            importSfxEntriesFromBinary(completeRomFilepath, sfxOffset, inRamPreloadOffset, driverOffset, pitchEffectsOffset, ymLevelsOffset, ymInstruments[0], psgInstruments, ssgEg, ymTimerBIncrement, sampleTableOffset, sampleCount, multiBankSampleTableFormat, sampleBankOffsets, sfxCount, sfxParamSize, averageYmTimerBValue);
            String completeAsmOutputFilePath = completeRomFilepath.substring(0, completeRomFilepath.lastIndexOf(File.separator)+1) + MASS_EXPORT_FOLDER_ASM + File.separator + ".\\sfx\\";
            exportSfxEntriesAsAsm(completeAsmOutputFilePath, DEFAULT_ASM_SFX_ENTRY_NAME, false, false);
            String completeFurnaceClipboardOutputFilePath = completeRomFilepath.substring(0, completeRomFilepath.lastIndexOf(File.separator)+1) + MASS_EXPORT_FOLDER_FURNACE_CLIPBOARD + File.separator + ".\\sfx\\";
            exportSfxEntriesAsFurnaceClipboards(completeFurnaceClipboardOutputFilePath);
            String completeFurnaceOutputFilePath = completeRomFilepath.substring(0, completeRomFilepath.lastIndexOf(File.separator)+1) + MASS_EXPORT_FOLDER_FURNACE + File.separator + ".\\sfx\\";
            exportSfxEntriesAsFurnaceFiles(templateFilePath, completeFurnaceOutputFilePath);
            System.out.println("... "+gameName+" SFX exported.");            
            String assetsOutputFilePath = completeRomFilepath.substring(0, completeRomFilepath.lastIndexOf(File.separator)+1) + MASS_EXPORT_FOLDER_ASM + File.separator + ".\\assets\\";
            SampleEntry[] sampleEntries = SampleEntry.parseSampleEntries(ses[0].getSampleEntries(),ses[0].getSampleBanks(), sampleBankOffsets, multiBankSampleTableFormat);
            String sampleEntriesOutputFilePath = assetsOutputFilePath + File.separator + "pcm_samples.asm";
            CubeAsmManager.exportSampleEntriesAsAsm(sampleEntries, sampleEntriesOutputFilePath);
            CubeBinaryManager.exportSamples(assetsOutputFilePath, sampleEntries);
            System.out.println("Exporting "+gameName+" YM instruments ...");
            String ymInstrumentsEntriesOutputFilePath = assetsOutputFilePath + File.separator + "yminstruments.bin";
            CubeBinaryManager.exportYmInstrumentsToSingleFile(ymInstrumentsEntriesOutputFilePath, ses[0].getYmInstruments(), ssgEg);
            String ymInstrumentsIndividualEntriesOutputPath = assetsOutputFilePath + File.separator + ".\\yminstruments\\";
            for(int k=0;k<ses.length;k++){
                if(ses[k]!=null){
                    if(ses[k].getYmInstruments().length>yminstruments.length){
                        yminstruments = ses[k].getYmInstruments();
                    }
                }
            }
            CubeBinaryManager.exportYmInstrumentsToIndividualFiles(ymInstrumentsIndividualEntriesOutputPath, yminstruments, mes, ses);
            System.out.println("... "+gameName+" YM instruments exported. ("+yminstruments.length+")");    
            System.out.println("... "+gameName+" assets exported.");
        }    
        /*
        System.out.println("Unrecognized pitch effect strings : ");
        for(String s : C2FPatternConverter.unrecognizedPitchEffectStrings){
            System.out.println(s);
        }
        */
        System.out.println("CubeConversionManager.massExportFromBinaryMusicBankToFurnaceFiles() - ... Done.");
    }
    
    
}
