/*
 * ATTVModeConfigPanel.java
 *
 * Created on Mar 24, 2010, 5:08:15 PM
 */

package JavaTron;
import javax.swing.JColorChooser;
import java.awt.Color;

/**
 *
 * @author Joe Culbreth
 */
public class ATTVModeConfigPanel extends javax.swing.JPanel {
	AudioTronState at;
    /** Creates new form ATTVModeConfigPanel */
    public ATTVModeConfigPanel(AudioTronState at_) {
        at=at_;
		initComponents();
		initValues();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel6 = new javax.swing.JLabel();
        sampleLabel = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        bgColorChooserButton = new javax.swing.JButton();
        bgColorPanel = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        fontNameCombo = new javax.swing.JComboBox();
        fontSizeCombo = new javax.swing.JComboBox();
        fColorChooserButton = new javax.swing.JButton();
        fontColorPanel = new javax.swing.JPanel();

        jLabel6.setText("jLabel6");
        jLabel6.setName("jLabel6"); // NOI18N

        sampleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        sampleLabel.setText("Sample Text");
        sampleLabel.setBorder(javax.swing.BorderFactory.createTitledBorder("Sample"));
        sampleLabel.setName("sampleLabel"); // NOI18N
        sampleLabel.setOpaque(true);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("TV Mode Settings"));
        jPanel1.setName("jPanel1"); // NOI18N

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Background Color"));
        jPanel2.setName("jPanel2"); // NOI18N

        bgColorChooserButton.setText("...");
        bgColorChooserButton.setName("bgColorChooserButton"); // NOI18N
        bgColorChooserButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bgColorChooserButtonActionPerformed(evt);
            }
        });

        bgColorPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        bgColorPanel.setName("bgColorPanel"); // NOI18N

        javax.swing.GroupLayout bgColorPanelLayout = new javax.swing.GroupLayout(bgColorPanel);
        bgColorPanel.setLayout(bgColorPanelLayout);
        bgColorPanelLayout.setHorizontalGroup(
            bgColorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 81, Short.MAX_VALUE)
        );
        bgColorPanelLayout.setVerticalGroup(
            bgColorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 19, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(bgColorPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(bgColorChooserButton, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(89, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(bgColorChooserButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(bgColorPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Font"));
        jPanel4.setName("jPanel4"); // NOI18N

        fontNameCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Arial", "Plain", "Serif", "Tahoma" }));
        fontNameCombo.setEnabled(false);
        fontNameCombo.setName("fontNameCombo"); // NOI18N
        fontNameCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fontNameComboActionPerformed(evt);
            }
        });

        fontSizeCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "8", "9", "10", "11", "12", "13", "14", "16", "18", "20", "22", "24", "26", "28", "30", "32", "34", "36", "38", "40", "48", "52", "60", "72", "86" }));
        fontSizeCombo.setSelectedIndex(11);
        fontSizeCombo.setName("fontSizeCombo"); // NOI18N
        fontSizeCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fontSizeComboActionPerformed(evt);
            }
        });

        fColorChooserButton.setText("...");
        fColorChooserButton.setName("fColorChooserButton"); // NOI18N
        fColorChooserButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fColorChooserButtonActionPerformed(evt);
            }
        });

        fontColorPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        fontColorPanel.setName("fontColorPanel"); // NOI18N

        javax.swing.GroupLayout fontColorPanelLayout = new javax.swing.GroupLayout(fontColorPanel);
        fontColorPanel.setLayout(fontColorPanelLayout);
        fontColorPanelLayout.setHorizontalGroup(
            fontColorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 72, Short.MAX_VALUE)
        );
        fontColorPanelLayout.setVerticalGroup(
            fontColorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 16, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(fontNameCombo, 0, 91, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fontSizeCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(34, 34, 34))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(fontColorPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fColorChooserButton, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fontNameCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fontSizeCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(fontColorPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fColorChooserButton))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(sampleLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sampleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

	private void initValues(){
		String font="Arial";
		String size="24";
		String color="100,100,100";
		String bgcolor="0,0,0";

		if(Configuration.getProperty(Configuration.KEY_TV_FONT) != null)
			font=Configuration.getProperty(Configuration.KEY_TV_FONT);
		else
			Configuration.setProperty(Configuration.KEY_TV_FONT,font);
		if(Configuration.getProperty(Configuration.KEY_TV_FONT_SIZE) != null)
			size=Configuration.getProperty(Configuration.KEY_TV_FONT_SIZE);
		else
			Configuration.setProperty(Configuration.KEY_TV_FONT_SIZE, size);
		if(Configuration.getProperty(Configuration.KEY_TV_FONT_COLOR) != null)
			color=Configuration.getProperty(Configuration.KEY_TV_FONT_COLOR);
		else
			Configuration.setProperty(Configuration.KEY_TV_FONT_COLOR, color);
		if(Configuration.getProperty(Configuration.KEY_TV_BGCOLOR) != null)
			bgcolor=Configuration.getProperty(Configuration.KEY_TV_BGCOLOR);
		else
			Configuration.setProperty(Configuration.KEY_TV_BGCOLOR, bgcolor);
		Configuration.saveProperties();

		fontNameCombo.setSelectedItem(font);
		fontSizeCombo.setSelectedItem(size);
		fontColorPanel.setBackground(JTP.parseRGB(color));

		bgColorPanel.setBackground(JTP.parseRGB(bgcolor));

		sampleLabel.setForeground(JTP.parseRGB(color));
		sampleLabel.setBackground(JTP.parseRGB(bgcolor));

	}
	private void fontNameComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fontNameComboActionPerformed
		//System.out.println("Bing!");
		Configuration.setProperty(Configuration.KEY_TV_FONT,
									(String)fontNameCombo.getSelectedItem());
	}//GEN-LAST:event_fontNameComboActionPerformed

	private void fontSizeComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fontSizeComboActionPerformed
		Configuration.setIntProperty(Configuration.KEY_TV_FONT_SIZE,
										Integer.valueOf((String)fontSizeCombo.getSelectedItem()));
		sampleLabel.setFont(
				sampleLabel.getFont().deriveFont(
							(float)Integer.valueOf((String)fontSizeCombo.getSelectedItem())
							));

	}//GEN-LAST:event_fontSizeComboActionPerformed

	private void fColorChooserButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fColorChooserButtonActionPerformed
		Color newColor = JColorChooser.showDialog(this, "Font Color",
							sampleLabel.getForeground() );
		if(newColor != null){
			sampleLabel.setForeground(newColor);
			fontColorPanel.setBackground(newColor);
		}
		System.out.println("FontColor="+parseColor(newColor));
		Configuration.setProperty(Configuration.KEY_TV_FONT_COLOR, parseColor(newColor));
	}//GEN-LAST:event_fColorChooserButtonActionPerformed

	private void bgColorChooserButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bgColorChooserButtonActionPerformed
				Color newColor = JColorChooser.showDialog(this, "Font Color",
							sampleLabel.getBackground() );
		if(newColor != null){
			sampleLabel.setBackground(newColor);
			bgColorPanel.setBackground(newColor);
		}
		System.out.println("Backgroun: "+parseColor(newColor));
		Configuration.setProperty(Configuration.KEY_TV_BGCOLOR,parseColor(newColor));

	}//GEN-LAST:event_bgColorChooserButtonActionPerformed

	private Color parseRGB(String rgb){
		String[] RGB=rgb.split(",");
		int red=Integer.valueOf(RGB[0]);
		int green=Integer.valueOf(RGB[1]);
		int blue=Integer.valueOf(RGB[2]);
		return new Color(red,green,blue);
	}

	private String parseColor(Color c){
		int red=c.getRed();
		int green=c.getGreen();
		int blue=c.getBlue();
		String csv=Integer.toString(red)+","+Integer.toString(green)+","+Integer.toString(blue);
		return csv;
	}
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bgColorChooserButton;
    private javax.swing.JPanel bgColorPanel;
    private javax.swing.JButton fColorChooserButton;
    private javax.swing.JPanel fontColorPanel;
    private javax.swing.JComboBox fontNameCombo;
    private javax.swing.JComboBox fontSizeCombo;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JLabel sampleLabel;
    // End of variables declaration//GEN-END:variables

}
