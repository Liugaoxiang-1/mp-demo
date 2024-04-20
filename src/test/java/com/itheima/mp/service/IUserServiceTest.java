package com.itheima.mp.service;

import com.itheima.mp.domain.po.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class IUserServiceTest {
    @Autowired
    private IUserService userService;

    @Test
    void tetsSaveUser(){
        User user = new User();
//        user.setId(5L);
        user.setUsername("Lilei");
        user.setPassword("123");
        user.setPhone("18688990011");
        user.setBalance(200);
        user.setInfo("{\"age\": 24, \"intro\": \"英文老师\", \"gender\": \"female\"}");
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userService.save(user);

    }
    @Test
    void testQuery(){
        List<User> users = userService.listByIds(List.of(1L, 2L, 3L));
        users.forEach(System.out::println);
    }
    private User buildUser(int  i){
        User user = new User();
        user.setUsername("user_"+i);
        user.setPassword("123");
        user.setPhone(""+ (18688190000L + i));
        user.setBalance(2000);
        user.setInfo("{\"age\": 24, \"intro\": \"英文老师\", \"gender\": \"female\"}");
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        return user;
    }
    @Test
    void testSaveOneByOne(){
        long b = System.currentTimeMillis();
        for (int i = 1; i <= 100000 ; i++) {
            userService.save(buildUser(i));

        }
        long e = System.currentTimeMillis();
        System.out.println("耗时："+(e-b));
    }

    @Test
    void testSaveBatch(){
        //每次插入1000条，总共插100次


        //准备容量为1000的集合
        ArrayList<User> list = new ArrayList<>(1000);
        long b = System.currentTimeMillis();
        for (int i = 1; i <= 100000 ; i++) {
            //添加一个user
            list.add(buildUser(i));

            //每1000条批量插入一次
            if(i%1000 == 0){
                userService.saveBatch(list);
                //清空集合准备下一批数据
                list.clear();
            }
            
        }
        long e = System.currentTimeMillis();
        System.out.println("耗时：" + (e - b));
    }
}