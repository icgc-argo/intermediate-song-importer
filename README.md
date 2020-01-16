# intermediate-song-importer
CLI tool to import icdc-dcc SONG data into an intermediate SONG server while preserving the objectIds. 
This is a temporary script that should only be run by system administrators for intermediate SONG. 
Warning: this script does not preserve the ID space of intermediate SONG. It is sole purpose is to 
import data into intermediate SONG and publish it without having to copy/move object data around.


## Building
```bash
make build
```

## Testing with Docker
```bash
make test
```

## Configuration
After building, you can create 
```bash
# Set the source song's configuration
./target/dist/bin/intermediate-song-importer config set source -p myprofile -a <accessToken> -u <sourceSongUrl>

# Set the target song's configuration
./target/dist/bin/intermediate-song-importer config set target -p myprofile -a <accessToken> -u <sourceSongUrl> \
  --db-name song --db-username postgres --db-password password --db-hostname localhost --db-port 5432
```

You can get a list of available profiles via
```bash
./target/dist/bin/intermediate-song-importer config get --list
```

And you can get the configuration for a profile via
```bash
./target/dist/bin/intermediate-song-importer config get -p myprofile
```

## Running
After completing configuration, you can run with
```bash
./target/dist/bin/intermediate-song-importer run -p myprofile -d <inputDir>
```
where `inputDir` contains files with names formatted to `<analysisId>.json`

## Real Scenario
After initializing the `argo-meta` submodule with
```bash
git submodule update --init --recursive
```

and creating the studyId `PACA-CA`, the following command can be run to execute the import
```bash
./target/dist/bin/intermediate-song-importer config set -p myprofile source -u https://song.cancercollaboratory.org -a <access token with collab.WRITE scope>
./target/dist/bin/intermediate-song-importer config set -p myprofile target -u <intermediate-song-url> -a <access token for intermediate-song> -dn <dbname> -du <username> -dq <password> -dh <hostname> -dp <port>
./target/dist/bin/intermediate-song-importer run -p myprofile -d ../../../argo-meta/icgc_song_payloads/PACA-CA
```
