package com.cds.mini.batch.user;

import com.cds.mini.entity.User;
import com.cds.mini.repository.UserRepository;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;

@Component
public class UserItemWriter implements ItemWriter<User> {
    private final UserRepository userRepository;

    public UserItemWriter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void write(List<? extends User> items) {
        items.forEach(user -> {
            User existingUser = userRepository.findByUserId(user.getUserId()).orElse(null);
            if (existingUser == null) {
                userRepository.save(user);
            } else {
                BeanUtils.copyProperties(user, existingUser, "id");
                userRepository.save(existingUser);
            }
        });
    }
}
