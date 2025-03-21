/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sega.md.snd.convert.cubetofurnace;

import com.sega.md.snd.formats.cube.MusicEntry;
import com.sega.md.snd.formats.furnace.file.FurnaceFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 * @author Wiz
 */
public class C2FSampleConverter {
    
    public static void convertSamples(byte[][] sampleEntries, byte[][] sampleBanks, boolean isMultiSampleBank, FurnaceFile ff){
        SortedSet<Byte>  bankIndexes = new TreeSet();
        if(isMultiSampleBank){
            for(int i=0;i<sampleEntries.length;i++){
                bankIndexes.add(sampleEntries[i][2]);
            }
        }
        List<Byte> bankIndexList = new ArrayList(bankIndexes);
        
        for(int i=0;i<sampleEntries.length;i++){
            int period = (sampleEntries[i][0] / 15)+1;
            int bankIndex = isMultiSampleBank?bankIndexList.indexOf(sampleEntries[i][2]) : 0;
            byte b5 = sampleEntries[i][3+(isMultiSampleBank?2:0)];
            byte b4 = sampleEntries[i][2+(isMultiSampleBank?2:0)];
            int i5 = (0xFF&b5)<<8;
            int i4 = (0xFF&b4);
            int length = i5 + i4;
            byte b7 = sampleEntries[i][5+(isMultiSampleBank?2:0)];
            byte b6 = sampleEntries[i][4+(isMultiSampleBank?2:0)];
            int i7 = (0x7F&b7)<<8;
            int i6 = (0xFF&b6);
            int offset = i7 + i6;
            byte[] bank = sampleBanks[bankIndex];
            byte[] baseSample = Arrays.copyOfRange(bank, offset, offset+length);
            byte[] targetSample = new byte[baseSample.length*period]; 
            for(int j=0;j<targetSample.length;j++){
                targetSample[j] = (byte)(0xFF & (0x80 + baseSample[j/period]));
            }
            ff.getSamples()[i].setDepth((byte)8);
            ff.getSamples()[i].setRawData(targetSample);
            ff.getSamples()[i].setLength(length);
            ff.getSamples()[i].setC4Rate(13250);
            ff.getSamples()[i].setCompatibilityRate(13250);
        }
    }
    
}
