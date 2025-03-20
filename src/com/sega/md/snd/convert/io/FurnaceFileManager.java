/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sega.md.snd.convert.io;

import com.sega.md.snd.formats.cube.MusicEntry;
import com.sega.md.snd.convert.cubetofurnace.C2FMusicFileConverter;
import com.sega.md.snd.convert.cubetofurnace.C2FSfxFileConverter;
import com.sega.md.snd.formats.cube.SfxEntry;
import com.sega.md.snd.formats.furnace.file.FurnaceFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Wiz
 */
public class FurnaceFileManager {
           
    public static MusicEntry importFurnaceFile(String filePath){
        MusicEntry me = null;
        try{
            File f = new File(filePath);
            byte[] data = Files.readAllBytes(Paths.get(f.getAbsolutePath()));
        } catch (IOException ex) {
            Logger.getLogger(CubeBinaryManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return me;
    }
    
    public static void exportMusicEntryAsFurnaceFile(MusicEntry me, String templateFilePath, String outputFilePath){
        try {
            System.out.println("FurnaceFileManager() - Exporting Furnace File ...");
            
            File f = new File(templateFilePath);
            byte[] inputData = Files.readAllBytes(Paths.get(f.getAbsolutePath()));
            FurnaceFile ff = new FurnaceFile(inputData);
            
            ff = C2FMusicFileConverter.convertMusicEntry(me, ff);
            
            File file = new File(outputFilePath);
            Path path = Paths.get(file.getAbsolutePath());
            byte[] outputData = ff.toByteArray();
            Files.write(path,outputData);
            
            System.out.println("FurnaceFileManager() - Furnace File exported.");
        } catch (IOException ex) {
            Logger.getLogger(FurnaceFileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void exportSfxEntryAsFurnaceFile(SfxEntry se, String templateFilePath, String outputFilePath){
        try {
            System.out.println("FurnaceFileManager() - Exporting Furnace File ...");
            
            File f = new File(templateFilePath);
            byte[] inputData = Files.readAllBytes(Paths.get(f.getAbsolutePath()));
            FurnaceFile ff = new FurnaceFile(inputData);
            
            ff = C2FSfxFileConverter.convertSfxEntry(se, ff);
            
            File file = new File(outputFilePath);
            Path path = Paths.get(file.getAbsolutePath());
            byte[] outputData = ff.toByteArray();
            Files.write(path,outputData);
            
            System.out.println("FurnaceFileManager() - Furnace File exported.");
        } catch (IOException ex) {
            Logger.getLogger(FurnaceFileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
