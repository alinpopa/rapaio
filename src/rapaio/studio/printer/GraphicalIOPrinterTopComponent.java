/*
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 *    Copyright 2013 Aurelian Tutuianu
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package rapaio.studio.printer;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;
import rapaio.graphics.base.Figure;
import rapaio.printer.FigurePanel;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//rapaio.studio.printer//GraphicalIOPrinter//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "GraphicalIOPrinterTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "navigator", openAtStartup = true)
@ActionID(category = "Window", id = "rapaio.studio.printer.GraphicalIOPrinterTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_GraphicalIOPrinterAction",
        preferredID = "GraphicalIOPrinterTopComponent"
)
@Messages({
    "CTL_GraphicalIOPrinterAction=GraphicalIOPrinter",
    "CTL_GraphicalIOPrinterTopComponent=GraphicalIOPrinter Window",
    "HINT_GraphicalIOPrinterTopComponent=This is a GraphicalIOPrinter window"
})
public final class GraphicalIOPrinterTopComponent extends TopComponent {

    private FigurePanel figurePanel;

    public GraphicalIOPrinterTopComponent() {
        initComponents();

        setName(Bundle.CTL_GraphicalIOPrinterTopComponent());
        setToolTipText(Bundle.HINT_GraphicalIOPrinterTopComponent());
        putClientProperty(TopComponent.PROP_KEEP_PREFERRED_SIZE_WHEN_SLIDED_IN, Boolean.TRUE);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        imgPanel = new javax.swing.JPanel();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });
        setLayout(new java.awt.BorderLayout());

        imgPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                imgPanelComponentResized(evt);
            }
        });
        imgPanel.setLayout(new java.awt.BorderLayout());
        add(imgPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void imgPanelComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_imgPanelComponentResized
        StandardIOPrinter.setSize(evt.getComponent().getSize());
    }//GEN-LAST:event_imgPanelComponentResized

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        StandardIOPrinter.setSize(evt.getComponent().getSize());
        // TODO add your handling code here:
    }//GEN-LAST:event_formComponentResized

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel imgPanel;
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }

    public void setFigure(Figure figure) {
        if (figurePanel != null) {
            imgPanel.remove(figurePanel);
        }
        figurePanel = new FigurePanel(figure);
        figurePanel.setVisible(true);
        imgPanel.add(figurePanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
    
    public void setImage(BufferedImage image) {
        if(figurePanel != null) {
            imgPanel.remove(figurePanel);
        }
        figurePanel = new FigurePanel(image);
        figurePanel.setVisible(true);
        imgPanel.add(figurePanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
}