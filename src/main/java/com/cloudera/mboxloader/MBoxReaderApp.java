package com.cloudera.mboxloader;

import java.io.*;
import java.util.*;
import javax.mail.*;

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
public class MBoxReaderApp {

    private final static CharsetEncoder ENCODER = Charset.forName("UTF-8").newEncoder();

    // simple example of how to split an mbox into individual files
    public static void main(String[] args) throws Exception {

        boolean autoMode = true;

        // String f = "/Users/kamir/Library/Mail/V2/AosIMAP-bitocean";
        String f = "/Users/kamir/Documents/ME/INBOX.mbox";

        String folderToRead = "INBOX";

        if (autoMode) {

            if (args.length != 2) {
                // DEFAULTS
            } else {

                f = args[0];
                folderToRead = args[1];

            }

        } else {

            if (args.length != 1) {
                System.out.println("Please supply a path to an mbox file to parse");

                //Create a file chooser
                final JFileChooser fc = new JFileChooser();
                fc.setCurrentDirectory(new File("/Users/kamir/Documents/ME"));

                //In response to a button click:
                int returnVal = fc.showOpenDialog(new JFrame());

                f = fc.getSelectedFile().getAbsolutePath();
            } else {
                f = args[0];
            }

        }

        File fo = new File(f);

        StringBuffer sb = new StringBuffer();

        try {

            processMailBox(fo.getAbsolutePath(), folderToRead);

        } catch (Exception ex) {
            sb.append(fo.getAbsolutePath() + "\n" + ex.getMessage() + "\n\n");
        }

        System.out.println(sb.toString());

    }

    private static void processMailBox(String f, String folderToRead) throws Exception {

        final File mbox = new File(f);

        int count = 0;

        Properties p = new Properties();

        p.put("mail.mbox.mailhome", mbox.getAbsolutePath());
        p.put("mail.mbox.inbox", "");

        Session session = Session.getDefaultInstance(new Properties());

        // protocol=mbox;
        // type=store;
        // class=gnu.mail.providers.mbox.MboxStore;
        // vendor=dog@gnu.org;
        session.addProvider(new Provider(Provider.Type.STORE, "mbox",
                "gnu.mail.providers.mbox.MboxStore", "dog@gnu.org", "1"));

        Store store = session.getStore("mbox");

        store.connect();

        System.out.println("connected: " + store.isConnected());

            //
        // read messages from Inbox..
        //
        Folder[] ins = store.getPersonalNamespaces();

        for (Folder fFF : ins) {
            fFF.open(Folder.READ_ONLY);
            System.out.println("FOLDER: " + fFF.getFullName());

            final int[] msgs = {0};
            System.out.println(fFF.getMessages(msgs));

        }

    }

 
}
