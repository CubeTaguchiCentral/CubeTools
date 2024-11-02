/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.sound.convert.io;

import com.sfc.sf2.sound.convert.io.cube.MusicEntry;
import com.sfc.sf2.sound.convert.io.furnace.*;
import com.sfc.sf2.sound.convert.io.furnace.clipboard.*;
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
public class FurnaceClipboardManager {
    
    
    
    public static void exportMusicEntryAsFurnaceClipboard(MusicEntry me, String filePath){
        try {
            System.out.println("com.sfc.sf2.sound.convert.io.FurnaceClipboardManager() - Exporting Furnace Clipboard ...");
            Path path = Paths.get(filePath);
            PrintWriter pw;
            pw = new PrintWriter(path.toString(),System.getProperty("file.encoding"));
            FurnacePattern fp = new FurnacePattern(me);
            pw.print(FurnaceClipboardProducer.produceClipboardOutput(fp));
            pw.close();
            System.out.println("com.sfc.sf2.sound.convert.io.FurnaceClipboardManager() - Furnace Clipboard exported.");
        } catch (IOException ex) {
            Logger.getLogger(FurnaceClipboardManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
