package com.epam.digital.data.platform.generator.permissionmap;

import java.util.List;
import org.springframework.data.repository.Repository;

interface PermissionRepository extends Repository<Permission, Long> {

  List<Permission> findAll();
}
