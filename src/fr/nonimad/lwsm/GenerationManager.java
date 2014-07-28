package fr.nonimad.lwsm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

public class GenerationManager {
    private ArrayList<WebSite> toDelete;
    private ArrayList<WebSite> toModify;
    private ArrayList<WebSite> toCreate;
    
    public GenerationManager() {
        toDelete = new ArrayList<WebSite>();
        toModify = new ArrayList<WebSite>();
        toCreate = new ArrayList<WebSite>();
    }
    
    private void fillTablesWithSites(Collection<WebSite> list) {
        toDelete.clear();
        toModify.clear();
        toCreate.clear();
        
        for(WebSite site : list) {
            switch(site.getState()) {
            case CREATE:
                toCreate.add(site);
                break;
            case DELETE:
                toDelete.add(site);
                break;
            case MODIFY:
                toModify.add(site);
                break;
            case UPTODATE:
                break;
            }
        }
    }
    
    public void run(boolean regenUptodate) {
        String vHostDir = LWSM.options.getProperty("VHostDir");
        if(vHostDir.contains("\\") && !vHostDir.endsWith("\\")) {
            vHostDir += "\\";
        } else if(vHostDir.contains("/") && !vHostDir.endsWith("/")) {
            vHostDir += "/";
        }

        this.moveToNewVHostDir(vHostDir);
        
        String srvNameList = "";
        Collection<WebSite> allSites = LWSM.instance.websites.values();
        
        for(WebSite site : allSites) {
            if(site.getState() == WebState.UPTODATE) {
                boolean exists = new File(vHostDir + site.getServerName()).exists(); 
                if(exists && regenUptodate) {
                    site.setState(WebState.MODIFY);
                } else if(!exists) {
                    site.setState(WebState.CREATE);
                }
            }
            
            if(site.getState() != WebState.DELETE) {
                srvNameList += " " + site.getServerName();
            }
        }
        
        generate(vHostDir, allSites);
        
        try {
            File hosts = new File("C:\\Windows\\System32\\drivers\\etc\\hosts");
            List<String> lines = Files.readAllLines(Paths.get(hosts.getPath()), StandardCharsets.UTF_8);
            BufferedWriter writer = new BufferedWriter(new FileWriter(hosts));
            
            for(String line : lines) {
                if(!line.contains("#")) {
                    if(line.contains("127.0.0.1 localhost")) {
                        writer.write("127.0.0.1 localhost" + srvNameList);
                    } else if(line.contains("::1 localhost")) {
                        writer.write("::1 localhost" + srvNameList);
                    }
                } else {
                    writer.write(line);
                }
                writer.newLine();
            }
            
            writer.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        
        JOptionPane.showMessageDialog(LWSM.instance, "Génération Terminée !", "Fin", JOptionPane.INFORMATION_MESSAGE);
        
        /*JPanel p = new JPanel(new GridLayout(3, 1, 0, 10));
        p.add(new JLabel("Copiez les lignes suivantes dans le fichier C:\\Windows\\System32\\drivers\\etc\\hosts à la place des lignes existantes semblables."));
        JTextArea textArea = new JTextArea();
        textArea.setText("127.0.0.1 localhost" + srvNameList + "\n::1 localhost" + srvNameList);
        textArea.setEditable(false);
        p.add(textArea);
        JButton openHostDir = new JButton("Ouvrir le dossier");
        openHostDir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try
                {
                    Desktop.getDesktop().browse(new URI("file://C:/Windows/System32/drivers/etc"));
                }
                catch (Exception exception)
                {
                    exception.printStackTrace();
                }
            }
        });
        p.add(openHostDir);
        
        JOptionPane.showMessageDialog(LWSM.instance, p, "Dernière étape", JOptionPane.INFORMATION_MESSAGE);*/
        
        LWSM.instance.saveAndReload();
    }
    
    public void generate(String vHostDir, Collection<WebSite> allSites) {
        Logger genLog = Logger.getLogger("LWSM");
        
        fillTablesWithSites(allSites);
        
        if(toDelete.isEmpty() && toModify.isEmpty() && toCreate.isEmpty()) {
            return;
        }
        
        genLog.info("Removing old VirtualHosts");
        for(WebSite site : toDelete) {
            File fhost = new File(vHostDir + site.getServerName());
            if(!fhost.exists())
                genLog.warning("Trying to remove VHost " + site.getServerName() + " but the file didn't exists !");
            else
                fhost.delete();
            
            File fconfig = new File("sites/" + site.getServerName() + ".lws");
            if(!fconfig.exists())
                genLog.warning("Trying to remove " + site.getServerName() + " config file but the file didn't exists !");
            else {
                fconfig.delete();
            }
            
            LWSM.instance.websites.remove(site.getServerName());
        }
        genLog.info("Finished removing old VirtualHosts");
        
        genLog.info("Generating new VirtualHosts");
        for(WebSite site : toCreate) {
            try {
                File f = new File(vHostDir + site.getServerName());
                if(f.exists()) {
                    if(JOptionPane.showConfirmDialog(LWSM.instance, "Le fichier de VirtualHost " + site.getServerName() + " existe déjà, voulez-vous l'écraser ?", "Remplacement", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        f.delete();
                    } else {
                        continue;
                    }
                }
                f.createNewFile();
                
                BufferedWriter output = new BufferedWriter(new FileWriter(f, true));
                this.writeVHost(output, site);
                output.flush();
                output.close();
                site.setState(WebState.UPTODATE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        genLog.info("Finished generating new VirtualHosts");
        
        genLog.info("Modifying existing VirtualHosts");
        for(WebSite site : toModify) {
            try {
                File f = new File(vHostDir + site.getPreviousServerName());
                f.delete();
                f = new File(vHostDir + site.getServerName());
                f.createNewFile();
                
                BufferedWriter output = new BufferedWriter(new FileWriter(f, true));
                this.writeVHost(output, site);
                output.flush();
                output.close();
                site.setState(WebState.UPTODATE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        genLog.info("Finished modifying existing VirtualHosts");
    }
    
    private void moveToNewVHostDir(String vHostDir) {
        Logger genLog = Logger.getLogger("LWSM");
        String prevVHostDir = LWSM.options.getProperty("PrevVHostDir");
        
        if(!prevVHostDir.equals("")) {
            if(prevVHostDir.contains("\\") && !prevVHostDir.endsWith("\\")) {
                prevVHostDir += "\\";
            } else if(prevVHostDir.contains("/") && !prevVHostDir.endsWith("/")) {
                prevVHostDir += "/";
            }
            
            genLog.info("Moving VirtualHosts from folder " + prevVHostDir + " to " + vHostDir + "...");
            int errorCount = 0;
            
            if(!new File(prevVHostDir).exists()) {
                JOptionPane.showMessageDialog(LWSM.instance, "Le dossier précédent est indiqué mais vide. La valeur a été remise à zéro.", "Erreur critique", JOptionPane.ERROR_MESSAGE);
                LWSM.options.setProperty("PrevVHostDir", "");
            } else {
                for(WebSite site : LWSM.instance.websites.values()) {
                    File f = new File(prevVHostDir + site.getServerName());
                    if(!f.exists())
                        continue;
                    if(!f.renameTo(new File(vHostDir + site.getServerName()))) {
                        JOptionPane.showMessageDialog(LWSM.instance, "Erreur lors du déplacement dans le nouveau répertoire du fichier : " + site.getServerName(), "Erreur critique", JOptionPane.ERROR_MESSAGE);
                        genLog.severe("Error while moving file : " + site.getServerName());
                        errorCount++;
                    }
                }
            }

            genLog.info("Finished moving files with " + errorCount + " errors");
        }
        
        LWSM.options.setProperty("PrevVHostDir", "");
        
        try {
            FileOutputStream stream = new FileOutputStream(new File("lwsm.properties"));
            LWSM.options.store(stream, null);
            stream.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
    
    private void writeVHost(BufferedWriter b, WebSite ws) throws IOException {
        this.writeLn(b, "<VirtualHost *:80>");
        this.writeLn(b, "    ServerName " + ws.getServerName());
        this.writeLn(b, "    DocumentRoot \"" + ws.getDocumentRoot() + "\"");
        this.writeLn(b, "    <Directory  \"" + ws.getDocumentRoot() + (ws.getDocumentRoot().contains("/") ? "/" : "\\") + "\">");
        this.writeLn(b, "        AllowOverride All");
        this.writeLn(b, "        Require all granted");
        this.writeLn(b, "    </Directory>");
        this.writeLn(b, "</VirtualHost>");
    }
    
    private void writeLn(BufferedWriter b, String s) throws IOException {
        b.write(s);
        b.newLine();
    }
}
