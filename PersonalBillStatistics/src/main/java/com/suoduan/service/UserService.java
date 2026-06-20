package com.suoduan.service;

import com.suoduan.entity.User;
import com.suoduan.mapper.UserMapper;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserMapper accountMapper;

    @Autowired
    public UserService(UserMapper accountMapper) {
        this.accountMapper = accountMapper;
    }

    public User register(String username, String password, String email) {
        User existingAccount = accountMapper.findByUsername(username);
        if (existingAccount != null) {
            throw new RuntimeException("用户名已存在");
        }
        User newAccount = new User();
        newAccount.setUsername(username);
        newAccount.setPassword(encrypt(password));
        newAccount.setEmail(email);
        accountMapper.insert(newAccount);
        return newAccount;
    }

    public User login(String username, String password) {
        User account = accountMapper.findByUsername(username);
        if (account == null || !BCrypt.checkpw(password, account.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        return account;
    }

    public User findById(Integer id) {
        return accountMapper.findById(id);
    }

    public void update(User user) {
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(encrypt(user.getPassword()));
        }
        accountMapper.update(user);
    }

    private String encrypt(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }
}
