#!/bin/bash

gpg --quiet --batch --yes --decrypt --passphrase=${ENCRYPT_KEY} --output release/keystore.jks release/keystore.gpg
gpg --quiet --batch --yes --decrypt --passphrase=${ENCRYPT_KEY} --output release/serviceAccount.json release/serviceAccount.gpg
gpg --quiet --batch --yes --decrypt --passphrase=${ENCRYPT_KEY} --output core/utils/publish.properties core/utils/publish.properties.gpg
gpg --quiet --batch --yes --decrypt --passphrase=${ENCRYPT_KEY} --output app/google-services.json app/google-services.gpg
