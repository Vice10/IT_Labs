package com.it.grpc;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface IWebApiRepo extends MongoRepository<Base, String> {
}