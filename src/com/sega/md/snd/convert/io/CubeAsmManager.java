/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sega.md.snd.convert.io;

import com.sega.md.snd.formats.cube.MusicEntry;
import com.sega.md.snd.formats.cube.MusicEntry;
import com.sega.md.snd.formats.cube.SampleEntry;
import com.sega.md.snd.formats.cube.SfxEntry;
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
            System.out.println("CubeAsmManager.exportMusicEntryAsAsm() - Exporting ASM ...");
            Path path = Paths.get(filePath);
            PrintWriter pw;
            pw = new PrintWriter(path.toString(),System.getProperty("file.encoding"));
            pw.print(me.produceAsmOutput());
            pw.close();
            System.out.println("CubeAsmManager.exportMusicEntryAsAsm() - ASM exported.");
        } catch (IOException ex) {
            Logger.getLogger(CubeAsmManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void exportSfxEntryAsAsm(SfxEntry se, String filePath){
        try {
            System.out.println("CubeAsmManager.exportSfxEntryAsAsm() - Exporting ASM ...");
            Path path = Paths.get(filePath);
            PrintWriter pw;
            pw = new PrintWriter(path.toString(),System.getProperty("file.encoding"));
            pw.print(se.produceAsmOutput());
            pw.close();
            System.out.println("CubeAsmManager.exportSfxEntryAsAsm() - ASM exported.");
        } catch (IOException ex) {
            Logger.getLogger(CubeAsmManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void exportSampleEntriesAsAsm(SampleEntry[] ses, String filePath){
        try {
            System.out.println("CubeAsmManager.exportSampleEntriesAsAsm() - Exporting ASM ...");
            Path path = Paths.get(filePath);
            PrintWriter pw;
            pw = new PrintWriter(path.toString(),System.getProperty("file.encoding"));
            pw.print(SampleEntry.toString(ses));
            pw.close();
            System.out.println("CubeAsmManager.exportSampleEntriesAsAsm() - ASM exported.");
        } catch (IOException ex) {
            Logger.getLogger(CubeAsmManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
    
}
