/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sega.md.snd.formats.cube;

/**
 *
 * @author Wiz
 */
public enum Pitch {

    C1(0x00),
    Cs1(0x01),
    D1(0x02),
    Ds1(0x03),
    E1(0x04),
    F1(0x05),
    Fs1(0x06),
    G1(0x07),
    Gs1(0x08),
    A1(0x09),
    As1(0x0A),
    B1(0x0B),
    C2(0x0C),
    Cs2(0x0D),
    D2(0x0E),
    Ds2(0x0F),
    E2(0x10),
    F2(0x11),
    Fs2(0x12),
    G2(0x13),
    Gs2(0x14),
    A2(0x15),
    As2(0x16),
    B2(0x17),
    C3(0x18),
    Cs3(0x19),
    D3(0x1A),
    Ds3(0x1B),
    E3(0x1C),
    F3(0x1D),
    Fs3(0x1E),
    G3(0x1F),
    Gs3(0x20),
    A3(0x21),
    As3(0x22),
    B3(0x23),
    C4(0x24),
    Cs4(0x25),
    D4(0x26),
    Ds4(0x27),
    E4(0x28),
    F4(0x29),
    Fs4(0x2A),
    G4(0x2B),
    Gs4(0x2C),
    A4(0x2D),
    As4(0x2E),
    B4(0x2F),
    C5(0x30),
    Cs5(0x31),
    D5(0x32),
    Ds5(0x33),
    E5(0x34),
    F5(0x35),
    Fs5(0x36),
    G5(0x37),
    Gs5(0x38),
    A5(0x39),
    As5(0x3A),
    B5(0x3B),
    C6(0x3C),
    Cs6(0x3D),
    D6(0x3E),
    Ds6(0x3F),
    E6(0x40),
    F6(0x41),
    Fs6(0x42),
    G6(0x43),
    Gs6(0x44),
    A6(0x45),
    As6(0x46),
    B6(0x47),
    C7(0x48),
    Cs7(0x49),
    D7(0x4A),
    Ds7(0x4B),
    E7(0x4C),
    F7(0x4D),
    Fs7(0x4E),
    G7(0x4F),
    Gs7(0x50),
    A7(0x51),
    As7(0x52),
    B7(0x53),
    C8(0x54),
    Cs8(0x55),
    D8(0x56),
    Ds8(0x57),
    E8(0x58),
    F8(0x59),
    Fs8(0x5A),
    G8(0x5B),
    Gs8(0x5C),
    A8(0x5D),
    As8(0x5E),
    B8(0x5F),
    C9(0x60),
    Cs9(0x61),
    D9(0x62),
    Ds9(0x63),
    E9(0x64),
    F9(0x65),
    Fs9(0x66),
    G9(0x67),
    Gs9(0x68),
    A9(0x69),
    As9(0x6A),
    B9(0x6B);

    private final int value;
    public int getValue(){
        return value;
    }
    private Pitch(int value){
        this.value = value;
    }
    
    public static Pitch valueOf(int value) {
        for (Pitch p : values()) {
            if (p.getValue()==value) {
                return p;
            }
        }
        return C1;
    }  
  
}
