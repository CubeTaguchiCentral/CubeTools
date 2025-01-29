/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.sound.formats.furnace.pattern;

/**
 *
 * @author Wiz
 */
public enum Pitch {

    C0( 0x3C,	"C-0"),
    Cs0(0x3D,	"C#0"),
    D0( 0x3E,	"D-0"),
    Ds0(0x3F,	"D#0"),
    E0( 0x40,	"E-0"),
    F0( 0x41,	"F-0"),
    Fs0(0x42,	"F#0"),
    G0( 0x43,	"G-0"),
    Gs0(0x44,	"G#0"),
    A0( 0x45,	"A-0"),
    As0(0x46,	"A#0"),
    B0( 0x47,	"B-0"),
    C1( 0x48,	"C-1"),
    Cs1(0x49,	"C#1"),
    D1( 0x4A,	"D-1"),
    Ds1(0x4B,	"D#1"),
    E1( 0x4C,	"E-1"),
    F1( 0x4D,	"F-1"),
    Fs1(0x4E,	"F#1"),
    G1( 0x4F,	"G-1"),
    Gs1(0x50,	"G#1"),
    A1( 0x51,	"A-1"),
    As1(0x52,	"A#1"),
    B1( 0x53,	"B-1"),
    C2( 0x54,	"C-2"),
    Cs2(0x55,	"C#2"),
    D2( 0x56,	"D-2"),
    Ds2(0x57,	"D#2"),
    E2( 0x58,	"E-2"),
    F2( 0x59,	"F-2"),
    Fs2(0x5A,	"F#2"),
    G2( 0x5B,	"G-2"),
    Gs2(0x5C,	"G#2"),
    A2( 0x5D,	"A-2"),
    As2(0x5E,	"A#2"),
    B2( 0x5F,	"B-2"),
    C3( 0x60,	"C-3"),
    Cs3(0x61,	"C#3"),
    D3( 0x62,	"D-3"),
    Ds3(0x63,	"D#3"),
    E3( 0x64,	"E-3"),
    F3( 0x65,	"F-3"),
    Fs3(0x66,	"F#3"),
    G3( 0x67,	"G-3"),
    Gs3(0x68,	"G#3"),
    A3( 0x69,	"A-3"),
    As3(0x6A,	"A#3"),
    B3( 0x6B,	"B-3"),
    C4( 0x6C,	"C-4"),
    Cs4(0x6D,	"C#4"),
    D4( 0x6E,	"D-4"),
    Ds4(0x6F,	"D#4"),
    E4( 0x70,	"E-4"),
    F4( 0x71,	"F-4"),
    Fs4(0x72,	"F#4"),
    G4( 0x73,	"G-4"),
    Gs4(0x74,	"G#4"),
    A4( 0x75,	"A-4"),
    As4(0x76,	"A#4"),
    B4( 0x77,	"B-4"),
    C5( 0x78,	"C-5"),
    Cs5(0x79,	"C#5"),
    D5( 0x7A,	"D-5"),
    Ds5(0x7B,	"D#5"),
    E5( 0x7C,	"E-5"),
    F5( 0x7D,	"F-5"),
    Fs5(0x7E,	"F#5"),
    G5( 0x7F,	"G-5"),
    Gs5(0x80,	"G#5"),
    A5( 0x81,	"A-5"),
    As5(0x82,	"A#5"),
    B5( 0x83,	"B-5"),
    C6( 0x84,	"C-6"),
    Cs6(0x85,	"C#6"),
    D6( 0x86,	"D-6"),
    Ds6(0x87,	"D#6"),
    E6( 0x88,	"E-6"),
    F6( 0x89,	"F-6"),
    Fs6(0x8A,	"F#6"),
    G6( 0x8B,	"G-6"),
    Gs6(0x8C,	"G#6"),
    A6( 0x8D,	"A-6"),
    As6(0x8E,	"A#6"),
    B6( 0x8F,	"B-6"),
    C7( 0x90,	"C-7"),
    Cs7(0x91,	"C#7"),
    D7( 0x92,	"D-7"),
    Ds7(0x93,	"D#7"),
    E7( 0x94,	"E-7"),
    F7( 0x95,	"F-7"),
    Fs7(0x96,	"F#7"),
    G7( 0x97,	"G-7"),
    Gs7(0x98,	"G#7"),
    A7( 0x99,	"A-7"),
    As7(0x9A,	"A#7"),
    B7( 0x9B,	"B-7"),
    C8( 0x9C,	"C-8"),
    Cs8(0x9D,	"C#8"),
    D8( 0x9E,	"D-8"),
    Ds8(0x9F,	"D#8"),
    E8( 0xA0,	"E-8"),
    F8( 0xA1,	"F-8"),
    Fs8(0xA2,	"F#8"),
    G8( 0xA3,	"G-8"),
    Gs8(0xA4,	"G#8"),
    A8( 0xA5,	"A-8"),
    As8(0xA6,	"A#8"),
    B8( 0xA7,	"B-8");

    private final int value;
    private final String stringValue;
    
    public int getValue(){
        return value;
    }

    public String getStringValue() {
        return stringValue;
    }
    
    private Pitch(int value, String stringValue){
        this.value = value;
        this.stringValue=stringValue;
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
