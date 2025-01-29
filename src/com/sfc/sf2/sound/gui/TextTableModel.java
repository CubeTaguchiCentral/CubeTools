/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.sound.gui;

//import com.sfc.sf2.sound.Manager;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author wiz
 */
public class TextTableModel extends AbstractTableModel {
    
    private final String[][] tableData;
    private final String[] columns = {"Index", "Line"};
 
    public TextTableModel(String[] gamescript) {
        super();
        tableData = new String[gamescript.length][];
        for(int i=0;i<gamescript.length;i++){
            tableData[i] = new String[2];
            String hexIndex = Integer.toHexString(i).toUpperCase();
            while(hexIndex.length()<4){
                hexIndex = "0" + hexIndex;
            }
            tableData[i][0] = hexIndex;
            tableData[i][1] = gamescript[i];
        }
        
    }
    
    @Override
    public Object getValueAt(int row, int col) {
        return tableData[row][col];
    }
    @Override
    public void setValueAt(Object value, int row, int col) {
        //tableData[row][col] = (String)value;
        //TextManager.setLine(row, (String)value);
    }    
 
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex==1;
    }    
    
    @Override
    public int getRowCount() {
        return tableData.length;
    }
 
    @Override
    public int getColumnCount() {
        return columns.length;
    }
 
    @Override
    public String getColumnName(int columnIndex) {
        return columns[columnIndex];
    }
 
}
