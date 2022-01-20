package com.taramin.testProject.entity;

public class CurrentUser {
    private static Long currentUserId;

    public static Long getCurrentUserId() {
        return currentUserId;
    }
    public static void setCurrentUserId(Long currentUserId) {
        CurrentUser.currentUserId = currentUserId;
    }
}
