package com.margosha.kse.calories.mcp;

import com.margosha.kse.calories.business.dto.UserDto;
import com.margosha.kse.calories.business.service.UserService;
import com.margosha.kse.calories.presentation.model.PageResponse;
import com.margosha.kse.calories.presentation.model.Pagination;
import lombok.AllArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
@AllArgsConstructor
public class UserMcpTools {

    private final UserService userService;

    @Tool(description = "Get a list of all registered users with pagination")
    public PageResponse<UserDto> getUsers(
            @ToolParam(description = "Pagination parameters with limit and offset", required = false)
            Pagination pagination) {
        if(pagination == null)pagination = new Pagination();
        Page<UserDto> page = userService.getAllUsers(pagination.getLimit(), pagination.getOffset());
        return PageResponse.from(page);
    }

    @Tool(description = "Get detailed information about a specific user by ID")
    public UserDto getUserById(
            @ToolParam(description = "User unique identifier (UUID)") UUID userId) {
        return userService.getUserById(userId);
    }

    @Tool(description = "Get user information by email address")
    public UserDto getUserByEmail(@ToolParam(description = "User email address") String email) {
        return userService.getUserByEmail(email);
    }

    @Tool(description = "Create a new user account with complete profile information")
    public UserDto createUser(
            @ToolParam(description = "User profile data including email, name, birth date, gender, weight, height, activity level, and goal")
            UserDto userRequest) {
        return userService.createUser(userRequest);
    }

    @Tool(description = "Update user profile information")
    public UserDto updateUser(
            @ToolParam(description = "User unique identifier (UUID)") UUID userId,
            @ToolParam(description = "Updated user profile information (provide any fields you want to update)")
            UserDto userRequest) {
        return userService.updateUser(userRequest, userId);
    }

    @Tool(description = "Delete a user account from the system")
    public boolean deleteUser(
            @ToolParam(description = "User unique identifier (UUID)") UUID userId) {
        return userService.deleteUser(userId);
    }

    @Tool(description = "Calculate daily calorie target for a specific user based on their profile")
    public int calculateDailyCalorieTarget(
            @ToolParam(description = "User unique identifier (UUID)") UUID userId) {
        return userService.getDailyTarget(userId);
    }
}