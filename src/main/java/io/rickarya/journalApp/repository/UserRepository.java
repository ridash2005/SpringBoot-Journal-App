package io.rickarya.journalApp.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import io.rickarya.journalApp.entity.JournalEntry;
import io.rickarya.journalApp.entity.User;

public interface UserRepository extends MongoRepository<User, ObjectId> {
    User findByUserName(String username);

    void deleteByUserName(String username);
}


