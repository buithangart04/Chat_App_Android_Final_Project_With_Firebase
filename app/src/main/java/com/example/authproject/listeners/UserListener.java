package com.example.authproject.listeners;

import com.example.authproject.models.Group;
import com.example.authproject.models.User;

public interface UserListener {
    void onUserCLick(User user, Group group);
}
