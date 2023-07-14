package k.service;

import k.model.Usuario;

public interface TokenJwtService {
    public String generateJwt(Usuario usuario);
}
