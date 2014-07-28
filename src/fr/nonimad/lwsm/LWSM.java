package fr.nonimad.lwsm;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

public class LWSM extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;
    
    public HashMap<String, WebSite> websites = new HashMap<String, WebSite>();
    public JList<String> list_sites;
    
    public JButton b_add;
    public JButton b_edit;
    public JButton b_delete;
    public JButton b_reload;
    public JButton b_options;
    public JButton b_generate;
    public JButton b_regenerate;
    public JButton b_about;
    
    public GenerationManager genManager;
    
    public LWSM() {
        genManager = new GenerationManager();
        
        this.setTitle("Local WebSites Manager");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setResizable(false);
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout(10, 0));
        contentPane.setPreferredSize(new Dimension(400, 300));
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        this.setContentPane(contentPane);
        
        // --- Sites --- //
        
        JPanel p_list = new JPanel(new BorderLayout());
        p_list.setBorder(BorderFactory.createTitledBorder("Mes sites"));
        
        list_sites = new JList<String>();
        list_sites.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list_sites.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        list_sites.setCellRenderer(new CustomCellRenderer());
        list_sites.setBackground(null);
        list_sites.addMouseListener(new MouseAdapter() {
             @SuppressWarnings("unchecked")
             public void mouseClicked(MouseEvent evt) {
                JList<String> list = (JList<String>)evt.getSource();
                if (evt.getClickCount() == 2) {
                    int index = list.locationToIndex(evt.getPoint());
                    try {
                        Desktop.getDesktop().browse(new URI("http://" + list.getModel().getElementAt(index)));
                    } catch (IOException | URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        
        JScrollPane sp_list = new JScrollPane(list_sites);
        sp_list.setBorder(null);
        p_list.add(sp_list, BorderLayout.CENTER);
        
        // --- Buttons --- //
        
        JPanel p_buttons = new JPanel(new BorderLayout());
        JPanel p_buttons_grid = new JPanel(new GridLayout(10, 1, 0, 1));
        p_buttons_grid.setBorder(new EmptyBorder(6, 0, 0, 0));
        
        b_add = new JButton("Ajouter");
        b_add.addActionListener(this);
        
        b_edit = new JButton("Modifier");
        b_edit.addActionListener(this);

        b_delete = new JButton("Supprimer");
        b_delete.addActionListener(this);
        
        b_reload = new JButton("Recharger");
        b_reload.addActionListener(this);
        
        b_options = new JButton("Options");
        b_options.addActionListener(this);
        
        b_generate = new JButton("Générer");
        b_generate.addActionListener(this);
        
        b_regenerate = new JButton("Re-Générer");
        b_regenerate.addActionListener(this);
        
        b_about = new JButton("A propos");
        b_about.addActionListener(this);
        
        p_buttons_grid.add(b_add);
        p_buttons_grid.add(b_edit);
        p_buttons_grid.add(b_delete);
        p_buttons_grid.add(b_reload);
        p_buttons_grid.add(b_options);
        p_buttons_grid.add(b_generate);
        p_buttons_grid.add(b_regenerate);
        p_buttons_grid.add(b_about);
        
        p_buttons.add(p_buttons_grid, BorderLayout.NORTH);
        
        // --- Add Globaux --- //
        
        this.add(p_list, BorderLayout.CENTER);
        this.add(p_buttons, BorderLayout.EAST);
        this.add(new JLabel("<html><body style='text-align:center; width:300px; margin-top: 5px;'>Attention, aucune modification ne sera effectuée<br>tant que vous n'aurez pas appuyé sur le bouton 'Générer' !</body></html>"), BorderLayout.SOUTH);
        
        this.loadSites();
        
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == b_add) {
            new FrameEditSite(this, new WebSite());
        } else if(e.getSource() == b_edit) {
            if(list_sites.getSelectedIndex() > -1) {
                if(websites.containsKey(list_sites.getSelectedValue())) {
                    new FrameEditSite(this, websites.get(list_sites.getSelectedValue()));
                } else {
                    this.loadSites();
                    JOptionPane.showMessageDialog(this, "La liste des sites affichés n'est pas correctement synchronisée, un nouveau chargement a été effectué.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Vous n'avez pas sélectionné de site à modifier !", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } else if(e.getSource() == b_delete) {
            if(list_sites.getSelectedIndex() > -1) {
                String selectedValue = list_sites.getSelectedValue();
                if(websites.containsKey(selectedValue)) {
                    websites.get(selectedValue).setState(WebState.DELETE);
                } else {
                    this.loadSites();
                    JOptionPane.showMessageDialog(this, "La liste des sites affichés n'est pas correctement synchronisée, un nouveau chargement a été effectué.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Vous n'avez pas sélectionné de site à supprimer !", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } else if(e.getSource() == b_reload) {
            this.loadSites();
        } else if(e.getSource() == b_options) {
            new FrameOption(this);
        } else if(e.getSource() == b_generate) {
            genManager.run(false);
        } else if(e.getSource() == b_regenerate) {
            if(JOptionPane.showConfirmDialog(this, "Cela va réécrire tout les fichiers de VirtualHosts, en supprimant les anciens. Voulez-vous continuez ?", "Attention", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                genManager.run(true);    
            }
        } else if(e.getSource() == b_about) {
            JPanel p = new JPanel();
            
            JButton webSite = new JButton("Accéder au site");
            webSite.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    try {
                        Desktop.getDesktop().browse(new URI("http://www.nonimad.fr/"));
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            });
            
            p.add(new JLabel("<html>Local WebSites Manager<br>Version : Release 1.1<br><br>Développé par NoNiMad<br>Site Web : <a href=\"http://www.nonimad.fr/\">http://www.nonimad.fr/</a><br>Twitter : @NoNiMad</html>"));
            p.add(webSite);
            
            JOptionPane.showMessageDialog(this, p, "A propos de LWSM", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void loadSites() {
        websites.clear();
        
        File folder = new File("sites");
        if(!folder.exists()) {
            folder.mkdir();
        }
        File[] sites = folder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if(name.endsWith(".lws"))
                    return true;
                return false;
            }
        });
        
        for(File site : sites) {
            WebSite p = new WebSite();
            try {
                FileInputStream stream = new FileInputStream(site);
                p.load(stream);
                stream.close();
                String simpleName = site.getName().substring(0, site.getName().lastIndexOf(".")); 
                websites.put(simpleName, p);
                list_sites.setListData(websites.keySet().toArray(new String[0]));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        this.pack();
    }
    
    public void saveSites() {
        for(WebSite site : websites.values()) {
            try {
                FileOutputStream stream = new FileOutputStream(new File("sites/" + site.getServerName() + ".lws"));
                site.store(stream, null);
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void saveAndReload() {
        saveSites();
        loadSites();
    }
    
    public static LWSM instance;
    public static Properties options;
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        
        options = new Properties();
        options.setProperty("VHostDir", System.getProperty("user.home") + File.separator);
        options.setProperty("PrevVHostDir", "");
        
        File settings = new File("lwsm.properties");
        if(!settings.exists()) {
            try {
                FileOutputStream stream = new FileOutputStream(settings);
                LWSM.options.store(stream, null);
                stream.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        
        try {
            FileInputStream stream = new FileInputStream(settings);
            options.load(stream);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        LWSM.instance = new LWSM();
    }
}
