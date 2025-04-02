/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sega.md.snd.convert.io;

import com.sega.md.snd.formats.cube.MusicEntry;
import com.sega.md.snd.formats.cube.SampleEntry;
import com.sega.md.snd.formats.cube.SfxEntry;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Wiz
 */
public class CubeBinaryManager {
    
    public static final int BANK_SIZE = 0x8000;
    public static final int SAMPLE_ENTRY_SIZE = 6;
    public static final int SAMPLE_ENTRY_SIZE_MULTI_BANK = 8;
    public static final int YM_INSTRUMENT_SIZE = 29;
    public static final int YM_INSTRUMENT_SIZE_NOSSGEG = 25;
    
    public static MusicEntry importMusicEntry(String filePath, int ptOffset, int index) throws Exception{
        return importMusicEntry(filePath, ptOffset, 0, index, 0, 0, 0, 0, 0, true, 0);
    }
       
    public static MusicEntry importMusicEntry(String filePath, int ptOffset, int ramPreloadOffset, int index, int driverOffset, int pitchEffectsOffset, int ymLevelsOffset, int ymInstOffset, int psgInstOffset, boolean ssgEg, int ymTimerBIncrement) throws Exception{
        MusicEntry me = null;
        try{
            File f = new File(filePath);
            byte[] data = Files.readAllBytes(Paths.get(f.getAbsolutePath()));
            int bankBaseOffset = ptOffset - (ptOffset % BANK_SIZE);
            index--;
            byte offsetLow = data[ptOffset + 2*index];
            byte offsetHigh = data[ptOffset + 2*index + 1];
            int offset = ((offsetHigh&0xFF)<<8) + (offsetLow&0xFF);
            if(offset<0x8000){
                //throw new Exception("Invalid music entry offset : "+String.format("0x%04X", offset));
                System.out.println("Invalid music entry offset : "+String.format("0x%04X", offset));
                return null;
            }
            int musicEntryOffset = bankBaseOffset + offset - BANK_SIZE;
            if(data[musicEntryOffset]!=0){
                //throw new Exception("Not a music entry");
                System.out.println("Not a music entry");
                return null;
            }
            int baseOffset = bankBaseOffset - BANK_SIZE;
            if(ramPreloadOffset!=0){
                baseOffset = musicEntryOffset - ramPreloadOffset;
            }
            me = new MusicEntry(data, musicEntryOffset, baseOffset, driverOffset,  pitchEffectsOffset, ymLevelsOffset, ymInstOffset, psgInstOffset, ssgEg, ymTimerBIncrement);
        } catch (IOException ex) {
            Logger.getLogger(CubeBinaryManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return me;
    }
       
    public static SfxEntry importSfxEntry(String filePath, int ptOffset, int sfxParamSize, int ramPreloadOffset, int index, int driverOffset, int pitchEffectsOffset, int ymLevelsOffset, int ymInstOffset, int psgInstOffset, boolean ssgEg, int ymTimerBIncrement, byte averageYmTimerBValue) throws Exception{
        SfxEntry se = null;
        try{
            File f = new File(filePath);
            byte[] data = Files.readAllBytes(Paths.get(f.getAbsolutePath()));
            int bankBaseOffset = ptOffset - (ptOffset % BANK_SIZE);
            index--;
            byte offsetLow = data[ptOffset + (2+sfxParamSize)*index + sfxParamSize];
            byte offsetHigh = data[ptOffset + (2+sfxParamSize)*index + sfxParamSize + 1];
            int offset = ((offsetHigh&0xFF)<<8) + (offsetLow&0xFF);
            int baseOffset = bankBaseOffset - BANK_SIZE;
            if(driverOffset<ptOffset && ptOffset<(driverOffset+0x2000)){
                baseOffset = driverOffset;
            }
            int sfxEntryOffset = baseOffset + offset;
            se = new SfxEntry(data, sfxEntryOffset, baseOffset, driverOffset,  pitchEffectsOffset, ymLevelsOffset, ymInstOffset, psgInstOffset, ssgEg, ymTimerBIncrement, averageYmTimerBValue);
        } catch (IOException ex) {
            Logger.getLogger(CubeBinaryManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return se;
    }
    
    public static byte[][] importSampleEntries(String filePath, int sampleEntriesOffset, boolean multiSampleBank, int maxSampleIndex){
        byte[][] sampleEntries = new byte[maxSampleIndex+1][];
        try{
            File f = new File(filePath);
            byte[] data = Files.readAllBytes(Paths.get(f.getAbsolutePath()));
            int sampleEntrySize = multiSampleBank?SAMPLE_ENTRY_SIZE_MULTI_BANK:SAMPLE_ENTRY_SIZE;
            for(int i=0;i<sampleEntries.length;i++){
                sampleEntries[i] = Arrays.copyOfRange(data, sampleEntriesOffset+i*sampleEntrySize, sampleEntriesOffset+i*sampleEntrySize+sampleEntrySize);
            }
        } catch (IOException ex) {
            Logger.getLogger(CubeBinaryManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sampleEntries;
    }
    
    public static byte[][] importSampleBanks(String filePath, int[] sampleBanksOffsets){
        byte[][] sampleBanks = new byte[sampleBanksOffsets.length][];
        try{
            File f = new File(filePath);
            byte[] data = Files.readAllBytes(Paths.get(f.getAbsolutePath()));
            for(int i=0;i<sampleBanks.length;i++){
                sampleBanks[i] = Arrays.copyOfRange(data, sampleBanksOffsets[i], sampleBanksOffsets[i]+0x8000);
            }
        } catch (IOException ex) {
            Logger.getLogger(CubeBinaryManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sampleBanks;
    }
    
    public static void exportSamples(String basePath, SampleEntry[] ses){
        try{
            Set<String> identifiers = new HashSet();
            for(int i=0;i<ses.length;i++){
                SampleEntry se = ses[i];
                if(identifiers.add(se.getIdentifierString())){
                    File nf = new File(basePath+se.getFilenameString());
                    Path path = Paths.get(nf.getAbsolutePath());
                    Files.write(path,SampleEntry.getSample(ses, i)); 
                }
                
            }  
        } catch (Exception ex) {
            Logger.getLogger(CubeBinaryManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void exportYmInstrumentsToIndividualFiles(String basePath, byte[][] yminstruments, MusicEntry[] mes, SfxEntry[] ses){
        try{
            for(int i=0;i<yminstruments.length;i++){
                File nf = new File(basePath+"yminstrument"+String.format("%03d",i)+".bin");
                Path path = Paths.get(nf.getAbsolutePath());
                Files.write(path,yminstruments[i]); 
            }
            Set<Integer> usedYmInstrumentIndexes = new HashSet();
            for(int i=0;i<mes.length;i++){
                usedYmInstrumentIndexes.addAll(mes[i].getUsedYmInstrumentIndexes());
            }
            for(int i=0;i<ses.length;i++){
                usedYmInstrumentIndexes.addAll(ses[i].getUsedYmInstrumentIndexes());
            }
            List<Integer> unusedYmInstrumentIndexes = new ArrayList();
            for(int i=0;i<yminstruments.length;i++){
                if(!usedYmInstrumentIndexes.contains(i)){
                    unusedYmInstrumentIndexes.add(i);
                }
            }
            String usedIndexesString = new ArrayList(usedYmInstrumentIndexes).toString();
            String unusedIndexesString = unusedYmInstrumentIndexes.toString();
            Path path = Paths.get(basePath+"yminstruments.asm");
            PrintWriter pw;
            pw = new PrintWriter(path.toString(),System.getProperty("file.encoding"));
            StringBuilder sb = new StringBuilder();
            sb.append("\n");
            sb.append("; ");
            sb.append(usedYmInstrumentIndexes.size());
            sb.append(" used YM instrument indexes : ");
            sb.append(usedIndexesString);
            sb.append("\n");
            sb.append("; ");
            sb.append(unusedYmInstrumentIndexes.size());
            sb.append(" unused YM instrument indexes : ");
            sb.append(unusedIndexesString);
            sb.append("\n");
            sb.append("\n");
            for(int i=0;i<yminstruments.length;i++){
                sb.append("    incbin \"yminstrument");
                sb.append(String.format("%03d",i));
                sb.append(".bin\"");
                if(unusedYmInstrumentIndexes.contains(i)){
                    sb.append(" ; unused");
                }
                sb.append("\n");
            }            
            pw.print(sb.toString());
            pw.close();
        } catch (Exception ex) {
            Logger.getLogger(CubeBinaryManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    } 
    
    public static void exportYmInstrumentsToSingleFile(String filePath, byte[][] yminstruments, boolean ssgEg){
        try{
            int instrumentSize = ssgEg?YM_INSTRUMENT_SIZE:YM_INSTRUMENT_SIZE_NOSSGEG;
            File nf = new File(filePath);
            Path path = Paths.get(nf.getAbsolutePath());
            byte[] bytes = new byte[yminstruments.length*instrumentSize];
            for(int i=0;i<yminstruments.length;i++){
                System.arraycopy(yminstruments[i], 0, bytes, i*instrumentSize, instrumentSize);
            }  
            Files.write(path,bytes); 
        } catch (Exception ex) {
            Logger.getLogger(CubeBinaryManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    } 
    
    public static void exportMusicEntry(MusicEntry me, String filePath, int ptOffset, int index){
        
        try{
            File f = new File(filePath);
            byte[] data = Files.readAllBytes(Paths.get(f.getAbsolutePath()));
            int bankBaseOffset = ptOffset - (ptOffset % BANK_SIZE);
            byte[] dataBeforeMusicBank = new byte[bankBaseOffset];
            System.arraycopy(data, 0, dataBeforeMusicBank, 0, dataBeforeMusicBank.length);
            byte[] dataAfterMusicBank = new byte[data.length-(bankBaseOffset+BANK_SIZE)];
            System.arraycopy(data, bankBaseOffset+BANK_SIZE, dataAfterMusicBank, 0, dataAfterMusicBank.length);
            MusicEntry[] mes = new MusicEntry[32];
            for(int i=0;i<32;i++){
                mes[i] = CubeBinaryManager.importMusicEntry(filePath, ptOffset, i+1);
            }
            mes[index-1] = me;
            byte[][] meBytes = new byte[32][];
            int baseOffset = 0x8040;
            for(int i=0;i<32;i++){
                meBytes[i] = mes[i].produceBinaryOutput(baseOffset);
                baseOffset+=meBytes[i].length;
            }
            int[] pt = new int[32];
            pt[0] = 0x8040;
            for(int i=1;i<32;i++){
                pt[i] = pt[i-1]+meBytes[i-1].length;
            }
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            for(int i=0;i<32;i++){
                output.write((byte)(pt[i]&0xFF));
                output.write((byte)((pt[i]>>8)&0xFF));
            }
            for(int i=0;i<32;i++){
                output.write(meBytes[i]);
            }
            byte[] newMusicBankBytes = output.toByteArray();
            if(newMusicBankBytes.length<=BANK_SIZE){
                System.arraycopy(newMusicBankBytes, 0, data, bankBaseOffset, newMusicBankBytes.length);
            }else{
                System.out.println("New Music Bank is too large : $"+Integer.toHexString(newMusicBankBytes.length)+" bytes, maximum is $8000 (32kB).");
            }
            String newFilePath = filePath+"_withnewmusicentry.bin";
            File nf = new File(newFilePath);
            Path path = Paths.get(nf.getAbsolutePath());
            Files.write(path,data);   
        } catch (Exception ex) {
            Logger.getLogger(CubeBinaryManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }    
    
    
}
