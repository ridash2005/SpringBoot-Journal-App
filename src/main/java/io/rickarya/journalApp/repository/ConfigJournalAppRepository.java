package io.rickarya.journalApp.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import io.rickarya.journalApp.entity.ConfigJournalAppEntity;
import io.rickarya.journalApp.entity.User;

public interface ConfigJournalAppRepository extends MongoRepository<ConfigJournalAppEntity, ObjectId> {

}


