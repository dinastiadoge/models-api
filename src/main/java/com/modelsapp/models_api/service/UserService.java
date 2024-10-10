package com.modelsapp.models_api.service;

import com.modelsapp.models_api.Execptions.UserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.modelsapp.models_api.entity.Role;
import com.modelsapp.models_api.entity.User;
import com.modelsapp.models_api.repository.IRoleRepository;
import com.modelsapp.models_api.repository.IUserRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    @Autowired
    private IUserRepository iUserRepository;

    @Autowired
    private IRoleRepository iRoleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Optional<User> obterUsuarioId(Long usuarioId) {
        return this.iUserRepository.findById(usuarioId);
    }

    public User salvarUsuario(User usuario) {
        usuario.setRoles(usuario.getRoles()
                .stream()
                .map(role -> iRoleRepository.findByName(role.getName()))
                .toList());
        // CRIPTOGRAFIA
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return this.iUserRepository.save(usuario);
    }

    public User atualizarUsuario(User usuario) {
        usuario.setRoles(usuario.getRoles()
                .stream()
                .map(role -> iRoleRepository.findByName(role.getName()))
                .toList());
        // CRIPTOGRAFIA
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return this.iUserRepository.save(usuario);
    }

    public List<User> getUsersByRoles(String role) throws UserException {
         Optional<List<User>> filtredByRoleUsers = this.iUserRepository.getUsersByRoles(role);

         if(filtredByRoleUsers.isPresent()) {
             return filtredByRoleUsers.get();
         } else {
            throw new UserException("Não foi encontrado nenhum usuário com o papel " + role);
         }
    }

    public void excluirUsuario(User usuario) {
        this.iUserRepository.deleteById(usuario.getId());
    }

    public List<User> obterUsuarios() {
        return this.iUserRepository.findAll();
    }

    public boolean isUserLoggedIn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        Object principal = authentication.getPrincipal();
        return principal instanceof UserDetails;
    }
}
