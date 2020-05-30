/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.sound.ym;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wiz
 */
public class YMInstManager {
    
    
    public static void main(String[] args){
        
        
        
        exportTFI();
        
        
        
        
    }
    
    
    
    private static void exportTFI(){
        
        String yminstFilepath = "C:\\SEGADEV\\GITHUB\\LSUSDISASM\\disasm\\data\\sound\\yminst.bin";
        
        byte[][] instruments = null;
        try {
            int cursor = 0;
            Path yminstPath = Paths.get(yminstFilepath);
            byte[] data = Files.readAllBytes(yminstPath);
            List<byte[]> instList = new ArrayList();
            while(true){
                if( (cursor >= data.length) || ((int)(data[cursor]) ==-1)){
                    break;
                }
                byte[] inst = new byte[29];
                System.arraycopy(data, cursor, inst, 0, 29);
                cursor += 29;
                instList.add(inst);
            }
            instruments = new byte[instList.size()][];
            instruments = instList.toArray(instruments);
        } catch (IOException ex) {
            System.out.println("Error while reading yminst file : " + ex.getMessage());
            ex.printStackTrace();
        }
        
        String destDir = "C:\\SEGADEV\\AUDIO\\vgmmusicmaker111\\ls\\yminst\\";
        try { 
            for(int i=0;i<instruments.length;i++){
                byte[] data = instruments[i];
                byte[] tfi = new byte[42];
                
                tfi[0] = (byte)(data[28]&0x07);
                tfi[1] = (byte)((data[28]&0x70)>>4);
                for(int op=0;op<4;op++){
                    tfi[2+0+op*10] = (byte)(data[0*4+op]&0x0F); // Multiplier
                    tfi[2+1+op*10] = (byte)((data[0*4+op]&0x70)>>4); // Detune
                    if(op==3){
                        tfi[2+2+op*10] = 16;
                    }else{
                        tfi[2+2+op*10] = data[1*4+op];
                    } // Total Level
                    tfi[2+3+op*10] = (byte)((data[2*4+op]&0xC0)>>6); // Rate Scaling
                    tfi[2+4+op*10] = (byte)(data[2*4+op]&0x1F); // Attack Rate
                    tfi[2+5+op*10] = (byte)(data[3*4+op]&0x1F); // Decay Rate
                    tfi[2+6+op*10] = (byte)(data[4*4+op]&0x1F); // Sustain Rate
                    tfi[2+7+op*10] = (byte)(data[5*4+op]&0x0F); // Release Rate
                    tfi[2+8+op*10] = (byte)((data[5*4+op]&0xF0)>>4); // Sustain Level
                    tfi[2+9+op*10] = (byte)(data[6*4+op]&0x0F); // SSG-EG
                }
                
                String index = String.valueOf(i);
                if(index.length()<2){
                    index = "0"+index;
                }
                Path tfiFilepath = Paths.get(destDir+"yminst"+index+".tfi");
                Files.write(tfiFilepath,tfi);
                System.out.println(tfi.length + " bytes into " + tfiFilepath);
                
            }
        } catch (Exception ex) {
            System.out.println("Error while writing tfi file : " + ex.getMessage());
            ex.printStackTrace();
            System.out.println(ex);
        }   
        
        
        
    }
    
    
    
    
}
