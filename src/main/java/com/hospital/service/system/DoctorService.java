package com.hospital.service.system;

import com.hospital.common.response.PageResult;
import com.hospital.dto.pagequery.UserPageQuery;
import com.hospital.dto.user.UserDTO;

public interface DoctorService {

    /**
     * 分页查询
     * @param userPageQuery
     * @return
     */
    PageResult pageQuery(UserPageQuery userPageQuery);

    /**
     * 新增用户
     */
    boolean addUser(UserDTO userDTO);
}
