/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.sound.convert.io.cubetofurnace;

import com.sfc.sf2.sound.convert.io.cube.MusicEntry;
import com.sfc.sf2.sound.convert.io.furnace.file.FurnaceFile;
import com.sfc.sf2.sound.convert.io.furnace.file.section.Feature;

/**
 *
 * @author Wiz
 */
public class C2FYmInstrumentConverter {
    
    public static void convertYmInstruments(MusicEntry me, FurnaceFile ff){
        byte[][] cubeInstruments = me.getYmInstruments();
        //ff.setInstruments(new InstrumentBlock[cubeInstruments.length]);
        for(int i=0;i<cubeInstruments.length;i++){
            Feature[] newFeatures = new Feature[2];
            newFeatures[0] = new Feature("yminst"+String.format("%02d", i));
            newFeatures[1] = new Feature(cubeInstruments[i], me.isSsgEgAvailable());
            /*if(ff.getInstruments()[i]==null){
                ff.getInstruments()[i] = new InstrumentBlock();
            }*/
            ff.getInstruments()[i].setRawData(null);
            ff.getInstruments()[i].setFeatures(newFeatures);
        }
    }
    
}
