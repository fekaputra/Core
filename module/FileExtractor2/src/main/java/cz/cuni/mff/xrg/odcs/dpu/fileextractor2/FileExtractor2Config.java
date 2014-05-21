package cz.cuni.mff.xrg.odcs.dpu.fileextractor2;

import java.util.LinkedHashMap;
import java.util.Map;

import cz.cuni.mff.xrg.odcs.commons.module.config.DPUConfigObjectBase;

public class FileExtractor2Config extends DPUConfigObjectBase {

    /**
     * 
     */
    private static final long serialVersionUID = -1976290368429585223L;

    private Map<String, String> symbolicNameToBaseURIMap;
    private Map<String, String> symbolicNameToFormatMap;
    

    // DPUTemplateConfig must provide public non-parametric constructor
    public FileExtractor2Config() {
        this.symbolicNameToBaseURIMap = new LinkedHashMap<>();
        this.symbolicNameToFormatMap = new LinkedHashMap<>();
    }

    public Map<String, String> getSymbolicNameToBaseURIMap() {
        return symbolicNameToBaseURIMap;
    }


    public void setSymbolicNameToBaseURIMap(Map<String, String> symbolicNameToBaseURIMap) {
        this.symbolicNameToBaseURIMap = symbolicNameToBaseURIMap;
    }


    public Map<String, String> getSymbolicNameToFormatMap() {
        return symbolicNameToFormatMap;
    }

    public void setSymbolicNameToFormatMap(Map<String, String> symbolicNameToFormatMap) {
        this.symbolicNameToFormatMap = symbolicNameToFormatMap;
    }
}