package com.hospital.mapper.sys;

import com.github.pagehelper.Page;
import com.hospital.dto.pagequery.UserPageQuery;
import com.hospital.dto.user.UserDTO;
import com.hospital.entity.sys.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DoctorMapper {

    /**
     * 分页查询
     * @param userPageQuery
     * @return
     */
    Page<User> pageQuery(UserPageQuery userPageQuery);

    /**
     * 新增用户
     */
    boolean addUser(UserDTO userDTO);
}
