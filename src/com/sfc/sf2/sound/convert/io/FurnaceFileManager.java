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
public class FurnaceFileManager {
    
    
    
    public static void exportMusicEntryAsFurnaceFile(MusicEntry me, String filePath){
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
                //pw.print(FurnaceClipboardProducer.produceClipboardOutput(intro));
                System.out.println("intro channel sizes :\n"
                        + intro.getChannels()[0].getRows().length+" rows\n"
                        + intro.getChannels()[1].getRows().length+" rows\n"
                        + intro.getChannels()[2].getRows().length+" rows\n"
                        + intro.getChannels()[3].getRows().length+" rows\n"
                        + intro.getChannels()[4].getRows().length+" rows\n"
                        + intro.getChannels()[5].getRows().length+" rows\n"
                        + intro.getChannels()[6].getRows().length+" rows\n"
                        + intro.getChannels()[7].getRows().length+" rows\n"
                        + intro.getChannels()[8].getRows().length+" rows\n"
                        + intro.getChannels()[9].getRows().length+" rows"
                        );
                FurnacePattern mainLoop = new FurnacePattern(me, false, true);
                System.out.println("mainLoop channel sizes :\n"
                        + mainLoop.getChannels()[0].getRows().length+" rows\n"
                        + mainLoop.getChannels()[1].getRows().length+" rows\n"
                        + mainLoop.getChannels()[2].getRows().length+" rows\n"
                        + mainLoop.getChannels()[3].getRows().length+" rows\n"
                        + mainLoop.getChannels()[4].getRows().length+" rows\n"
                        + mainLoop.getChannels()[5].getRows().length+" rows\n"
                        + mainLoop.getChannels()[6].getRows().length+" rows\n"
                        + mainLoop.getChannels()[7].getRows().length+" rows\n"
                        + mainLoop.getChannels()[8].getRows().length+" rows\n"
                        + mainLoop.getChannels()[9].getRows().length+" rows"
                        );
                FurnacePattern fp = new FurnacePattern(intro, mainLoop);
                pw.print(FurnaceClipboardProducer.produceClipboardOutput(fp));
            }
            pw.close();
            System.out.println("com.sfc.sf2.sound.convert.io.FurnaceClipboardManager() - Furnace Clipboard exported.");
        } catch (IOException ex) {
            Logger.getLogger(FurnaceFileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
