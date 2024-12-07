/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.sound.convert.io.furnace.file.section;

import com.sfc.sf2.sound.convert.io.furnace.file.FurnaceFile;
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
    private byte speed1 = 0;
    private byte speed2 = 0;
    private byte initialArpeggioTime = 0;
    private float ticksPerSecond = 60;
    private short patternLength = 256;
    private short ordersLength = 1;
    private byte highlightA = 0;
    private byte highlightB = 0;
    private short instrumentCount = 80;
    private short wavetableCount = 0;
    private short sampleCount = 12;
    private int patternCount = 10;
    private byte[] soundChips = new byte[32];
    private byte[] soundChipVolumes = new byte[32];
    private byte[] soundChipPanning = new byte[32];
    private int[] soundChipFlagPointers = new int[32];
    private String songName = "";
    private String songAuthor = "";
    private float a4Tuning = 0;
    private byte limitSlides = 0;
    private byte linearPitch = 0;
    private byte loopModality = 0;
    private byte properNoiseLayout = 0;
    private byte waveDutyIsVolume = 0;
    private byte resetMacroOnPorta = 0;
    private byte legacyVolumeSlides = 0;
    private byte compatibleArpeggio = 0;
    private byte noteOffResetsSlides = 0;
    private byte targetResetsSlides = 0;
    private byte arpeggioInhibitsPortamento = 0;
    private byte wackAlgorithmMacro = 0;
    private byte brokenShortcutSlides = 0;
    private byte ignoreDuplicateSlides = 0;
    private byte stopPortamentoOnNoteOff = 0;
    private byte continuousVibrato = 0;
    private byte brokenDacMode = 0;
    private byte oneTickCut = 0;
    private byte instrumentChangeAllowedDuringPorta = 0;
    private byte resetNoteBaseOnArpeggioEffectStop = 0;
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
    private String songComment = "";
    private float masterVolume = 1;
    private byte brokenSpeedSelection = 0;
    private byte noSlidesOnFirstTick = 0;
    private byte nextRowResetArpPos = 0;
    private byte ignoreJumpAtEnd = 0;
    private byte buggyPortamentoAfterSlide = 0;
    private byte newInsAffectsEnveloppeGB = 0;
    private byte extChStateIsShared = 0;
    private byte ignoreDacModeChangeOutsideOfIntendedChannel = 0;
    private byte e1xyAnde2xyAlsoTakePriorityOverSlide00 = 0;
    private byte newSegaPcm = 0;
    private byte weirdFnumBlockBasedChipPitchSlides = 0;
    private byte snDutyMacroAlwaysResetsPhase = 0;
    private byte pitchMacroIsLinear = 0;
    private byte pitchSlideSpeedInFullLinearPitchMode = 0;
    private byte oldOctaveBoundaryBehaviour = 0;
    private byte disableOpn2DacVolumeControl = 0;
    private byte newVolumeScalingStrategy = 0;
    private byte volumeMacroStillAppliesAfterEnd = 0;
    private byte brokenOutVol = 0;
    private byte e1xyAnde2xyStopOnSameNote = 0;
    private byte brokenInitialPositionOfPortaAfterArp = 0;
    private byte snPeriodsUnder8AreTreatedAs1 = 0;
    private byte cutDelayEffectPolicy = 0;
    private byte effect0b0dtreatment = 0;
    private byte automaticSystemNameDetection = 0;
    private byte disableSampleMacro = 0;
    private byte brokenOutVolEpisode2 = 0;
    private byte oldArpeggioStrategy = 0;
    private short virtualTempoNumerator = 0;
    private short virtualTempoDenominator = 0;
    private String firstSubsongName = "";
    private String firstSubsongComment = "";
    private byte numberOfAdditionalSubsongs = 0;
    private byte[] additionalSubsongsReserved = new byte[3];
    private int[] subsongDataPointers = new int[0];
    private String systemName = "";
    private String albumCategoryGameName = "";
    private String songNameJapanese = "";
    private String songAuthorJapanese = "";
    private String systemNameJapanese = "";
    private String albumCategoryGameNameJapanese = "";
    private float[] extraChipOutputSettings = new float[2*3];
    private int patchbayConnectionCount = 0;
    private int[] patchbays = new int[0];
    private byte automaticPatchbay = 0;
    private byte brokenPortamentoDuringLegato = 0;
    private byte brokenMacroDuringNoteOffInSomeFmChips = 0;
    private byte preNoteC64DoesNotCompensateForPortamentoOrLegato = 0;
    private byte disableNewNesDpcmFeatures = 0;
    private byte resetArpEffectPhaseOnNewNote = 0;
    private byte linearVolumeScalingRoundsUp = 0;
    private byte legacyAlwaysSetVolumeBehavior = 0;
    private byte legacySampleOffsetEffect = 0;
    private byte lengthOfSpeedPattern = 0;
    private byte[] speedPattern = new byte[16];
    private byte grooveListEntryNumber = 0;
    private byte[] grooveEntries = new byte[0];
    private int instrumentDirectoriesPointer = 0;
    private int wavetableDirectoriesPointer = 0;
    private int sampleDirectoriesPointer = 0;
    
    public SongInfoBlock(byte[] data, int startPointer){
        ByteBuffer SongInfoBlock = ByteBuffer.wrap(data, startPointer, data.length-startPointer);
        SongInfoBlock.order(ByteOrder.LITTLE_ENDIAN);
        SongInfoBlock.position(startPointer);
        blockId = getString(SongInfoBlock, 4);
        blockSize = SongInfoBlock.getInt();
        timebase = SongInfoBlock.get();
        speed1 = SongInfoBlock.get();
        speed2 = SongInfoBlock.get();
        initialArpeggioTime = SongInfoBlock.get();
        ticksPerSecond = SongInfoBlock.getFloat();
        patternLength = SongInfoBlock.getShort();
        ordersLength = SongInfoBlock.getShort();
        highlightA = SongInfoBlock.get();
        highlightB = SongInfoBlock.get();
        instrumentCount = SongInfoBlock.getShort();
        wavetableCount = SongInfoBlock.getShort();
        sampleCount = SongInfoBlock.getShort();
        patternCount = SongInfoBlock.getInt();
        soundChips = getByteArray(SongInfoBlock, 32);
        soundChipVolumes = getByteArray(SongInfoBlock, 32);
        soundChipPanning = getByteArray(SongInfoBlock, 32);
        soundChipFlagPointers = getIntArray(SongInfoBlock, 32);
        songName = getString(SongInfoBlock);
        songAuthor = getString(SongInfoBlock);
        a4Tuning = SongInfoBlock.getFloat();
        limitSlides = SongInfoBlock.get();
        linearPitch = SongInfoBlock.get();
        loopModality = SongInfoBlock.get();
        properNoiseLayout = SongInfoBlock.get();
        waveDutyIsVolume = SongInfoBlock.get();
        resetMacroOnPorta = SongInfoBlock.get();
        legacyVolumeSlides = SongInfoBlock.get();
        compatibleArpeggio = SongInfoBlock.get();
        noteOffResetsSlides = SongInfoBlock.get();
        targetResetsSlides = SongInfoBlock.get();
        arpeggioInhibitsPortamento = SongInfoBlock.get();
        wackAlgorithmMacro = SongInfoBlock.get();
        brokenShortcutSlides = SongInfoBlock.get();
        ignoreDuplicateSlides = SongInfoBlock.get();
        stopPortamentoOnNoteOff = SongInfoBlock.get();
        continuousVibrato = SongInfoBlock.get();
        brokenDacMode = SongInfoBlock.get();
        oneTickCut = SongInfoBlock.get();
        
        instrumentChangeAllowedDuringPorta = SongInfoBlock.get();
        resetNoteBaseOnArpeggioEffectStop = SongInfoBlock.get();
        instrumentPointers = getIntArray(SongInfoBlock, instrumentCount);
        
        wavetablePointers = getIntArray(SongInfoBlock, wavetableCount);
        samplePointers = getIntArray(SongInfoBlock, sampleCount);
        patternPointers = getIntArray(SongInfoBlock, patternCount);
        
        orders = getByteArray(SongInfoBlock, ordersLength*10);
        effectColumns = getByteArray(SongInfoBlock, 10);
        channelHideStatus = getByteArray(SongInfoBlock, 10);
        channelCollapseStatus = getByteArray(SongInfoBlock, 10);
        channelNames = getStringArray(SongInfoBlock, 10);
        channelShortNames = getStringArray(SongInfoBlock, 10);
        songComment = getString(SongInfoBlock);
        masterVolume = SongInfoBlock.getFloat();
        brokenSpeedSelection = SongInfoBlock.get();
        noSlidesOnFirstTick = SongInfoBlock.get();
        nextRowResetArpPos = SongInfoBlock.get();
        ignoreJumpAtEnd = SongInfoBlock.get();
        buggyPortamentoAfterSlide = SongInfoBlock.get();
        newInsAffectsEnveloppeGB = SongInfoBlock.get();
        extChStateIsShared = SongInfoBlock.get();
        ignoreDacModeChangeOutsideOfIntendedChannel = SongInfoBlock.get();
        e1xyAnde2xyAlsoTakePriorityOverSlide00 = SongInfoBlock.get();
        newSegaPcm = SongInfoBlock.get();
        weirdFnumBlockBasedChipPitchSlides = SongInfoBlock.get();
        snDutyMacroAlwaysResetsPhase = SongInfoBlock.get();
        pitchMacroIsLinear = SongInfoBlock.get();
        pitchSlideSpeedInFullLinearPitchMode = SongInfoBlock.get();
        oldOctaveBoundaryBehaviour = SongInfoBlock.get();
        disableOpn2DacVolumeControl = SongInfoBlock.get();
        newVolumeScalingStrategy = SongInfoBlock.get();
        volumeMacroStillAppliesAfterEnd = SongInfoBlock.get();
        brokenOutVol = SongInfoBlock.get();
        e1xyAnde2xyStopOnSameNote = SongInfoBlock.get();
        brokenInitialPositionOfPortaAfterArp = SongInfoBlock.get();
        snPeriodsUnder8AreTreatedAs1 = SongInfoBlock.get();
        cutDelayEffectPolicy = SongInfoBlock.get();
        effect0b0dtreatment = SongInfoBlock.get();
        automaticSystemNameDetection = SongInfoBlock.get();
        disableSampleMacro = SongInfoBlock.get();
        brokenOutVolEpisode2 = SongInfoBlock.get();
        oldArpeggioStrategy = SongInfoBlock.get();
        virtualTempoNumerator = SongInfoBlock.getShort();
        virtualTempoDenominator = SongInfoBlock.getShort();
        firstSubsongName = getString(SongInfoBlock);
        firstSubsongComment = getString(SongInfoBlock);
        
        numberOfAdditionalSubsongs = SongInfoBlock.get();
        additionalSubsongsReserved = getByteArray(SongInfoBlock, 3);
        subsongDataPointers = getIntArray(SongInfoBlock, numberOfAdditionalSubsongs);
        systemName = getString(SongInfoBlock);
        albumCategoryGameName = getString(SongInfoBlock);
        songNameJapanese = getString(SongInfoBlock);
        songAuthorJapanese = getString(SongInfoBlock);
        systemNameJapanese = getString(SongInfoBlock);
        albumCategoryGameNameJapanese = getString(SongInfoBlock);
        int numberofChips = findNumberOfChips();
        extraChipOutputSettings = getFloatArray(SongInfoBlock, numberofChips*3);
        
        patchbayConnectionCount = SongInfoBlock.getInt();
        patchbays = getIntArray(SongInfoBlock, patchbayConnectionCount);
        automaticPatchbay = SongInfoBlock.get();
        brokenPortamentoDuringLegato = SongInfoBlock.get();
        brokenMacroDuringNoteOffInSomeFmChips = SongInfoBlock.get();
        preNoteC64DoesNotCompensateForPortamentoOrLegato = SongInfoBlock.get();
        disableNewNesDpcmFeatures = SongInfoBlock.get();
        resetArpEffectPhaseOnNewNote = SongInfoBlock.get();
        linearVolumeScalingRoundsUp = SongInfoBlock.get();
        legacyAlwaysSetVolumeBehavior = SongInfoBlock.get();
        legacySampleOffsetEffect = SongInfoBlock.get();
        lengthOfSpeedPattern = SongInfoBlock.get();
        speedPattern = getByteArray(SongInfoBlock, 16);
        grooveListEntryNumber = SongInfoBlock.get();
        grooveEntries = getByteArray(SongInfoBlock, grooveListEntryNumber*17);
        instrumentDirectoriesPointer = SongInfoBlock.getInt();
        wavetableDirectoriesPointer = SongInfoBlock.getInt();
        sampleDirectoriesPointer = SongInfoBlock.getInt();    
        
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
    
    
    
    
    
    
}
