//package org.ethereum.util;
//
//import java.io.BufferedInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//
///**
// * Created by esaulpaugh on 1/19/17.
// */
//public class IOUtils {
//
//    private static final int BUFFER_LENGTH = 8192;
//
//    private static final int OUTPUT_BUFFER_INITIAL_CAPACITY = 8192;
//
//    public static byte[] readFully(InputStream is) throws IOException {
//        return readFully(is, -1);
//    }
//
//    /**
//     * Fully buffers, reads, and closes the {@link InputStream}
//     * @param is
//     * @param contentLength
//     * @return
//     * @throws IOException
//     */
//    public static byte[] readFully(InputStream is, int contentLength) throws IOException {
//
//        if(contentLength != -1) {
//
//            final byte[] data = new byte[contentLength];
//
//            try (BufferedInputStream bis = new BufferedInputStream(is, Math.min(contentLength, BUFFER_LENGTH))) {
//                int read;
//                do {
//                    read = bis.read(data);
//                } while (read > 0);
//            }
//
//            return data;
//        }
//
//        ByteArrayOutputStream out = new ByteArrayOutputStream(OUTPUT_BUFFER_INITIAL_CAPACITY);
//
//        try (BufferedInputStream bis = new BufferedInputStream(is, BUFFER_LENGTH)) {
//            final byte[] temp = new byte[BUFFER_LENGTH];
//            int read;
//            while ((read = bis.read(temp)) > 0) {
//                out.write(temp, 0, read);
//            }
//        }
//
//        return out.toByteArray();
//    }
//}
