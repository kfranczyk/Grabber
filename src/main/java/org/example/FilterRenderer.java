package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.ArrayList;

public class FilterRenderer extends DefaultTableCellRenderer {

    ArrayList<Integer> duplicatedRowsIndx;

    public FilterRenderer(ArrayList<Integer> copyIndex) {
        this.duplicatedRowsIndx = copyIndex;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {

        table.setRowHeight(30);
        table.getColumnModel().getColumn(column).setMinWidth(150);
        table.getColumnModel().getColumn(column).setPreferredWidth(180);
        table.getColumnModel().getColumn(column).setMaxWidth(300);


        table.getTableHeader().setFont(new Font("Dialog", Font.BOLD, 12));
        table.getTableHeader().setPreferredSize(
                new Dimension(table.getColumnModel().getTotalColumnWidth(), 52));

        Component c = super.getTableCellRendererComponent(table, value,
                isSelected, hasFocus, row, column);


        // Customize the style for each row
        if (duplicatedRowsIndx.contains(row)) {
            c.setBackground(Color.RED);
        } else {
            c.setBackground(Color.lightGray);
        }
        if (isSelected) {
            c.setBackground(Color.WHITE);
        }

        return c;
    }
}

