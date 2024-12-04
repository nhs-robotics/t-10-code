package t10.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class IOUtils {
    public static ByteBuffer readInputStream(InputStream stream, int capacity) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(capacity);
        int readBytes = 0;

        byte[] buffer = new byte[1024];

        do {
            int length = stream.read(buffer);

            if (length <= 0) {
                break;
            }

            readBytes += length;
            byteBuffer.put(buffer, 0, length);
        } while (true);

        return (ByteBuffer) byteBuffer.limit(readBytes+1).position(0);
    }
}
