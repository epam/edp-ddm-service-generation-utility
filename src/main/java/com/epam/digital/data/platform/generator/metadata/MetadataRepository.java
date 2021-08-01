package com.epam.digital.data.platform.generator.metadata;

import java.util.List;
import org.springframework.data.repository.Repository;

interface MetadataRepository extends Repository<Metadata, Long> {

  List<Metadata> findAll();
}
