package com.naman14.timber.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * Created by Christoph Walcher on 03.12.16.
 */

public class LyricsExtractor {
    public static String getLyrics(File file){
        String filename = file.getName();
        String fileending = filename.substring(filename.lastIndexOf('.')+1,filename.length()).toLowerCase();
        try{
            switch(fileending){
                case "mp3":
                    return getLyricsID3(file);
                case "mp4":
                case "m4a":
                case "aac":
                    return getLyricsMP4(file);
                case "ogg":
                case "oga":
                    return getLyricsVorbis(file);
            }
        }catch(Exception e){}
        return null;
    }

    private static int readOgg(byte[] buf, InputStream in, int bytesinpage, int skip) throws IOException {
        int toread = skip!=-1?skip:buf.length;
        int offset = 0;
        while(toread>0){
            if(bytesinpage==0){
                byte magic[] = new byte[4];
                in.read(magic);
                if(!Arrays.equals(magic,new byte[]{'O','g','g','S'})){
                    in.close();
                    throw new IOException();
                }
                byte header[] = new byte[23];
                in.read(header);
                int count = header[22]& 0xFF;
                while(count-->0){
                    bytesinpage += in.read();
                }
            }
            int read = toread;
            if(bytesinpage-toread<0)read = bytesinpage;
            if(skip != -1)
                in.skip(read);
            else
                in.read(buf, offset, read);
            offset += read;
            toread -= read;
            bytesinpage -= read;
        }
        return bytesinpage;
    }

    private static String getLyricsVorbis(File file) throws Exception{
        FileInputStream in = new FileInputStream(file);
        int bytesinpage = 0;
        byte buffer[] = new byte[7];
        bytesinpage = readOgg(buffer,in,bytesinpage,-1);
        if(!Arrays.equals(buffer, new byte[]{1,'v','o','r','b','i','s'})){
            in.close();
            return null;
        }
        bytesinpage = readOgg(null,in,bytesinpage, 23);
        bytesinpage = readOgg(buffer,in,bytesinpage,-1);
        if(!Arrays.equals(buffer, new byte[]{3,'v','o','r','b','i','s'})){
            in.close();
            return null;
        }
        byte length[] = new byte[4];
        bytesinpage = readOgg(length, in, bytesinpage,-1);
        bytesinpage = readOgg(null, in, bytesinpage, byteArrayToInt(length));
        bytesinpage = readOgg(length, in, bytesinpage,-1);
        int count = byteArrayToIntLE(length);
        while(count-->0){
            bytesinpage = readOgg(length, in, bytesinpage,-1);
            int comment_len = byteArrayToIntLE(length);
            byte lyrics_tag[] = new byte[]{'L','Y','R','I','C','S','='};
            if(comment_len<=lyrics_tag.length){
                bytesinpage = readOgg(null, in, bytesinpage, comment_len);
                continue;
            }
            byte comment_probe[] = new byte[lyrics_tag.length];
            bytesinpage = readOgg(comment_probe, in, bytesinpage,-1);
            if(Arrays.equals(comment_probe,lyrics_tag)){
                byte  lyrics[] = new byte[comment_len - lyrics_tag.length];
                readOgg(lyrics, in, bytesinpage,-1);
                in.close();
                return new String(lyrics);
            }else{
                bytesinpage = readOgg(null, in, bytesinpage, comment_len - lyrics_tag.length);
            }
        }
        in.close();
        return null;

    }


    private static String getLyricsMP4(File file) throws Exception{
        FileInputStream in = new FileInputStream(file);

        byte head[] = new byte[4];
        in.read(head);
        int len = byteArrayToInt(head);
        in.read(head);
        if (!Arrays.equals(head, new byte[]{'f','t','y','p'})){
            in.close();
            return null;
        }
        in.skip(len - 8);
        final byte path[][] = new byte[][]{{'m','o','o','v'},{'u','d','t','a'},{'m','e','t','a'},{'i','l','s','t'},{(byte) '©','l','y','r'},{'d','a','t','a'}};
        int atom_size = Integer.MAX_VALUE;
        outter:
        for(byte[] atom: path){
            while(in.available()>0){
                byte buffer[] = new byte[4];
                in.read(buffer);
                len = byteArrayToInt(buffer);
                if(len==0)continue;
                in.read(buffer);
                if(len>atom_size){
                    in.close();
                    return null;
                }
                if (Arrays.equals(buffer, atom)){
                    atom_size = len - 8;
                    //Found Atom search next atom
                    continue outter;
                }else{
                    //Skip Atom
                    in.skip(len - 8);
                    atom_size-=len;
                }
            }
            in.close();
            return null;
        }
        in.skip(8);
        byte buffer[] = new byte[atom_size-8];
        in.read(buffer);
        in.close();
        return new String(buffer);
    }


    private static String getLyricsID3(File file) throws Exception{
        FileInputStream in = new FileInputStream(file);
        byte buffer[] = new byte[4];
        in.read(buffer, 0, 3);
        if (!Arrays.equals(buffer, new byte[] { 'I', 'D', '3', 0 })){
            in.close();
            return null;
        }

        in.read(buffer, 0, 3);
        boolean ext = (buffer[2] & (byte) 0b0100000) != 0;
        in.read(buffer);
        int len = buffer[3] & 0x7F | (buffer[2] & 0x7F) << 7 | (buffer[1] & 0x7F) << 14 | (buffer[0] & 0x7F) << 21;
        if (ext) {
            in.read(buffer); len-=4;
            int ext_len = byteArrayToInt(buffer);
            in.skip(ext_len); len -= ext_len;

        }
        while (len > 0) {
            byte tag_name[] = new byte[4];
            in.read(tag_name); len-=4;
            if(tag_name[0]==0)break;
            in.read(buffer); len -=4;
            int tag_len = byteArrayToInt(buffer);
            in.read(buffer,0,2); len-=2;
            if(Arrays.equals(tag_name, new byte[] { 'U', 'S', 'L', 'T' })){
                byte head[] = new byte[4];
                in.read(head); len -= 4; tag_len -= 4;
                while(in.read()!=0){
                    len--;
                    tag_len--;
                }
                if(head[0]==1)in.read();
                byte tag_value[] = new byte[tag_len];
                in.read(tag_value); len -= tag_len;
                in.close();
                Charset charset = null;
                switch (head[0]){
                    case 0: charset = Charset.forName("ISO-8859-1");
                        break;
                    case 1: charset = Charset.forName("UTF-16");
                        break;
                    case 2: charset = Charset.forName("UTF-16BE");
                        break;
                    case 3: charset = Charset.forName("UTF-8");
                        break;
                    default:
                        return null;
                }
                return new String(tag_value,charset);

            }else{
                in.skip(tag_len); len -= tag_len;
            }

        }
        in.close();
        return null;

    }

    private static int byteArrayToInt(byte[] b) {
        return b[3] & 0xFF | (b[2] & 0xFF) << 8 | (b[1] & 0xFF) << 16 | (b[0] & 0xFF) << 24;
    }

    private static int byteArrayToIntLE(byte[] b) {
        return b[0] & 0xFF | (b[1] & 0xFF) << 8 | (b[2] & 0xFF) << 16 | (b[3] & 0xFF) << 24;
    }

}
