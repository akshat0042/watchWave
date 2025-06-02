package org.learn.watchwave.auth.config;

import org.learn.watchwave.auth.model.entity.Role;
import org.learn.watchwave.auth.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public DataInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== INITIALIZING DEFAULT ROLES ===");

        createRoleIfNotExists("USER", "Default user role");
        createRoleIfNotExists("CREATOR", "Content creator role");
        createRoleIfNotExists("ADMIN", "Administrator role");

        System.out.println("Total roles in database: " + roleRepository.count());
    }

    private void createRoleIfNotExists(String roleName, String description) {
        if (!roleRepository.existsByRoleName(roleName)) {
            Role role = new Role();
            role.setRoleName(roleName);
            role.setDescription(description);
            roleRepository.save(role);
            System.out.println("✅ Created role: " + roleName);
        } else {
            System.out.println("ℹ️ Role already exists: " + roleName);
        }
    }
}
