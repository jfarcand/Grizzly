/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package com.sun.grizzly.websockets;

import com.sun.grizzly.tcp.OutputBuffer;
import com.sun.grizzly.tcp.Response;
import com.sun.grizzly.tcp.http11.OutputFilter;
import com.sun.grizzly.util.buf.ByteChunk;

import java.io.IOException;

public class WebSocketOutputFilter implements OutputFilter {
    private Response response;
    private static final byte[] ENCODING_NAME = "UTF-8".getBytes();
    private static final ByteChunk ENCODING = new ByteChunk();
    private OutputBuffer buffer;

    static {
        ENCODING.setBytes(ENCODING_NAME, 0, ENCODING_NAME.length);
    }

    public int doWrite(ByteChunk chunk, Response unused) throws IOException {
        String text = new String(chunk.getBytes(), chunk.getStart(), chunk.getLength());
        DataFrame frame = new DataFrame(text);
        final byte[] bytes = frame.frame();
        ByteChunk framed = new ByteChunk(bytes.length);
        framed.setBytes(bytes, 0, bytes.length);
        buffer.doWrite(framed, response);
        return bytes.length;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public void recycle() {
        response = null;
        buffer = null;
    }

    public ByteChunk getEncodingName() {
        return ENCODING;
    }

    public void setBuffer(OutputBuffer buffer) {
        this.buffer = buffer;
    }

    public long end() throws IOException {
        return 0;
    }
}
