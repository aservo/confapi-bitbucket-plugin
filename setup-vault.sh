#!/bin/bash
# Stop on first error
set -e;

#docker run --cap-add=IPC_LOCK -d -e 'VAULT_DEV_ROOT_TOKEN_ID=myroot' -p 127.0.0.1:8200:8200/tcp vault

#echo ----------------Use root token----------------
VAULT_TOKEN=myroot
#curl -X GET -H "X-Vault-Token:$VAULT_TOKEN" http://127.0.0.1:8200/v1/auth/token/lookup-self
# {"request_id":"782a5bb9-a93a-c941-6e9e-df0f4a8c470e","lease_id":"","renewable":false,"lease_duration":0,"data":{"accessor":"818a0619-cb14-c773-00a8-98a721b31037","creation_time":1484065896,"creation_ttl":0,"display_name":"root","explicit_max_ttl":0,"id":"858a6658-682e-345a-e4c4-a6e14e6f7853","meta":null,"num_uses":0,"orphan":true,"path":"auth/token/root","policies":["root"],"ttl":0},"wrap_info":null,"warnings":null,"auth":null}

echo ----------------Enable approle auth----------------
curl -X POST -H "X-Vault-Token:$VAULT_TOKEN" -d '{"type":"approle"}' http://127.0.0.1:8200/v1/sys/auth/approle
#curl -X GET -H "X-Vault-Token:$VAULT_TOKEN" http://127.0.0.1:8200/v1/sys/auth
# {"token/":{"config":{"default_lease_ttl":0,"max_lease_ttl":0},"description":"token based credentials","type":"token"},"approle/":{"config":{"default_lease_ttl":0,"max_lease_ttl":0},"description":"","type":"approle"},"request_id":"eca2e6ac-7e92-9370-f224-e1827d40df7a","lease_id":"","renewable":false,"lease_duration":0,"data":{"approle/":{"config":{"default_lease_ttl":0,"max_lease_ttl":0},"description":"","type":"approle"},"token/":{"config":{"default_lease_ttl":0,"max_lease_ttl":0},"description":"token based credentials","type":"token"}},"wrap_info":null,"warnings":null,"auth":null}

echo ----------------Create policy----------------
curl --fail -X POST -H "X-Vault-Token:$VAULT_TOKEN" -d '{"policy":"# Dev servers have version 2 of KV secrets engine mounted by default, so will\n# need these paths to grant permissions:\npath \"secret/data/*\" {\n  capabilities = [\"create\", \"update\"]\n}\n\npath \"secret/data/foo\" {\n  capabilities = [\"read\"]\n}\n"}' http://127.0.0.1:8200/v1/sys/policies/acl/my-policy
curl --fail -X GET -H "X-Vault-Token:$VAULT_TOKEN" http://127.0.0.1:8200/v1/sys/policies/acl/my-policy
# {"name":"dev","rules":"{\"name\": \"dev\", \"path\": {\"secret/*\": {\"policy\": \"write\"}}}","request_id":"b171e046-ce5a-d55d-2fd6-20ef3bc01adc","lease_id":"","renewable":false,"lease_duration":0,"data":{"name":"dev","rules":"{\"name\": \"dev\", \"path\": {\"secret/*\": {\"policy\": \"write\"}}}"},"wrap_info":null,"warnings":null,"auth":null}

echo ----------------Create roles----------------
curl -X POST -H "X-Vault-Token:$VAULT_TOKEN" -d '{"policies":"dev"}' http://127.0.0.1:8200/v1/auth/approle/role/app
curl -X GET -H "X-Vault-Token:$VAULT_TOKEN" http://127.0.0.1:8200/v1/auth/approle/role\?list\=true
# {"request_id":"8de3b02d-bbe3-b8b1-5072-c6ff26ef8633","lease_id":"","renewable":false,"lease_duration":0,"data":{"keys":["app"]},"wrap_info":null,"warnings":null,"auth":null}

echo ----------------Get token for app----------------
role_id=$(curl -s -X GET -H "X-Vault-Token:$VAULT_TOKEN" http://127.0.0.1:8200/v1/auth/approle/role/app/role-id | jq -r '.data.role_id')
# {"request_id":"de5753ba-adb4-7691-f38a-23c9e7eb2caf","lease_id":"","renewable":false,"lease_duration":0,"data":{"role_id":"00f01bdc-62b4-7abf-a7d6-f190d370a576"},"wrap_info":null,"warnings":null,"auth":null}
secret_id=$(curl -s -X POST -H "X-Vault-Token:$VAULT_TOKEN" http://127.0.0.1:8200/v1/auth/approle/role/app/secret-id | jq -r '.data.secret_id')

echo $role_id
echo $secret_id

# {"request_id":"40de755e-e9a7-d07e-e487-cb5b9db5a3c2","lease_id":"","renewable":false,"lease_duration":0,"data":{"secret_id":"f7208480-4665-4062-72ee-d9d575b02a59","secret_id_accessor":"a8f9ba04-cd88-8087-b1cf-9d0dbf38c0fa"},"wrap_info":null,"warnings":null,"auth":null}
app_token=$(curl -s -X POST -d '{"role_id": "'"$role_id "'","secret_id": "'"$secret_id"'"}' http://127.0.0.1:8200/v1/auth/approle/login | jq -r '.auth.client_token')
# {"request_id":"b71c3ce5-1d00-5924-a24a-3124c58cd6f8","lease_id":"","renewable":false,"lease_duration":0,"data":null,"wrap_info":null,"warnings":null,"auth":{"client_token":"69d26d5d-7bae-0688-9682-f1a3a8272cb8","accessor":"90c300cb-2533-7253-fa79-8aa3332abfbe","policies":["default","dev"],"metadata":{},"lease_duration":2764800,"renewable":true}}

echo $app_token

echo ----------------Access as app----------------
VAULT_TOKEN=$app_token
curl -X GET -H "X-Vault-Token:$VAULT_TOKEN" http://127.0.0.1:8200/v1/auth/token/lookup-self
# {"request_id":"06c1db47-e418-49f6-5eb3-d32c8d3b16b5","lease_id":"","renewable":false,"lease_duration":0,"data":{"accessor":"90c300cb-2533-7253-fa79-8aa3332abfbe","creation_time":1484066058,"creation_ttl":2764800,"display_name":"approle","explicit_max_ttl":0,"id":"69d26d5d-7bae-0688-9682-f1a3a8272cb8","meta":{},"num_uses":0,"orphan":true,"path":"auth/approle/login","policies":["default","dev"],"renewable":true,"ttl":2764762},"wrap_info":null,"warnings":null,"auth":null}
curl -X POST -H "X-Vault-Token:$VAULT_TOKEN" -d '{"password":"drowssap"}' http://127.0.0.1:8200/v1/secret/data/user1
curl -X GET -H "X-Vault-Token:$VAULT_TOKEN" http://127.0.0.1:8200/v1/secret/data/user1
# {"request_id":"1a8eba9b-a557-6eb1-139b-89f6204479bd","lease_id":"","renewable":false,"lease_duration":2764800,"data":{"password":"drowssap"},"wrap_info":null,"warnings":null,"auth":null}

exit 0