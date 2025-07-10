package com.escribasmostachos.Escribasmostachos.utils;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import org.hashids.Hashids;

@Component
public class PublicIdGenerator {
    private final Hashids hashIds;

    public PublicIdGenerator(@Value("${hashid.salt}") String salt) {
        this.hashIds = new Hashids(salt, 8);
    }

    public String encode(Long id) {
        return hashIds.encode(id);
    }

    public Long decode(String publicId) {
        long[] numbers = hashIds.decode(publicId);
        return numbers.length > 0 ? numbers[0] : null;
    }
}
