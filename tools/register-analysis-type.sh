#!/bin/bash

register_analysis_type_file=$1

if [ ! -f $register_analysis_type_file ]; then
	echo "The file $register_analysis_type_file does not exist"
	exit 2
fi

curl -XPOST \
    -H 'Content-Type: application/json'  \
	-H 'Authorization: Bearer f69b726d-d40f-4261-b105-1ec7e6bf04d5' \
	-d "@$register_analysis_type_file" \
	http://localhost:9080/schemas
