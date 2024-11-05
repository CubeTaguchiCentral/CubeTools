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
            if(!me.hasIntro()){
            //if(true){
                FurnacePattern fp = new FurnacePattern(me, false, false);
                pw.print(FurnaceClipboardProducer.produceClipboardOutput(fp));
            }else{
                FurnacePattern intro = new FurnacePattern(me, true, false);
                pw.print(FurnaceClipboardProducer.produceClipboardOutput(intro));
                System.out.println("intro size : "+intro.getChannels()[0].getRows().length+" rows");
                FurnacePattern mainLoop = new FurnacePattern(me, false, true);
                pw.print(FurnaceClipboardProducer.produceClipboardOutput(mainLoop));
                System.out.println("mainLoop size : "+intro.getChannels()[0].getRows().length+" rows");
            }
            pw.close();
            System.out.println("com.sfc.sf2.sound.convert.io.FurnaceClipboardManager() - Furnace Clipboard exported.");
        } catch (IOException ex) {
            Logger.getLogger(FurnaceClipboardManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
