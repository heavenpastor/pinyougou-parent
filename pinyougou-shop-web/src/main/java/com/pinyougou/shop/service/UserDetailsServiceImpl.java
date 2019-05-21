package com.pinyougou.shop.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class UserDetailsServiceImpl implements UserDetailsService {


    private SellerService sellService;

    public SellerService getSellService() {
        return sellService;
    }

    public void setSellService(SellerService sellService) {
        this.sellService = sellService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("经过了 UserDetailsServiceImpl");
        //构建角色列表
        List<GrantedAuthority> grantAuths=new ArrayList();
        grantAuths.add(new SimpleGrantedAuthority("ROLE_SELLER"));


        TbSeller tbSeller = sellService.findOne(username);

        User user = new User(username, tbSeller.getPassword(), grantAuths);
        if(tbSeller!=null){
            if(tbSeller.getStatus().equals("1")){
                return new User(username,tbSeller.getPassword(),grantAuths);
            }else{
                return null;
            }
        }else{
            return null;
        }


    }
}
