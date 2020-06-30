server_url=$1
curl -X GET "$server_url/schemas/sequencing_experiment?unrenderedOnly=true" -H  "accept: */*" > sequencing_experiment.at.json
curl -X GET "$server_url/schemas/sequencing_alignment?unrenderedOnly=true" -H  "accept: */*" > sequencing_alignment.at.json
curl -X GET "$server_url/schemas/qc_metrics?unrenderedOnly=true" -H  "accept: */*" > qc_metrics.at.json

