package com.lzz.java.aio.socket;

import java.nio.ByteBuffer;  
import java.nio.CharBuffer;  
import java.nio.charset.CharacterCodingException;  
import java.nio.charset.Charset;  
import java.nio.charset.CharsetDecoder;  
import java.nio.charset.CharsetEncoder;  
  
public class CharsetUtil {  
	
    private static final String UTF_8 = "UTF-8";  
	private static CharsetEncoder encoder = Charset.forName(UTF_8).newEncoder();  
    private static CharsetDecoder decoder = Charset.forName(UTF_8).newDecoder();  
    
    /**
     * 编码方法
     * @param charBuffer
     * @return
     * @throws CharacterCodingException
     */
    public static ByteBuffer encode(CharBuffer charBuffer) 
    		throws CharacterCodingException {  
        return encoder.encode(charBuffer);  
    }
    
    /**
     * 编码方法
     * @param message
     * @return
     * @throws CharacterCodingException
     */
    public static ByteBuffer encode(String message) 
    		throws CharacterCodingException {
    	return encoder.encode(CharBuffer.wrap(message));
    }
    
    /**
     * 解码方法
     * @param byteBuffer
     * @return
     * @throws CharacterCodingException
     */
    public static CharBuffer decode(ByteBuffer byteBuffer) 
    		throws CharacterCodingException {  
        return decoder.decode(byteBuffer);  
    }
    
}  
