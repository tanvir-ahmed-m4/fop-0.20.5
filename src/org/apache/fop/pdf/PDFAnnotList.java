/*
 * $Id: PDFAnnotList.java,v 1.3.2.4 2003/02/25 14:29:37 jeremias Exp $
 * ============================================================================
 *                    The Apache Software License, Version 1.1
 * ============================================================================
 * 
 * Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modifica-
 * tion, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. The end-user documentation included with the redistribution, if any, must
 *    include the following acknowledgment: "This product includes software
 *    developed by the Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself, if
 *    and wherever such third-party acknowledgments normally appear.
 * 
 * 4. The names "FOP" and "Apache Software Foundation" must not be used to
 *    endorse or promote products derived from this software without prior
 *    written permission. For written permission, please contact
 *    apache@apache.org.
 * 
 * 5. Products derived from this software may not be called "Apache", nor may
 *    "Apache" appear in their name, without prior written permission of the
 *    Apache Software Foundation.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * APACHE SOFTWARE FOUNDATION OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLU-
 * DING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ============================================================================
 * 
 * This software consists of voluntary contributions made by many individuals
 * on behalf of the Apache Software Foundation and was originally created by
 * James Tauber <jtauber@jtauber.com>. For more information on the Apache
 * Software Foundation, please see <http://www.apache.org/>.
 */ 
package org.apache.fop.pdf;

// Java
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * class representing an object which is a list of annotations.
 *
 * This PDF object is a list of references to /Annot objects. So far we
 * are dealing only with links.
 */
public class PDFAnnotList extends PDFObject {

    /**
     * the /Annot objects
     */
    protected ArrayList links = new ArrayList();

    /**
     * the number of /Annot objects
     */
    protected int count = 0;

    /**
     * create a /Annots object.
     *
     * @param number the object's number
     */
    public PDFAnnotList(int number) {

        /* generic creation of object */
        super(number);
    }

    /**
     * add an /Annot object of /Subtype /Link.
     *
     * @param link the PDFLink to add.
     */
    public void addLink(PDFLink link) {
        this.links.add(link);
        this.count++;
    }

    /**
     * get the count of /Annot objects
     *
     * @return the number of links
     */
    public int getCount() {
        return this.count;
    }

    /**
     * represent the object in PDF
     *
     * @return the PDF string
     */
    public byte[] toPDF() {
        StringBuffer p = new StringBuffer(this.number + " " + this.generation
                                          + " obj\n[\n");
        for (int i = 0; i < this.count; i++) {
            p = p.append(((PDFObject)links.get(i)).referencePDF()
                         + "\n");
        }
        p = p.append("]\nendobj\n");

        try {
            return p.toString().getBytes(PDFDocument.ENCODING);
        } catch (UnsupportedEncodingException ue) {
            return p.toString().getBytes();
        }       
    }

    /*
     * example
     * 20 0 obj
     * [
     * 19 0 R
     * ]
     * endobj
     */
}
