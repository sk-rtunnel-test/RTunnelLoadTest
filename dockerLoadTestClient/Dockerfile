FROM alpine
FROM adoptopenjdk/openjdk11
WORKDIR /opt/client
COPY dockerRTunnelServer/run.sh /opt/cient
COPY target/RestClient.jar /opt/client/RestClient.jar
RUN chmod u+x /opt/client/run.sh
RUN chmod u+x /opt/client/RestClient.jar
ENTRYPOINT ["/opt/client/run.sh"]