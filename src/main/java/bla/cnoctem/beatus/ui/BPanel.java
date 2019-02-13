package bla.cnoctem.beatus.ui;

import bla.cnoctem.beatus.json.JSonBeautifier;
import com.bulenkov.darcula.DarculaLaf;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BPanel {

    public static void main(String[] args) throws UnsupportedLookAndFeelException, IOException, BadLocationException {
        UIManager.getFont("Label.font");
        UIManager.setLookAndFeel(new DarculaLaf());

        JTextPane tp = new JTextPane();
        tp.setEditable(false);

        StringBuilder sb = new StringBuilder();
        Files.lines(Paths.get(args[0])).forEach(sb::append);

        tp.setDocument(new JSonBeautifier(sb.toString()).getDoc());

        JFrame frame = new JFrame(args[0]);
        frame.setDefaultCloseOperation(3);
        JScrollPane sp = new JScrollPane(tp);
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        frame.setContentPane(sp);

        frame.pack();
        frame.setVisible(true);

    }


}
