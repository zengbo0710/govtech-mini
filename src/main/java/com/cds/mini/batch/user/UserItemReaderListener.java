package com.cds.mini.batch.user;

import com.cds.mini.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserItemReaderListener implements ItemReadListener<User> {
    @Override
    public void beforeRead() {

    }

    @Override
    public void afterRead(User item) {
        log.info("Read successfully {}", item);
    }

    @Override
    public void onReadError(Exception ex) {
        log.error("Error in reading: {}", ex.getMessage());
    }
}
