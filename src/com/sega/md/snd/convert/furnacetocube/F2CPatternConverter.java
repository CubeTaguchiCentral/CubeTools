/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sega.md.snd.convert.furnacetocube;

/**
 *
 * @author Wiz
 */
public class F2CPatternConverter {
    
    public static final int MD_CRYSTAL_FREQUENCY = 53693175;
    public static final float YM2612_INPUT_FREQUENCY = MD_CRYSTAL_FREQUENCY / 7;
    public static final int YM2612_CHANNEL_SAMPLE_CYCLES = 6*24;
    public static final float YM2612_OUTPUT_RATE = YM2612_INPUT_FREQUENCY / YM2612_CHANNEL_SAMPLE_CYCLES;
    
    public static int calculateYmTimerB(float ticksPerSecond, int speed){  
        int ymTimerB;
        float timerPeriod = speed / ticksPerSecond;
        ymTimerB = Math.round(256 - (timerPeriod * (YM2612_INPUT_FREQUENCY/2) / (8*144)));
        System.out.println(ticksPerSecond+" ticks per second"+" -> "+"Timer B value "+Integer.toString(0xFF&ymTimerB));
        return ymTimerB;
    }
    
}
