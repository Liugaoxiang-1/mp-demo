package com.itheima.mp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.itheima.mp.domain.po.Address;
import com.itheima.mp.domain.po.User;
import com.itheima.mp.domain.vo.AddressVO;
import com.itheima.mp.domain.vo.UserVO;
import com.itheima.mp.mapper.UserMapper;
import com.itheima.mp.service.IUserService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper,User> implements IUserService  {
    @Override
    public void deductionBalance(Long id, Integer money) {
        //1,查询用户
        User user = getById(id);

        //2,校验用户状态
        if (user == null || user.getStatus() == 2){
            throw new RuntimeException("用户状态异常！");
        }

        //3,校验用户余额是否充足
        if (user.getBalance() < money){
            throw new RuntimeException("用户余额不足！");
        }

        //4，扣减余额
        int remainBalance = user.getBalance() - money;
        lambdaUpdate()
                .set(User::getBalance,remainBalance)
                .set(remainBalance == 0,User::getStatus,2)
                .eq(User::getId,id)
                .eq(User::getBalance,user.getBalance())
                .update();
    }

    @Override
    public List<User> queryUsers(String name, Integer status, Integer minBalance, Integer maxBalance) {
        return lambdaQuery()
                .like(name != null, User::getUsername, name)
                .eq(status != null, User::getStatus, status)
                .ge(minBalance != null, User::getBalance, minBalance)
                .le(name != null, User::getBalance, maxBalance)
                .list();
    }

    @Override
    public UserVO queryUserAndAddressById(Long id) {
        //1.查询用户
        User user = getById(id);
        if(user == null || user.getStatus() == 2){
            throw new RuntimeException("用户状态异常！");
        }
        //2.查询地址
        List<Address> addresses = Db.lambdaQuery(Address.class).eq(Address::getUserId, id).list();

        //3.封装VO
        //3.1 转User的PO为VO
        UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);
        //3.2 转地址VO
        if (CollUtil.isNotEmpty(addresses)){


            userVO.setAddresses(BeanUtil.copyToList(addresses, AddressVO.class));
        }
        return userVO;
    }

    @Override
    public List<UserVO> queryUserAndAddressByIds(List<Long> ids) {
        //1.查询用户
        List<User> users = listByIds(ids);
        if (CollUtil.isNotEmpty(users)){
            return Collections.emptyList();
        }
        //2.查询地址
        //2.1 获取用户ID集合
        List<Long> userIds = users.stream().map(User::getId).collect(Collectors.toList());
        //2.2 根据用户ID查询地址
        List<Address> addresses = Db.lambdaQuery(Address.class).in(Address::getUserId, userIds).list();
        //2.3 转地址VO
        List<AddressVO> addressVOList = BeanUtil.copyToList(addresses, AddressVO.class);
        //2.4 用户地址集合，分类整理，相同用户的放入到一个集合（组）中
        Map<Long, List<AddressVO>> addressMap = new HashMap<>(0);
        if (CollUtil.isNotEmpty(addressVOList)) {
            addressMap = addressVOList.stream().collect(Collectors.groupingBy(AddressVO::getUserId));
        }
        //3.转VO集合
        ArrayList<UserVO> list = new ArrayList<>(users.size());
        for (User user : users) {
            //3.1 转User的PO为VO
            UserVO vo = BeanUtil.copyProperties(users, UserVO.class);
            list.add(vo);
            //3.2 转地址VO
            vo.setAddresses(addressMap.get(user.getId()));
        }
        return null;
    }
}
