package:
	@mvn clean package -DskipTests

client: package
	@cd target; tar zxvf *.tar.gz

