/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.sound.formats.furnace.file.section;

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
    
    public Feature(String code, short length, byte[] data){
        this.code = code;
        this.length = length;
        this.data = data;
    }
    
    public Feature(String name){
        code = "NA";
        length = (short)(name.length()+1);
        data = new byte[name.length()+1];
        System.arraycopy(name.getBytes(StandardCharsets.UTF_8), 0, data, 0, name.length());
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
