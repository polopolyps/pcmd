package com.polopoly.ps.pcmd.tool.export;

public enum ContentFileFormat {
    TEXT("content"), XML("xml");

    private String extension;

    ContentFileFormat(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }
}
