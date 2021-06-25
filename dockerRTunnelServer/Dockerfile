FROM alpine
FROM adoptopenjdk/openjdk11
WORKDIR /opt/server
COPY dockerRTunnelServer/run.sh /opt/server
COPY target/rtunnel-loadtest-1.0-SNAPSHOT.jar /opt/server/RestServer.jar
RUN chmod u+x /opt/server/run.sh
RUN chmod u+x /opt/server/RestServer.jar
ENTRYPOINT ["/opt/server/run.sh"]
