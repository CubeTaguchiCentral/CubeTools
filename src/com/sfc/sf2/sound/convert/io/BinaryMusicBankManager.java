/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.sound.convert.io;

import com.sfc.sf2.sound.convert.io.cube.CubeChannel;
import com.sfc.sf2.sound.convert.io.cube.CubeCommand;
import com.sfc.sf2.sound.convert.io.cube.MusicEntry;
import com.sfc.sf2.sound.convert.io.cube.Pitch;
import com.sfc.sf2.sound.convert.io.cube.command.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Wiz
 */
public class BinaryMusicBankManager {
    
    public static final int BANK_SIZE = 0x8000;
    public static final int YM_INSTRUMENT_SIZE = 29;
    public static final int YM_INSTRUMENT_NUMBER = 64;
    public static final int SAMPLE_ENTRY_SIZE = 8;
    
    public static MusicEntry importMusicEntry(String filePath, int ptOffset, int index) throws Exception{
        return importMusicEntry(filePath, ptOffset, index, 0, true);
    }
       
    public static MusicEntry importMusicEntry(String filePath, int ptOffset, int index, int ymInstOffset, boolean ssgEg) throws Exception{
        MusicEntry me = null;
        try{
            File f = new File(filePath);
            byte[] data = Files.readAllBytes(Paths.get(f.getAbsolutePath()));
            int bankBaseOffset = ptOffset - (ptOffset % BANK_SIZE);
            index--;
            byte offsetLow = data[ptOffset + 2*index];
            byte offsetHigh = data[ptOffset + 2*index + 1];
            int offset = ((offsetHigh&0xFF)<<8) + (offsetLow&0xFF);
            int musicEntryOffset = bankBaseOffset + offset - BANK_SIZE;
            if(data[musicEntryOffset]!=0){
                throw new Exception("Not a music entry");
            }
            me = new MusicEntry(data, musicEntryOffset, bankBaseOffset - BANK_SIZE, ymInstOffset, ssgEg);
        } catch (IOException ex) {
            Logger.getLogger(BinaryMusicBankManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return me;
    }
    
    public static byte[][] importYmInstruments(String filePath, int ymInstOffset){
        byte[][] ymInstruments = new byte[YM_INSTRUMENT_NUMBER][];
        try{
            File f = new File(filePath);
            byte[] data = Files.readAllBytes(Paths.get(f.getAbsolutePath()));
            for(int i=0;i<YM_INSTRUMENT_NUMBER;i++){
                ymInstruments[i] = Arrays.copyOfRange(data, ymInstOffset+i*YM_INSTRUMENT_SIZE, ymInstOffset+i*YM_INSTRUMENT_SIZE+YM_INSTRUMENT_SIZE);
            }
        } catch (IOException ex) {
            Logger.getLogger(BinaryMusicBankManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ymInstruments;
    }
    
    public static byte[][] importSampleEntries(String filePath, int sampleEntriesOffset, int maxSampleIndex){
        byte[][] sampleEntries = new byte[Math.min(maxSampleIndex+1,12)][];
        try{
            File f = new File(filePath);
            byte[] data = Files.readAllBytes(Paths.get(f.getAbsolutePath()));
            for(int i=0;i<sampleEntries.length;i++){
                sampleEntries[i] = Arrays.copyOfRange(data, sampleEntriesOffset+i*SAMPLE_ENTRY_SIZE, sampleEntriesOffset+i*SAMPLE_ENTRY_SIZE+SAMPLE_ENTRY_SIZE);
            }
        } catch (IOException ex) {
            Logger.getLogger(BinaryMusicBankManager.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(BinaryMusicBankManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sampleBanks;
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
                mes[i] = BinaryMusicBankManager.importMusicEntry(filePath, ptOffset, i+1);
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
            Logger.getLogger(BinaryMusicBankManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }    
    
    
}
