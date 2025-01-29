/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.sound.formats.furnace.file.section;

import com.sfc.sf2.sound.formats.furnace.file.FurnaceFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author Wiz
 */
public class SongInfoBlock {
    
    private String blockId = "INFO";
    private int blockSize = 0;
    private byte timebase = 0;
    private byte speed1 = 1;
    private byte speed2 = 1;
    private byte initialArpeggioTime = 1;
    private float ticksPerSecond = 60;
    private short patternLength = 256;
    private short ordersLength = 1;
    private byte highlightA = 8;
    private byte highlightB = 16;
    private short instrumentCount = 92;
    private short wavetableCount = 0;
    private short sampleCount = 12;
    private int patternCount = 10;
    private byte[] soundChips = new byte[32];
    private byte[] soundChipVolumes = new byte[32];
    private byte[] soundChipPanning = new byte[32];
    private int[] soundChipFlagPointers = new int[32];
    private String songName = "Song Name";
    private String songAuthor = "Song Author";
    private float a4Tuning = 440;
    private byte limitSlides = 0;
    private byte linearPitch = 2;
    private byte loopModality = 2;
    private byte properNoiseLayout = 1;
    private byte waveDutyIsVolume = 0;
    private byte resetMacroOnPorta = 0;
    private byte legacyVolumeSlides = 0;
    private byte compatibleArpeggio = 0;
    private byte noteOffResetsSlides = 1;
    private byte targetResetsSlides = 1;
    private byte arpeggioInhibitsPortamento = 0;
    private byte wackAlgorithmMacro = 0;
    private byte brokenShortcutSlides = 0;
    private byte ignoreDuplicateSlides = 1;
    private byte stopPortamentoOnNoteOff = 0;
    private byte continuousVibrato = 0;
    private byte brokenDacMode = 0;
    private byte oneTickCut = 0;
    private byte instrumentChangeAllowedDuringPorta = 1;
    private byte resetNoteBaseOnArpeggioEffectStop = 1;
    private int[] instrumentPointers = new int[instrumentCount];
    private int[] wavetablePointers = new int[wavetableCount];
    private int[] samplePointers = new int[sampleCount];
    private int[] patternPointers = new int[patternCount];
    private byte[] orders = new byte[ordersLength*10];
    private byte[] effectColumns = new byte[10];
    private byte[] channelHideStatus = new byte[10];
    private byte[] channelCollapseStatus = new byte[10];
    private String[] channelNames = new String[10];
    private String[] channelShortNames = new String[10];
    private String songComment = "Song Comment";
    private float masterVolume = 1;
    private byte brokenSpeedSelection = 0;
    private byte noSlidesOnFirstTick = 0;
    private byte nextRowResetArpPos = 0;
    private byte ignoreJumpAtEnd = 0;
    private byte buggyPortamentoAfterSlide = 0;
    private byte newInsAffectsEnveloppeGB = 1;
    private byte extChStateIsShared = 1;
    private byte ignoreDacModeChangeOutsideOfIntendedChannel = 0;
    private byte e1xyAnde2xyAlsoTakePriorityOverSlide00 = 0;
    private byte newSegaPcm = 1;
    private byte weirdFnumBlockBasedChipPitchSlides = 0;
    private byte snDutyMacroAlwaysResetsPhase = 0;
    private byte pitchMacroIsLinear = 1;
    private byte pitchSlideSpeedInFullLinearPitchMode = 4;
    private byte oldOctaveBoundaryBehaviour = 0;
    private byte disableOpn2DacVolumeControl = 0;
    private byte newVolumeScalingStrategy = 1;
    private byte volumeMacroStillAppliesAfterEnd = 1;
    private byte brokenOutVol = 0;
    private byte e1xyAnde2xyStopOnSameNote = 0;
    private byte brokenInitialPositionOfPortaAfterArp = 0;
    private byte snPeriodsUnder8AreTreatedAs1 = 0;
    private byte cutDelayEffectPolicy = 2;
    private byte effect0b0dtreatment = 0;
    private byte automaticSystemNameDetection = 1;
    private byte disableSampleMacro = 0;
    private byte brokenOutVolEpisode2 = 0;
    private byte oldArpeggioStrategy = 0;
    private short virtualTempoNumerator = 150;
    private short virtualTempoDenominator = 150;
    private String firstSubsongName = "First Subsong Name";
    private String firstSubsongComment = "First Subsong Comment";
    private byte numberOfAdditionalSubsongs = 0;
    private byte[] additionalSubsongsReserved = new byte[3];
    private int[] subsongDataPointers = new int[0];
    private String systemName = "System Name";
    private String albumCategoryGameName = "Album Category Game Name";
    private String songNameJapanese = "Song Name Japanese";
    private String songAuthorJapanese = "Song Author Japanese";
    private String systemNameJapanese = "System Name Japanese";
    private String albumCategoryGameNameJapanese = "Album Category Game Name Japanese";
    private float[] extraChipOutputSettings = new float[2*3];
    private int patchbayConnectionCount = 50;
    private int[] patchbays = new int[0];
    private byte automaticPatchbay = 1;
    private byte brokenPortamentoDuringLegato = 0;
    private byte brokenMacroDuringNoteOffInSomeFmChips = 0;
    private byte preNoteC64DoesNotCompensateForPortamentoOrLegato = 0;
    private byte disableNewNesDpcmFeatures = 0;
    private byte resetArpEffectPhaseOnNewNote = 0;
    private byte linearVolumeScalingRoundsUp = 0;
    private byte legacyAlwaysSetVolumeBehavior = 0;
    private byte legacySampleOffsetEffect = 0;
    private byte lengthOfSpeedPattern = 1;
    private byte[] speedPattern = new byte[16];
    private byte grooveListEntryNumber = 0;
    private byte[] grooveEntries = new byte[0];
    private int instrumentDirectoriesPointer = 0;
    private int wavetableDirectoriesPointer = 0;
    private int sampleDirectoriesPointer = 0;
    
    public SongInfoBlock(byte[] data, int startPointer){
        ByteBuffer bb = ByteBuffer.wrap(data, startPointer, data.length-startPointer);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.position(startPointer);
        blockId = getString(bb, 4);
        blockSize = bb.getInt();
        timebase = bb.get();
        speed1 = bb.get();
        speed2 = bb.get();
        initialArpeggioTime = bb.get();
        ticksPerSecond = bb.getFloat();
        patternLength = bb.getShort();
        ordersLength = bb.getShort();
        highlightA = bb.get();
        highlightB = bb.get();
        instrumentCount = bb.getShort();
        wavetableCount = bb.getShort();
        sampleCount = bb.getShort();
        patternCount = bb.getInt();
        soundChips = getByteArray(bb, 32);
        soundChipVolumes = getByteArray(bb, 32);
        soundChipPanning = getByteArray(bb, 32);
        soundChipFlagPointers = getIntArray(bb, 32);
        songName = getString(bb);
        songAuthor = getString(bb);
        a4Tuning = bb.getFloat();
        limitSlides = bb.get();
        linearPitch = bb.get();
        loopModality = bb.get();
        properNoiseLayout = bb.get();
        waveDutyIsVolume = bb.get();
        resetMacroOnPorta = bb.get();
        legacyVolumeSlides = bb.get();
        compatibleArpeggio = bb.get();
        noteOffResetsSlides = bb.get();
        targetResetsSlides = bb.get();
        arpeggioInhibitsPortamento = bb.get();
        wackAlgorithmMacro = bb.get();
        brokenShortcutSlides = bb.get();
        ignoreDuplicateSlides = bb.get();
        stopPortamentoOnNoteOff = bb.get();
        continuousVibrato = bb.get();
        brokenDacMode = bb.get();
        oneTickCut = bb.get();
        
        instrumentChangeAllowedDuringPorta = bb.get();
        resetNoteBaseOnArpeggioEffectStop = bb.get();
        instrumentPointers = getIntArray(bb, instrumentCount);
        wavetablePointers = getIntArray(bb, wavetableCount);
        samplePointers = getIntArray(bb, sampleCount);
        patternPointers = getIntArray(bb, patternCount);
        
        orders = getByteArray(bb, ordersLength*10);
        effectColumns = getByteArray(bb, 10);
        channelHideStatus = getByteArray(bb, 10);
        channelCollapseStatus = getByteArray(bb, 10);
        channelNames = getStringArray(bb, 10);
        channelShortNames = getStringArray(bb, 10);
        songComment = getString(bb);
        masterVolume = bb.getFloat();
        brokenSpeedSelection = bb.get();
        noSlidesOnFirstTick = bb.get();
        nextRowResetArpPos = bb.get();
        ignoreJumpAtEnd = bb.get();
        buggyPortamentoAfterSlide = bb.get();
        newInsAffectsEnveloppeGB = bb.get();
        extChStateIsShared = bb.get();
        ignoreDacModeChangeOutsideOfIntendedChannel = bb.get();
        e1xyAnde2xyAlsoTakePriorityOverSlide00 = bb.get();
        newSegaPcm = bb.get();
        weirdFnumBlockBasedChipPitchSlides = bb.get();
        snDutyMacroAlwaysResetsPhase = bb.get();
        pitchMacroIsLinear = bb.get();
        pitchSlideSpeedInFullLinearPitchMode = bb.get();
        oldOctaveBoundaryBehaviour = bb.get();
        disableOpn2DacVolumeControl = bb.get();
        newVolumeScalingStrategy = bb.get();
        volumeMacroStillAppliesAfterEnd = bb.get();
        brokenOutVol = bb.get();
        e1xyAnde2xyStopOnSameNote = bb.get();
        brokenInitialPositionOfPortaAfterArp = bb.get();
        snPeriodsUnder8AreTreatedAs1 = bb.get();
        cutDelayEffectPolicy = bb.get();
        effect0b0dtreatment = bb.get();
        automaticSystemNameDetection = bb.get();
        disableSampleMacro = bb.get();
        brokenOutVolEpisode2 = bb.get();
        oldArpeggioStrategy = bb.get();
        virtualTempoNumerator = bb.getShort();
        virtualTempoDenominator = bb.getShort();
        firstSubsongName = getString(bb);
        firstSubsongComment = getString(bb);
        
        numberOfAdditionalSubsongs = bb.get();
        additionalSubsongsReserved = getByteArray(bb, 3);
        subsongDataPointers = getIntArray(bb, numberOfAdditionalSubsongs);
        systemName = getString(bb);
        albumCategoryGameName = getString(bb);
        songNameJapanese = getString(bb);
        songAuthorJapanese = getString(bb);
        systemNameJapanese = getString(bb);
        albumCategoryGameNameJapanese = getString(bb);
        int numberofChips = findNumberOfChips();
        extraChipOutputSettings = getFloatArray(bb, numberofChips*3);
        
        patchbayConnectionCount = bb.getInt();
        patchbays = getIntArray(bb, patchbayConnectionCount);
        automaticPatchbay = bb.get();
        brokenPortamentoDuringLegato = bb.get();
        brokenMacroDuringNoteOffInSomeFmChips = bb.get();
        preNoteC64DoesNotCompensateForPortamentoOrLegato = bb.get();
        disableNewNesDpcmFeatures = bb.get();
        resetArpEffectPhaseOnNewNote = bb.get();
        linearVolumeScalingRoundsUp = bb.get();
        legacyAlwaysSetVolumeBehavior = bb.get();
        legacySampleOffsetEffect = bb.get();
        lengthOfSpeedPattern = bb.get();
        speedPattern = getByteArray(bb, 16);
        grooveListEntryNumber = bb.get();
        grooveEntries = getByteArray(bb, grooveListEntryNumber*17);
        instrumentDirectoriesPointer = bb.getInt();
        wavetableDirectoriesPointer = bb.getInt();
        sampleDirectoriesPointer = bb.getInt();    
        
    }

    private byte[] getByteArray(ByteBuffer bb, int length){
        return FurnaceFile.getByteArray(bb, length);
    }

    private int[] getIntArray(ByteBuffer bb, int length){
        return FurnaceFile.getIntArray(bb, length);
    }

    private float[] getFloatArray(ByteBuffer bb, int length){
        return FurnaceFile.getFloatArray(bb, length);
    }

    private String getString(ByteBuffer bb){
        return FurnaceFile.getString(bb);
    }

    private String getString(ByteBuffer bb, int length){
        return FurnaceFile.getString(bb, length);
    }
    
    private int findStringLength(ByteBuffer bb, int cursor){
        return FurnaceFile.findStringLength(bb, cursor);
    }

    private String[] getStringArray(ByteBuffer bb, int length){
        return FurnaceFile.getStringArray(bb, length);
    }

    public int findNumberOfChips(){
        for(int i=0;i<soundChips.length;i++){
            if(soundChips[i]==0){
                return i;
            }
        }
        return 32;
    }

    public String getBlockId() {
        return blockId;
    }

    public void setBlockId(String blockId) {
        this.blockId = blockId;
    }

    public int getBlockSize() {
        return blockSize;
    }

    public void setBlockSize(int blockSize) {
        this.blockSize = blockSize;
    }

    public byte getTimebase() {
        return timebase;
    }

    public void setTimebase(byte timebase) {
        this.timebase = timebase;
    }

    public byte getSpeed1() {
        return speed1;
    }

    public void setSpeed1(byte speed1) {
        this.speed1 = speed1;
    }

    public byte getSpeed2() {
        return speed2;
    }

    public void setSpeed2(byte speed2) {
        this.speed2 = speed2;
    }

    public byte getInitialArpeggioTime() {
        return initialArpeggioTime;
    }

    public void setInitialArpeggioTime(byte initialArpeggioTime) {
        this.initialArpeggioTime = initialArpeggioTime;
    }

    public float getTicksPerSecond() {
        return ticksPerSecond;
    }

    public void setTicksPerSecond(float ticksPerSecond) {
        this.ticksPerSecond = ticksPerSecond;
    }

    public short getPatternLength() {
        return patternLength;
    }

    public void setPatternLength(short patternLength) {
        this.patternLength = patternLength;
    }

    public short getOrdersLength() {
        return ordersLength;
    }

    public void setOrdersLength(short ordersLength) {
        this.ordersLength = ordersLength;
    }

    public byte getHighlightA() {
        return highlightA;
    }

    public void setHighlightA(byte highlightA) {
        this.highlightA = highlightA;
    }

    public byte getHighlightB() {
        return highlightB;
    }

    public void setHighlightB(byte highlightB) {
        this.highlightB = highlightB;
    }

    public short getInstrumentCount() {
        return instrumentCount;
    }

    public void setInstrumentCount(short instrumentCount) {
        this.instrumentCount = instrumentCount;
    }

    public short getWavetableCount() {
        return wavetableCount;
    }

    public void setWavetableCount(short wavetableCount) {
        this.wavetableCount = wavetableCount;
    }

    public short getSampleCount() {
        return sampleCount;
    }

    public void setSampleCount(short sampleCount) {
        this.sampleCount = sampleCount;
    }

    public int getPatternCount() {
        return patternCount;
    }

    public void setPatternCount(int patternCount) {
        this.patternCount = patternCount;
    }

    public byte[] getSoundChips() {
        return soundChips;
    }

    public void setSoundChips(byte[] soundChips) {
        this.soundChips = soundChips;
    }

    public byte[] getSoundChipVolumes() {
        return soundChipVolumes;
    }

    public void setSoundChipVolumes(byte[] soundChipVolumes) {
        this.soundChipVolumes = soundChipVolumes;
    }

    public byte[] getSoundChipPanning() {
        return soundChipPanning;
    }

    public void setSoundChipPanning(byte[] soundChipPanning) {
        this.soundChipPanning = soundChipPanning;
    }

    public int[] getSoundChipFlagPointers() {
        return soundChipFlagPointers;
    }

    public void setSoundChipFlagPointers(int[] soundChipFlagPointers) {
        this.soundChipFlagPointers = soundChipFlagPointers;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSongAuthor() {
        return songAuthor;
    }

    public void setSongAuthor(String songAuthor) {
        this.songAuthor = songAuthor;
    }

    public float getA4Tuning() {
        return a4Tuning;
    }

    public void setA4Tuning(float a4Tuning) {
        this.a4Tuning = a4Tuning;
    }

    public byte getLimitSlides() {
        return limitSlides;
    }

    public void setLimitSlides(byte limitSlides) {
        this.limitSlides = limitSlides;
    }

    public byte getLinearPitch() {
        return linearPitch;
    }

    public void setLinearPitch(byte linearPitch) {
        this.linearPitch = linearPitch;
    }

    public byte getLoopModality() {
        return loopModality;
    }

    public void setLoopModality(byte loopModality) {
        this.loopModality = loopModality;
    }

    public byte getProperNoiseLayout() {
        return properNoiseLayout;
    }

    public void setProperNoiseLayout(byte properNoiseLayout) {
        this.properNoiseLayout = properNoiseLayout;
    }

    public byte getWaveDutyIsVolume() {
        return waveDutyIsVolume;
    }

    public void setWaveDutyIsVolume(byte waveDutyIsVolume) {
        this.waveDutyIsVolume = waveDutyIsVolume;
    }

    public byte getResetMacroOnPorta() {
        return resetMacroOnPorta;
    }

    public void setResetMacroOnPorta(byte resetMacroOnPorta) {
        this.resetMacroOnPorta = resetMacroOnPorta;
    }

    public byte getLegacyVolumeSlides() {
        return legacyVolumeSlides;
    }

    public void setLegacyVolumeSlides(byte legacyVolumeSlides) {
        this.legacyVolumeSlides = legacyVolumeSlides;
    }

    public byte getCompatibleArpeggio() {
        return compatibleArpeggio;
    }

    public void setCompatibleArpeggio(byte compatibleArpeggio) {
        this.compatibleArpeggio = compatibleArpeggio;
    }

    public byte getNoteOffResetsSlides() {
        return noteOffResetsSlides;
    }

    public void setNoteOffResetsSlides(byte noteOffResetsSlides) {
        this.noteOffResetsSlides = noteOffResetsSlides;
    }

    public byte getTargetResetsSlides() {
        return targetResetsSlides;
    }

    public void setTargetResetsSlides(byte targetResetsSlides) {
        this.targetResetsSlides = targetResetsSlides;
    }

    public byte getArpeggioInhibitsPortamento() {
        return arpeggioInhibitsPortamento;
    }

    public void setArpeggioInhibitsPortamento(byte arpeggioInhibitsPortamento) {
        this.arpeggioInhibitsPortamento = arpeggioInhibitsPortamento;
    }

    public byte getWackAlgorithmMacro() {
        return wackAlgorithmMacro;
    }

    public void setWackAlgorithmMacro(byte wackAlgorithmMacro) {
        this.wackAlgorithmMacro = wackAlgorithmMacro;
    }

    public byte getBrokenShortcutSlides() {
        return brokenShortcutSlides;
    }

    public void setBrokenShortcutSlides(byte brokenShortcutSlides) {
        this.brokenShortcutSlides = brokenShortcutSlides;
    }

    public byte getIgnoreDuplicateSlides() {
        return ignoreDuplicateSlides;
    }

    public void setIgnoreDuplicateSlides(byte ignoreDuplicateSlides) {
        this.ignoreDuplicateSlides = ignoreDuplicateSlides;
    }

    public byte getStopPortamentoOnNoteOff() {
        return stopPortamentoOnNoteOff;
    }

    public void setStopPortamentoOnNoteOff(byte stopPortamentoOnNoteOff) {
        this.stopPortamentoOnNoteOff = stopPortamentoOnNoteOff;
    }

    public byte getContinuousVibrato() {
        return continuousVibrato;
    }

    public void setContinuousVibrato(byte continuousVibrato) {
        this.continuousVibrato = continuousVibrato;
    }

    public byte getBrokenDacMode() {
        return brokenDacMode;
    }

    public void setBrokenDacMode(byte brokenDacMode) {
        this.brokenDacMode = brokenDacMode;
    }

    public byte getOneTickCut() {
        return oneTickCut;
    }

    public void setOneTickCut(byte oneTickCut) {
        this.oneTickCut = oneTickCut;
    }

    public byte getInstrumentChangeAllowedDuringPorta() {
        return instrumentChangeAllowedDuringPorta;
    }

    public void setInstrumentChangeAllowedDuringPorta(byte instrumentChangeAllowedDuringPorta) {
        this.instrumentChangeAllowedDuringPorta = instrumentChangeAllowedDuringPorta;
    }

    public byte getResetNoteBaseOnArpeggioEffectStop() {
        return resetNoteBaseOnArpeggioEffectStop;
    }

    public void setResetNoteBaseOnArpeggioEffectStop(byte resetNoteBaseOnArpeggioEffectStop) {
        this.resetNoteBaseOnArpeggioEffectStop = resetNoteBaseOnArpeggioEffectStop;
    }

    public int[] getInstrumentPointers() {
        return instrumentPointers;
    }

    public void setInstrumentPointers(int[] instrumentPointers) {
        this.instrumentPointers = instrumentPointers;
    }

    public int[] getWavetablePointers() {
        return wavetablePointers;
    }

    public void setWavetablePointers(int[] wavetablePointers) {
        this.wavetablePointers = wavetablePointers;
    }

    public int[] getSamplePointers() {
        return samplePointers;
    }

    public void setSamplePointers(int[] samplePointers) {
        this.samplePointers = samplePointers;
    }

    public int[] getPatternPointers() {
        return patternPointers;
    }

    public void setPatternPointers(int[] patternPointers) {
        this.patternPointers = patternPointers;
    }

    public byte[] getOrders() {
        return orders;
    }

    public void setOrders(byte[] orders) {
        this.orders = orders;
    }

    public byte[] getEffectColumns() {
        return effectColumns;
    }

    public void setEffectColumns(byte[] effectColumns) {
        this.effectColumns = effectColumns;
    }

    public byte[] getChannelHideStatus() {
        return channelHideStatus;
    }

    public void setChannelHideStatus(byte[] channelHideStatus) {
        this.channelHideStatus = channelHideStatus;
    }

    public byte[] getChannelCollapseStatus() {
        return channelCollapseStatus;
    }

    public void setChannelCollapseStatus(byte[] channelCollapseStatus) {
        this.channelCollapseStatus = channelCollapseStatus;
    }

    public String[] getChannelNames() {
        return channelNames;
    }

    public void setChannelNames(String[] channelNames) {
        this.channelNames = channelNames;
    }

    public String[] getChannelShortNames() {
        return channelShortNames;
    }

    public void setChannelShortNames(String[] channelShortNames) {
        this.channelShortNames = channelShortNames;
    }

    public String getSongComment() {
        return songComment;
    }

    public void setSongComment(String songComment) {
        this.songComment = songComment;
    }

    public float getMasterVolume() {
        return masterVolume;
    }

    public void setMasterVolume(float masterVolume) {
        this.masterVolume = masterVolume;
    }

    public byte getBrokenSpeedSelection() {
        return brokenSpeedSelection;
    }

    public void setBrokenSpeedSelection(byte brokenSpeedSelection) {
        this.brokenSpeedSelection = brokenSpeedSelection;
    }

    public byte getNoSlidesOnFirstTick() {
        return noSlidesOnFirstTick;
    }

    public void setNoSlidesOnFirstTick(byte noSlidesOnFirstTick) {
        this.noSlidesOnFirstTick = noSlidesOnFirstTick;
    }

    public byte getNextRowResetArpPos() {
        return nextRowResetArpPos;
    }

    public void setNextRowResetArpPos(byte nextRowResetArpPos) {
        this.nextRowResetArpPos = nextRowResetArpPos;
    }

    public byte getIgnoreJumpAtEnd() {
        return ignoreJumpAtEnd;
    }

    public void setIgnoreJumpAtEnd(byte ignoreJumpAtEnd) {
        this.ignoreJumpAtEnd = ignoreJumpAtEnd;
    }

    public byte getBuggyPortamentoAfterSlide() {
        return buggyPortamentoAfterSlide;
    }

    public void setBuggyPortamentoAfterSlide(byte buggyPortamentoAfterSlide) {
        this.buggyPortamentoAfterSlide = buggyPortamentoAfterSlide;
    }

    public byte getNewInsAffectsEnveloppeGB() {
        return newInsAffectsEnveloppeGB;
    }

    public void setNewInsAffectsEnveloppeGB(byte newInsAffectsEnveloppeGB) {
        this.newInsAffectsEnveloppeGB = newInsAffectsEnveloppeGB;
    }

    public byte getExtChStateIsShared() {
        return extChStateIsShared;
    }

    public void setExtChStateIsShared(byte extChStateIsShared) {
        this.extChStateIsShared = extChStateIsShared;
    }

    public byte getIgnoreDacModeChangeOutsideOfIntendedChannel() {
        return ignoreDacModeChangeOutsideOfIntendedChannel;
    }

    public void setIgnoreDacModeChangeOutsideOfIntendedChannel(byte ignoreDacModeChangeOutsideOfIntendedChannel) {
        this.ignoreDacModeChangeOutsideOfIntendedChannel = ignoreDacModeChangeOutsideOfIntendedChannel;
    }

    public byte getE1xyAnde2xyAlsoTakePriorityOverSlide00() {
        return e1xyAnde2xyAlsoTakePriorityOverSlide00;
    }

    public void setE1xyAnde2xyAlsoTakePriorityOverSlide00(byte e1xyAnde2xyAlsoTakePriorityOverSlide00) {
        this.e1xyAnde2xyAlsoTakePriorityOverSlide00 = e1xyAnde2xyAlsoTakePriorityOverSlide00;
    }

    public byte getNewSegaPcm() {
        return newSegaPcm;
    }

    public void setNewSegaPcm(byte newSegaPcm) {
        this.newSegaPcm = newSegaPcm;
    }

    public byte getWeirdFnumBlockBasedChipPitchSlides() {
        return weirdFnumBlockBasedChipPitchSlides;
    }

    public void setWeirdFnumBlockBasedChipPitchSlides(byte weirdFnumBlockBasedChipPitchSlides) {
        this.weirdFnumBlockBasedChipPitchSlides = weirdFnumBlockBasedChipPitchSlides;
    }

    public byte getSnDutyMacroAlwaysResetsPhase() {
        return snDutyMacroAlwaysResetsPhase;
    }

    public void setSnDutyMacroAlwaysResetsPhase(byte snDutyMacroAlwaysResetsPhase) {
        this.snDutyMacroAlwaysResetsPhase = snDutyMacroAlwaysResetsPhase;
    }

    public byte getPitchMacroIsLinear() {
        return pitchMacroIsLinear;
    }

    public void setPitchMacroIsLinear(byte pitchMacroIsLinear) {
        this.pitchMacroIsLinear = pitchMacroIsLinear;
    }

    public byte getPitchSlideSpeedInFullLinearPitchMode() {
        return pitchSlideSpeedInFullLinearPitchMode;
    }

    public void setPitchSlideSpeedInFullLinearPitchMode(byte pitchSlideSpeedInFullLinearPitchMode) {
        this.pitchSlideSpeedInFullLinearPitchMode = pitchSlideSpeedInFullLinearPitchMode;
    }

    public byte getOldOctaveBoundaryBehaviour() {
        return oldOctaveBoundaryBehaviour;
    }

    public void setOldOctaveBoundaryBehaviour(byte oldOctaveBoundaryBehaviour) {
        this.oldOctaveBoundaryBehaviour = oldOctaveBoundaryBehaviour;
    }

    public byte getDisableOpn2DacVolumeControl() {
        return disableOpn2DacVolumeControl;
    }

    public void setDisableOpn2DacVolumeControl(byte disableOpn2DacVolumeControl) {
        this.disableOpn2DacVolumeControl = disableOpn2DacVolumeControl;
    }

    public byte getNewVolumeScalingStrategy() {
        return newVolumeScalingStrategy;
    }

    public void setNewVolumeScalingStrategy(byte newVolumeScalingStrategy) {
        this.newVolumeScalingStrategy = newVolumeScalingStrategy;
    }

    public byte getVolumeMacroStillAppliesAfterEnd() {
        return volumeMacroStillAppliesAfterEnd;
    }

    public void setVolumeMacroStillAppliesAfterEnd(byte volumeMacroStillAppliesAfterEnd) {
        this.volumeMacroStillAppliesAfterEnd = volumeMacroStillAppliesAfterEnd;
    }

    public byte getBrokenOutVol() {
        return brokenOutVol;
    }

    public void setBrokenOutVol(byte brokenOutVol) {
        this.brokenOutVol = brokenOutVol;
    }

    public byte getE1xyAnde2xyStopOnSameNote() {
        return e1xyAnde2xyStopOnSameNote;
    }

    public void setE1xyAnde2xyStopOnSameNote(byte e1xyAnde2xyStopOnSameNote) {
        this.e1xyAnde2xyStopOnSameNote = e1xyAnde2xyStopOnSameNote;
    }

    public byte getBrokenInitialPositionOfPortaAfterArp() {
        return brokenInitialPositionOfPortaAfterArp;
    }

    public void setBrokenInitialPositionOfPortaAfterArp(byte brokenInitialPositionOfPortaAfterArp) {
        this.brokenInitialPositionOfPortaAfterArp = brokenInitialPositionOfPortaAfterArp;
    }

    public byte getSnPeriodsUnder8AreTreatedAs1() {
        return snPeriodsUnder8AreTreatedAs1;
    }

    public void setSnPeriodsUnder8AreTreatedAs1(byte snPeriodsUnder8AreTreatedAs1) {
        this.snPeriodsUnder8AreTreatedAs1 = snPeriodsUnder8AreTreatedAs1;
    }

    public byte getCutDelayEffectPolicy() {
        return cutDelayEffectPolicy;
    }

    public void setCutDelayEffectPolicy(byte cutDelayEffectPolicy) {
        this.cutDelayEffectPolicy = cutDelayEffectPolicy;
    }

    public byte getEffect0b0dtreatment() {
        return effect0b0dtreatment;
    }

    public void setEffect0b0dtreatment(byte effect0b0dtreatment) {
        this.effect0b0dtreatment = effect0b0dtreatment;
    }

    public byte getAutomaticSystemNameDetection() {
        return automaticSystemNameDetection;
    }

    public void setAutomaticSystemNameDetection(byte automaticSystemNameDetection) {
        this.automaticSystemNameDetection = automaticSystemNameDetection;
    }

    public byte getDisableSampleMacro() {
        return disableSampleMacro;
    }

    public void setDisableSampleMacro(byte disableSampleMacro) {
        this.disableSampleMacro = disableSampleMacro;
    }

    public byte getBrokenOutVolEpisode2() {
        return brokenOutVolEpisode2;
    }

    public void setBrokenOutVolEpisode2(byte brokenOutVolEpisode2) {
        this.brokenOutVolEpisode2 = brokenOutVolEpisode2;
    }

    public byte getOldArpeggioStrategy() {
        return oldArpeggioStrategy;
    }

    public void setOldArpeggioStrategy(byte oldArpeggioStrategy) {
        this.oldArpeggioStrategy = oldArpeggioStrategy;
    }

    public short getVirtualTempoNumerator() {
        return virtualTempoNumerator;
    }

    public void setVirtualTempoNumerator(short virtualTempoNumerator) {
        this.virtualTempoNumerator = virtualTempoNumerator;
    }

    public short getVirtualTempoDenominator() {
        return virtualTempoDenominator;
    }

    public void setVirtualTempoDenominator(short virtualTempoDenominator) {
        this.virtualTempoDenominator = virtualTempoDenominator;
    }

    public String getFirstSubsongName() {
        return firstSubsongName;
    }

    public void setFirstSubsongName(String firstSubsongName) {
        this.firstSubsongName = firstSubsongName;
    }

    public String getFirstSubsongComment() {
        return firstSubsongComment;
    }

    public void setFirstSubsongComment(String firstSubsongComment) {
        this.firstSubsongComment = firstSubsongComment;
    }

    public byte getNumberOfAdditionalSubsongs() {
        return numberOfAdditionalSubsongs;
    }

    public void setNumberOfAdditionalSubsongs(byte numberOfAdditionalSubsongs) {
        this.numberOfAdditionalSubsongs = numberOfAdditionalSubsongs;
    }

    public byte[] getAdditionalSubsongsReserved() {
        return additionalSubsongsReserved;
    }

    public void setAdditionalSubsongsReserved(byte[] additionalSubsongsReserved) {
        this.additionalSubsongsReserved = additionalSubsongsReserved;
    }

    public int[] getSubsongDataPointers() {
        return subsongDataPointers;
    }

    public void setSubsongDataPointers(int[] subsongDataPointers) {
        this.subsongDataPointers = subsongDataPointers;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getAlbumCategoryGameName() {
        return albumCategoryGameName;
    }

    public void setAlbumCategoryGameName(String albumCategoryGameName) {
        this.albumCategoryGameName = albumCategoryGameName;
    }

    public String getSongNameJapanese() {
        return songNameJapanese;
    }

    public void setSongNameJapanese(String songNameJapanese) {
        this.songNameJapanese = songNameJapanese;
    }

    public String getSongAuthorJapanese() {
        return songAuthorJapanese;
    }

    public void setSongAuthorJapanese(String songAuthorJapanese) {
        this.songAuthorJapanese = songAuthorJapanese;
    }

    public String getSystemNameJapanese() {
        return systemNameJapanese;
    }

    public void setSystemNameJapanese(String systemNameJapanese) {
        this.systemNameJapanese = systemNameJapanese;
    }

    public String getAlbumCategoryGameNameJapanese() {
        return albumCategoryGameNameJapanese;
    }

    public void setAlbumCategoryGameNameJapanese(String albumCategoryGameNameJapanese) {
        this.albumCategoryGameNameJapanese = albumCategoryGameNameJapanese;
    }

    public float[] getExtraChipOutputSettings() {
        return extraChipOutputSettings;
    }

    public void setExtraChipOutputSettings(float[] extraChipOutputSettings) {
        this.extraChipOutputSettings = extraChipOutputSettings;
    }

    public int getPatchbayConnectionCount() {
        return patchbayConnectionCount;
    }

    public void setPatchbayConnectionCount(int patchbayConnectionCount) {
        this.patchbayConnectionCount = patchbayConnectionCount;
    }

    public int[] getPatchbays() {
        return patchbays;
    }

    public void setPatchbays(int[] patchbays) {
        this.patchbays = patchbays;
    }

    public byte getAutomaticPatchbay() {
        return automaticPatchbay;
    }

    public void setAutomaticPatchbay(byte automaticPatchbay) {
        this.automaticPatchbay = automaticPatchbay;
    }

    public byte getBrokenPortamentoDuringLegato() {
        return brokenPortamentoDuringLegato;
    }

    public void setBrokenPortamentoDuringLegato(byte brokenPortamentoDuringLegato) {
        this.brokenPortamentoDuringLegato = brokenPortamentoDuringLegato;
    }

    public byte getBrokenMacroDuringNoteOffInSomeFmChips() {
        return brokenMacroDuringNoteOffInSomeFmChips;
    }

    public void setBrokenMacroDuringNoteOffInSomeFmChips(byte brokenMacroDuringNoteOffInSomeFmChips) {
        this.brokenMacroDuringNoteOffInSomeFmChips = brokenMacroDuringNoteOffInSomeFmChips;
    }

    public byte getPreNoteC64DoesNotCompensateForPortamentoOrLegato() {
        return preNoteC64DoesNotCompensateForPortamentoOrLegato;
    }

    public void setPreNoteC64DoesNotCompensateForPortamentoOrLegato(byte preNoteC64DoesNotCompensateForPortamentoOrLegato) {
        this.preNoteC64DoesNotCompensateForPortamentoOrLegato = preNoteC64DoesNotCompensateForPortamentoOrLegato;
    }

    public byte getDisableNewNesDpcmFeatures() {
        return disableNewNesDpcmFeatures;
    }

    public void setDisableNewNesDpcmFeatures(byte disableNewNesDpcmFeatures) {
        this.disableNewNesDpcmFeatures = disableNewNesDpcmFeatures;
    }

    public byte getResetArpEffectPhaseOnNewNote() {
        return resetArpEffectPhaseOnNewNote;
    }

    public void setResetArpEffectPhaseOnNewNote(byte resetArpEffectPhaseOnNewNote) {
        this.resetArpEffectPhaseOnNewNote = resetArpEffectPhaseOnNewNote;
    }

    public byte getLinearVolumeScalingRoundsUp() {
        return linearVolumeScalingRoundsUp;
    }

    public void setLinearVolumeScalingRoundsUp(byte linearVolumeScalingRoundsUp) {
        this.linearVolumeScalingRoundsUp = linearVolumeScalingRoundsUp;
    }

    public byte getLegacyAlwaysSetVolumeBehavior() {
        return legacyAlwaysSetVolumeBehavior;
    }

    public void setLegacyAlwaysSetVolumeBehavior(byte legacyAlwaysSetVolumeBehavior) {
        this.legacyAlwaysSetVolumeBehavior = legacyAlwaysSetVolumeBehavior;
    }

    public byte getLegacySampleOffsetEffect() {
        return legacySampleOffsetEffect;
    }

    public void setLegacySampleOffsetEffect(byte legacySampleOffsetEffect) {
        this.legacySampleOffsetEffect = legacySampleOffsetEffect;
    }

    public byte getLengthOfSpeedPattern() {
        return lengthOfSpeedPattern;
    }

    public void setLengthOfSpeedPattern(byte lengthOfSpeedPattern) {
        this.lengthOfSpeedPattern = lengthOfSpeedPattern;
    }

    public byte[] getSpeedPattern() {
        return speedPattern;
    }

    public void setSpeedPattern(byte[] speedPattern) {
        this.speedPattern = speedPattern;
    }

    public byte getGrooveListEntryNumber() {
        return grooveListEntryNumber;
    }

    public void setGrooveListEntryNumber(byte grooveListEntryNumber) {
        this.grooveListEntryNumber = grooveListEntryNumber;
    }

    public byte[] getGrooveEntries() {
        return grooveEntries;
    }

    public void setGrooveEntries(byte[] grooveEntries) {
        this.grooveEntries = grooveEntries;
    }

    public int getInstrumentDirectoriesPointer() {
        return instrumentDirectoriesPointer;
    }

    public void setInstrumentDirectoriesPointer(int instrumentDirectoriesPointer) {
        this.instrumentDirectoriesPointer = instrumentDirectoriesPointer;
    }

    public int getWavetableDirectoriesPointer() {
        return wavetableDirectoriesPointer;
    }

    public void setWavetableDirectoriesPointer(int wavetableDirectoriesPointer) {
        this.wavetableDirectoriesPointer = wavetableDirectoriesPointer;
    }

    public int getSampleDirectoriesPointer() {
        return sampleDirectoriesPointer;
    }

    public void setSampleDirectoriesPointer(int sampleDirectoriesPointer) {
        this.sampleDirectoriesPointer = sampleDirectoriesPointer;
    }
    
    public byte[] toByteArray(){
        ByteBuffer bb = ByteBuffer.allocate(findLength());
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.position(0);
        bb.put(blockId.getBytes(StandardCharsets.UTF_8));
        bb.putInt(findLength()-4-4);
        bb.put(timebase);
        bb.put(speed1);
        bb.put(speed2);
        bb.put(initialArpeggioTime);
        bb.putFloat(ticksPerSecond);
        bb.putShort(patternLength);
        bb.putShort(ordersLength);
        bb.put(highlightA);
        bb.put(highlightB);
        bb.putShort(instrumentCount);
        bb.putShort(wavetableCount);
        bb.putShort(sampleCount);
        bb.putInt(patternCount);
        bb.put(soundChips);
        bb.put(soundChipVolumes);
        bb.put(soundChipPanning);
        bb.put(toByteArray(soundChipFlagPointers));
        bb.put(songName.getBytes(StandardCharsets.UTF_8));
        bb.put((byte)0);
        bb.put(songAuthor.getBytes(StandardCharsets.UTF_8));
        bb.put((byte)0);
        bb.putFloat(a4Tuning);
        bb.put(limitSlides);
        bb.put(linearPitch);
        bb.put(loopModality);
        bb.put(properNoiseLayout);
        bb.put(waveDutyIsVolume);
        bb.put(resetMacroOnPorta);
        bb.put(legacyVolumeSlides);
        bb.put(compatibleArpeggio);
        bb.put(noteOffResetsSlides);
        bb.put(targetResetsSlides);
        bb.put(arpeggioInhibitsPortamento);
        bb.put(wackAlgorithmMacro);
        bb.put(brokenShortcutSlides);
        bb.put(ignoreDuplicateSlides);
        bb.put(stopPortamentoOnNoteOff);
        bb.put(continuousVibrato);
        bb.put(brokenDacMode);
        bb.put(oneTickCut);
        bb.put(instrumentChangeAllowedDuringPorta);
        bb.put(resetNoteBaseOnArpeggioEffectStop);
        bb.put(toByteArray(instrumentPointers));
        bb.put(toByteArray(wavetablePointers));
        bb.put(toByteArray(samplePointers));
        bb.put(toByteArray(patternPointers));
        bb.put(orders);
        bb.put(effectColumns);
        bb.put(channelHideStatus);
        bb.put(channelCollapseStatus);
        bb.put(toByteArray(channelNames));
        bb.put(toByteArray(channelShortNames));
        bb.put(songComment.getBytes(StandardCharsets.UTF_8));
        bb.put((byte)0);
        bb.putFloat(masterVolume);
        bb.put(brokenSpeedSelection);
        bb.put(noSlidesOnFirstTick);
        bb.put(nextRowResetArpPos);
        bb.put(ignoreJumpAtEnd);
        bb.put(buggyPortamentoAfterSlide);
        bb.put(newInsAffectsEnveloppeGB);
        bb.put(extChStateIsShared);
        bb.put(ignoreDacModeChangeOutsideOfIntendedChannel);
        bb.put(e1xyAnde2xyAlsoTakePriorityOverSlide00);
        bb.put(newSegaPcm);
        bb.put(weirdFnumBlockBasedChipPitchSlides);
        bb.put(snDutyMacroAlwaysResetsPhase);
        bb.put(pitchMacroIsLinear);
        bb.put(pitchSlideSpeedInFullLinearPitchMode);
        bb.put(oldOctaveBoundaryBehaviour);
        bb.put(disableOpn2DacVolumeControl);
        bb.put(newVolumeScalingStrategy);
        bb.put(volumeMacroStillAppliesAfterEnd);
        bb.put(brokenOutVol);
        bb.put(e1xyAnde2xyStopOnSameNote);
        bb.put(brokenInitialPositionOfPortaAfterArp);
        bb.put(snPeriodsUnder8AreTreatedAs1);
        bb.put(cutDelayEffectPolicy);
        bb.put(effect0b0dtreatment);
        bb.put(automaticSystemNameDetection);
        bb.put(disableSampleMacro);
        bb.put(brokenOutVolEpisode2);
        bb.put(oldArpeggioStrategy);
        bb.putShort(virtualTempoNumerator);
        bb.putShort(virtualTempoDenominator);
        bb.put(firstSubsongName.getBytes(StandardCharsets.UTF_8));
        bb.put((byte)0);
        bb.put(firstSubsongComment.getBytes(StandardCharsets.UTF_8));
        bb.put((byte)0);
        bb.put(numberOfAdditionalSubsongs);
        bb.put(additionalSubsongsReserved);
        bb.put(toByteArray(subsongDataPointers));
        bb.put(systemName.getBytes(StandardCharsets.UTF_8));
        bb.put((byte)0);
        bb.put(albumCategoryGameName.getBytes(StandardCharsets.UTF_8));
        bb.put((byte)0);
        bb.put(songNameJapanese.getBytes(StandardCharsets.UTF_8));
        bb.put((byte)0);
        bb.put(songAuthorJapanese.getBytes(StandardCharsets.UTF_8));
        bb.put((byte)0);
        bb.put(systemNameJapanese.getBytes(StandardCharsets.UTF_8));
        bb.put((byte)0);
        bb.put(albumCategoryGameNameJapanese.getBytes(StandardCharsets.UTF_8));
        bb.put((byte)0);
        for(int i=0;i<findNumberOfChips();i++){
            bb.putFloat(extraChipOutputSettings[3*i+0]);
            bb.putFloat(extraChipOutputSettings[3*i+1]);
            bb.putFloat(extraChipOutputSettings[3*i+2]);
        }
        bb.putInt(patchbayConnectionCount);
        bb.put(toByteArray(patchbays));
        bb.put(automaticPatchbay);
        bb.put(brokenPortamentoDuringLegato);
        bb.put(brokenMacroDuringNoteOffInSomeFmChips);
        bb.put(preNoteC64DoesNotCompensateForPortamentoOrLegato);
        bb.put(disableNewNesDpcmFeatures);
        bb.put(resetArpEffectPhaseOnNewNote);
        bb.put(linearVolumeScalingRoundsUp);
        bb.put(legacyAlwaysSetVolumeBehavior);
        bb.put(legacySampleOffsetEffect);
        bb.put(lengthOfSpeedPattern);
        bb.put(speedPattern);
        bb.put(grooveListEntryNumber);
        bb.put(grooveEntries);
        bb.putInt(instrumentDirectoriesPointer);
        bb.putInt(wavetableDirectoriesPointer);
        bb.putInt(sampleDirectoriesPointer);        
        return bb.array();
    }
    
    private int getStringArrayLength(String[] stringArray){
        return FurnaceFile.getStringArrayLength(stringArray);
    }
    
    private byte[] toByteArray(int[] intArray){
        return FurnaceFile.toByteArray(intArray);
    }
    
    private byte[] toByteArray(String[] stringArray){
        return FurnaceFile.toByteArray(stringArray);
    }
    
    public int findLength(){
        return 4+4+1+1+1+1+4+2+2+1+1+2+2+2+4+32+32+32+128
                +songName.length()+1
                +songAuthor.length()+1
                +4+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1
                +4*instrumentCount+4*wavetableCount+4*sampleCount+4*patternCount
                +10*ordersLength+10+10+10
                +getStringArrayLength(channelNames)
                +getStringArrayLength(channelShortNames)
                +songComment.length()+1
                +4
                +1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+2+2
                +firstSubsongName.length()+1
                +firstSubsongComment.length()+1
                +1+3+4*numberOfAdditionalSubsongs
                +systemName.length()+1
                +albumCategoryGameName.length()+1
                +songNameJapanese.length()+1
                +songAuthorJapanese.length()+1
                +systemNameJapanese.length()+1
                +albumCategoryGameNameJapanese.length()+1
                +4*3*findNumberOfChips()
                +4+4*patchbayConnectionCount+1
                +1+1+1+1+1+1+1+1
                +1+16
                +1+17*grooveListEntryNumber
                +4+4+4;
    }
    
}
