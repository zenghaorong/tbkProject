package com.aebiz.baseframework.page.datatable;

import com.aebiz.baseframework.page.datatable.DataTableColumn;
import com.aebiz.baseframework.page.datatable.DataTableOrder;
import org.nutz.lang.util.NutMap;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wizzer on 2017/6/12.
 */
public class DataTable implements Serializable {
    private static final long serialVersionUID = 1L;

    private int start;
    private int length;
    private int draw;
    private List<DataTableColumn> columns;
    private List<DataTableOrder> orders;

    public DataTable() {
    }

    public DataTable(int start, int length, int draw, List<DataTableColumn> columns, List<DataTableOrder> orders) {
        this.start = start;
        this.length = length;
        this.draw = draw;
        this.columns = columns;
        this.orders = orders;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getDraw() {
        return draw;
    }

    public void setDraw(int draw) {
        this.draw = draw;
    }

    public List<DataTableColumn> getColumns() {
        return columns;
    }

    public void setColumns(List<DataTableColumn> columns) {
        this.columns = columns;
    }

    public List<DataTableOrder> getOrders() {
        return orders;
    }

    public void setOrders(List<DataTableOrder> orders) {
        this.orders = orders;
    }
}
