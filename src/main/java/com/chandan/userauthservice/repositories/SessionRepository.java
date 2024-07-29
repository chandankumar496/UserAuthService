package com.chandan.userauthservice.repositories;

import com.chandan.userauthservice.models.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {

   //Optional<Session> save(Session session);

   Optional<Session> findByTokenAndUser_Id(String token, Long id);
}
