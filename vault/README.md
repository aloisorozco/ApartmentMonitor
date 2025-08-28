How to set up the vault!


Preamble:
Obtain a certificate and RSA key pair (For developmnet purposes, you can use openssl to generate these 2 files).
Move the certificate and key under the certs folder and have them names as \certs\vault.crt and certs\vault.key.
You can then move these to the proper folder for use in application.properties (src/main/resources/ssl/).

For example:
openssl req -x509 -newkey rsa:4096 -nodes -keyout vault.key -out vault.crt -days 365 -addext "subjectAltName=DNS:localhost,IP:127.0.0.1"

openssl pkcs12 -export -in vault.crt -out vault-truststore.p12 -name vault -nokeys -passout pass:TrustStorePass

Step 1:
Open a terminal from the vault folder and run:
vault server -config='vaultconfig.hcl'
This will start the vault server on https://127.0.0.1:8200.


Step 2:
Open a new terminal in the vault folder and initiate the vault using:
$env:VAULT_ADDR = "https://127.0.0.1:8200"
$env:VAULT_SKIP_VERIFY = "true"
vault operator init

STORE THE KEYS AND ROOT TOKEN IN A SECURE LOCATION!

set the following environment variables:

You can then get the unseal keys and root token. The vault will then be sealed!


Step 3:
To now interact with the vault you will need to unseal it. To do so, you will need to use the multiple keys you were given and run:

vault operator unseal <key1>
vault operator unseal <key2>
vault operator unseal <key3>
etc.
Until the vault is unsealed. You can then log in with the root token:
vault login <root-token>


Step 4:
Now that you are able to interact with the vault. You can set up the SQL credential management using your root account for SQL:

vault secrets enable database

vault write database/roles/db-role `
   db_name=my-mysql-db `
   creation_statements="CREATE USER '{{name}}'@'%' IDENTIFIED BY '{{password}}'; GRANT SELECT, INSERT, UPDATE, DELETE ON *.* TO '{{name}}'@'%';" `
   default_ttl="1m" `
   max_ttl="24h"

vault write database/config/my-mysql-db `
     plugin_name=mysql-database-plugin `
     connection_url="{{username}}:{{password}}@tcp(127.0.0.1:3306)/" `
     allowed_roles="db-role" `
     username="root" `
     password="yourrootpassword"

vault auth enable approle

vault policy write spring-policy springpolicy.hcl

vault write auth/approle/role/spring-role `
    token_policies="spring-policy" `
    secret_id_ttl=24h `
    token_ttl=1h `
    token_max_ttl=4h

Step 5:
Now you can edit your application.properties to have the tokens to connect to the vault!

to get the roleID and secretID, run:

vault read auth/approle/role/spring-role/role-id
vault write -f auth/approle/role/spring-role/secret-id

Closing Notes:
The root credentials are used and stored in the vault. The vault then manages creating tem porary users with limited access. Currenty, the vault is used unsealed and with the root token coded in. This is bad practice. When we move to kubernetes clusters, we will replace the root token with application tokens.