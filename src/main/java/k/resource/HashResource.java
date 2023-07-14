package k.resource;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import k.service.HashService;

@ApplicationScoped
public class HashResource{

    @Inject
    HashService service;
    
    public String getHashSenha(String senha) {
        return service.getHashSenha(senha);
    }

}

