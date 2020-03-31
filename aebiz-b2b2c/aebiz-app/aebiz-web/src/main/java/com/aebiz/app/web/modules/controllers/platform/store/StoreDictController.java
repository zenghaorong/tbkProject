package com.aebiz.app.web.modules.controllers.platform.store;

import com.aebiz.app.sys.modules.models.Sys_dict;
import com.aebiz.app.sys.modules.services.SysDictService;
import com.aebiz.baseframework.base.Result;
import com.aebiz.baseframework.view.annotation.SJson;
import com.aebiz.commons.utils.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Cnd;
import org.nutz.dao.Sqls;
import org.nutz.lang.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/store/sys/dict")
public class StoreDictController {

    @Autowired
    private SysDictService sysDictService;

    @RequestMapping("")
    @RequiresPermissions("store.sys.dict")
    public String index(HttpServletRequest req) {
        req.setAttribute("obj", sysDictService
                .query(Cnd.where("parentId", "=", "")
                        .and("sysType","=","store")
                        .and("storeId","=",StringUtil.getStoreId())
                        .asc("location").asc("path")));
        return "pages/platform/store/dict/index";
    }

    @RequestMapping(value = {"/add/{id}", "/add"})
    public String add(@PathVariable(required = false) String id, HttpServletRequest req) {
        req.setAttribute("obj", Strings.isBlank(id) ? null : sysDictService.fetch(id));
        return "pages/platform/store/dict/add";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/addDo")
    @SJson
    @RequiresPermissions("store.sys.dict.add")
    public Object addDo(Sys_dict dict, String parentId) {
        try {
            dict.setSysType("store");
            dict.setStoreId(StringUtil.getStoreId());
            sysDictService.save(dict, parentId);
            sysDictService.clearCache();
            return Result.success("globals.result.success");
        } catch (Exception e) {
            return Result.error("globals.result.error");
        }
    }

    @RequestMapping("/child/{id}")
    public String child(@PathVariable String id, HttpServletRequest req) {
        req.setAttribute("obj", sysDictService.query(Cnd.where("parentId", "=", id).asc("location").asc("path")));
        return "pages/platform/store/dict/child";
    }

    @RequestMapping("/edit/{id}")
    public String edit(@PathVariable String id, HttpServletRequest req) {
        Sys_dict dict = sysDictService.fetch(id);
        if (dict != null) {
            req.setAttribute("parentUnit", sysDictService.fetch(dict.getParentId()));
        }
        req.setAttribute("obj", dict);
        return "pages/platform/store/dict/edit";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/editDo")
    @SJson
    @RequiresPermissions("store.sys.dict.edit")
    public Object editDo(Sys_dict dict, HttpServletRequest req) {
        try {
            dict.setOpBy(StringUtil.getUid());
            dict.setOpAt((int) (System.currentTimeMillis() / 1000));
            dict.setSysType("store");
            dict.setStoreId(StringUtil.getStoreId());
            sysDictService.updateIgnoreNull(dict);
            sysDictService.clearCache();
            return Result.success("globals.result.success");
        } catch (Exception e) {
            return Result.error("globals.result.error");
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/delete/{id}")
    @SJson
    public Object delete(@PathVariable String id, HttpServletRequest req) {
        try {
            Sys_dict dict = sysDictService.fetch(id);
            sysDictService.deleteAndChild(dict);
            sysDictService.clearCache();
            return Result.success("globals.result.success");
        } catch (Exception e) {
            return Result.error("globals.result.error");
        }
    }

    @RequestMapping(value = {"/tree", "/tree/{pid}"})
    @SJson
    public Object tree(@PathVariable(required = false) String pid, HttpServletRequest req) {
        List<Sys_dict> list = sysDictService.query(Cnd.where("parentId", "=", Strings.sBlank(pid))
                .and("sysType","=","store")
                .asc("path"));
        List<Map<String, Object>> tree = new ArrayList<>();
        for (Sys_dict dict : list) {
            Map<String, Object> obj = new HashMap<>();
            obj.put("id", dict.getId());
            obj.put("text", dict.getName());
            obj.put("children", dict.isHasChildren());
            tree.add(obj);
        }
        return tree;
    }

    @RequestMapping("/sort")
    public String sort(HttpServletRequest req) {
        List<Sys_dict> list = sysDictService.query(Cnd.where("sysType","=","store")
                .and("storeId","=",StringUtil.getStoreId())
                .asc("location").asc("path"));
        List<Sys_dict> firstMenus = new ArrayList<>();
        Map<String, List<Sys_dict>> secondMenus = new HashMap<>();
        for (Sys_dict menu : list) {
            if (menu.getPath().length() > 4) {
                List<Sys_dict> s = secondMenus.get(StringUtil.getParentId(menu.getPath()));
                if (s == null)
                    s = new ArrayList<>();
                s.add(menu);
                secondMenus.put(StringUtil.getParentId(menu.getPath()), s);
            } else if (menu.getPath().length() == 4) {
                firstMenus.add(menu);
            }
        }
        req.setAttribute("firstMenus", firstMenus);
        req.setAttribute("secondMenus", secondMenus);
        return "pages/platform/store/dict/sort";
    }

    @RequestMapping(value = "/sortDo/{ids}")
    @SJson
    public Object sortDo(@PathVariable String ids, HttpServletRequest req) {
        try {
            String[] menuIds = StringUtils.split(ids, ",");
            int i = 0;
            sysDictService.dao().execute(Sqls.create("update sys_dict set location=0"));
            for (String s : menuIds) {
                if (!Strings.isBlank(s)) {
                    sysDictService.update(org.nutz.dao.Chain.make("location", i), Cnd.where("id", "=", s));
                    i++;
                }
            }
            sysDictService.clearCache();
            return Result.success("globals.result.success");
        } catch (Exception e) {
            return Result.error("globals.result.error");
        }
    }

}
