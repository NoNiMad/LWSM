package fr.nonimad.lwsm;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

public class FrameOption extends JDialog implements ActionListener {
    private static final long serialVersionUID = 1L;
    
    public JTextField tf_vhost;
    
    public JButton btn_selectvhost;
    public JButton btn_cancel;
    public JButton btn_ok;
    
    public FrameOption(JFrame parent) {
        super(parent, true);
        this.setTitle("Options de LWSM");
        
        JPanel contentPane = new JPanel(new SpringLayout());
        
        tf_vhost = new JTextField(LWSM.options.getProperty("vhostdir"), 40);
        JLabel lbl_vhosts = new JLabel("Dossier où créer les vhosts :");
        lbl_vhosts.setLabelFor(tf_vhost);
        
        btn_selectvhost = new JButton("...");
        btn_selectvhost.addActionListener(this);
        
        JPanel p_vhost = new JPanel(new BorderLayout());
        p_vhost.add(tf_vhost, BorderLayout.CENTER);
        p_vhost.add(btn_selectvhost, BorderLayout.EAST);
        
        contentPane.add(lbl_vhosts);
        contentPane.add(p_vhost);
        
        btn_cancel = new JButton("Annuler");
        btn_cancel.addActionListener(this);
        
        btn_ok = new JButton("Ok");
        btn_ok.addActionListener(this);
                contentPane.add(btn_cancel);        contentPane.add(btn_ok);
        
        SpringUtilities.makeCompactGrid(contentPane,
                                        contentPane.getComponentCount()/2, 2 //rows, cols
                                        , 6, 6 //initX, initY
                                        , 6, 6); //xPad, yPad
        
        this.setContentPane(contentPane);
        
        this.pack();
        this.setLocationRelativeTo(parent);
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == btn_cancel) {
            this.dispose();
        } else if(e.getSource() == btn_ok) {
            if(tf_vhost.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vous ne pouvez pas laisser le chemin vide !", "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if(!new File(tf_vhost.getText()).exists()) {
                JOptionPane.showMessageDialog(this, "Le chemin spécifié est inexistant !", "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if(LWSM.options.getProperty("VHostDir").equals(tf_vhost.getText()))
                this.dispose();
            
            LWSM.options.setProperty("PrevVHostDir", LWSM.options.getProperty("VHostDir"));
            LWSM.options.setProperty("VHostDir", tf_vhost.getText());
            try {
                FileOutputStream stream = new FileOutputStream(new File("lwsm.properties"));
                LWSM.options.store(stream, null);
                stream.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            
            this.dispose();
        } else if(e.getSource() == btn_selectvhost) {
            JFileChooser fc_vhost = new JFileChooser();
            fc_vhost.setDialogTitle("Selection du Dossier racine de votre site");
            fc_vhost.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fc_vhost.setAcceptAllFileFilterUsed(false);
            
            if (fc_vhost.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                tf_vhost.setText(fc_vhost.getSelectedFile().getAbsolutePath());
            }
        }
    }
}
