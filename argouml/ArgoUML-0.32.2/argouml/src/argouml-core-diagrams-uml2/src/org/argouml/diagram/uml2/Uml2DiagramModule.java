/* $Id: Uml2DiagramModule.java 17882 2010-01-12 21:09:56Z linus $
 *****************************************************************************
 * Copyright (c) 2009 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    bobtarling
 *****************************************************************************
 *
 * Some portions of this file was previously release using the BSD License:
 */

// Copyright (c) 2007 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation without fee, and without a written
// agreement is hereby granted, provided that the above copyright notice
// and this paragraph appear in all copies. This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "AS
// IS", without any accompanying services from The Regents. The Regents
// does not warrant that the operation of the program will be
// uninterrupted or error-free. The end-user understands that the program
// was developed for research purposes and is advised not to rely
// exclusively on the program for any reason. IN NO EVENT SHALL THE
// UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT,
// SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS,
// ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
// THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF
// SUCH DAMAGE. THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY
// WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
// MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE
// PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
// CALIFORNIA HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT,
// UPDATES, ENHANCEMENTS, OR MODIFICATIONS.

package org.argouml.diagram.uml2;

import org.apache.log4j.Logger;
import org.argouml.moduleloader.ModuleInterface;
import org.argouml.uml.diagram.DiagramFactory;
import org.argouml.uml.diagram.DiagramFactoryInterface2;
import org.argouml.uml.diagram.DiagramFactory.DiagramType;
import org.argouml.uml.ui.PropPanelFactoryManager;

/**
 * The Class Diagram Module description.
 *
 * @see org.argouml.moduleloader.ModuleInterface
 */
public class Uml2DiagramModule implements ModuleInterface {

    private static final Logger LOG =
        Logger.getLogger(Uml2DiagramModule.class);

    private ClassDiagram2PropPanelFactory propPanelFactory;
    
    public boolean enable() {
        
        propPanelFactory =
            new ClassDiagram2PropPanelFactory();
        PropPanelFactoryManager.addPropPanelFactory(propPanelFactory);
        // TODO: Remove the casting to DiagramFactoryInterface2
        // as soon as DiagramFactoryInterface is removed.
        DiagramFactory.getInstance().registerDiagramFactory(
                DiagramType.Class, 
                (DiagramFactoryInterface2) new ClassDiagram2Factory());
        DiagramFactory.getInstance().registerDiagramFactory(
                DiagramType.UseCase, 
                (DiagramFactoryInterface2) new UseCaseDiagram2Factory());

        LOG.info("ClassDiagram Module enabled.");
        return true;
    }

    public boolean disable() {

        PropPanelFactoryManager.removePropPanelFactory(propPanelFactory);

        // TODO: Remove the casting to DiagramFactoryInterface2
        // as soon as DiagramFactoryInterface is removed.
        DiagramFactory.getInstance().registerDiagramFactory(
                DiagramType.Class, (DiagramFactoryInterface2) null);

        LOG.info("ClassDiagram Module disabled.");
        return true;
    }
    
    public String getName() {
        return "ArgoUML-Class";
    }

    public String getInfo(int type) {
        switch (type) {
        case DESCRIPTION:
            return "The UML2 diagrams implementation";
        case AUTHOR:
            return "ArgoUML Core Development Team";
        case VERSION:
            return "0.28";
        case DOWNLOADSITE:
            return "http://argouml.tigris.org";
        default:
            return null;
        }
    }
}
