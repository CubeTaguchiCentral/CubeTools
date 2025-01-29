/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.sound.formats.cube;

/**
 *
 * @author Wiz
 */
public enum Pitch {

    C0(0x00),
    Cs0(0x01),
    D0(0x02),
    Ds0(0x03),
    E0(0x04),
    F0(0x05),
    Fs0(0x06),
    G0(0x07),
    Gs0(0x08),
    A0(0x09),
    As0(0x0A),
    B0(0x0B),
    C1(0x0C),
    Cs1(0x0D),
    D1(0x0E),
    Ds1(0x0F),
    E1(0x10),
    F1(0x11),
    Fs1(0x12),
    G1(0x13),
    Gs1(0x14),
    A1(0x15),
    As1(0x16),
    B1(0x17),
    C2(0x18),
    Cs2(0x19),
    D2(0x1A),
    Ds2(0x1B),
    E2(0x1C),
    F2(0x1D),
    Fs2(0x1E),
    G2(0x1F),
    Gs2(0x20),
    A2(0x21),
    As2(0x22),
    B2(0x23),
    C3(0x24),
    Cs3(0x25),
    D3(0x26),
    Ds3(0x27),
    E3(0x28),
    F3(0x29),
    Fs3(0x2A),
    G3(0x2B),
    Gs3(0x2C),
    A3(0x2D),
    As3(0x2E),
    B3(0x2F),
    C4(0x30),
    Cs4(0x31),
    D4(0x32),
    Ds4(0x33),
    E4(0x34),
    F4(0x35),
    Fs4(0x36),
    G4(0x37),
    Gs4(0x38),
    A4(0x39),
    As4(0x3A),
    B4(0x3B),
    C5(0x3C),
    Cs5(0x3D),
    D5(0x3E),
    Ds5(0x3F),
    E5(0x40),
    F5(0x41),
    Fs5(0x42),
    G5(0x43),
    Gs5(0x44),
    A5(0x45),
    As5(0x46),
    B5(0x47),
    C6(0x48),
    Cs6(0x49),
    D6(0x4A),
    Ds6(0x4B),
    E6(0x4C),
    F6(0x4D),
    Fs6(0x4E),
    G6(0x4F),
    Gs6(0x50),
    A6(0x51),
    As6(0x52),
    B6(0x53),
    C7(0x54),
    Cs7(0x55),
    D7(0x56),
    Ds7(0x57),
    E7(0x58),
    F7(0x59),
    Fs7(0x5A),
    G7(0x5B),
    Gs7(0x5C),
    A7(0x5D),
    As7(0x5E),
    B7(0x5F),
    C8(0x60),
    Cs8(0x61),
    D8(0x62),
    Ds8(0x63),
    E8(0x64),
    F8(0x65),
    Fs8(0x66),
    G8(0x67),
    Gs8(0x68),
    A8(0x69),
    As8(0x6A),
    B8(0x6B);

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
        return C0;
    }  
  
}
