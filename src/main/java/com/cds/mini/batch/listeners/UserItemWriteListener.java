package com.cds.mini.batch.listeners;

import com.cds.mini.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemWriteListener;

import java.util.List;

@Slf4j
public class UserItemWriteListener implements ItemWriteListener<User> {
    @Override
    public void beforeWrite(List<? extends User> items) {

    }

    @Override
    public void afterWrite(List<? extends User> items) {
        items.forEach(user -> log.info("Insert successfully for {}", user));
    }

    @Override
    public void onWriteError(Exception exception, List<? extends User> items) {

    }
}
