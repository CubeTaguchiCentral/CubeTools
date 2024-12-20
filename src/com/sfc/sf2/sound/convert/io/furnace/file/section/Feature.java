/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.sound.convert.io.furnace.file.section;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author Wiz
 */
public class Feature {
    
    private String code = "";
    private short length = 0;
    private byte[] data = null;
    
    public Feature(String name){
        code = "NA";
        length = (short)(name.length()+1);
        data = new byte[name.length()+1];
        System.arraycopy(name.getBytes(StandardCharsets.UTF_8), 0, data, 0, name.length());
    }
    
    public Feature(byte[] cubeFmInstrument){
        code = "FM";
        length = (short)36;
        data = new byte[36];
        data[0] = (byte)0xF4;
        int algo = cubeFmInstrument[28]&0x07;
        int feedback = (cubeFmInstrument[28]&0x38)>>3;
        data[1] = (byte)((algo<<4)+feedback);
        data[2] = 0;
        data[3] = 0x20;
        data[4+0*8+0] = cubeFmInstrument[0*4+0];
        data[4+0*8+1] = (cubeFmInstrument[1*4+0]==0x7F)?0:cubeFmInstrument[1*4+0];
        data[4+0*8+2] = cubeFmInstrument[2*4+0];
        data[4+0*8+3] = cubeFmInstrument[3*4+0];
        data[4+0*8+4] = (byte)(cubeFmInstrument[4*4+0]+0x40);
        data[4+0*8+5] = cubeFmInstrument[5*4+0];
        data[4+0*8+6] = cubeFmInstrument[6*4+0];
        data[4+0*8+7] = 0;
        data[4+1*8+0] = cubeFmInstrument[0*4+1];
        data[4+1*8+1] = (cubeFmInstrument[1*4+1]==0x7F)?0:cubeFmInstrument[1*4+1];
        data[4+1*8+2] = cubeFmInstrument[2*4+1];
        data[4+1*8+3] = cubeFmInstrument[3*4+1];
        data[4+1*8+4] = (byte)(cubeFmInstrument[4*4+1]+0x40);
        data[4+1*8+5] = cubeFmInstrument[5*4+1];
        data[4+1*8+6] = cubeFmInstrument[6*4+1];
        data[4+1*8+7] = 0;
        data[4+2*8+0] = cubeFmInstrument[0*4+2];
        data[4+2*8+1] = (cubeFmInstrument[1*4+2]==0x7F)?0:cubeFmInstrument[1*4+2];
        data[4+2*8+2] = cubeFmInstrument[2*4+2];
        data[4+2*8+3] = cubeFmInstrument[3*4+2];
        data[4+2*8+4] = (byte)(cubeFmInstrument[4*4+2]+0x40);
        data[4+2*8+5] = cubeFmInstrument[5*4+2];
        data[4+2*8+6] = cubeFmInstrument[6*4+2];
        data[4+2*8+7] = 0;
        data[4+3*8+0] = cubeFmInstrument[0*4+3];
        data[4+3*8+1] = (cubeFmInstrument[1*4+3]==0x7F)?0:cubeFmInstrument[1*4+3];
        data[4+3*8+2] = cubeFmInstrument[2*4+3];
        data[4+3*8+3] = cubeFmInstrument[3*4+3];
        data[4+3*8+4] = (byte)(cubeFmInstrument[4*4+3]+0x40);
        data[4+3*8+5] = cubeFmInstrument[5*4+3];
        data[4+3*8+6] = cubeFmInstrument[6*4+3];
        data[4+3*8+7] = 0;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public short getLength() {
        return length;
    }

    public void setLength(short length) {
        this.length = length;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
    
    public byte[] toByteArray(){
        ByteBuffer bb = ByteBuffer.allocate(findLength());
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.position(0);
        bb.put(code.getBytes(StandardCharsets.UTF_8));
        bb.putShort((short)(findLength()-2-2));
        bb.put(data);
        return bb.array();
    }
    
    public int findLength(){
        return 2+2+data.length;
    }
    
}
