package org.spring.aicloud.service.impl;

import org.spring.aicloud.entity.User;
import org.spring.aicloud.mapper.UserMapper;
import org.spring.aicloud.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author li
 * @since 2024-06-30
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
