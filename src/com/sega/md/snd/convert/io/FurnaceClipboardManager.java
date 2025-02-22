/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sega.md.snd.convert.io;

import com.sega.md.snd.formats.furnace.clipboard.FurnaceClipboardProducer;
import com.sega.md.snd.formats.cube.MusicEntry;
import static com.sega.md.snd.convert.cubetofurnace.C2FFileConverter.concatenatePatterns;
import static com.sega.md.snd.convert.cubetofurnace.C2FFileConverter.convertPatterns;
import static com.sega.md.snd.convert.cubetofurnace.C2FFileConverter.fillChannelsAndApplyLoop;
import static com.sega.md.snd.convert.cubetofurnace.C2FFileConverter.fillChannelsToMaxLength;
import com.sega.md.snd.convert.cubetofurnace.C2FPatternConverter;
import com.sega.md.snd.formats.furnace.pattern.Pattern;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import static com.sega.md.snd.convert.cubetofurnace.C2FFileConverter.applyChannelEnds;
import com.sega.md.snd.formats.furnace.pattern.Effect;

/**
 *
 * @author Wiz
 */
public class FurnaceClipboardManager {
    
    private static final int CHANNEL_COUNT = 10;
    private static final int PATTERN_LENGTH = 256;
    
    public static void exportMusicEntryAsFurnaceClipboard(MusicEntry me, String filePath){
        try {
            System.out.println("FurnaceClipboardManager() - Exporting Furnace Clipboard ...");
            Path path = Paths.get(filePath);
            PrintWriter pw;
            pw = new PrintWriter(path.toString(),System.getProperty("file.encoding"));

            /* Stateful converters */
            C2FPatternConverter[] converters = C2FPatternConverter.instantiateConverterArray(CHANNEL_COUNT);
            Pattern[] patterns = null;
            if(!me.hasMainLoop() && !me.hasRepeatLoop()){
                patterns = convertPatterns(me, converters, false, false);
                applyChannelEnds(patterns);
            }else{         
                Pattern[] introPatterns = convertPatterns(me, converters, true, false);
                Pattern[] mainLoopPatterns = convertPatterns(me, converters, false, true);
                patterns = concatenatePatterns(introPatterns, mainLoopPatterns);
                fillChannelsAndApplyLoop(introPatterns, mainLoopPatterns, patterns, converters);
            } 
            fillChannelsToMaxLength(patterns);
        
            if(!me.hasMainLoop() && !me.hasRepeatLoop()){
                patterns[0].getRows()[patterns[0].getRows().length-1].getEffectList().add(new Effect(0xFF,0x00));
            }
            pw.print(FurnaceClipboardProducer.produceClipboardOutput(patterns, PATTERN_LENGTH));
            pw.close();
            System.out.println("FurnaceClipboardManager() - Furnace Clipboard exported.");
        } catch (IOException ex) {
            Logger.getLogger(FurnaceClipboardManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void printChannelSizes(Pattern[] patterns){
        for(int i=0;i<patterns[i].getRows().length;i++){
            System.out.println(patterns[0].getRows().length+" rows\n");
        }
    }
    
}
