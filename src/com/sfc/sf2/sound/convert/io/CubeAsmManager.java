/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.sound.convert.io;

import com.sfc.sf2.sound.formats.cube.MusicEntry;
import com.sfc.sf2.sound.formats.cube.MusicEntry;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Wiz
 */
public class CubeAsmManager {
    
    
    
    public static void exportMusicEntryAsAsm(MusicEntry me, String filePath){
        try {
            System.out.println("AsmMusicEntryManager.exportMusicEntryAsAsm() - Exporting ASM ...");
            Path path = Paths.get(filePath);
            PrintWriter pw;
            pw = new PrintWriter(path.toString(),System.getProperty("file.encoding"));
            pw.print(me.produceAsmOutput());
            pw.close();
            System.out.println("AsmMusicEntryManager.exportMusicEntryAsAsm() - ASM exported.");
        } catch (IOException ex) {
            Logger.getLogger(CubeAsmManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
}
