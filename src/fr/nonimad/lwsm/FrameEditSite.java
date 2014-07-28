package fr.nonimad.lwsm;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

public class FrameEditSite extends JDialog implements ActionListener {
    private static final long serialVersionUID = 1L;
    
    public WebSite site;
    
    public JCheckBox cb_activated;
    public JTextField tf_srvName;
    public JTextField tf_docRoot;
    
    public JButton btn_selectDocRoot;
    public JButton btn_cancel;
    public JButton btn_ok;
    
    public FrameEditSite(JFrame parent, WebSite editing) {
        super(parent, true);
        this.site = editing;
        this.setTitle("Ajouter/Editer un site");
        
        JPanel contentPane = new JPanel(new SpringLayout());
        
        cb_activated = new JCheckBox();
        cb_activated.setSelected(site.getIfActivated());
        JLabel lbl_activated = new JLabel("Site activé :");
        lbl_activated.setLabelFor(cb_activated);
        
        contentPane.add(lbl_activated);
        contentPane.add(cb_activated);
        
        tf_srvName = new JTextField(site.getServerName(), 40);
        JLabel lbl_srvName = new JLabel("Nom de domaine :");
        lbl_srvName.setLabelFor(tf_srvName);
        
        contentPane.add(lbl_srvName);
        contentPane.add(tf_srvName);
        
        tf_docRoot = new JTextField(site.getDocumentRoot(), 40);
        JLabel lbl_docRoot = new JLabel("Dossier racine :");
        lbl_docRoot.setLabelFor(tf_docRoot);
        
        btn_selectDocRoot = new JButton("...");
        btn_selectDocRoot.addActionListener(this);
        
        JPanel p_docRoot = new JPanel(new BorderLayout());
        p_docRoot.add(tf_docRoot, BorderLayout.CENTER);
        p_docRoot.add(btn_selectDocRoot, BorderLayout.EAST);
        
        contentPane.add(lbl_docRoot);
        contentPane.add(p_docRoot);
        
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
            if(tf_srvName.getText().isEmpty() || tf_docRoot.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vous ne pouvez pas laisser les champs textes vides !", "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if(!site.getServerName().equals(tf_srvName.getText())) {
                new File("sites/" + site.getServerName() + ".lws").renameTo(new File("sites/" + tf_srvName.getText() + ".lws"));
                LWSM.instance.websites.remove(site.getServerName());
            }
            
            if(site.getPreviousServerName().equals("")) {
                site.setState(WebState.CREATE);
            } else {
                site.setState(WebState.MODIFY);
            }
            
            site.setIfActivated(cb_activated.isSelected());
            site.setPreviousServerName(site.getServerName());
            site.setServerName(tf_srvName.getText());
            site.setDocumentRoot(tf_docRoot.getText().replace("\\", "/"));
            
            LWSM.instance.websites.put(site.getServerName(), site);
            LWSM.instance.saveAndReload();
            this.dispose();
        } else if(e.getSource() == btn_selectDocRoot) {
            JFileChooser fc_docRoot = new JFileChooser(tf_docRoot.getText());
            fc_docRoot.setDialogTitle("Selection du Dossier racine de votre site");
            fc_docRoot.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fc_docRoot.setAcceptAllFileFilterUsed(false);
            
            if (fc_docRoot.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                tf_docRoot.setText(fc_docRoot.getSelectedFile().getAbsolutePath());
            }
        }
    }
}
