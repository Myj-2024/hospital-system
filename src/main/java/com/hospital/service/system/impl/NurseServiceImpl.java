package com.hospital.service.system.impl;

import com.hospital.mapper.sys.NurseMapper;
import com.hospital.service.system.NurseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NurseServiceImpl implements NurseService {

    @Autowired
    private NurseMapper nurseMapper;
}
