package com.aebiz.baseframework.view.beetl.format;

import org.beetl.core.Format;
import org.nutz.lang.Strings;

/**
 * HTML字符串安全处理
 * Created by wizzer on 2016/12/20.
 */
public class HtmlEscapeFormat implements Format {

	public Object format(Object data, String pattern) {
		return Strings.escapeHtml(String.valueOf(data==null?"":data));
	}

}
