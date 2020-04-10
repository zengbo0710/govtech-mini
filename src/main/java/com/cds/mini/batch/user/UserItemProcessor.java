package com.cds.mini.batch.user;

import com.cds.mini.entity.User;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Component
public class UserItemProcessor implements ItemProcessor<User, User> {
    @Override
    public User process(User item) {
        Timestamp today = Timestamp.valueOf(LocalDateTime.now());
        item.setCreatedBy(1);
        item.setCreatedDate(today);
        item.setUpdatedBy(1);
        item.setUpdatedDate(today);

        return item;
    }
}
