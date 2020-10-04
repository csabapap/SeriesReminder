#!bin/sh

ENCRYPT_KEY=$1

#decrypt services file
openssl aes-256-cbc -md sha256 -d -in signing/google-services.aes -out app/google-services.json -k "$ENCRYPT_KEY"