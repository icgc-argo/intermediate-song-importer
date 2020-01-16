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
where `inputDir` contains files labeled with `<analysisId>.json`
