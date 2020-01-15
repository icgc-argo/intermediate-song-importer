FROM openjdk:11-jdk as builder

COPY . /srv
WORKDIR /srv
RUN ./mvnw clean package -DskipTests

###############################################################################################################
FROM openjdk:11-jre-stretch

ENV SRV_HOME      /srv
ENV SRV_DIST_DIR  /srv-dist
ENV PATH /usr/local/openjdk-11/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:$SRV_HOME/bin

COPY --from=builder /srv/target/intermediate-song-importer-*-dist.tar.gz /importer.tar.gz

RUN mkdir /srv/temp \
	&& tar zxvf /importer.tar.gz -C /srv/temp \
	&& rm -rf /importer.tar.gz \
	&& mv /srv/temp/intermediate-song-importer* /srv/temp/something \
	&& mv /srv/temp/something/* /srv \
	&& rm -rf /srv/temp

# Set working directory for convenience with interactive usage
WORKDIR $SRV_HOME
