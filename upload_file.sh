

TOKEN=$( echo "{\"key\": \"36b75fa39f3c0aac17080f2f05c8721f4a5c2d30\"}" | jq -r '.key' );
REPO_ID=$( curl -k -L --silent --header "Authorization: Token ${TOKEN}" https://files.n-core.ru/api2/repos/ | jq -r ".[] | select (.name==\"gitlab\") | .id" )
UPLOAD_HOST=$( echo "https://files.n-core.ru/api2/repos/${REPO_ID}/upload-link/" )
UPLOAD_LINK=$( curl -k -L --silent --header "Authorization: Token ${TOKEN}" "${UPLOAD_HOST}" | jq --raw-output '.')
FILENAME=$( echo "${CI_COMMIT_TAG}_${CI_RUNNER_ID}_${CI_COMMIT_REF_NAME}.apk" )
curl -k -L --silent --header "Authorization: Token ${TOKEN}" -F "file=@app/build/outputs/${FILENAME}" -F "filename=${FILENAME}" -F parent_dir=/  "${UPLOAD_LINK}"
