/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.sound.convert.io;

import com.sfc.sf2.sound.convert.io.furnace.PatternRange;
import com.sfc.sf2.sound.convert.io.cube.MusicEntry;
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
            System.out.println("FurnaceClipboardManager() - Exporting Furnace Clipboard ...");
            Path path = Paths.get(filePath);
            PrintWriter pw;
            pw = new PrintWriter(path.toString(),System.getProperty("file.encoding"));
            if(!me.hasIntro()){
            //if(true){
                PatternRange fp = new PatternRange(me, false, false);
                pw.print(FurnaceClipboardProducer.produceClipboardOutput(fp));
            }else{
                PatternRange intro = new PatternRange(me, true, false);
                //pw.print(FurnaceClipboardProducer.produceClipboardOutput(intro));
                printChannelSizes(intro);
                PatternRange mainLoop = new PatternRange(me, false, true);
                //pw.print(FurnaceClipboardProducer.produceClipboardOutput(mainLoop));
                printChannelSizes(mainLoop);
                PatternRange fp = new PatternRange(intro, mainLoop);
                pw.print(FurnaceClipboardProducer.produceClipboardOutput(fp));
            }
            pw.close();
            System.out.println("FurnaceClipboardManager() - Furnace Clipboard exported.");
        } catch (IOException ex) {
            Logger.getLogger(FurnaceClipboardManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void printChannelSizes(PatternRange pg){
        for(int i=0;i<pg.getPatterns()[i].getRows().length;i++){
            System.out.println(pg.getPatterns()[0].getRows().length+" rows\n");
        }
    }
    
}
