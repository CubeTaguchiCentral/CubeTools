/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.sound.convert.io.cube;

/**
 *
 * @author Wiz
 */
public abstract class CubeCommand {
     
    public abstract byte[] produceBinaryOutput();
    public abstract String produceStringOutput();
    public abstract boolean equals(CubeCommand cc);
}
