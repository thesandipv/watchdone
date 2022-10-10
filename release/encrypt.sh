#!/bin/bash

gpg --passphrase=${ENCRYPT_KEY} --cipher-algo AES256 --symmetric --output release/keystore.gpg release/keystore.jks
gpg --passphrase=${ENCRYPT_KEY} --cipher-algo AES256 --symmetric --output release/serviceAccount.gpg release/serviceAccount.json
gpg --passphrase=${ENCRYPT_KEY} --cipher-algo AES256 --symmetric --output core/core/publish.properties.gpg core/core/publish.properties
gpg --passphrase=${ENCRYPT_KEY} --cipher-algo AES256 --symmetric --output app/google-services.gpg app/google-services.json
