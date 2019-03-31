package com.aebiz.app.web.modules.controllers.open.H5;

import com.aebiz.app.acc.modules.models.Account_user;
import com.aebiz.app.cms.modules.models.Cms_article;
import com.aebiz.app.member.modules.models.Member_address;
import com.aebiz.app.member.modules.services.MemberAddressService;
import com.aebiz.baseframework.base.Result;
import com.aebiz.baseframework.view.annotation.SJson;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.nutz.dao.Cnd;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;


/**
 * @Auther: zenghaorong
 * @Date: 2019/3/31  23:19
 * @Description: 收货地址接口
 */
@Controller
@RequestMapping("/open/h5/address")
public class AddressH5Controller{

    private static final Log log = Logs.get();

    private MemberAddressService memberAddressService;

    /**
     * 进入收货地址列表页
     */
    @RequestMapping("goAddress.html")
    public String goAddress(){
        return "pages/front/h5/niantu/address";
    }

    /**
     * 进入收货地址编辑页
     */
    @RequestMapping("goAddressUp.html")
    public String goAddressUp(){
        return "pages/front/h5/niantu/addressUp";
    }


    /**
     * 进入收货地址添加页
     */
    @RequestMapping("goAddressAdd.html")
    public String goAddressAdd(){
        return "pages/front/h5/niantu/addressAdd";
    }


    /**
     * 查询收货地址列表
     */
    @RequestMapping("addressList.html")
    @SJson
    public Result addressList(String id){
        try {
            Subject subject = SecurityUtils.getSubject();
            Account_user accountUser = (Account_user) subject.getPrincipal();
            Cnd cnd = Cnd.NEW();
            cnd.and("accountId", "=", accountUser.getId() );
            List<Member_address> list = memberAddressService.query(cnd);
            return Result.success("ok",list);
        } catch (Exception e) {
            log.error("查询收货地址列表异常",e);
            return Result.error("fail");
        }
    }

    /**
     * 添加收货地址
     */
    @RequestMapping("addAddress.html")
    @SJson
    public Result addAddress(String id){
        try {
            Subject subject = SecurityUtils.getSubject();
            Account_user accountUser = (Account_user) subject.getPrincipal();
            Cnd cnd = Cnd.NEW();
            cnd.and("accountId", "=", accountUser.getId() );
            Member_address member_address =new Member_address();
            memberAddressService.insert(member_address);
            return Result.success("ok");
        } catch (Exception e) {
            log.error("查询收货地址列表异常",e);
            return Result.error("fail");
        }
    }
    /**
     * 编辑收货地址
     */




}
