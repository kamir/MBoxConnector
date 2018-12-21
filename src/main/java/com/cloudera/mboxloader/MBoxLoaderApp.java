package com.cloudera.mboxloader;

/**
 * **************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one * or more
 * contributor license agreements. See the NOTICE file * distributed with this
 * work for additional information * regarding copyright ownership. The ASF
 * licenses this file * to you under the Apache License, Version 2.0 (the *
 * "License"); you may not use this file except in compliance * with the
 * License. You may obtain a copy of the License at * *
 * http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable
 * law or agreed to in writing, * software distributed under the License is
 * distributed on an * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY *
 * KIND, either express or implied. See the License for the * specific language
 * governing permissions and limitations * under the License. *
 * **************************************************************
 */
import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.dom.Message;
import org.apache.james.mime4j.dom.MessageBuilder;
import org.apache.james.mime4j.message.DefaultMessageBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

/**
 * Simple example of how to use Apache Mime4j Mbox Iterator.
 *
 * We index the content of one ore many mbox file to execute communication
 * analysis on top of this data.
 *
 * @author Mirko Kämpf
 */
public class MBoxLoaderApp {

    private final static CharsetEncoder ENCODER = Charset.forName("UTF-8").newEncoder();

    // simple example of how to split an mbox into individual files
    public static void main(String[] args) throws Exception {

        boolean autoMode = true;

        String f = null;
        
        String folder1 = null;
        String folder2 = null;
        
        if (autoMode) {
            
            f = "data/wrong-data/Al-Monitor mbox";
            
            folder1 = "data/wrong-data";
            folder2 = "data/correct-data";
            
        } 
        else {

            if (args.length != 1) {
                System.out.println("Please supply a path to an mbox file to parse");

                //Create a file chooser
                final JFileChooser fc = new JFileChooser();
                fc.setCurrentDirectory(new File("/Users/kamir/Documents/expoted.mail"));

                //In response to a button click:
                int returnVal = fc.showOpenDialog(new JFrame());

                f = fc.getSelectedFile().getAbsolutePath();
            } else {
                f = args[0];
            }

        }

        // processMailBox( f );
        
        File fo = new File( folder1 );
//        File fo = new File( folder2 );
        File[] files = fo.listFiles();
        
        StringBuffer sb = new StringBuffer();
        
        for( File fi : files ) {
        
            try {
                processMailBox( fi.getAbsolutePath() );
            } 
            catch (Exception ex) {
                sb.append( fi.getAbsolutePath() + "\n" + ex.getMessage() + "\n\n");
            }
        
        }
        System.out.println( sb.toString() );
        
    }
        

    private static void saveMessageToFile(int count, CharBuffer buf) throws IOException {
        FileOutputStream fout = new FileOutputStream(new File("target/messages/msg-" + count));
        FileChannel fileChannel = fout.getChannel();
        ByteBuffer buf2 = ENCODER.encode(buf);
        fileChannel.write(buf2);
        fileChannel.close();
        fout.close();
    }

    private static void indexMessageLocally(int count, CharBuffer buf) throws IOException {
        FileOutputStream fout = new FileOutputStream(new File("target/messages/msg-" + count));
        FileChannel fileChannel = fout.getChannel();
        ByteBuffer buf2 = ENCODER.encode(buf);
        fileChannel.write(buf2);
        fileChannel.close();
        fout.close();
    }

    /**
     * Parse a message and return a simple {@link String} representation of some
     * important fields.
     *
     * @param messageBytes the message as {@link java.io.InputStream}
     * @return String
     * @throws IOException
     * @throws MimeException
     */
    private static String messageSummary(InputStream messageBytes) throws IOException, MimeException {
        MessageBuilder builder = new DefaultMessageBuilder();
        Message message = builder.parseMessage(messageBytes);
        return String.format("\nMessage %s \n"
                + "Sent by:\t%s\n"
                + "To:\t%s\n",
                message.getSubject(),
                message.getSender(),
                message.getTo());
    }

    
    private static void processMailBox(String f) throws IOException, MimeException {

        final File mbox = new File(f);
        long start = System.currentTimeMillis();
        int count = 0;

        for (CharBufferWrapper message : MBoxIterator.fromFile(mbox).charset(ENCODER.charset()).build()) {
            // saveMessageToFile(count, buf);
            System.out.println(messageSummary(message.asInputStream(ENCODER.charset())));
            count++;
        }
        System.out.println("Found " + count + " messages");
        long end = System.currentTimeMillis();
        System.out.println("Done in: " + (end - start) + " milis");
        
    }
}
