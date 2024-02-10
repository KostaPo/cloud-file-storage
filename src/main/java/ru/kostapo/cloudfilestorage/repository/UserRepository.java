package ru.kostapo.cloudfilestorage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kostapo.cloudfilestorage.entity.AppUser;

@Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {

}
