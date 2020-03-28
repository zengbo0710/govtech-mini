package com.cds.mini.batch.listeners;

import com.cds.mini.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemReadListener;

@Slf4j
public class UserItemReadListener implements ItemReadListener<User> {
    @Override
    public void beforeRead() {

    }

    @Override
    public void afterRead(User item) {
        log.info("Read successfully for {}", item);
    }

    @Override
    public void onReadError(Exception ex) {
        log.warn(ex.getMessage(), ex);
    }
}
