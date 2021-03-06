/*
 * $Id: TableColumn.java,v 1.20.2.6 2003/04/11 00:24:39 pietsch Exp $
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
package org.apache.fop.fo.flow;

// FOP
import org.apache.fop.fo.*;
import org.apache.fop.fo.properties.*;
import org.apache.fop.layout.*;
import org.apache.fop.apps.FOPException;
import org.apache.fop.datatypes.*;

public class TableColumn extends FObj {

    Length columnWidthPropVal;
    int columnWidth;
    int columnOffset;
    int numColumnsRepeated;
    int iColumnNumber;

    boolean setup = false;

    AreaContainer areaContainer;

    public static class Maker extends FObj.Maker {
        public FObj make(FObj parent, PropertyList propertyList,
                        String systemId, int line, int column)
            throws FOPException {
            return new TableColumn(parent, propertyList,
                                   systemId, line, column);
        }
    }

    public static FObj.Maker maker() {
        return new TableColumn.Maker();
    }

    public TableColumn(FObj parent, PropertyList propertyList,
                        String systemId, int line, int column)
        throws FOPException {
        super(parent, propertyList, systemId, line, column);
        if (!(parent instanceof Table)) {
            throw new FOPException("A table column must be child of fo:table, not "
                                   + parent.getName(), systemId, line, column);
        }
    }

    public String getName() {
        return "fo:table-column";
    }

    public Length getColumnWidthAsLength() {
        return columnWidthPropVal;
    }

    public int getColumnWidth() {
        return columnWidth;
    }

    /**
     * Set the column width value in base units which overrides the
     * value from the column-width Property.
     */
    public void setColumnWidth(int columnWidth) {
        this.columnWidth = columnWidth;
    }

    public int getColumnNumber() {
        return iColumnNumber;
    }

    public int getNumColumnsRepeated() {
        return numColumnsRepeated;
    }

    public void doSetup(Area area) throws FOPException {

        // Common Border, Padding, and Background Properties
        // only background apply, border apply if border-collapse
        // is collapse.
        BorderAndPadding bap = propMgr.getBorderAndPadding();
        BackgroundProps bProps = propMgr.getBackgroundProps();

        // this.properties.get("column-width");
        // this.properties.get("number-columns-repeated");
        // this.properties.get("number-columns-spanned");
        // this.properties.get("visibility");

        this.iColumnNumber =
        this.properties.get("column-number").getNumber().intValue();

        this.numColumnsRepeated =
                this.properties.get("number-columns-repeated").getNumber().intValue();

        this.columnWidthPropVal =
                this.properties.get("column-width").getLength();
        // This won't include resolved table-units or % values yet.
        this.columnWidth = columnWidthPropVal.mvalue();

        // initialize id
        String id = this.properties.get("id").getString();
        try {
            area.getIDReferences().initializeID(id, area);
        }
        catch(FOPException e) {
            if (!e.isLocationSet()) {
                e.setLocation(systemId, line, column);
            }
            throw e;
        }

        setup = true;
    }

    public int layout(Area area) throws FOPException {
        if (this.marker == BREAK_AFTER) {
            return Status.OK;
        }

        if (this.marker == START) {
            if (!setup) {
                doSetup(area);
            }
        }
        if (columnWidth > 0) {
            this.areaContainer =
                    new AreaContainer(propMgr.getFontState(area.getFontInfo()),
                            columnOffset, 0, columnWidth,
                            area.getContentHeight(), Position.RELATIVE);
            areaContainer.foCreator = this;    // G Seshadri
            areaContainer.setPage(area.getPage());
            areaContainer.setBorderAndPadding(propMgr.getBorderAndPadding());
            areaContainer.setBackground(propMgr.getBackgroundProps());
            areaContainer.setHeight(area.getHeight());
            area.addChild(areaContainer);
        }
        return Status.OK;
    }

    public void setColumnOffset(int columnOffset) {
        this.columnOffset = columnOffset;
    }

    public void setHeight(int height) {
        if (areaContainer != null) {
            areaContainer.setMaxHeight(height);
            areaContainer.setHeight(height);
        }
    }

}
