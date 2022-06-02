package by.bondarik.drawandguess.view.component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;

public class Canvas extends JPanel {
    private final ArrayList<Point> points;

    public Canvas(MouseAdapter adapter, MouseMotionAdapter motionAdapter) {
        setBackground(Color.WHITE);
        addMouseListener(adapter);
        addMouseMotionListener(motionAdapter);

        points = new ArrayList<>();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);

        for (int i = 0; i < points.size() - 1; i++) {
            g.drawLine(points.get(i).x, points.get(i).y, points.get(i).x, points.get(i).y);
        }
    }

    public void addPoint(Point point) {
        points.add(point);
        repaint();
    }

    public void clear() {
        points.clear();
        repaint();
    }
}
