FROM adoptopenjdk/openjdk11:jdk-11.0.6_10-alpine as builder

COPY . /srv
WORKDIR /srv
RUN ./mvnw clean package -DskipTests

###############################################################################################################
FROM adoptopenjdk/openjdk11:jre-11.0.6_10-alpine

ENV SRV_HOME      /srv
ENV SRV_DIST_DIR  /srv-dist

RUN apk add bash

COPY --from=builder /srv/target/intermediate-song-importer-*-dist.tar.gz /importer.tar.gz

RUN mkdir /srv/temp \
	&& tar zxvf /importer.tar.gz -C /srv/temp \
	&& rm -rf /importer.tar.gz \
	&& mv /srv/temp/intermediate-song-importer* /srv/temp/something \
	&& mv /srv/temp/something/* /srv \
	&& rm -rf /srv/temp

# Set working directory for convenience with interactive usage
WORKDIR $SRV_HOME
