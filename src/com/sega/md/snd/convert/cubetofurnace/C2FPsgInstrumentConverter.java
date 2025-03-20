/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sega.md.snd.convert.cubetofurnace;

import com.sega.md.snd.formats.cube.MusicEntry;
import com.sega.md.snd.formats.furnace.file.FurnaceFile;
import com.sega.md.snd.formats.furnace.file.section.Feature;

/**
 *
 * @author Wiz
 */
public class C2FPsgInstrumentConverter {
    
    public static final int FURNACE_TEMPLATE_FILE_PSG_INSTRUMENT_INDEX_OFFSET = 0xA0;
    
    public static void convertPsgInstruments(byte[][] cubeInstruments, FurnaceFile ff){
        //ff.setInstruments(new InstrumentBlock[cubeInstruments.length]);
        if(cubeInstruments!=null){
            for(int i=0;i<cubeInstruments.length;i++){
                Feature[] newFeatures = new Feature[2];
                newFeatures[0] = new Feature("psginst"+String.format("%02d", i));
                newFeatures[1] = convertCubeInstrumentToFurnaceFeature(cubeInstruments[i]);
                //newFeatures[2] = new Feature("EN", (short)0, new byte[0]);
                /*if(ff.getInstruments()[i]==null){
                    ff.getInstruments()[i] = new InstrumentBlock();
                }*/
                ff.getInstruments()[FURNACE_TEMPLATE_FILE_PSG_INSTRUMENT_INDEX_OFFSET+i].setRawData(null);
                ff.getInstruments()[FURNACE_TEMPLATE_FILE_PSG_INSTRUMENT_INDEX_OFFSET+i].setFeatures(newFeatures);
            }
        }
    }
    
    public static Feature convertCubeInstrumentToFurnaceFeature(byte[] cubeInstrument){
        String code = "MA";
        int cubeInstrumentLength = cubeInstrument.length;
        int release = 0;
        while((cubeInstrument[release]&0x80)==0){
            release++;
        }
        byte[] data = new byte[10+cubeInstrumentLength+1];
        data[0] = 8; // header length
        data[1] = 0;
        data[2] = 0; // macro code : vol
        data[3] = (byte)cubeInstrumentLength;
        data[4] = -1; // macro loop
        data[5] = (byte)release; // macro release
        data[6] = 0; // macro mode
        data[7] = 1; // macro open/type/word size
        data[8] = 0; // macro delay
        data[9] = 1; // macro speed
        for(int i=0;i<cubeInstrument.length;i++){
            data[10+i] = (byte)(cubeInstrument[i]&0x7F);
        }
        data[data.length-1] = -1;
        short length = (short)(data.length);
        return new Feature(code, length, data);
    }    
    
}
