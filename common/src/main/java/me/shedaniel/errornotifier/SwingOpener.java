package me.shedaniel.errornotifier;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SwingOpener {
    public static void main(String[] args) throws IOException {
        DataInputStream stream = new DataInputStream(System.in);
        String title = stream.readUTF();
        int size = stream.readInt();
        List<Map.Entry<String, String>> errors = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            String message = stream.readUTF();
            String url = stream.readUTF();
            errors.add(Map.entry(message, url.isEmpty() ? null : url));
        }
        // set system look and feel
        System.setProperty("apple.awt.application.appearance", "system");
        System.setProperty("apple.awt.application.name", title);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(600, 400));
        frame.setSize(800, 600);
        frame.setLocationByPlatform(true);
        
        
        String message = "";
        for (Map.Entry<String, String> error : errors) {
            message += error.getKey() + "<br>";
            if (error.getValue() != null) {
                message += "<a href=\"" + error.getValue() + "\">" + error.getValue() + "</a><br>";
            }
        }
        
        JLabel label = new JLabel();
        Font font = label.getFont();
        Color bgColor = label.getBackground();
        String editorPaneStyle = String.format(Locale.ENGLISH,
                "font-family:%s;font-weight:%s;font-size:%dpt;background-color: rgb(%d,%d,%d);",
                font.getFamily(), "normal", font.getSize() * 3 / 2, bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue()
        );
        
        JEditorPane area = new JEditorPane("text/html", "<html><body style=\"" + editorPaneStyle + "\">" + message + "</body></html>");
        area.setEditable(false);
        area.setBackground(bgColor);
        area.addHyperlinkListener(e -> {
            try {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                        Desktop.getDesktop().browse(e.getURL().toURI());
                    } else {
                        throw new UnsupportedOperationException("Failed to open " + e.getURL().toString());
                    }
                }
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(area);
        JLabel errorsLabel = new JLabel("Startup Errors!");
        errorsLabel.setFont(errorsLabel.getFont().deriveFont(errorsLabel.getFont().getSize() * 2F).deriveFont(Font.PLAIN));
        errorsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        errorsLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        JPanel anotherPanel = new JPanel(new BorderLayout());
        anotherPanel.add(scrollPane, BorderLayout.CENTER);
        anotherPanel.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 3));
        frame.add(errorsLabel, BorderLayout.NORTH);
        frame.add(anotherPanel, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JButton close = new JButton("Close");
        close.addActionListener(e -> {
            frame.dispose();
            System.exit(0);
        });
        bottomPanel.add(close, BorderLayout.EAST);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        frame.add(bottomPanel, BorderLayout.SOUTH);
        
        frame.setVisible(true);
    }
}
