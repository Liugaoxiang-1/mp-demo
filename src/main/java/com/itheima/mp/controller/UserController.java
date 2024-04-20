package com.itheima.mp.controller;

import cn.hutool.core.bean.BeanUtil;
import com.itheima.mp.domain.dto.UserFormDTO;
import com.itheima.mp.domain.po.User;
import com.itheima.mp.domain.query.UserQuery;
import com.itheima.mp.domain.vo.UserVO;
import com.itheima.mp.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Api(tags = "用户管理接口")
@RequestMapping("/users")
@RestController
@RequiredArgsConstructor
public class UserController {



    private final IUserService userService;


    @ApiOperation("新增用户接口")
    @PostMapping
    public void saveUser(@RequestBody UserFormDTO userDTO){
        //1.把DTO拷贝到PO
        User user = BeanUtil.copyProperties(userDTO, User.class);

        //2.新增
        userService.save(user);

    }

    @ApiOperation("删除用户接口")
    @DeleteMapping("{id}")
    public void deleteUserById(@ApiParam("用户id") @PathVariable("id") Long id){
    userService.removeById(id);

    }

    @ApiOperation("根据id查询用户接口")
    @GetMapping("{id}")
    public UserVO queryUserById(@ApiParam("用户id") @PathVariable("id") Long id){


        return userService.queryUserAndAddressById(id);
    }

    @ApiOperation("根据id批量查询")
    @GetMapping
    public List<UserVO> queryUserByIds(@ApiParam("用户id") @RequestParam("ids") List<Long> ids){
        return userService.queryUserAndAddressByIds(ids);

    }


    @ApiOperation("扣减用户余额接口")
    @PutMapping("/{id}/deduction/{money}")
    public void deductionBalance(@ApiParam("用户id") @PathVariable("id") Long id,
                                   @ApiParam("要扣减的金额") @PathVariable("money") Integer money){
        userService.deductionBalance(id,money);


    }

    @ApiOperation("根据复杂条件查询用户接口")
    @GetMapping("/list")
    public List<UserVO> queryUsers(UserQuery query){
        List<User> users = userService.queryUsers
                (query.getName(),query.getStatus(),query.getMinBalance(),query.getMaxBalance());
        return BeanUtil.copyToList(users,UserVO.class);
    }
}

