/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sega.md.snd.formats.cube;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 * @author Wiz
 */
public class SampleEntry {
    
    private int playbackPeriod;
    private boolean multibank;
    private int bankIndex;
    private int length;
    private int offset;

    public int getPlaybackPeriod() {
        return playbackPeriod;
    }

    public void setPlaybackPeriod(int playbackPeriod) {
        this.playbackPeriod = playbackPeriod;
    }

    public boolean isMultibank() {
        return multibank;
    }

    public void setMultibank(boolean multibank) {
        this.multibank = multibank;
    }

    public int getBankIndex() {
        return bankIndex;
    }

    public void setBankIndex(int bankIndex) {
        this.bankIndex = bankIndex;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
    
    public static SampleEntry[] parseSampleEntries(byte[][] entries, boolean isMultibank){
        SortedSet<Byte> bankIndexes = new TreeSet();
        for(int i=0;i<entries.length;i++){
            if(isMultibank){
                bankIndexes.add(entries[i][2]);
            }
        }
        List<Byte> bankIndexList = new ArrayList(bankIndexes);
        SampleEntry[] ses = new SampleEntry[entries.length];
        for(int i=0;i<ses.length;i++){
            SampleEntry se = new SampleEntry();
            se.setPlaybackPeriod(entries[i][0]);
            se.setBankIndex(isMultibank?bankIndexList.indexOf(entries[i][2]):0);
            byte b5 = entries[i][3+(isMultibank?2:0)];
            byte b4 = entries[i][2+(isMultibank?2:0)];
            int i5 = (0xFF&b5)<<8;
            int i4 = (0xFF&b4);
            se.setLength(i5 + i4);
            byte b7 = entries[i][5+(isMultibank?2:0)];
            byte b6 = entries[i][4+(isMultibank?2:0)];
            int i7 = (0x7F&b7)<<8;
            int i6 = (0xFF&b6);
            se.setOffset(i7 + i6 + 0x8000);   
            ses[i] = se;
        }     
        return ses;
    }
    
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("    dw ");
        sb.append(String.format("%02d", this.playbackPeriod));
        sb.append(", 0");
        sb.append(String.format("%01x", this.bankIndex));
        sb.append("h, 0");
        sb.append(String.format("%04x", this.length));
        sb.append("h, 0");
        sb.append(String.format("%04x", this.offset));
        sb.append("h\n");        
        return sb.toString();
    }
    
    public static String toString(SampleEntry[] ses){
        StringBuilder sb = new StringBuilder();
        sb.append("\n" +
            "; Playback period (higher=slower), bank index, length, offset\n" +
            "\n" +
            "PCM_SAMPLE_ENTRIES:" +
            "\n");
        for(int i=0;i<ses.length;i++){
            sb.append(ses[i].toString());
        }
        return sb.toString();
    }
    
}
