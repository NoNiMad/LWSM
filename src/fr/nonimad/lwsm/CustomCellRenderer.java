package fr.nonimad.lwsm;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

@SuppressWarnings("serial")
public class CustomCellRenderer extends JLabel implements ListCellRenderer<String>
{
    public CustomCellRenderer() {
        super();
        setOpaque(true);
        setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 0));
    }
    
    @Override
    public Component getListCellRendererComponent(JList<? extends String> list, String value, int index, boolean isSelected, boolean cellHasFocus)
    {
        setText(value);
        
        if(isSelected || cellHasFocus)
        {
            setForeground(Color.white);
            setBackground(new Color(10, 10, 10, 150));
        } else {
            setForeground(Color.black);
            if(index % 2 == 0)
                setBackground(new Color(10, 10, 10, 30));
            else
                setBackground(new Color(0, 0, 0, 0));
        }
        
        return this;
    }
    
}