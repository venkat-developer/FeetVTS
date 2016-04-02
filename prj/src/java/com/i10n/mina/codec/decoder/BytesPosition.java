package com.i10n.mina.codec.decoder;

public class BytesPosition {
    private int location;
    private int length;
    
    public BytesPosition(int location, int length){
        this.location = location;
        this.length = length;
    }

    /**
     * @return the location
     */
    public int getLocation() {
        return location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(int location) {
        this.location = location;
    }

    /**
     * @return the length
     */
    public int getLength() {
        return length;
    }

    /**
     * @param length the length to set
     */
    public void setLength(int length) {
        this.length = length;
    }

}
