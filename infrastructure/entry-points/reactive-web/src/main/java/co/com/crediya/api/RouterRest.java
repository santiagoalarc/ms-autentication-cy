package co.com.crediya.api;

import co.com.crediya.api.config.UserPath;
import co.com.crediya.api.dto.CreateUserDto;
import co.com.crediya.api.dto.LoginReqDto;
import co.com.crediya.api.dto.UserInfoDto;
import co.com.crediya.api.exception.ErrorResponse;
import co.com.crediya.model.user.TokenDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class RouterRest {

    private final Handler userHandler;
    private final UserPath userPath;

    @Bean
    @RouterOperation(operation = @Operation(
            operationId = "createUser",
            summary = "Create a new user",
            description = "Creates a new user with the provided information",
            tags = { "User Management" },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User data to create",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreateUserDto.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User created successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CreateUserDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid user data provided - Missing required fields or invalid format",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    ))
    public RouterFunction<ServerResponse> createUserRoute() {
        return route(POST(userPath.getUsers()), userHandler::listenSaveUser);
    }

    @Bean
    @RouterOperation(operation = @Operation(
            operationId = "getUserByDocId",
            summary = "Find user by document identification",
            description = "Retrieves a user by their document identification number",
            tags = { "User Management" },
            parameters = {
                    @Parameter(
                            in = ParameterIn.PATH,
                            name = "docId",
                            description = "User document identification number",
                            required = true,
                            schema = @Schema(type = "string")
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User found successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CreateUserDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "400", description = "Invalid document ID format"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    ))
    public RouterFunction<ServerResponse> getUserByDocIdRoute() {
        return route(GET(userPath.getUserByDocId()), userHandler::listenGetUserByDocId);
    }

    @Bean
    @RouterOperation(operation = @Operation(
            operationId = "loginUser",
            summary = "Authenticate user and get a JWT token",
            description = "Authenticates a user with provided credentials (email and password) and returns a JWT token if successful.",
            tags = { "User Management" },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User credentials for authentication",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginReqDto.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Authentication successful, returns a JWT token",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TokenDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid credentials provided",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    ))
    public RouterFunction<ServerResponse> loginUser(){
        return route(POST(userPath.getUserLogin()), userHandler::loginUser);
    }

    @Bean
    @RouterOperation(operation = @Operation(
            operationId = "getUsersByEmail",
            summary = "Get users by email addresses",
            description = "Retrieves multiple users by providing a comma-separated list of email addresses. Requires ADMIN role.",
            tags = { "User Management" },
            parameters = {
                    @Parameter(
                            in = ParameterIn.QUERY,
                            name = "emails",
                            description = "Comma-separated list of email addresses to search for users",
                            required = true,
                            schema = @Schema(type = "string"),
                            example = "user1@example.com,user2@example.com,user3@example.com"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Users found successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            type = "array",
                                            implementation = UserInfoDto.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid email format or missing emails parameter",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Access denied - ADMIN role required",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "No users found with the provided email addresses"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error"
                    )
            },
            security = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth")
    ))
    public RouterFunction<ServerResponse> getUsersInfo(){
        return route(GET(userPath.getUsersByEmail()), userHandler::getUserByEmail);
    }

    @Bean
    public RouterFunction<ServerResponse> healthCheck(){
        return route(GET("/health"), userHandler::healthCheck);
    }
}