package jh61b.gradescope;

import java.io.ByteArrayOutputStream;

/**
 * CappedByteArrayOutputSream is a ByteArrayOutputStream whose size cannot
 * exceed that specified in the constructor.
 * <p>
 * Author: Josh Hug, 2/2/2017.
 */
public class CappedByteArrayOutputStream extends ByteArrayOutputStream {
    private final int maxSize;
    private int usedSpace = 0;
    private boolean capped = false;
    private boolean written = false;

    public CappedByteArrayOutputStream(int maxSize) {
        this.maxSize = maxSize;
    }

    /**
     * Returns true if output was so large that it had to be capped / truncated.
     */
    public boolean truncated() {
        return capped;
    }

    /**
     * Returns the maximum size
     */
    public int getMaxSize() {
        return maxSize;
    }

    /**
     * Writes to the buffer if space is available.
     */
    public void write(byte b) {
        if (usedSpace >= maxSize) {
            capped = true;
            return;
        }
        usedSpace += 1;
        written = true;
        super.write(b);
    }

    /**
     * Writes to the buffer if space is available.
     */
    public void write(byte[] b, int off, int len) {
        if (usedSpace + len >= maxSize) {
            capped = true;
            // Read partial if possible
            if (usedSpace >= maxSize) {
                return;
            }
            len = maxSize - usedSpace;
        }
        usedSpace += len;
        written = true;
        super.write(b, off, len);
    }

    public boolean written() {
        return this.written;
    }
}
