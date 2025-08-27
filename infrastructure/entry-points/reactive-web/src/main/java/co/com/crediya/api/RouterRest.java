package co.com.crediya.api;

import co.com.crediya.api.config.UserPath;
import co.com.crediya.api.dto.CreateUserDto;
import co.com.crediya.api.exception.ErrorResponse;
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
    public RouterFunction<ServerResponse> routerFunction() {
        return route(POST(userPath.getUsers()), userHandler::listenSaveUser)
                .andRoute(GET(userPath.getUserByDocId()), userHandler::listenGetUserByDocId);
    }

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
}