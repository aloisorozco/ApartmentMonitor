
storage "file" {
    path= "./vault-data"
}

listener "tcp" {
  address = "127.0.0.1:8200"
  tls_cert_file = "certs/vault.crt"
  tls_key_file = "certs/vault.key"
}

disable_mlock=true