/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
*/
package edu.indiana.soic.ts.streaming.storm.utils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * This wrapper class customs its own serialized function (writeObject and readObject) for byte array.
 * As we know, Storm will serialize tuples between spout and bolt, however by default the serialization
 * of java byte array is so inefficient! This is an extremely huge performance tuning especially when
 * dealing with BIG DATA from HBase cluster.
 *
 */
public class StreamData implements Serializable {

    private static final long serialVersionUID = 4755376588252668977L;

    private byte[] data;

    public StreamData(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeInt(data.length);
        out.write(data, 0, data.length);
    }

    private void readObject(ObjectInputStream in) throws IOException {
        int len = in.readInt();
        data = new byte[len];
        int start = 0;
        while(len > 0)
        {
            int read =in.read(data,start,len);
            if(read <=0)
            {
                throw new IOException("Read Failed!");
            }
            start += read;
            len -= read;
        }
    }
}