package com.aebiz.baseframework.page.datatable;

import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wizzer on 2017/6/12.
 */
public class DataTableResolver implements HandlerMethodArgumentResolver {
    private final static Log log= Logs.get();

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(DataTable.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        int start = Integer.parseInt(webRequest.getParameter("start"));
        int length = Integer.parseInt(webRequest.getParameter("length"));
        int draw = Integer.parseInt(webRequest.getParameter("draw"));

        // column
        int idx = 0;
        Map<String, String[]> parameterMap = webRequest.getParameterMap();
        List<DataTableColumn> columns = new ArrayList<>();
        while (parameterMap.containsKey("columns[" + idx + "][data]")) {
            DataTableColumn column = new DataTableColumn();
            column.setData(parameterMap.get("columns[" + idx + "][data]")[0]);
            column.setName(parameterMap.get("columns[" + idx + "][data]")[0]);
            column.setOrderable(Boolean.parseBoolean(parameterMap.get("columns[" + idx + "][orderable]")[0]));
            column.setSearchable(Boolean.parseBoolean(parameterMap.get("columns[" + idx + "][searchable]")[0]));
            columns.add(column);
            idx++;
        }

        // order
        idx = 0;
        List<DataTableOrder> orders = new ArrayList<>();
        while (parameterMap.containsKey("order[" + idx + "][column]")) {
            int colIdx = Integer.parseInt(parameterMap.get("order[" + idx + "][column]")[0]);
            DataTableOrder order = new DataTableOrder();
            order.setColumn(colIdx);
            order.setDir(parameterMap.get("order[" + idx + "][dir]")[0]);
            orders.add(order);
            idx++;
        }
        return new DataTable(start, length, draw, columns, orders);
    }
}
