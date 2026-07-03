package br.com.techtins.contaquiz.resource;

import br.com.techtins.contaquiz.dto.auth.AuthRequest;
import br.com.techtins.contaquiz.dto.auth.AuthResponse;
import br.com.techtins.contaquiz.dto.auth.RegisterRequest;
import br.com.techtins.contaquiz.service.AuthService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {
    
    @Inject
    AuthService authService;

    @POST
    @Path("/register")
    public Response register(@Valid RegisterRequest dto) {
        AuthResponse response = authService.registerWithToken(dto);
        return Response.status(201).entity(response).build();
    }

    @POST
    @Path("/login")
    public Response login(@Valid AuthRequest dto) {
        AuthResponse response = authService.login(dto);
        return Response.ok(response).build();
    }
}
