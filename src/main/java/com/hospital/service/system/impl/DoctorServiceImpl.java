package com.hospital.service.system.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hospital.common.response.PageResult;
import com.hospital.dto.pagequery.UserPageQuery;
import com.hospital.dto.user.UserDTO;
import com.hospital.entity.sys.User;
import com.hospital.mapper.sys.DoctorMapper;
import com.hospital.service.system.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DoctorServiceImpl implements DoctorService {

    @Autowired
    private DoctorMapper doctorMapper;

    /**
     * 分页查询
     * @param userPageQuery
     * @return
     */
    @Override
    public PageResult pageQuery(UserPageQuery userPageQuery) {
        PageHelper.startPage(userPageQuery.getPageNum(), userPageQuery.getPageSize());
        Page<User> page = doctorMapper.pageQuery(userPageQuery);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public boolean addUser(UserDTO userDTO) {
        return doctorMapper.addUser(userDTO);
    }


}
