/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.sound.convert.cubetofurnace;

import com.sfc.sf2.sound.formats.cube.MusicEntry;
import com.sfc.sf2.sound.formats.furnace.file.FurnaceFile;
import com.sfc.sf2.sound.formats.furnace.file.section.Feature;

/**
 *
 * @author Wiz
 */
public class C2FYmInstrumentConverter {
    
    private static final int[] ALGO_SLOTS = {
        0b1000,
        0b1000,
        0b1000,
        0b1000,
        0b1100,
        0b1110,
        0b1110,
        0b1111
    };
    
    public static void convertYmInstruments(MusicEntry me, FurnaceFile ff){
        byte[][] cubeInstruments = me.getYmInstruments();
        //ff.setInstruments(new InstrumentBlock[cubeInstruments.length]);
        for(int i=0;i<cubeInstruments.length;i++){
            Feature[] newFeatures = new Feature[2];
            newFeatures[0] = new Feature("yminst"+String.format("%02d", i));
            newFeatures[1] = convertCubeInstrumentToFurnaceFeature(cubeInstruments[i], me.isSsgEgAvailable());
            /*if(ff.getInstruments()[i]==null){
                ff.getInstruments()[i] = new InstrumentBlock();
            }*/
            ff.getInstruments()[i].setRawData(null);
            ff.getInstruments()[i].setFeatures(newFeatures);
        }
    }
    
    public static Feature convertCubeInstrumentToFurnaceFeature(byte[] cubeFmInstrument, boolean ssgEg){
        String code = "FM";
        short length = (short)36;
        byte[] data = new byte[36];
        data[0] = (byte)0xF4;
        int algo = cubeFmInstrument[ssgEg?28:24]&0x07;
        int feedback = (cubeFmInstrument[ssgEg?28:24]&0x38)>>3;
        data[1] = (byte)((algo<<4)+feedback);
        data[2] = 0;
        data[3] = 0x20;
        data[4+0*8+0] = cubeFmInstrument[0*4+0];
        data[4+0*8+1] = ((ALGO_SLOTS[algo]&0b0001)>0)?0:cubeFmInstrument[1*4+0];
        data[4+0*8+2] = cubeFmInstrument[2*4+0];
        data[4+0*8+3] = cubeFmInstrument[3*4+0];
        data[4+0*8+4] = (byte)(cubeFmInstrument[4*4+0]+0x40);
        data[4+0*8+5] = cubeFmInstrument[5*4+0];
        if(ssgEg){
            data[4+0*8+6] = cubeFmInstrument[6*4+0];
        }
        data[4+0*8+7] = 0;
        data[4+1*8+0] = cubeFmInstrument[0*4+1];
        data[4+1*8+1] = ((ALGO_SLOTS[algo]&0b0010)>0)?0:cubeFmInstrument[1*4+1];
        data[4+1*8+2] = cubeFmInstrument[2*4+1];
        data[4+1*8+3] = cubeFmInstrument[3*4+1];
        data[4+1*8+4] = (byte)(cubeFmInstrument[4*4+1]+0x40);
        data[4+1*8+5] = cubeFmInstrument[5*4+1];
        if(ssgEg){
            data[4+1*8+6] = cubeFmInstrument[6*4+1];
        }
        data[4+1*8+7] = 0;
        data[4+2*8+0] = cubeFmInstrument[0*4+2];
        data[4+2*8+1] = ((ALGO_SLOTS[algo]&0b0100)>0)?0:cubeFmInstrument[1*4+2];
        data[4+2*8+2] = cubeFmInstrument[2*4+2];
        data[4+2*8+3] = cubeFmInstrument[3*4+2];
        data[4+2*8+4] = (byte)(cubeFmInstrument[4*4+2]+0x40);
        data[4+2*8+5] = cubeFmInstrument[5*4+2];
        if(ssgEg){
            data[4+2*8+6] = cubeFmInstrument[6*4+2];
        }
        data[4+2*8+7] = 0;
        data[4+3*8+0] = cubeFmInstrument[0*4+3];
        data[4+3*8+1] = ((ALGO_SLOTS[algo]&0b1000)>0)?0:cubeFmInstrument[1*4+3];
        data[4+3*8+2] = cubeFmInstrument[2*4+3];
        data[4+3*8+3] = cubeFmInstrument[3*4+3];
        data[4+3*8+4] = (byte)(cubeFmInstrument[4*4+3]+0x40);
        data[4+3*8+5] = cubeFmInstrument[5*4+3];
        if(ssgEg){
            data[4+3*8+6] = cubeFmInstrument[6*4+3];
        }
        data[4+3*8+7] = 0;
        
        return new Feature(code, length, data);
    }    
    
}
