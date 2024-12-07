/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.sound.convert.io.furnace;

import com.sfc.sf2.sound.convert.io.cube.CubeChannel;
import com.sfc.sf2.sound.convert.io.cube.CubeCommand;
import com.sfc.sf2.sound.convert.io.cube.MusicEntry;
import com.sfc.sf2.sound.convert.io.cube.command.Inst;
import com.sfc.sf2.sound.convert.io.cube.command.MainLoopStart;
import com.sfc.sf2.sound.convert.io.cube.command.PsgInst;
import com.sfc.sf2.sound.convert.io.cube.command.PsgNote;
import com.sfc.sf2.sound.convert.io.cube.command.PsgNoteL;
import com.sfc.sf2.sound.convert.io.cube.command.SetRelease;
import com.sfc.sf2.sound.convert.io.cube.command.Shifting;
import com.sfc.sf2.sound.convert.io.cube.command.Stereo;
import com.sfc.sf2.sound.convert.io.cube.command.Sustain;
import com.sfc.sf2.sound.convert.io.cube.command.Vibrato;
import com.sfc.sf2.sound.convert.io.cube.command.Vol;
import com.sfc.sf2.sound.convert.io.cube.command.Wait;
import com.sfc.sf2.sound.convert.io.cube.command.WaitL;
import com.sfc.sf2.sound.convert.io.furnace.pattern.Pattern;
import com.sfc.sf2.sound.convert.io.furnace.pattern.Row;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Wiz
 */
public class PatternRange {
    
    private static final int TYPE_FM = 0;
    private static final int TYPE_DAC = 1;
    private static final int TYPE_PSGTONE = 2;
    private static final int TYPE_PSGNOISE = 3;
    
    private static final int MAX_CHANNELS_SIZE=10;
    
    private Pattern[] patterns = new Pattern[MAX_CHANNELS_SIZE];

    public PatternRange(byte[] rawData) {
        
    }

    public Pattern[] getPatterns() {
        return patterns;
    }
    
    public PatternRange(MusicEntry me, boolean introOnly, boolean mainLoopOnly){
        for(int i=0;i<patterns.length;i++){
            if(i<5){
                patterns[i] = new Pattern(me.getChannels()[i], TYPE_FM, introOnly, mainLoopOnly);
            }else if(i==5){
                if(!me.isYm6InDacMode()){
                    patterns[i] = new Pattern(me.getChannels()[i], TYPE_FM, introOnly, mainLoopOnly);
                }else{
                    patterns[i] = new Pattern(me.getChannels()[i], TYPE_DAC, introOnly, mainLoopOnly);
                }
            }else if(i<9){
                patterns[i] = new Pattern(me.getChannels()[i], TYPE_PSGTONE, introOnly, mainLoopOnly);
            }else{
                patterns[i] = new Pattern(me.getChannels()[i], TYPE_PSGNOISE, introOnly, mainLoopOnly);
            }
        }
        
        /*
        if(mainLoopOnly){
            repeatMainLoopToMaxLength();
        }
        
        fillChannelsToMaxLength();
        */
    }
    
    public void fillChannelsToMaxLength(){
        int maxLength=0;
        for (int i=0;i<patterns.length;i++){
            if(maxLength<patterns[i].getRows().length){
                maxLength = patterns[i].getRows().length;
            }
        }
        for (int i=0;i<patterns.length;i++){
            Row[] rows = patterns[i].getRows();
            if(rows.length<maxLength){
                Row[] newRows = new Row[maxLength];
                System.arraycopy(rows, 0, newRows, 0, rows.length);
                for(int j=0;(rows.length+j)<maxLength;j++){
                    Row voidRow = new Row();
                    newRows[rows.length+j] = voidRow;
                }
                patterns[i].setRows(newRows);
            }
        }
    }
    
    public void repeatMainLoopToMaxLength(){
        int maxLength=0;
        for (int i=0;i<patterns.length;i++){
            if(maxLength<patterns[i].getRows().length){
                maxLength = patterns[i].getRows().length;
            }
        }
        for (int i=0;i<patterns.length;i++){
            if(patterns[i].getRows()!=null && patterns[i].getRows().length>0){
                patterns[i].setRows(repeat(patterns[i].getRows(),maxLength));
            }
        }
    }
    
    public static <T> T[] repeat(T[] arr, int newLength) {
        T[] dup = Arrays.copyOf(arr, newLength);
        for (int last = arr.length; last != 0 && last < newLength; last <<= 1) {
            System.arraycopy(dup, 0, dup, last, Math.min(last << 1, newLength) - last);
        }
        return dup;
    }
    
    public PatternRange(PatternRange intro, PatternRange mainLoop){
        Pattern[] introChannels = intro.getPatterns();
        Pattern[] mainLoopChannels = mainLoop.getPatterns();
        int maxLength=0;
        for (int i=0;i<MAX_CHANNELS_SIZE;i++){
            Row[] introRows = introChannels[i].getRows();
            Row[] mainLoopRows = mainLoopChannels[i].getRows();
            if(maxLength<(introRows.length+mainLoopRows.length)){
                maxLength = introRows.length+mainLoopRows.length;
            }
        }
        System.out.println("maxLength : "+maxLength);
        for (int i=0;i<MAX_CHANNELS_SIZE;i++){
            Row[] introRows = introChannels[i].getRows();
            Row[] mainLoopRows = mainLoopChannels[i].getRows();
            int targetMainLoopLength = maxLength - introRows.length;
            if(mainLoopRows!=null && mainLoopRows.length>0){
               mainLoopRows = repeat(mainLoopRows,targetMainLoopLength);
            }            
            mainLoop.getPatterns()[i].setRows(mainLoopRows);
        }
        for (int i=0;i<MAX_CHANNELS_SIZE;i++){
            Row[] introRows = introChannels[i].getRows();
            Row[] mainLoopRows = mainLoopChannels[i].getRows();
            Row[] rows = concatenate(introRows, mainLoopRows);
            Pattern fc = new Pattern();
            fc.setRows(rows);
            patterns[i] = fc;
        }
        fillChannelsToMaxLength();
    }
    
    public <T> T[] concatenate(T[] a, T[] b) {
        int aLen = a.length;
        int bLen = b.length;

        @SuppressWarnings("unchecked")
        T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);

        return c;
    }
}
