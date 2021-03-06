/*
 * $Id: SpanArea.java,v 1.3.2.3 2003/02/25 14:07:05 jeremias Exp $
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
package org.apache.fop.layout;

// FOP
import org.apache.fop.render.Renderer;
import org.apache.fop.fo.properties.Position;

// Java
import java.util.Iterator;

public class SpanArea extends AreaContainer {

    private int columnCount;
    private int currentColumn = 1;
    private int columnGap = 0;

    // has the area been balanced?
    private boolean isBalanced = false;

    public SpanArea(FontState fontState, int xPosition, int yPosition,
                    int allocationWidth, int maxHeight, int columnCount,
                    int columnGap) {
        super(fontState, xPosition, yPosition, allocationWidth, maxHeight,
              Position.ABSOLUTE);

        this.contentRectangleWidth = allocationWidth;
        this.columnCount = columnCount;
        this.columnGap = columnGap;

        int columnWidth = (allocationWidth - columnGap * (columnCount - 1))
                          / columnCount;
        for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
            int colXPosition = (xPosition
                                + columnIndex * (columnWidth + columnGap));
            int colYPosition = yPosition;
            ColumnArea colArea = new ColumnArea(fontState, colXPosition,
                                                colYPosition, columnWidth,
                                                maxHeight, columnCount);
            addChild(colArea);
            colArea.setColumnIndex(columnIndex + 1);
        }
    }

    public void render(Renderer renderer) {
        renderer.renderSpanArea(this);
    }

    public void end() {}

    public void start() {}

    public int spaceLeft() {
        return maxHeight - currentHeight;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public int getCurrentColumn() {
        return currentColumn;
    }

    public void setCurrentColumn(int currentColumn) {
        if (currentColumn <= columnCount)
            this.currentColumn = currentColumn;
        else
            this.currentColumn = columnCount;
    }

    public AreaContainer getCurrentColumnArea() {
        return (AreaContainer)getChildren().get(currentColumn - 1);
    }

    public boolean isBalanced() {
        return isBalanced;
    }

    public void setIsBalanced() {
        this.isBalanced = true;
    }

    public int getTotalContentHeight() {
        int totalContentHeight = 0;
        for (Iterator e = getChildren().iterator(); e.hasNext(); ) {
            totalContentHeight +=
                ((AreaContainer)e.next()).getContentHeight();
        }
        return totalContentHeight;
    }

    public int getMaxContentHeight() {
        int maxContentHeight = 0;
        for (Iterator e = getChildren().iterator(); e.hasNext(); ) {
            AreaContainer nextElm = (AreaContainer)e.next();
            if (nextElm.getContentHeight() > maxContentHeight)
                maxContentHeight = nextElm.getContentHeight();
        }
        return maxContentHeight;
    }

    public void setPage(Page page) {
        this.page = page;
        for (Iterator e = getChildren().iterator(); e.hasNext(); ) {
            ((AreaContainer)e.next()).setPage(page);
        }
    }

    public boolean isLastColumn() {
        return (currentColumn == columnCount);
    }

}
