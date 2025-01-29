/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.sound.convert.io;

import com.sfc.sf2.sound.formats.cube.MusicEntry;
import com.sfc.sf2.sound.formats.cube.MusicEntry;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Wiz
 */
public class CubeEntryManager {
       
    public static void exportMusicEntryAsBinary(MusicEntry me, String filePath){
    
        try {
            System.out.println("BinaryMusicEntryManager.exportMusicEntryAsBinary() - Writing file ...");
            File file = new File(filePath);
            Path path = Paths.get(file.getAbsolutePath());
            byte[] data = me.produceBinaryOutput(0);
            Files.write(path,data);
            /*
            if(me.getYmInstruments()!=null){
                List<Integer> instList = me.getYmInstrumentList();
                for(Integer i : instList){
                    String instFilePath = filePath.substring(0,filePath.length()-4)+"-yminst"+String.format("%02d", i)+".bin";
                    File instFile = new File(instFilePath);
                    Path instPath = Paths.get(instFile.getAbsolutePath());
                    byte[] instData = me.getYmInstruments()[i];
                    Files.write(instPath,instData);                    
                }
            } 
            */
            System.out.println(data.length + " bytes into " + filePath);  
            System.out.println("BinaryMusicEntryManager.exportMusicEntryAsBinary() - File written.");
        } catch (IOException ex) {
            Logger.getLogger(CubeEntryManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }
       
    public static MusicEntry importMusicEntry(String filePath){
        MusicEntry me = null;
        try{
            File f = new File(filePath);
            byte[] data = Files.readAllBytes(Paths.get(f.getAbsolutePath()));
            me = new MusicEntry(data, 0, 0);
        } catch (IOException ex) {
            Logger.getLogger(CubeBankManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return me;
    }
    
}
